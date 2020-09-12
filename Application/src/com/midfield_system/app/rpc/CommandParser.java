
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
		// JSON文字列とJavaオブジェクトの変換に用いる Gson インスタンスを生成する．
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		this.gson = builder.create();

		// コマンドを RpcRequest インスタンスに変換するための
		// パーサクラスのインスタンスを生成する．
		CltMfsNode cltMfsNode			= new CltMfsNode(this);
		CltDeviceInfoManager cltDiMgr	= new CltDeviceInfoManager(this);
		CltStreamInfoManager cltSiMgr	= new CltStreamInfoManager(this);
		CltSegmentIo cltSegIo			= new CltSegmentIo(this);
		CltStreamPerformer cltStmPfmr	= new CltStreamPerformer(this);

		// パーサクラスのインスタンスを配列にする．
		Object[] parserInstanceArray = {
			cltMfsNode,
			cltDiMgr,
			cltSiMgr,
			cltSegIo,
			cltStmPfmr,
		};
		// パーサ用メソッド名をキーとし，
		// InvocableMethod インスタンスを値とするマップを生成する．
		this.methodMap = new TreeMap<String, InvocableMethod>();
		
		// パーサクラスのメソッドをマップに登録する．
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
		// コマンドをセパレータで区切り，文字列の配列にする．
		String[] args = command.split(ARG_SEPARATOR);
		if ((args == null) || (args.length < 1)) {
			// RPC要求に変換できる情報は無いが，
			// RpcRequest を生成し，
			// それをRPC要求(JSON文字列)に変換して戻る．
			RpcRequest rpcReq = requestNothing();
			return this.gson.toJson(rpcReq);
				// JsonIOException, ... (RuntimeException)
		}
		// コマンド内のメソッド名に対応する InvocableMethod インスタンスを取得する．
		String methodName = args[0];
		InvocableMethod method = this.methodMap.get(methodName);
		if (method == null) {
			// メソッド名からメソッドインスタンスを取得できないが，
			// RpcRequest を生成し，
			// それをRPC要求(JSON文字列)に変換して戻る．
			RpcRequest rpcReq = unknownMethod(methodName);
			return this.gson.toJson(rpcReq);
				// JsonIOException, ... (RuntimeException)
		}
		// 対応するメソッドを呼び出し，RPC要求を生成する．
		RpcRequest rpcReq = (RpcRequest)method.invoke(args);
			// InvocationTargetException, IllegalAccessException
			
		// RPC要求を返す．
		return this.gson.toJson(rpcReq);
			// JsonIOException, ... (RuntimeException)
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	RpcRequest createRequest(String methodName)
	{
		// RPC要求を生成する．
		RpcRequest rpcReq = new RpcRequest(RpcVersion.VERSION_1, methodName);
		
		// IDを設定する
		rpcReq.setId(nextRequestId());
		
		// RPC要求を返す．
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
		// パーサオブジェクトのクラスオブジェクトを取得する．
		Class<?> parserClass = parserObject.getClass();
		
		// パーサクラス内のメソッドの配列を取得する．
		Method[] methods = parserClass.getMethods();

		// パーサクラス内のメソッドを走査する．
		for (int i = 0; i < methods.length; i++) {
			// このメソッドが，与えられたパーサクラス内で定義されている
			// メソッドであることを確認する．
			Class<?> declaringClass = methods[i].getDeclaringClass();
			if (parserClass.equals(declaringClass) == false) {
				continue;
			}
			// このメソッドが public であるかを確認する．
			if ((methods[i].getModifiers() & Modifier.PUBLIC) != 1) {
				continue;
			}
			// このメソッドが RpcRequest を返すことを確認する．
			Class<?> returnType = methods[i].getReturnType();
			if (returnType.equals(RpcRequest.class) == false) {
				continue;
			}
			// このメソッドの引数が 1つであることを確認する．
			Class<?>[] paramTypes = methods[i].getParameterTypes();
			if (paramTypes.length != 1) {
				continue;
			}
			// 引数の型が String[] であることを確認する．
			Class<?> paramClass = paramTypes[0];
			if (paramClass.equals(String[].class) == false) {
				continue;
			}
			// 登録するメソッドのクラス名として，
			// 「Clt」で始まるクラス名の「Clt」(3文字)を取り除いた文字列を生成する．
			String name = declaringClass.getSimpleName().substring(3);
			
			// クラス名付きのメソッド名を生成する．
			name = name.concat(STR_DOT);
			name = name.concat(methods[i].getName());
			
			// メソッドと InvocableMethod のインスタンスをマップへ登録する．
			InvocableMethod method = new InvocableMethod(methods[i], parserObject);
			map.put(name, method);
		}		
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private RpcRequest requestNothing()
	{
		// メソッド名が無いRPC要求を生成する．
		RpcRequest rpcReq = new RpcRequest(RpcVersion.VERSION_1, null);
		
		// IDを設定する．
		rpcReq.setId(nextRequestId());

		// RPC要求を返す．
		return rpcReq;
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private RpcRequest unknownMethod(String methodName)
	{
		// RPC要求を生成する．
		RpcRequest rpcReq = new RpcRequest(RpcVersion.VERSION_1, methodName);

		// IDを設定する．
		rpcReq.setId(nextRequestId());
		
		// RPC要求を返す．
		return rpcReq;
	}
}
