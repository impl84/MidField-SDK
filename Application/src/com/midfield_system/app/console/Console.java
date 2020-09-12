
package com.midfield_system.app.console;

import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.system.MfsNode;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: SimpleConsole
 *
 * Date Modified: 2020.03.17
 *
 */

//==============================================================================
public class Console
{
//==============================================================================
//  CLASS METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC STATIC METHOD:
//------------------------------------------------------------------------------

	//======== [MAIN METHOD] ===================================================
	//	
	public static void main(String[] args)
	{
		Console console = new Console();
		console.exec();
	}
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	ConsoleState curStat = null;
	SegmentIo segIo = null;
	StreamPerformer pfmr = null;

//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	public Console()
	{
		this.segIo = new SegmentIo();
	}
		
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void exec()
	{
		//----------------------------------------------------------------------
		// SystemEventViewer の生成・表示
		//----------------------------------------------------------------------
		SystemEventViewer sysEvVwr = new SystemEventViewer();
		sysEvVwr.setVisible(true);
		
		//----------------------------------------------------------------------
		// MidField System の初期化
		//----------------------------------------------------------------------
		MfsNode mfs = null;
		try {
			// MidField System を初期化する．
			mfs = MfsNode.initialize();
				// SystemException
			
			// MidField System を起動する．
			mfs.activate();
				// SystemException
		}
		catch (SystemException ex) {
			ex.printStackTrace();
			return;
		}
		//----------------------------------------------------------------------
		// ExternalConsole の初期化
		//----------------------------------------------------------------------
		// ExternalConsole の各状態のインスタンス生成		
		SystemCommandState	sysStat = new SystemCommandState(this, mfs);
		SetupInputState		inStat  = new SetupInputState(this, mfs);
		SetupOutputState	outStat = new SetupOutputState(this, mfs);
		SetupOptionState	optStat = new SetupOptionState(this, mfs);
		StreamControlState	stmStat = new StreamControlState(this, mfs);
		
		// ExternalConsole の各状態の初期化
		sysStat.setStateInstance(inStat,  stmStat);
		inStat.setStateInstance( sysStat, outStat);
		outStat.setStateInstanue(sysStat, optStat, stmStat);
		optStat.setStateInstance(sysStat, stmStat);
		stmStat.setStateInstance(sysStat);
		
		// イベントリスナ登録設定
		mfs.addSystemEventListener(sysEvVwr);
		
		//----------------------------------------------------------------------
		// コマンド処理
		//----------------------------------------------------------------------
		// 初期状態設定
		this.curStat = sysStat;
		System.out.printf("\n\n■ExternalConsole (MidField System 用テストプログラム)\n\n");
		// コマンド処理および状態遷移ループ
		while (this.curStat != null) {
			this.curStat = this.curStat.execute();
		}
		//----------------------------------------------------------------------
		// MidField System の終了処理
		//----------------------------------------------------------------------
		// イベントリスナ設定解除，MidField System の停止とインスタンスの削除		
		mfs.removeSystemEventListener(sysEvVwr);
		mfs.shutdown();

		//----------------------------------------------------------------------
		// SystemEventViewer の終了処理
		//----------------------------------------------------------------------
		sysEvVwr.dispose();
	}

//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------

	//- PACKAGE METHOD ---------------------------------------------------------
	//
	SegmentIo getSegmentIo()
	{
		return this.segIo;
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void setSelectedActiveStream(StreamPerformer pfmr)
	{
		this.pfmr = pfmr;
	}

	//- PACKAGE METHOD ---------------------------------------------------------
	//
	StreamPerformer getSelectedActiveStream()
	{
		return this.pfmr;
	}
}
