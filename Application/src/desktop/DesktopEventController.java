
package desktop;

import java.util.UUID;

import com.midfield_system.api.stream.IoParam;
import com.midfield_system.api.system.CommPacket;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.api.system.ObjectId;
import com.midfield_system.api.system.PacketIoException;
import com.midfield_system.api.util.Log;

import desktop.DesktopEvent.MessageType;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: DesktopEventController 
 *
 * Date Modified: 2018.08.03
 *
 */

//==============================================================================
public class DesktopEventController
	extends	AppCommObject
{
	//- PUBLIC CONSTANT VALUE --------------------------------------------------	
	public static final String
		CONTROLLER_NAME = "DesktopEventController"; //$NON-NLS-1$
	
	//- PRIVATE CONSTANT VALUE -------------------------------------------------	
	private static final String
		STR_ILLEGAL_MESSAGE = "不正なメッセージを受信しました．: %s from %s\n";
	
//==============================================================================
//  CLASS VARIABLE:
//==============================================================================
	
//------------------------------------------------------------------------------
// [PRIVATE STATIC FIELD]
//------------------------------------------------------------------------------
	
	private static int instanceNumber = 0;

//------------------------------------------------------------------------------
//  PRIVATE STATIC METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private static String getNewObjectIdName()
	{
		String coName = new String(
			CONTROLLER_NAME + DesktopEventController.instanceNumber++
		);
		return coName;
	}	
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private ObjectId dstId = null;
	private FrameViewer viewer = null;
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	public DesktopEventController()
		throws	SystemException
	{
		// MfsCommObject を初期化する．
		super(getNewObjectIdName());	// SystemException
		
		// 遠隔デスクトップ用ビューワを生成する．
		this.viewer = new FrameViewer(this);
		this.viewer.setupGui();
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void shutdown()
	{
		this.viewer.shutdown();
	}

//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------

	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void startControl(String dstAddr)
		throws	SystemException
	{
		MfsNode mfs = MfsNode.getInstance();
		UUID dstNodeId = mfs.resolveNodeId(dstAddr);
			// SystemException
		
		// イベント配送先の ObjectId を生成する．
		this.dstId = new ObjectId(
			DesktopEventProcessor.PROCESSOR_NAME, dstNodeId
		);
		// 制御開始イベントを配送する．
		DesktopEvent request = new DesktopEvent(
			MessageType.START_CONTROL, null
		);
		dispatchMessageEvent(this.dstId, request);
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void dispatchControlMessage(
		DesktopEvent.ActionType actType, Object obj
	) {
		// 制御メッセージイベントを配送する．
		DesktopEvent ev = new DesktopEvent(
			MessageType.CONTROL_MESSAGE, actType, obj
		);
		dispatchMessageEvent(this.dstId, ev);
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void stopControl()
	{
		// 制御停止イベントを配送する．
		DesktopEvent request = new DesktopEvent(
			MessageType.STOP_CONTROL, null
		);
		dispatchMessageEvent(this.dstId, request);
	}
	
//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------

	//- PROTECTED METHOD -------------------------------------------------------
	//- OVERRIDES: CommObject
	//	
	@Override
	protected boolean incomingPacketHandler(CommPacket inPkt)
	{
		boolean wasHandled = true;
		
		// パケットから MessageEvent のインスタンスを取得する．
		MessageEvent ev = getMessageEventFromPacket(inPkt);
		if (ev == null) {
			// MessageEvent のインスタンスを取得できなかった場合，
			// この受信パケットを処理できなかったものとして false を返す．
			wasHandled = false;
			return wasHandled;
		}
		// 取得した MessageEvent を DesktopEvent として扱う．
		DesktopEvent dcEv = (DesktopEvent)ev;
		
		switch (dcEv.getMessageType()) {
		case START_CONTROL		: evHn_illegalMessage(dcEv);	break;
		case CONTROL_ACCEPTED	: evHn_ControlAccepted(dcEv);	break;
		case CONTROL_REFUSED	: evHn_ControlRefused(dcEv);	break;
		case CONTROL_MESSAGE	: evHn_illegalMessage(dcEv);	break;
		case STOP_CONTROL		: evHn_illegalMessage(dcEv);	break;
		default 				: wasHandled = false;
		}
		return wasHandled;
	}

	//- PROTECTED METHOD -------------------------------------------------------
	//- OVERRIDES: CommObject
	//
	@Override
	protected void packetIoExceptionHandler(PacketIoException ex)
	{
		// メッセージ送信の失敗に対する処理を実行する．
		this.viewer.failedMessageHandler(ex);
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//- OVERRIDES: CommObject
	//	
	@Override
	protected void unreachablePacketHandler(CommPacket inPkt)
	{
		// 実装無し．
	}	

//------------------------------------------------------------------------------
//  PRIVATE METHOD: CONNECT_SUCCESS
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//		
	private void evHn_ControlAccepted(DesktopEvent ev)
	{
		IoParam inPrm = null;

		Object obj = ev.getObject();
		if (obj != null) {
			inPrm = (IoParam)obj;
		}
		this.viewer.controlAccepted(inPrm);
	}

//------------------------------------------------------------------------------
//  PRIVATE METHOD: CONNECT_FAILURE
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//		
	private void evHn_ControlRefused(DesktopEvent ev)
	{
		SystemException ex = null;
		
		Object obj = ev.getObject();
		if ((obj != null) && (obj instanceof SystemException)) {
			ex = (SystemException)obj;	
		}
		this.viewer.controlRefused(ex);
	}

//------------------------------------------------------------------------------
//  PRIVATE METHOD: Illigal message
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_illegalMessage(DesktopEvent ev)
	{
		Log.warning(STR_ILLEGAL_MESSAGE,
			ev.getMessageType(),
			ev.getSourceObjectId()
		);
	}
}
