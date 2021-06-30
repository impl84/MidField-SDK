
package selector;

import java.io.IOException;
import java.util.List;

import com.midfield_system.api.stream.ConnectionMode;
import com.midfield_system.api.stream.DeviceInfo;
import com.midfield_system.api.stream.DeviceInfoManager;
import com.midfield_system.api.stream.ProtocolType;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamFormat;
import com.midfield_system.api.stream.StreamInfoManager;
import com.midfield_system.protocol.StreamInfo;

import util.LineReader;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: ConfigTool
 *
 * Date Modified: 2020.10.01
 *
 */

//==============================================================================
class ConfigTool
{
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PACKAGE METHOD: SegmentIo の入力を構成するメソッド
//------------------------------------------------------------------------------

	//- PACKAGE METHOD ---------------------------------------------------------
	// SegmentIo の入力(デバイス)を構成する．
	//
	void configureInputDevice(SegmentIo segIo)
		throws	IOException	
	{
		// ビデオとオーディオの入力デバイス情報リストを取得する．
		DeviceInfoManager devInfMgr = DeviceInfoManager.getInstance();
		List<DeviceInfo> lsVidDev = devInfMgr.getInputVideoDeviceInfoList();
		List<DeviceInfo> lsAudDev = devInfMgr.getInputAudioDeviceInfoList();
		
		// ビデオとオーディオの入力デバイス情報を選択する．
		// （ここでは最初の要素を選択する．）
		DeviceInfo vidDev = lsVidDev.get(0);
		DeviceInfo audDev = lsAudDev.get(0);
		
		// 入力デバイスの出力フォーマットを決める．
		// （ここではデフォルトのフォーマットを利用する．）
		StreamFormat vidFmt = vidDev.getDefaultOutputFormat();
		StreamFormat audFmt = audDev.getDefaultOutputFormat();

		// Segment I/O の入力を入力デバイス情報と出力フォーマットで構成する．
		segIo.configureInputDevice(vidDev, vidFmt, audDev, audFmt);
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	// SegmentIo の入力(受信ストリーム)を構成する．
	//
	void configureIncomingStream(SegmentIo segIo)
		throws	IOException	
	{
		// 送信ホスト名/IPアドレスをコマンドラインから取得する．
		String srcAddr = LineReader.readLine("  送信ホスト名/IPアドレス：");
			// IOException
		
		// ストリーム情報リストを送信ホストから取得する．
		StreamInfoManager stmInfMgr = StreamInfoManager.getInstance();
		List<StreamInfo> lsStmInf = stmInfMgr.fetchSourceStreamInfoList(srcAddr);
		if (lsStmInf.size() <= 0) {
			throw new IOException("  ※受信可能なストリームがありません．");
		}
		// 送信ホストの出力ストリーム情報を選択（ここでは最初の要素を選択）し，
		// SegmentIo の入力として構成する．
		StreamInfo stmInf = lsStmInf.get(0);
		segIo.configureIncomingStream(stmInf);		
	}
	
//------------------------------------------------------------------------------
//  PACKAGE METHOD: SegmentIo の出力を構成するメソッド
//------------------------------------------------------------------------------
	
	//- PACKAGE METHOD ---------------------------------------------------------
	// SegmentIo の出力(レンダラ)を構成する．
	//
	void configureDefaultRenderer(SegmentIo segIo)
		throws	IOException	
	{
		// 出力をデフォルトレンダラとして構成する．
		segIo.configureDefaultRenderer();

		// ライブソースオプションを有効にする．
		segIo.setLiveSource(true);
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	// SegmentIo の出力(送信ストリーム)を構成する．
	//
	void configureOutgoingStream(SegmentIo segIo)
		throws	IOException	
	{
		// 出力可能なフォーマット情報リストを取得する．
		List<StreamFormat> lsOutVidFmt = segIo.getOutputVideoFormatList();
		List<StreamFormat> lsOutAudFmt = segIo.getOutputAudioFormatList();
		
		// 出力フォーマット情報を取得する．（ここでは最初の要素を選択）
		StreamFormat vidFmt = lsOutVidFmt.get(0);
		StreamFormat audFmt = lsOutAudFmt.get(0);
		
		// 選択したビデオフォーマットとプロトコルを出力として構成する．
		segIo.configureOutgoingStream(vidFmt, audFmt);
		
		// トランスポートプロトコルの設定を行う．
		segIo.setTransportProtocol(
			ProtocolType.TCP,		// TCPを利用する．
			ConnectionMode.PASSIVE	// コネクション接続要求を受け入れる．
		);
		// プレビューワ―を利用するためのオプションを有効にする．
		segIo.setPreviewer();
		
		// ライブソースオプションを有効にする．
		segIo.setLiveSource(true);		
	}
}
