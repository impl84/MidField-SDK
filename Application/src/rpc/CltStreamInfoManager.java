
package rpc;

import static com.midfield_system.api.system.rpc.ParamName.SOURCE_NODE_ADDRESS;

import java.util.HashMap;
import java.util.Map;

import com.midfield_system.api.system.rpc.RpcRequest;

//------------------------------------------------------------------------------
/**
 * CltStreamInfoManager 
 *
 * Copyright (C) Koji Hashimoto
 *
 * Date Modified: 2020.08.11
 * Koji Hashimoto 
 *
 */

//==============================================================================
public class CltStreamInfoManager
{
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private CommandParser parser = null;
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	public CltStreamInfoManager(CommandParser parser)
	{
		this.parser = parser;
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPC���\�b�h
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public RpcRequest fetchSourceStreamInfoList(String[] args)
	{
		// RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC�v���ɕK�v�ƂȂ�����̐����m�F����D
		if (args.length < 2) {
			return rpcReq;
		}
		// RPC�v���p�̃}�b�v�𐶐�����D
		Map<String, Object> map = new HashMap<String, Object>();
		
		// �\�[�X�m�[�h�A�h���X���}�b�v�ɐݒ肷��D
		map.put(SOURCE_NODE_ADDRESS, args[1]);
			
		// RPC�v���p�����[�^�Ƀ}�b�v��ݒ肵�CRPC�v����Ԃ��D
		rpcReq.setParams(map);			
		return rpcReq;
	}
}