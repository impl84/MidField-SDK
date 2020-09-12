
package com.midfield_system.app.performer.ex0;

import com.midfield_system.api.stream.ConnectionMode;
import com.midfield_system.api.stream.DeviceInfo;
import com.midfield_system.api.stream.DeviceInfoManager;
import com.midfield_system.api.stream.ProtocolType;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.app.util.LineReader;
import com.midfield_system.app.util.SimpleViewer;

// Sample code of MidField System API
// Date Modified: 2020.10.01
//
public class DeviceToStreamEx0
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
			
			// ビデオとオーディオの入力デバイス情報リストを取得し，
			// 利用する入力デバイスを選択する．（ここでは最初の要素を選択する．）
			DeviceInfoManager devInfMgr = DeviceInfoManager.getInstance();
			DeviceInfo vidDev = devInfMgr.getInputVideoDeviceInfoList().get(0);
			DeviceInfo audDev = devInfMgr.getInputAudioDeviceInfoList().get(0);
			
			// 入力デバイスで SegmentIo の入力を構成する．
			SegmentIo segIo = new SegmentIo();
			segIo.configureInputDevice(vidDev, audDev);
			
			// SegmentIo の出力を送信ストリームとして構成し，
			// トランスポートプロトコルの設定を行う．
			segIo.configureOutgoingStream(
				segIo.getOutputVideoFormatList().get(0),
				segIo.getOutputAudioFormatList().get(0)
			);
			segIo.setTransportProtocol(
				ProtocolType.TCP,		// TCPを利用する．
				ConnectionMode.PASSIVE	// コネクション接続要求を受け入れる．
			);
			// オプションの設定をする．
			segIo.setPreviewer();		// プレビューワ―を利用する．
			segIo.setLiveSource(true);	// ライブソースオプションを有効にする．	

			// SegmentIo をもとに StreamPerformer を生成する．
			pfmr = StreamPerformer.newInstance(segIo);	// SystemException, StreamException

			// StreamPerformer から VideoCanvas を取得し，SimpleViewer に追加する．
			viewer = new SimpleViewer("DeviceToStream", pfmr.getVideoCanvas());

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