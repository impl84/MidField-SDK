
package selector;

import java.io.IOException;

import com.midfield_system.api.stream.StreamException;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.api.system.SystemException;

import performer.ex1.AbstractSampleCode;
import performer.ex1.DeviceToRendererEx1;
import performer.ex1.DeviceToStreamEx1;
import performer.ex1.StreamToRendererEx1;
import performer.ex1.StreamToStreamEx1;
import util.LineReader;
import util.SimpleViewer;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: SampleCodeSelector
 *
 * Date Modified: 2020.10.05
 *
 */

//==============================================================================
public class SampleCodeSelector
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final int
		END_OF_SELECTOR		= -1,
		INDEX_OUT_OF_BOUNDS	= -2;

//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------

	// MidField System
	private MfsNode mfs = null;

	// ビデオを表示するためのビューワ
	private SimpleViewer viewer = null;

	// サンプルコードとして実行可能なインスタンスの配列
	private AbstractSampleCode[] samples = null;

//==============================================================================
//  INSTANCE METHOD:
//==============================================================================
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public SampleCodeSelector()
		throws	SystemException
	{
		// MidField System を初期化する．
		this.mfs = MfsNode.initialize();
			// SystemException

		// MidField System を起動する．
		this.mfs.activate();
			// SystemException

		// SimpleViewer のインスタンスを生成する．
		this.viewer = new SimpleViewer("Sample Code Selector");
		
		// サンプルコードとして実行可能なインスタンスが要素となる配列を生成する．
		this.samples = new AbstractSampleCode[] {
			new DeviceToRendererEx1(this.viewer),
			new DeviceToStreamEx1(this.viewer),
			new StreamToRendererEx1(this.viewer),
			new StreamToStreamEx1(this.viewer)
		};
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public void mainLoop()
	{
		// サンプルコードを選択し，開始または停止を繰り返す．
		while (true) {
			// サンプルコードのリストを表示する．
			printSampleCodeList();
			
			// サンプルコードを選択する．
			int result = selectSampleCode();
			if (result == END_OF_SELECTOR) {
				// 終了番号が入力されたのでループを抜ける．
				break;
			}
			else if (result == INDEX_OUT_OF_BOUNDS) {
				// 範囲外の番号が入力されたので次のループの処理に入る．
				continue;
			}
			// 入力された番号のサンプルコードの処理を開始または終了する．
			changeRunningState(result);
		}
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public void cleanup()
	{
		// MidField System を利用しているかどうかを確認する．
		if (this.mfs == null) {
			return;
		}
		// 処理を終了していないサンプルコードがある場合は終了させる．
		cleanupAllSampleCodes();
		
		// SimpleViewer を終了する．
		if (this.viewer != null) {
			this.viewer.dispose();
		}
		// MidField System をシャットダウンする．
		this.mfs.shutdown();
		this.mfs = null;
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void printSampleCodeList()
	{
		// サンプルコードの一覧を表示する．
		System.out.println();
		System.out.println("▼サンプルコード一覧：");
		
		// サンプルコードとして実行可能なインスタンスの配列を走査する．
		for (int n = 0; n < this.samples.length; n++) {
			// サンプルコードのインスタンスを取得する．
			AbstractSampleCode sample = this.samples[n];

			// 動作状態に応じた出力文字列を決める．
			String runningState = null;
			if (sample.isRunning()) {
				runningState = "動作中";
			}
			else {
				runningState = "停止中";
			}
			// サンプルコードの番号・状態・説明を出力する．
			System.out.printf(" [%02d] (%s) %s\n", n,
				runningState,
				sample.getDescription()
			);
		}
	}
		
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private int selectSampleCode()
	{
		int res = 0;
		
		// サンプルコードを選択し，開始/停止する．
		try {
			// サンプルコード番号をキーボードから取得する．
			System.out.printf("> 番号入力[0-%d: 実行，%d: 終了]：",
				this.samples.length - 1, this.samples.length
			);
			String line = LineReader.readLine();	// IOException
			int n = Integer.parseInt(line);			// NumberFormatException
			
			// 入力された値の範囲を確認する．
			if ((n >= 0) && (n < this.samples.length)) {
				// 選択されたサンプルコードの配列のインデックス値を
				// リザルトコードに設定する．
				res = n;
			}
			else if (n == this.samples.length) {
				// 終了を意味する番号が入力されたので，
				// リザルトコードに END_OF_SELECTOR を設定する．
				System.out.printf("  プログラムを終了します．");
				res = END_OF_SELECTOR;
			}
			else {
				// リザルトコードに INDEX_OUT_OF_BOUNDS を設定する．
				System.out.println("  ※適切な番号を入力してください．");
				res = INDEX_OUT_OF_BOUNDS;
			}
		}
		catch (NumberFormatException ex) {
			// NumberFormatException が発生した場合は，
			// リザルトコードに INDEX_OUT_OF_BOUNDS を設定する．
			System.out.printf("  ※適切な番号を入力してください．(%s)\n",
				ex.getMessage()
			);
			res = INDEX_OUT_OF_BOUNDS;
		}
		catch (IOException ex) {
			// IOException が発生した場合は，例外メッセージを表示して，
			// リザルトコードに END_OF_SELECTOR を設定する．
			System.out.println("※キーボードからの入力処理で例外が発生しました．");
			ex.printStackTrace();
			res = END_OF_SELECTOR;
		}
		return res;
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void changeRunningState(int idx)
	{
		// サンプルコードを開始または停止する．
		try {
			// 選択されたサンプルコードのインスタンスを取得する．
			AbstractSampleCode selectedSample = this.samples[idx];
				
			// サンプルコードの動作状態により，
			// サンプルコードの処理を開始または終了する．
			if (selectedSample.isRunning()) {
				// サンプルコードの処理を終了する．
				selectedSample.cleanup();
			}
			else {
				// サンプルコードの処理を開始する．
				selectedSample.startup();
					// SystemException, StreamException
			}			
		}
		catch (SystemException | StreamException ex) {
			// サンプルコート実行時に例外が発生した場合は，
			// 例外メッセージを表示して戻る．
			System.out.println("※サンプルコード実行時に例外が発生しました．");
			ex.printStackTrace();
		}
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void cleanupAllSampleCodes()
	{
		// サンプルコードとして実行可能なインスタンスの配列を走査する．
		for (int n = 0; n < this.samples.length; n++) {
			// サンプルコードのインスタンスを取得する．
			AbstractSampleCode sample = this.samples[n];

			// 処理を終了していないサンプルコードがある場合は処理を終了させる．
			if (sample.isRunning()) {
				sample.cleanup();
			}
		}		
	}
}