
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
//  PUBLIC METHOD: RPCメソッド
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public RpcRequest fetchSourceStreamInfoList(String[] args)
	{
		// RPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC要求に必要となる引数の数を確認する．
		if (args.length < 2) {
			return rpcReq;
		}
		// RPC要求用のマップを生成する．
		Map<String, Object> map = new HashMap<String, Object>();
		
		// ソースノードアドレスをマップに設定する．
		map.put(SOURCE_NODE_ADDRESS, args[1]);
			
		// RPC要求パラメータにマップを設定し，RPC要求を返す．
		rpcReq.setParams(map);			
		return rpcReq;
	}
}