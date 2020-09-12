
package com.midfield_system.app.performer.ex1;

import java.io.IOException;

import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamException;
import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.viewer.VideoCanvas;
import com.midfield_system.app.util.SimpleViewer;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: AbstractSampleCode
 *
 * Date Modified: 2020.10.02
 *
 */

//==============================================================================
public abstract class AbstractSampleCode
{
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PROTECTED VARIABLE -----------------------------------------------------
	
	// このクラスを拡張したクラス内で利用される入出力構成ツール
	protected ConfigTool cfgTool = null;
	
	//- PRIVATE VARIABLE -------------------------------------------------------
	
	// StreamPerformer 
	private StreamPerformer pfmr = null;
	
	// StreamPerformer が映像表示に利用する VideoCanvas
	private VideoCanvas vidCvs = null;
	
	// VideoCanvas を配置して画面に表示するためのビューワ
	// ※インスタンス生成時に外部から与えられる．
	private SimpleViewer viewer = null;
	
	// サンプルコードの動作状態(true:動作中，false:停止中)
	private boolean isRunning = false;	
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	// コンストラクタ
	//
	public AbstractSampleCode(SimpleViewer viewer)
	{
		// このクラスを拡張したクラス内で利用される入出力構成ツールを生成する．
		this.cfgTool = new ConfigTool();
		
		// 与えられた SimpleViewer インスタンスを
		// ビューワとして利用するために保持する．
		this.viewer = viewer;
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	// サンプルコードの動作状態(true:動作中，false:停止中)を返す．
	//
	public boolean isRunning()
	{
		return this.isRunning;
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	// サンプルコードの処理を開始する．
	//
	public void startup()
		throws SystemException, StreamException
	{
		// 動作状態を確認する．
		if (this.isRunning) {
			return;
		}
		// Stream Performer を生成し，入出力処理を実行する．
		try {
			// SegmentIo を生成する．．
			SegmentIo segIo = new SegmentIo();
			
			// SegmentIo の入出力を構成する．．
			configureInput(segIo);	// IOException
			configureOutput(segIo);	// IOException

			// SegmentIo をもとに，StreamPerformer を生成する．
			this.pfmr = StreamPerformer.newInstance(segIo);
				// SystemException, StreamException

			// StreamPerformer から VideoCanvas を取得し，
			// ビューワに追加する．
			this.vidCvs = this.pfmr.getVideoCanvas();
			addVideoCanvasToViewer(this.vidCvs);

			// 入出力処理を開始する．
			this.pfmr.open();		// StreamException
			this.pfmr.start();		// StreamException
			
			// 動作状態を true にする．
			this.isRunning = true;
		}
		catch (IOException ex) {
			// 例外発生時のメッセージを出力する．
			System.out.println(ex.getMessage());
		}
		catch (SystemException | StreamException ex) {
			// このインスタンスの終了処理をする．
			delete();
			
			// 例外を呼び出し元へ投げる．
			throw ex;
		}
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	// サンプルコードの処理を終了する．
	//		
	public void cleanup()
	{
		// 動作状態を確認する．
		if (this.isRunning == false) {
			return;
		}
		try {
			// 入出力処理を終了する．
			this.pfmr.stop();	// StreamException
			this.pfmr.close();
		}
		catch (StreamException ex) {
			// 例外発生時のスタックトレースを出力する．
			ex.printStackTrace();
		}
		finally {
			// このインスタンスの終了処理をする．
			delete();
			
			// 動作状態を false にする．
			this.isRunning = false;
		}
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	// サンプルコードの概要説明を取得する．
	//
	public abstract String getDescription();
	
//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------
		
	//- PROTECTED METHOD -------------------------------------------------------
	// SegmentIo の入力を構成する．
	//
	protected abstract void configureInput(SegmentIo segIo)
		throws IOException;
	
	//- PROTECTED METHOD -------------------------------------------------------
	// SegmentIo の出力を構成する．
	//
	protected abstract void configureOutput(SegmentIo segIo)
		throws IOException;
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	// VideoCanvas をビューワに追加する．
	//
	private void addVideoCanvasToViewer(VideoCanvas vidCvs)
	{
		// ビューワのインスタンスが有る場合，
		// VideoCanvas をビューワに追加する． 
		if (this.viewer != null) {
			this.viewer.addVideoCanvas(this.vidCvs);
		}
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	// このインスタンスの終了処理をする．
	//
	private void delete()
	{
		// ビューワのインスタンスが有る場合，
		// ビューワから VideoCanvas を削除する．
		if (this.viewer != null) {
			this.viewer.removeVideoCanvas(this.vidCvs);
		}
		// StreamPerformer のインスタンスが有る場合，
		// StreamPerformer を終了する．
		if (this.pfmr != null) {
			this.pfmr.delete();
			this.pfmr = null;
		}
	}
}
