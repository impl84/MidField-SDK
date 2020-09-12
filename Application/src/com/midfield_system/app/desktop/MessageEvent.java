
package com.midfield_system.app.desktop;

import java.io.Serializable;

import com.midfield_system.api.system.ObjectId;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: MessageEvent 
 *
 * Date Modified: 2018.07.03
 *
 */

//==============================================================================
@SuppressWarnings("serial")
public class MessageEvent
	implements	Serializable
{
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private ObjectId srcId = null;

//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	public MessageEvent()
	{
		// �����Ȃ��D
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: Getter
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public ObjectId getSourceObjectId()
	{
		return	this.srcId;
	}

//------------------------------------------------------------------------------
//  PUBLIC METHOD: Setter
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void setSourceObjectId(ObjectId srcId)
	{
		this.srcId = srcId;
	}
}

