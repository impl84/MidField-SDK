
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
		STR_CANT_CREATE_INSTANCE	= "シリアライズされたデータからインスタンスを生成できません．",
		STR_CANT_SERIALIZE_INSTANCE	= "インスタンスをシリアライズできません．";

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
		// CommObject のコンストラクタを呼び出す．
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
		// 入力パケットのメッセージタイプを取得する．
		String msgType = pkt.getMessageType();		

		// メッセージタイプから，MessageEvent 用のパケットであることを確認する．
		// MessageEvent 用のパケットではない場合，
		// この受信パケットを処理できなかったものとして false を返す．
		if (msgType.equals(STR_MESSAGE_EVENT) == false) {
			return null;
		}		
		// パケットから MessageEvent のインスタンスを取得する．
		MessageEvent ev = (MessageEvent)getSerializableObject(pkt);
		
		// MessageEvent のインスタンスを取得できなかった場合，
		// この受信パケットを処理できなかったものとして false を返す．
		if (ev == null) {
			return null;
		}
		// MessageEvent の送信元を設定する．
		ev.setSourceObjectId(pkt.getSourceObjectId());
		
		return ev;
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD: オブジェクトの取得と設定
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
