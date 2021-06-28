
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
//  PUBLIC METHOD: RPCメソッド
//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest newInstance(String[] args)
	{
		// メソッド名のみのRPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;		
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest open(String[] args)
	{
		// open コマンド要求を生成して返す．
		return newRpcCommandRequest(args);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest start(String[] args)
	{
		// start コマンド要求を生成して返す．
		return newRpcCommandRequest(args);	
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest stop(String[] args)
	{
		// stop コマンド要求を生成して返す．
		return newRpcCommandRequest(args);		
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest close(String[] args)
	{
		// close コマンド要求を生成して返す．
		return newRpcCommandRequest(args);	
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest delete(String[] args)
	{
		// delete コマンド要求を生成して返す．
		return newRpcCommandRequest(args);		
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD: 
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private RpcRequest newRpcCommandRequest(String[] args)
	{
		// メソッド名のみのRPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		
		// RPC要求に必要となる引数の数を確認する．
		if (args.length < 2) {
			return rpcReq;
		}
		// RPC要求用のマップを生成する．
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// StreamPerformer の ID をマップに設定する．
		map.put(PERFORMER_NUMBER, args[1]);
			
		// RPC要求パラメータにマップを設定し，RPC要求を返す．
		rpcReq.setParams(map);
		return rpcReq;		
	}	
	
}