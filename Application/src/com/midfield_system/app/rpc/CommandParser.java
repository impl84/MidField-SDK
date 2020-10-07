
package com.midfield_system.app.rpc;

import static com.midfield_system.api.util.Constants.STR_DOT;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.midfield_system.api.system.rpc.InvocableMethod;
import com.midfield_system.api.system.rpc.RpcRequest;
import com.midfield_system.api.system.rpc.RpcVersion;

//------------------------------------------------------------------------------
/**
 * CommandParser 
 *
 * Copyright (C) Koji Hashimoto
 *
 * Date Modified: 2020.08.05
 * Koji Hashimoto 
 *
 */

//==============================================================================
class CommandParser
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	static final String
		ARG_SEPARATOR	= "\\s+";	//$NON-NLS-1$
		
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------	
	private Gson gson = null;
	private Map<String, InvocableMethod> methodMap = null;
	
	private int requestId = 0;
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================
	
//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------

	//- PACKAGE METHOD ---------------------------------------------------------
	//
	CommandParser()
	{
		// JSON�������Java�I�u�W�F�N�g�̕ϊ��ɗp���� Gson �C���X�^���X�𐶐�����D
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		this.gson = builder.create();

		// �R�}���h�� RpcRequest �C���X�^���X�ɕϊ����邽�߂�
		// �p�[�T�N���X�̃C���X�^���X�𐶐�����D
		CltMfsNode cltMfsNode			= new CltMfsNode(this);
		CltDeviceInfoManager cltDiMgr	= new CltDeviceInfoManager(this);
		CltStreamInfoManager cltSiMgr	= new CltStreamInfoManager(this);
		CltSegmentIo cltSegIo			= new CltSegmentIo(this);
		CltStreamPerformer cltStmPfmr	= new CltStreamPerformer(this);

		// �p�[�T�N���X�̃C���X�^���X��z��ɂ���D
		Object[] parserInstanceArray = {
			cltMfsNode,
			cltDiMgr,
			cltSiMgr,
			cltSegIo,
			cltStmPfmr,
		};
		// �p�[�T�p���\�b�h�����L�[�Ƃ��C
		// InvocableMethod �C���X�^���X��l�Ƃ���}�b�v�𐶐�����D
		this.methodMap = new TreeMap<String, InvocableMethod>();
		
		// �p�[�T�N���X�̃��\�b�h���}�b�v�ɓo�^����D
		for (int i = 0; i < parserInstanceArray.length; i++) {
			registerMethod(parserInstanceArray[i], this.methodMap);
		}
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	Set<String> getMethodNameSet()
	{
		Set<String> nameSet = this.methodMap.keySet();
		return nameSet;
	}	
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	String parseCommand(String command)
		throws	InvocationTargetException,
				IllegalAccessException	
	{
		// �R�}���h���Z�p���[�^�ŋ�؂�C������̔z��ɂ���D
		String[] args = command.split(ARG_SEPARATOR);
		if ((args == null) || (args.length < 1)) {
			// RPC�v���ɕϊ��ł�����͖������C
			// RpcRequest �𐶐����C
			// �����RPC�v��(JSON������)�ɕϊ����Ė߂�D
			RpcRequest rpcReq = requestNothing();
			return this.gson.toJson(rpcReq);
				// JsonIOException, ... (RuntimeException)
		}
		// �R�}���h���̃��\�b�h���ɑΉ����� InvocableMethod �C���X�^���X���擾����D
		String methodName = args[0];
		InvocableMethod method = this.methodMap.get(methodName);
		if (method == null) {
			// ���\�b�h�����烁�\�b�h�C���X�^���X���擾�ł��Ȃ����C
			// RpcRequest �𐶐����C
			// �����RPC�v��(JSON������)�ɕϊ����Ė߂�D
			RpcRequest rpcReq = unknownMethod(methodName);
			return this.gson.toJson(rpcReq);
				// JsonIOException, ... (RuntimeException)
		}
		// �Ή����郁�\�b�h���Ăяo���CRPC�v���𐶐�����D
		RpcRequest rpcReq = (RpcRequest)method.invoke(args);
			// InvocationTargetException, IllegalAccessException
			
		// RPC�v����Ԃ��D
		return this.gson.toJson(rpcReq);
			// JsonIOException, ... (RuntimeException)
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	RpcRequest createRequest(String methodName)
	{
		// RPC�v���𐶐�����D
		RpcRequest rpcReq = new RpcRequest(RpcVersion.VERSION_1, methodName);
		
		// ID��ݒ肷��
		rpcReq.setId(nextRequestId());
		
		// RPC�v����Ԃ��D
		return rpcReq;
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	int nextRequestId()
	{
		return this.requestId++;
	}

//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void registerMethod(Object parserObject, Map<String, InvocableMethod> map)
	{
		// �p�[�T�I�u�W�F�N�g�̃N���X�I�u�W�F�N�g���擾����D
		Class<?> parserClass = parserObject.getClass();
		
		// �p�[�T�N���X���̃��\�b�h�̔z����擾����D
		Method[] methods = parserClass.getMethods();

		// �p�[�T�N���X���̃��\�b�h�𑖍�����D
		for (int i = 0; i < methods.length; i++) {
			// ���̃��\�b�h���C�^����ꂽ�p�[�T�N���X���Œ�`����Ă���
			// ���\�b�h�ł��邱�Ƃ��m�F����D
			Class<?> declaringClass = methods[i].getDeclaringClass();
			if (parserClass.equals(declaringClass) == false) {
				continue;
			}
			// ���̃��\�b�h�� public �ł��邩���m�F����D
			if ((methods[i].getModifiers() & Modifier.PUBLIC) != 1) {
				continue;
			}
			// ���̃��\�b�h�� RpcRequest ��Ԃ����Ƃ��m�F����D
			Class<?> returnType = methods[i].getReturnType();
			if (returnType.equals(RpcRequest.class) == false) {
				continue;
			}
			// ���̃��\�b�h�̈����� 1�ł��邱�Ƃ��m�F����D
			Class<?>[] paramTypes = methods[i].getParameterTypes();
			if (paramTypes.length != 1) {
				continue;
			}
			// �����̌^�� String[] �ł��邱�Ƃ��m�F����D
			Class<?> paramClass = paramTypes[0];
			if (paramClass.equals(String[].class) == false) {
				continue;
			}
			// �o�^���郁�\�b�h�̃N���X���Ƃ��āC
			// �uClt�v�Ŏn�܂�N���X���́uClt�v(3����)����菜����������𐶐�����D
			String name = declaringClass.getSimpleName().substring(3);
			
			// �N���X���t���̃��\�b�h���𐶐�����D
			name = name.concat(STR_DOT);
			name = name.concat(methods[i].getName());
			
			// ���\�b�h�� InvocableMethod �̃C���X�^���X���}�b�v�֓o�^����D
			InvocableMethod method = new InvocableMethod(methods[i], parserObject);
			map.put(name, method);
		}		
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private RpcRequest requestNothing()
	{
		// ���\�b�h��������RPC�v���𐶐�����D
		RpcRequest rpcReq = new RpcRequest(RpcVersion.VERSION_1, null);
		
		// ID��ݒ肷��D
		rpcReq.setId(nextRequestId());

		// RPC�v����Ԃ��D
		return rpcReq;
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private RpcRequest unknownMethod(String methodName)
	{
		// RPC�v���𐶐�����D
		RpcRequest rpcReq = new RpcRequest(RpcVersion.VERSION_1, methodName);

		// ID��ݒ肷��D
		rpcReq.setId(nextRequestId());
		
		// RPC�v����Ԃ��D
		return rpcReq;
	}
}
