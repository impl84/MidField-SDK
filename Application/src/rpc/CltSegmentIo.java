
package rpc;

import static com.midfield_system.api.system.rpc.ParamName.AUDIO_DEVICE_INDEX;
import static com.midfield_system.api.system.rpc.ParamName.AUDIO_FORMAT_INDEX;
import static com.midfield_system.api.system.rpc.ParamName.CONNECTION_MODE;
import static com.midfield_system.api.system.rpc.ParamName.IS_LIVE_SOURCE;
import static com.midfield_system.api.system.rpc.ParamName.MULTICAST_ADDRESS;
import static com.midfield_system.api.system.rpc.ParamName.PROTOCOL_TYPE;
import static com.midfield_system.api.system.rpc.ParamName.STREAM_INFO_INDEX;
import static com.midfield_system.api.system.rpc.ParamName.VIDEO_DEVICE_INDEX;
import static com.midfield_system.api.system.rpc.ParamName.VIDEO_FORMAT_INDEX;

import java.util.Map;
import java.util.TreeMap;

import com.midfield_system.api.system.rpc.RpcRequest;

//------------------------------------------------------------------------------
/**
 * CltSegmentIo 
 *
 * Copyright (C) Koji Hashimoto
 *
 * Date Modified: 2020.08.18
 * Koji Hashimoto 
 *
 */

//==============================================================================
public class CltSegmentIo
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
	public CltSegmentIo(CommandParser parser)
	{
		this.parser = parser;
	}

//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPCメソッド
//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest reset(String[] args)
	{
		// メソッド名のみのRPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;		
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPCメソッド: 入力設定（入力デバイス）
//------------------------------------------------------------------------------	

	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public RpcRequest configureInputDevice(String[] args)
	{
		// RPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC要求に必要となる引数の数を確認する．
		if (args.length < 5) {
			return rpcReq;
		}
		// RPC要求用のマップを生成する．
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// デバイスとフォーマットのインデックス値をマップに設定する．
		map.put(VIDEO_DEVICE_INDEX, args[1]);
		map.put(VIDEO_FORMAT_INDEX, args[2]);
		map.put(AUDIO_DEVICE_INDEX, args[3]);
		map.put(AUDIO_FORMAT_INDEX, args[4]);
			
		// RPC要求パラメータにマップを設定し，RPC要求を返す．
		rpcReq.setParams(map);
		return rpcReq;
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPCメソッド: 入力設定（受信ストリーム）
//------------------------------------------------------------------------------	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public RpcRequest configureIncomingStream(String[] args)
	{
		// RPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC要求に必要となる引数の数を確認する．
		if (args.length < 2) {
			return rpcReq;
		}
		// RPC要求用のマップを生成する．
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// ストリーム情報のインデックス値をマップに設定する．
		map.put(STREAM_INFO_INDEX, args[1]);
			
		// RPC要求パラメータにマップを設定し，RPC要求を返す．
		rpcReq.setParams(map);
		return rpcReq;
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPCメソッド: 出力設定（送信ストリーム）
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest getOutputVideoFormatList(String[] args)
	{
		// メソッド名のみのRPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;		
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest getOutputAudioFormatList(String[] args)
	{
		// メソッド名のみのRPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;		
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest configureOutgoingStream(String[] args)
	{
		// RPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC要求に必要となる引数の数を確認する．
		if (args.length < 3) {
			return rpcReq;
		}
		// RPC要求用のマップを生成する．
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// ビデオとオーディオフォーマットのインデックス値をマップに設定する．
		map.put(VIDEO_FORMAT_INDEX, args[1]);
		map.put(AUDIO_FORMAT_INDEX, args[2]);
			
		// RPC要求パラメータにマップを設定し，RPC要求を返す．
		rpcReq.setParams(map);
		return rpcReq;	
	}	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest setTransportProtocol(String[] args)
	{
		// RPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC要求に必要となる引数の数を確認する．
		if (args.length < 3) {
			return rpcReq;
		}
		// RPC要求用のマップを生成する．
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// TCPを利用するか否か，およびコネクションモードの指定を
		// マップに設定する．
		map.put(PROTOCOL_TYPE, args[1]);
		map.put(CONNECTION_MODE, args[2]);
			
		// RPC要求パラメータにマップを設定し，RPC要求を返す．
		rpcReq.setParams(map);
		return rpcReq;		
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest setMulticastAddress(String[] args)
	{
		// RPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC要求に必要となる引数の数を確認する．
		if (args.length < 2) {
			return rpcReq;
		}
		// RPC要求用のマップを生成する．
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// マルチキャストアドレスをマップに設定する．
		map.put(MULTICAST_ADDRESS, args[1]);
			
		// RPC要求パラメータにマップを設定し，RPC要求を返す．
		rpcReq.setParams(map);
		return rpcReq;		
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPCメソッド: 出力設定（レンダラ）
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest configureDefaultRenderer(String[] args)
	{
		// メソッド名のみのRPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;		
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPCメソッド: プレビューワの設定
//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest setPreviewer(String[] args)
	{
		// メソッド名のみのRPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;		
	}	
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPCメソッド: 各種設定
//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//			
	public RpcRequest setLiveSource(String[] args)
	{
		// RPC要求を生成する．
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC要求に必要となる引数の数を確認する．
		if (args.length < 2) {
			return rpcReq;
		}
		// RPC要求用のマップを生成する．
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// ライブソースであるか否かをマップに設定する．
		map.put(IS_LIVE_SOURCE, args[1]);
			
		// RPC要求パラメータにマップを設定し，RPC要求を返す．
		rpcReq.setParams(map);
		return rpcReq;	
	}	
}