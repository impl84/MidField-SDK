
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
//  PUBLIC METHOD: RPC���\�b�h
//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest reset(String[] args)
	{
		// ���\�b�h���݂̂�RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;		
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPC���\�b�h: ���͐ݒ�i���̓f�o�C�X�j
//------------------------------------------------------------------------------	

	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public RpcRequest configureInputDevice(String[] args)
	{
		// RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC�v���ɕK�v�ƂȂ�����̐����m�F����D
		if (args.length < 5) {
			return rpcReq;
		}
		// RPC�v���p�̃}�b�v�𐶐�����D
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// �f�o�C�X�ƃt�H�[�}�b�g�̃C���f�b�N�X�l���}�b�v�ɐݒ肷��D
		map.put(VIDEO_DEVICE_INDEX, args[1]);
		map.put(VIDEO_FORMAT_INDEX, args[2]);
		map.put(AUDIO_DEVICE_INDEX, args[3]);
		map.put(AUDIO_FORMAT_INDEX, args[4]);
			
		// RPC�v���p�����[�^�Ƀ}�b�v��ݒ肵�CRPC�v����Ԃ��D
		rpcReq.setParams(map);
		return rpcReq;
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPC���\�b�h: ���͐ݒ�i��M�X�g���[���j
//------------------------------------------------------------------------------	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public RpcRequest configureIncomingStream(String[] args)
	{
		// RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC�v���ɕK�v�ƂȂ�����̐����m�F����D
		if (args.length < 2) {
			return rpcReq;
		}
		// RPC�v���p�̃}�b�v�𐶐�����D
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// �X�g���[�����̃C���f�b�N�X�l���}�b�v�ɐݒ肷��D
		map.put(STREAM_INFO_INDEX, args[1]);
			
		// RPC�v���p�����[�^�Ƀ}�b�v��ݒ肵�CRPC�v����Ԃ��D
		rpcReq.setParams(map);
		return rpcReq;
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPC���\�b�h: �o�͐ݒ�i���M�X�g���[���j
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest getOutputVideoFormatList(String[] args)
	{
		// ���\�b�h���݂̂�RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;		
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest getOutputAudioFormatList(String[] args)
	{
		// ���\�b�h���݂̂�RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;		
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest configureOutgoingStream(String[] args)
	{
		// RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC�v���ɕK�v�ƂȂ�����̐����m�F����D
		if (args.length < 3) {
			return rpcReq;
		}
		// RPC�v���p�̃}�b�v�𐶐�����D
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// �r�f�I�ƃI�[�f�B�I�t�H�[�}�b�g�̃C���f�b�N�X�l���}�b�v�ɐݒ肷��D
		map.put(VIDEO_FORMAT_INDEX, args[1]);
		map.put(AUDIO_FORMAT_INDEX, args[2]);
			
		// RPC�v���p�����[�^�Ƀ}�b�v��ݒ肵�CRPC�v����Ԃ��D
		rpcReq.setParams(map);
		return rpcReq;	
	}	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest setTransportProtocol(String[] args)
	{
		// RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC�v���ɕK�v�ƂȂ�����̐����m�F����D
		if (args.length < 3) {
			return rpcReq;
		}
		// RPC�v���p�̃}�b�v�𐶐�����D
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// TCP�𗘗p���邩�ۂ��C����уR�l�N�V�������[�h�̎w���
		// �}�b�v�ɐݒ肷��D
		map.put(PROTOCOL_TYPE, args[1]);
		map.put(CONNECTION_MODE, args[2]);
			
		// RPC�v���p�����[�^�Ƀ}�b�v��ݒ肵�CRPC�v����Ԃ��D
		rpcReq.setParams(map);
		return rpcReq;		
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest setMulticastAddress(String[] args)
	{
		// RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC�v���ɕK�v�ƂȂ�����̐����m�F����D
		if (args.length < 2) {
			return rpcReq;
		}
		// RPC�v���p�̃}�b�v�𐶐�����D
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// �}���`�L���X�g�A�h���X���}�b�v�ɐݒ肷��D
		map.put(MULTICAST_ADDRESS, args[1]);
			
		// RPC�v���p�����[�^�Ƀ}�b�v��ݒ肵�CRPC�v����Ԃ��D
		rpcReq.setParams(map);
		return rpcReq;		
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPC���\�b�h: �o�͐ݒ�i�����_���j
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest configureDefaultRenderer(String[] args)
	{
		// ���\�b�h���݂̂�RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;		
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPC���\�b�h: �v���r���[���̐ݒ�
//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public RpcRequest setPreviewer(String[] args)
	{
		// ���\�b�h���݂̂�RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);
		return rpcReq;		
	}	
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: RPC���\�b�h: �e��ݒ�
//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//			
	public RpcRequest setLiveSource(String[] args)
	{
		// RPC�v���𐶐�����D
		RpcRequest rpcReq = this.parser.createRequest(args[0]);

		// RPC�v���ɕK�v�ƂȂ�����̐����m�F����D
		if (args.length < 2) {
			return rpcReq;
		}
		// RPC�v���p�̃}�b�v�𐶐�����D
		Map<String, Object> map = new TreeMap<String, Object>();
		
		// ���C�u�\�[�X�ł��邩�ۂ����}�b�v�ɐݒ肷��D
		map.put(IS_LIVE_SOURCE, args[1]);
			
		// RPC�v���p�����[�^�Ƀ}�b�v��ݒ肵�CRPC�v����Ԃ��D
		rpcReq.setParams(map);
		return rpcReq;	
	}	
}