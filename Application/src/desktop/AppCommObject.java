
package desktop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.midfield_system.api.system.CommObject;
import com.midfield_system.api.system.CommPacket;
import com.midfield_system.api.system.ObjectId;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.util.Log;

//------------------------------------------------------------------------------
/**
 * MfsCommObject 
 *
 * Copyright (C) Koji Hashimoto
 *
 * Date Modified: 2018.07.24
 * Koji Hashimoto 
 *
 */

//==============================================================================
public abstract class AppCommObject
	extends	CommObject
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String
		STR_MESSAGE_EVENT			= "Message-Event";
	
	private static final String
		STR_CANT_CREATE_INSTANCE	= "�V���A���C�Y���ꂽ�f�[�^����C���X�^���X�𐶐��ł��܂���D",
		STR_CANT_SERIALIZE_INSTANCE	= "�C���X�^���X���V���A���C�Y�ł��܂���D";

//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	public AppCommObject(String coName)
		throws	SystemException
	{
		// CommObject �̃R���X�g���N�^���Ăяo���D
		super(coName);
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void dispatchMessageEvent(ObjectId dstId, MessageEvent ev)
	{
		CommPacket pkt = new CommPacket(STR_MESSAGE_EVENT, dstId);
		setSerializableObject(pkt, ev);
		
		dispatchPacket(pkt);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public MessageEvent getMessageEventFromPacket(CommPacket pkt)
	{
		// ���̓p�P�b�g�̃��b�Z�[�W�^�C�v���擾����D
		String msgType = pkt.getMessageType();		

		// ���b�Z�[�W�^�C�v����CMessageEvent �p�̃p�P�b�g�ł��邱�Ƃ��m�F����D
		// MessageEvent �p�̃p�P�b�g�ł͂Ȃ��ꍇ�C
		// ���̎�M�p�P�b�g�������ł��Ȃ��������̂Ƃ��� false ��Ԃ��D
		if (msgType.equals(STR_MESSAGE_EVENT) == false) {
			return null;
		}		
		// �p�P�b�g���� MessageEvent �̃C���X�^���X���擾����D
		MessageEvent ev = (MessageEvent)getSerializableObject(pkt);
		
		// MessageEvent �̃C���X�^���X���擾�ł��Ȃ������ꍇ�C
		// ���̎�M�p�P�b�g�������ł��Ȃ��������̂Ƃ��� false ��Ԃ��D
		if (ev == null) {
			return null;
		}
		// MessageEvent �̑��M����ݒ肷��D
		ev.setSourceObjectId(pkt.getSourceObjectId());
		
		return ev;
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD: �I�u�W�F�N�g�̎擾�Ɛݒ�
//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private Object getSerializableObject(CommPacket pkt)
	{
		Object obj = null;
		ObjectInputStream objIn = null;		
		try {
			byte[] content = pkt.getSerializedPayload();
			if (content != null) {
				ByteArrayInputStream
					byteIn = new ByteArrayInputStream(content);
				objIn = new ObjectInputStream(byteIn);
					// IOException
				
				obj = objIn.readObject();
					// IOException
					// ClassNotFoundException
			}
		}
		catch (Exception ex) {
			Log.error(STR_CANT_CREATE_INSTANCE, ex);
		}
		finally {
			if (objIn != null) {
				try {
					objIn.close();
				}
				catch (Exception ex) {
					Log.error(ex);
				}
			}
		}		
		return obj;
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setSerializableObject(CommPacket pkt, Serializable obj)
	{
		ObjectOutputStream objOut = null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			objOut = new ObjectOutputStream(byteOut);
				// IOException
			
			objOut.writeObject(obj);
				// IOException
			
			byte[] content = byteOut.toByteArray();

			pkt.setSerializedPayload(content);
		}
		catch (IOException ex) {
			Log.error(STR_CANT_SERIALIZE_INSTANCE, ex);			
		}
		finally {
			if (objOut != null) {
				try {
					objOut.close();
				}
				catch (Exception ex) {
					Log.error(ex);
				}
			}
		}		
	}	
}
