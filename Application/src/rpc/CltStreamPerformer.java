
package rpc;

import static com.midfield_system.api.system.rpc.ParamName.PERFORMER_NUMBER;

import java.util.Map;
import java.util.TreeMap;

import com.midfield_system.api.system.rpc.RpcRequest;

//------------------------------------------------------------------------------
/**
 * CltStreamPerformer 
 *
 * Copyright (C) Koji Hashimoto
 *
 * Date Modified: 2020.08.20
 * Koji Hashimoto 
 *
 */

//==============================================================================
public class CltStreamPerformer
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
	public CltStreamPerformer(CommandParser parser)
	{
		this.parser = parser;
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPC���\�b�h
//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest newInstance(String[] args)
	{
		// ���\�b�h���݂̂�RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;		
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest open(String[] args)
	{
		// open �R�}���h�v���𐶐����ĕԂ��D
		return newRpcCommandRequest(args);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest start(String[] args)
	{
		// start �R�}���h�v���𐶐����ĕԂ��D
		return newRpcCommandRequest(args);	
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest stop(String[] args)
	{
		// stop �R�}���h�v���𐶐����ĕԂ��D
		return newRpcCommandRequest(args);		
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest close(String[] args)
	{
		// close �R�}���h�v���𐶐����ĕԂ��D
		return newRpcCommandRequest(args);	
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest delete(String[] args)
	{
		// delete �R�}���h�v���𐶐����ĕԂ��D
		return newRpcCommandRequest(args);		
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD: 
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private RpcRequest newRpcCommandRequest(String[] args)
	{
		// ���\�b�h���݂̂�RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		
		// RPC�v���ɕK�v�ƂȂ�����̐����m�F����D
		if (args.length < 2) {
			return rpcReq;
		}
		// RPC�v���p�̃}�b�v�𐶐�����D
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// StreamPerformer �� ID ���}�b�v�ɐݒ肷��D
		map.put(PERFORMER_NUMBER, args[1]);
			
		// RPC�v���p�����[�^�Ƀ}�b�v��ݒ肵�CRPC�v����Ԃ��D
		rpcReq.setParams(map);
		return rpcReq;		
	}	
	
}