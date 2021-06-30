
package videochat;

import java.io.IOException;

import com.midfield_system.api.stream.StreamException;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.api.system.SystemException;

import selector.AbstractSampleCode;
import selector.DeviceToStreamEx1;
import selector.StreamToRendererEx1;
import util.LineReader;
import util.SimpleViewer;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: SimpleVideoChat
 *
 * Date Modified: 2020.10.02
 *
 */

//==============================================================================
public class SimpleVideoChat
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
		MfsNode mfs = null;
		SimpleViewer viewer = null;
		AbstractSampleCode sender = null;
		AbstractSampleCode receiver = null;
	
		try {
			// MidField System を初期化する．
			mfs = MfsNode.initialize();		// SystemException
			
			// MidField System を起動する．
			mfs.activate();					// SystemException
			
			// SimpleViewer を生成する．
			viewer = new SimpleViewer("Simple Video Chat", 640, 240, 0, 2);

			// ビデオ送信用のサンプルコードを実行する．
			sender = new DeviceToStreamEx1(viewer);
			sender.startup();			// SystemException, StreamException
			
			// ビデオ受信用のサンプルコードを実行する．
			receiver = new StreamToRendererEx1(viewer);
			receiver.startup();		// SystemException, StreamException
			
			// Enterキーの入力を待つ．
			LineReader.readLine("> Enter キーの入力を待ちます．");	// IOException
		}
		catch (SystemException | StreamException | IOException ex) {
			// 例外発生時のスタックトレースを表示する．
			ex.printStackTrace();
		}
		finally {
			// サンプルコードの処理を終了する．
			if (receiver != null) {
				receiver.cleanup();
			}
			if (sender != null) {
				sender.cleanup();
			}
			// SimpleViewer を終了する．
			if (viewer != null) {
				viewer.dispose();
			}
			// MidField System を終了する．
			if (mfs != null) {
				mfs.shutdown();
			}
			// 標準入力からの1行読み込みに利用した LineReader を解放する．
			LineReader.release();
		}
	}
}