
package com.midfield_system.app.rpc;

import com.midfield_system.api.system.rpc.RpcRequest;

//------------------------------------------------------------------------------
/**
 * CltDeviceInfoManager 
 *
 * Copyright (C) Koji Hashimoto
 *
 * Date Modified: 2020.08.18
 * Koji Hashimoto 
 *
 */

//==============================================================================
public class CltDeviceInfoManager
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
	public CltDeviceInfoManager(CommandParser parser)
	{
		this.parser = parser;
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPCメソッド
//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public RpcRequest getInputVideoDeviceInfoList(String[] args)
	{
		// メソッド名のみのRPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public RpcRequest getInputAudioDeviceInfoList(String[] args)
	{
		// メソッド名のみのRPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;
	}	
}