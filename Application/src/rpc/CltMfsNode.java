
package rpc;

import com.midfield_system.api.system.rpc.RpcRequest;

//------------------------------------------------------------------------------
/**
 * CltMfsNode 
 *
 * Copyright (C) Koji Hashimoto
 *
 * Date Modified: 2020.03.27
 * Koji Hashimoto 
 *
 */

//==============================================================================
public class CltMfsNode
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
	public CltMfsNode(CommandParser parser)
	{
		this.parser = parser;
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPCメソッド
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest isRunning(String[] args)
	{
		// メソッド名のみのRPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;	
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public RpcRequest lock(String[] args)
	{
		// メソッド名のみのRPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public RpcRequest unlock(String[] args)
	{
		// メソッド名のみのRPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);		
		return rpcReq;
	}	
}
