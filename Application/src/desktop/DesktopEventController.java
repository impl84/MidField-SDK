
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
		STR_ILLEGAL_MESSAGE = "�s���ȃ��b�Z�[�W����M���܂����D: %s from %s\n";
	
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
		// MfsCommObject ������������D
		super(getNewObjectIdName());	// SystemException
		
		// ���u�f�X�N�g�b�v�p�r���[���𐶐�����D
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
		
		// �C�x���g�z����� ObjectId �𐶐�����D
		this.dstId = new ObjectId(
			DesktopEventProcessor.PROCESSOR_NAME, dstNodeId
		);
		// ����J�n�C�x���g��z������D
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
		// ���䃁�b�Z�[�W�C�x���g��z������D
		DesktopEvent ev = new DesktopEvent(
			MessageType.CONTROL_MESSAGE, actType, obj
		);
		dispatchMessageEvent(this.dstId, ev);
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void stopControl()
	{
		// �����~�C�x���g��z������D
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
		
		// �p�P�b�g���� MessageEvent �̃C���X�^���X���擾����D
		MessageEvent ev = getMessageEventFromPacket(inPkt);
		if (ev == null) {
			// MessageEvent �̃C���X�^���X���擾�ł��Ȃ������ꍇ�C
			// ���̎�M�p�P�b�g�������ł��Ȃ��������̂Ƃ��� false ��Ԃ��D
			wasHandled = false;
			return wasHandled;
		}
		// �擾���� MessageEvent �� DesktopEvent �Ƃ��Ĉ����D
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
		// ���b�Z�[�W���M�̎��s�ɑ΂��鏈�������s����D
		this.viewer.failedMessageHandler(ex);
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//- OVERRIDES: CommObject
	//	
	@Override
	protected void unreachablePacketHandler(CommPacket inPkt)
	{
		// ���������D
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
