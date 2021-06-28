
package performer.ex0;

import java.io.IOException;
import java.util.List;

import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamInfoManager;
import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.protocol.StreamInfo;

import util.LineReader;
import util.SimpleViewer;

// Sample code of MidField System API
// Date Modified: 2020.10.01
//
public class StreamToRendererEx0
{
	public static void main(String[] args)
	{
		MfsNode mfs = null;
		StreamPerformer pfmr = null;
		SimpleViewer viewer = null;
		
		try {
			// MidField System を初期化し，起動する．
			mfs = MfsNode.initialize();		// SystemException
			mfs.activate();					// SystemException
			
			// 送信ホスト名/IPアドレスをコマンドラインから取得する．
			String srcAddr = LineReader.readLine("  送信ホスト名/IPアドレス：");	// IOException
			
			// ストリーム情報リストを送信ホストから取得する．
			StreamInfoManager stmInfMgr = StreamInfoManager.getInstance();
			List<StreamInfo> lsStmInf = stmInfMgr.fetchSourceStreamInfoList(srcAddr);
			if (lsStmInf.size() <= 0) {
				throw new IOException("  ※受信可能なストリームがありません．");
			}
			// ストリーム情報（ここでは最初の要素を選択）で SegmentIo の入力を構成する．
			SegmentIo segIo = new SegmentIo();
			segIo.configureIncomingStream(lsStmInf.get(0));	
		
			// SegmentIo の出力をデフォルトレンダラとして構成する．
			segIo.configureDefaultRenderer();
			
			// オプションの設定をする．
			segIo.setLiveSource(true);	// ライブソースオプションを有効にする．

			// SegmentIo をもとに StreamPerformer を生成する．
			pfmr = StreamPerformer.newInstance(segIo);	// SystemException, StreamException

			// StreamPerformer から VideoCanvas を取得し，SimpleViewer に追加する．
			viewer = new SimpleViewer("StreamToRenderer", pfmr.getVideoCanvas());

			// 入出力処理を開始する．
			pfmr.open();	// StreamException
			pfmr.start();	// StreamException
			
			// Enterキーの入力を待つ．
			LineReader.readLine("> Enter キーの入力を待ちます．");	// IOException
			
			// 入出力処理を終了する．
			pfmr.stop();	// StreamException
			pfmr.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			// SimpleViewer, StreamPerformer, MidField System を終了する．
			if (viewer != null) { viewer.dispose(); }
			if (pfmr != null) { pfmr.delete(); }
			if (mfs != null) { mfs.shutdown(); }
			
			// LineReader を解放する．
			LineReader.release();
		}
	}
}