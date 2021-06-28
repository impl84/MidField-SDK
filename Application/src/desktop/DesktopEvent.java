
package desktop;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: DesktopEvent 
 *
 * Date Modified: 2018.07.03
 *
 */

//==============================================================================
@SuppressWarnings("serial")
public class DesktopEvent
	extends		MessageEvent
{
	//- PUBLIC ENUM ------------------------------------------------------------
	public static enum MessageType
	{
		START_CONTROL,
		CONTROL_ACCEPTED,
		CONTROL_REFUSED,
		CONTROL_MESSAGE,
		STOP_CONTROL
	}
	
	//- PUBLIC ENUM ------------------------------------------------------------
	public static enum ActionType
	{
		KEY_PRESS,
		KEY_RELEASE,
		MOUSE_MOVE,
		MOUSE_PRESS,
		MOUSE_RELEASE,
		MOUSE_WHEEL,
		UNKNOWN
	}
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private MessageType msgType = null;
	private ActionType actType = null;
	private Object obj = null;
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	public DesktopEvent()
	{
		// é¿ëïÇ»ÇµÅD
	}

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	public DesktopEvent(MessageType msgType, Object obj)
	{
		this.msgType = msgType;
		this.actType = ActionType.UNKNOWN;
		this.obj = obj;
	}

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	public DesktopEvent(MessageType msgType, ActionType actType, Object obj)
	{
		this.msgType = msgType;
		this.actType = actType;
		this.obj = obj;
	}

//------------------------------------------------------------------------------
//  PUBLIC METHOD: Getters
//------------------------------------------------------------------------------

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	public MessageType getMessageType()	{ return this.msgType; }

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	public ActionType getActionType()	{ return this.actType; }
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	public Object getObject()			{ return this.obj; }

//------------------------------------------------------------------------------
//  PUBLIC METHOD: Setters
//------------------------------------------------------------------------------

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	public void setMessageType(MessageType msgType)	{ this.msgType = msgType; }

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	public void setActionType(ActionType actType)	{ this.actType = actType; }
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	public void setObject(Object obj)				{ this.obj = obj; }
}
