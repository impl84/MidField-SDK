
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
		STR_ACCEPT_CONTROL	= "�����u(%s)����̃f�X�N�g�b�v������J�n���܂��D",
		STR_REJECT_CONTROL	= "�����u(%s)����̃f�X�N�g�b�v��������ۂ��܂��D",
		STR_STOP_CONTROL	= "�����u����̃f�X�N�g�b�v������I�����܂��D";
	
	private static final String
		STR_CANT_CREATE_ROBOT	= "�f�X�N�g�b�v����p�̃C���X�^���X�𐶐��ł��܂���D",
		STR_ILLEGAL_MESSAGE		= "�s���ȃ��b�Z�[�W����M���܂����D: %s from %s\n";	

	private static enum ControlState
	{
		WAIT,		// �f�X�N�g�b�v�������ҋ@��
		CONTROLLED	// �f�X�N�g�b�v��������
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
		// MfsCommObject ������������D
		super(PROCESSOR_NAME);
		
		// �f�X�N�g�b�v����p�� Robot �𐶐�����D
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
			// ��ԕύX�F�f�X�N�g�b�v�������ҋ@���D
			this.state = ControlState.WAIT;
			
			// DesktopImageSender ���I������D
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
		// ���������D
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
//  PRIVATE METHOD: START_CONTROL
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_StartControl(DesktopEvent ev)
	{
		MessageType msgType = MessageType.CONTROL_REFUSED;
		Object obj = null;
		
		// �\�[�X�z�X�g�A�h���X���擾����D
		ObjectId srcId = ev.getSourceObjectId();
		UUID nodeId = srcId.getNodeId();
		String srcNodeId = nodeId.toString();
		
		// �f�X�N�g�b�v����̎���ۂ��m�F����D
		boolean doAccept = checkAccept(ev);
		if (doAccept) {
			// �f�X�N�g�b�v������󂯓����D
			try {
				// DesktopImageSender �𐶐�����D
				this.imgSender = new ImageSender(this);
					// SystemException, StreamException
				
				// ������b�Z�[�W���o�͂���D
				Log.message(STR_ACCEPT_CONTROL, srcNodeId);
				
				// ���b�Z�[�W�^�C�v�̐ݒ�Əo�̓p�����[�^�̎擾�D
				msgType = MessageType.CONTROL_ACCEPTED;
				obj = this.imgSender.getOutputParam();
			}
			catch (SystemException | StreamException ex) {
				// DesktopImageSender �̐����Ɏ��s�����̂ŁC
				// �f�X�N�g�b�v��������ۂ���D
				Log.message(STR_REJECT_CONTROL, srcNodeId);
				Log.message(ex);
				msgType = MessageType.CONTROL_REFUSED;
				obj = ex;
			}
		}
		else {
			// �f�X�N�g�b�v��������ۂ���D
			Log.message(STR_REJECT_CONTROL, srcNodeId);				
			msgType = MessageType.CONTROL_REFUSED;
			obj = null;
		}
		
		// �f�X�N�g�b�v�������v���ɑ΂��鉞����Ԃ��D
		ObjectId dstId = ev.getSourceObjectId();
		DesktopEvent response = new DesktopEvent(msgType, obj);

		dispatchMessageEvent(dstId, response);

		// �f�X�N�g�b�v������󂯓����ꍇ�́C
		// ��ԕύX�F�f�X�N�g�b�v���������D
		if (msgType == MessageType.CONTROL_ACCEPTED) {
			this.state = ControlState.CONTROLLED;
		}
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private boolean checkAccept(DesktopEvent ev)
	{
		boolean doAccept = false;

		// ����̉ۂ𔻒f����D
		if (this.state == ControlState.WAIT) {
			// �f�X�N�g�b�v�������ҋ@���̏ꍇ�́C���u������󂯓����D
			doAccept = true;
		}
		else {
			// �f�X�N�g�b�v���������̏ꍇ�́C������󂯓���Ȃ��D
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
