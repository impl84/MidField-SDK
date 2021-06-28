
package desktop;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.UUID;

import com.midfield_system.api.stream.StreamException;
import com.midfield_system.api.system.CommPacket;
import com.midfield_system.api.system.ObjectId;
import com.midfield_system.api.system.PacketIoException;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.util.Log;

import desktop.DesktopEvent.MessageType;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: DesktopEventProcessor 
 *
 * Date Modified: 2020.07.15
 *
 */

//==============================================================================
public class DesktopEventProcessor
	extends	AppCommObject
{
	//- PUBLIC CONSTANT VALUE --------------------------------------------------
	public static final String
		PROCESSOR_NAME = "DesktopEventProcessor"; //$NON-NLS-1$
	
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String
		STR_ACCEPT_CONTROL	= "※遠隔(%s)からのデスクトップ操作を開始します．",
		STR_REJECT_CONTROL	= "※遠隔(%s)からのデスクトップ操作を拒否します．",
		STR_STOP_CONTROL	= "※遠隔からのデスクトップ操作を終了します．";
	
	private static final String
		STR_CANT_CREATE_ROBOT	= "デスクトップ操作用のインスタンスを生成できません．",
		STR_ILLEGAL_MESSAGE		= "不正なメッセージを受信しました．: %s from %s\n";	

	private static enum ControlState
	{
		WAIT,		// デスクトップ制御受入待機中
		CONTROLLED	// デスクトップ制御受入中
	}

//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private Robot robot = null;
	private ImageSender imgSender = null;
	private ControlState state = ControlState.WAIT;
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	public DesktopEventProcessor()
		throws	SystemException
	{
		// MfsCommObject を初期化する．
		super(PROCESSOR_NAME);
		
		// デスクトップ操作用の Robot を生成する．
		try {
			this.robot = new Robot();
		}
		catch (AWTException ex) {
			SystemException ccEx
				= new SystemException(STR_CANT_CREATE_ROBOT, ex);
			throw ccEx;
		}
	}	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public boolean isControlled()
	{
		boolean isControlled = false;
		if (this.state == ControlState.CONTROLLED) {
			isControlled = true;
		}
		return isControlled;
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public void shutdown()
	{
		stopControl();
		delete();
	}
	
//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	synchronized void stopControl()
	{
		if (this.state == ControlState.CONTROLLED) {
			// 状態変更：デスクトップ制御受入待機中．
			this.state = ControlState.WAIT;
			
			// DesktopImageSender を終了する．
			if (this.imgSender != null) {
				this.imgSender.shutdown();
				this.imgSender = null;
			}
			Log.message(STR_STOP_CONTROL);
		}
	}	

//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------

	//- PROTECTED METHOD -------------------------------------------------------
	//- OVERRIDES: CommObject
	//	
	@Override
	protected synchronized boolean incomingPacketHandler(CommPacket inPkt)
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
		case START_CONTROL		: evHn_StartControl(dcEv);		break;
		case CONTROL_ACCEPTED	: evHn_illegalMessage(dcEv);	break;
		case CONTROL_REFUSED	: evHn_illegalMessage(dcEv);	break;
		case CONTROL_MESSAGE	: evHn_ControlMessage(dcEv);	break;
		case STOP_CONTROL		: evHn_StopControl(dcEv);		break;
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
		// 実装無し．
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
//  PRIVATE METHOD: START_CONTROL
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_StartControl(DesktopEvent ev)
	{
		MessageType msgType = MessageType.CONTROL_REFUSED;
		Object obj = null;
		
		// ソースホストアドレスを取得する．
		ObjectId srcId = ev.getSourceObjectId();
		UUID nodeId = srcId.getNodeId();
		String srcNodeId = nodeId.toString();
		
		// デスクトップ制御の受入可否を確認する．
		boolean doAccept = checkAccept(ev);
		if (doAccept) {
			// デスクトップ制御を受け入れる．
			try {
				// DesktopImageSender を生成する．
				this.imgSender = new ImageSender(this);
					// SystemException, StreamException
				
				// 受入メッセージを出力する．
				Log.message(STR_ACCEPT_CONTROL, srcNodeId);
				
				// メッセージタイプの設定と出力パラメータの取得．
				msgType = MessageType.CONTROL_ACCEPTED;
				obj = this.imgSender.getOutputParam();
			}
			catch (SystemException | StreamException ex) {
				// DesktopImageSender の生成に失敗したので，
				// デスクトップ制御を拒否する．
				Log.message(STR_REJECT_CONTROL, srcNodeId);
				Log.message(ex);
				msgType = MessageType.CONTROL_REFUSED;
				obj = ex;
			}
		}
		else {
			// デスクトップ制御を拒否する．
			Log.message(STR_REJECT_CONTROL, srcNodeId);				
			msgType = MessageType.CONTROL_REFUSED;
			obj = null;
		}
		
		// デスクトップ制御受入要求に対する応答を返す．
		ObjectId dstId = ev.getSourceObjectId();
		DesktopEvent response = new DesktopEvent(msgType, obj);

		dispatchMessageEvent(dstId, response);

		// デスクトップ制御を受け入れる場合は，
		// 状態変更：デスクトップ制御受入中．
		if (msgType == MessageType.CONTROL_ACCEPTED) {
			this.state = ControlState.CONTROLLED;
		}
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private boolean checkAccept(DesktopEvent ev)
	{
		boolean doAccept = false;

		// 受入の可否を判断する．
		if (this.state == ControlState.WAIT) {
			// デスクトップ制御受入待機中の場合は，遠隔制御を受け入れる．
			doAccept = true;
		}
		else {
			// デスクトップ制御受入中の場合は，制御を受け入れない．
			doAccept = false;
		}
		return doAccept;
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD: STOP_CONTROL
//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_StopControl(DesktopEvent ev)
	{
		stopControl();
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD: CONTROL_MESSAGE
//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_ControlMessage(DesktopEvent ev)
	{
		if (this.state == ControlState.WAIT) {
			return;
		}
		//----------------------------------------------------------------------
		DesktopEvent.ActionType type = ev.getActionType();
		Object obj = ev.getObject();

		switch (type) {
		case KEY_PRESS		: evHn_KeyPress((KeyEvent)obj);				break;
		case KEY_RELEASE	: evHn_KeyRelease((KeyEvent)obj);			break;
		case MOUSE_PRESS	: evHn_MousePress((MouseEvent)obj);			break;
		case MOUSE_RELEASE	: evHn_MouseRelease((MouseEvent)obj);		break;
		case MOUSE_MOVE		: evHn_MouseMove((MouseEvent)obj);			break;
		case MOUSE_WHEEL	: evHn_MouseWheel((MouseWheelEvent)obj);	break;
		case UNKNOWN		: /* error */								break;
		}
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_KeyPress(KeyEvent ev)
	{
		int keyCode = ev.getKeyCode();
		this.robot.keyPress(keyCode);
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_KeyRelease(KeyEvent ev)
	{
		int keyCode = ev.getKeyCode();
		this.robot.keyRelease(keyCode);
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_MousePress(MouseEvent ev)
	{
		int btnNum = getMouseButtonNumber(ev);
		if (btnNum != 0) {
			this.robot.mousePress(btnNum);
		}
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_MouseRelease(MouseEvent ev)
	{
		int btnNum = getMouseButtonNumber(ev);
		if (btnNum != 0) {
			this.robot.mouseRelease(btnNum);
		}
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_MouseMove(MouseEvent ev)
	{
		int x = ev.getX();
		int y = ev.getY();
		this.robot.mouseMove(x, y);
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_MouseWheel(MouseWheelEvent ev)
	{
		int wheelAmt = ev.getWheelRotation();
		this.robot.mouseWheel(wheelAmt);
	}
	
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private int getMouseButtonNumber(MouseEvent ev)
	{
		int btnNum = 0;
		
		switch (ev.getButton()) {
		case MouseEvent.NOBUTTON	: btnNum = 0;								break;
		case MouseEvent.BUTTON1		: btnNum = InputEvent.BUTTON1_DOWN_MASK;	break;
		case MouseEvent.BUTTON2		: btnNum = InputEvent.BUTTON2_DOWN_MASK;	break;
		case MouseEvent.BUTTON3		: btnNum = InputEvent.BUTTON3_DOWN_MASK;	break;
		default 					: /* error */								break;
		}
		return btnNum;
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
