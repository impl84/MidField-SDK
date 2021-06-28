
package console;

import java.util.List;

import com.midfield_system.api.stream.AudioFormat;
import com.midfield_system.api.stream.DeviceInfo;
import com.midfield_system.api.stream.DeviceInfoManager;
import com.midfield_system.api.stream.IoParam;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamFormat;
import com.midfield_system.api.stream.StreamInfoManager;
import com.midfield_system.api.stream.VideoFormat;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.protocol.StreamInfo;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: SetupInputState
 *
 * Date Modified: 2020.07.10
 *
 */

//==============================================================================
public class SetupInputState
	extends		IoSetupState
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String STR_INPUT_PROMPT = " in";
	
	private static final int
		CMD_IN_SHOW_SEGIO		= 0,

		CMD_IN_ADD_CAP_DEV		= 1,
		CMD_IN_ADD_LIVE_STM		= 2,
		CMD_IN_ADD_MEDIA_FILE	= 3,

		CMD_IN_GATHER_LIVE_STM	= 4,

		CMD_IN_SETUP_OUTPUT		= 5,
		CMD_IN_QUIT				= 9;

	private static final String
		CMD_TITLE = "□システム ■入力設定 □出力設定 □オプション設定 □ストリーム操作";
	
	private static final CommandDesc[] CMD_DESCS = new CommandDesc[] {
		new CommandDesc(CMD_IN_SHOW_SEGIO,		"入出力設定表示"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_IN_ADD_CAP_DEV,		"入力デバイス追加"),
		new CommandDesc(CMD_IN_ADD_LIVE_STM,	"受信ストリーム追加"),
		new CommandDesc(CMD_IN_ADD_MEDIA_FILE,	"メディアファイル追加"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_IN_GATHER_LIVE_STM,	"受信ストリーム情報収集"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_IN_SETUP_OUTPUT,	"出力設定       → □出力設定"),
		new CommandDesc(CMD_IN_QUIT,			"設定キャンセル → □システム")
	};
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private SetupOutputState outStat = null;	
		
//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public SetupInputState(Console console, MfsNode mfs)
	{
		super(console, mfs);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void setStateInstance(SystemCommandState sysStat, SetupOutputState outStat)
	{
		this.sysStat = sysStat;
		this.outStat = outStat;
	}

//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------

	//- PROTECTED METHOD -------------------------------------------------------
	//
	@Override
	protected String getPrompt()
	{
		return STR_INPUT_PROMPT;
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	@Override
	protected ConsoleState execute()
	{
		ConsoleState nextStat = this;
	
		int n = getCommandNumber(CMD_TITLE, CMD_DESCS);
		switch (n) {
		case CMD_IN_SHOW_SEGIO		: showSegmentIo();			break;
		case CMD_IN_ADD_CAP_DEV		: addCaptureDevice();		break;
		case CMD_IN_ADD_LIVE_STM	: addLiveStream();			break;
		case CMD_IN_ADD_MEDIA_FILE	: addMediaFile();			break;
		case CMD_IN_GATHER_LIVE_STM	: refreshSourcePerformerInformation();		break;
		case CMD_IN_SETUP_OUTPUT	: nextStat = setupOutput();	break;
		case CMD_IN_QUIT			: nextStat = quit();		break;
		default						: warningPause("コマンドをキャンセルしました．\n");
									  nextStat = this;			break;
		}
		return nextStat;
	}
		
//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void addCaptureDevice()
	{
		SegmentIo segIo = this.console.getSegmentIo();

		//----------------------------------------------------------------------
		// ■入力デバイスの選択
		//----------------------------------------------------------------------
		// 入力デバイス情報リストを取得する．
		DeviceInfoManager devMgr = DeviceInfoManager.getInstance();
		List<DeviceInfo> lsDevInf = devMgr.getDeviceInfoList();
		int size = lsDevInf.size();
		if (size <= 0) {
			warningPause("利用可能な入力デバイスがありません．\n");
			return;
		}
		//----------------------------------------------------------------------
		// 入力デバイス情報リストの各要素を表示する．
		printDeviceInfoList(lsDevInf);

		// 入力デバイス情報リストの要素番号を選択する．
		int num = selectNumber("入力デバイス番号");
		if (num >= size) {
			warningPause("コマンドをキャンセルしました．\n");
			return;
		}
		//----------------------------------------------------------------------
		// 選択した入力デバイス情報を取得する．
		DeviceInfo devInf = lsDevInf.get(num);

		//----------------------------------------------------------------------
		// ■出力フォーマットの選択
		//----------------------------------------------------------------------
		// 出力フォーマット情報リストを取得する．
		List<StreamFormat> lsStmFmt = devInf.getOutputFormatList();
		size = lsStmFmt.size();
		if (size <= 0) {
			warningPause("利用可能な出力フォーマットがありません．\n");
			return;
		}
		//----------------------------------------------------------------------
		// 出力フォーマット情報リストの各要素を表示する．
		printStreamFormatList(lsStmFmt);

		// 出力フォーマット情報リストの要素番号を選択する．
		num = selectNumber("フォーマット番号");
		if (num >= size) {
			warningPause("コマンドをキャンセルしました．\n");
			return;
		}
		//----------------------------------------------------------------------
		// 選択した出力フォーマット情報を取得する．
		StreamFormat stmFmt = lsStmFmt.get(num);

		//----------------------------------------------------------------------
		// ■入力デバイス・出力フォーマット情報を SegmentIo へ追加
		//----------------------------------------------------------------------
		segIo.addInputDevice(devInf, stmFmt);

		//----------------------------------------------------------------------
		// ■現在の SegmentIo を表示する．
		showSegmentIo();
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void addLiveStream()
	{
		SegmentIo segIo = this.console.getSegmentIo();

		//----------------------------------------------------------------------
		// ■受信ストリームの選択
		//----------------------------------------------------------------------
		// 受信ストリーム情報のリストを取得する．
		StreamInfoManager manager = StreamInfoManager.getInstance();
		List<StreamInfo> lsOutInf = manager.getSourceStreamInfoList();
		int size = lsOutInf.size();
		if (size <= 0) {
			warningPause("受信可能なストリームがありません．\n");
			return;
		}
		//----------------------------------------------------------------------
		// 受信ストリーム情報の配列の各要素を表示する．
		printOutputInformationList("受信ストリーム一覧", lsOutInf);

		// 受信ストリーム情報の配列要素番号を選択する．
		int num = selectNumber("受信ストリーム番号");
		if (num >= size) {
			warningPause("コマンドをキャンセルしました．\n");
			return;
		}
		//----------------------------------------------------------------------
		List<IoParam> lsInPrm = lsOutInf.get(num).restoreIoParamList();
		IoParam inPrm = lsInPrm.get(0);

/*
		//----------------------------------------------------------------------
		// ■マルチキャストで受信する場合の設定
		// （※受信ストリームがUDPを利用している場合のみ）
		//----------------------------------------------------------------------
		String mcAddr = null;
		String xportProt = inPrm.getTransportProtocol();
		if (xportProt.equals("UDP")) {
			String strBuf = getLine("マルチキャストを利用しますか？[Y/N]");
			if (strBuf.startsWith("Y") || strBuf.startsWith("y")) {
				// マルチキャストを利用する場合
				mcAddr = getLine("マルチキャストアドレス");
			}
		}
*/		
		//----------------------------------------------------------------------
		// ■受信ストリーム情報を SegmentIo へ追加
		//----------------------------------------------------------------------
		segIo.addIncomingStream(inPrm);
		//----------------------------------------------------------------------
		// ■現在の SegmentIo を表示する．
		showSegmentIo();
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void addMediaFile()
	{
		SegmentIo segIo = this.console.getSegmentIo();
		VideoFormat vidFmt = null;
		AudioFormat audFmt = null;

		//----------------------------------------------------------------------
		// ■メディアファイル名とフォーマット情報の取得
		//----------------------------------------------------------------------
		// メディアファイル名を取得する．
		print("\n");
		String mediaFile = getLine("メディアファイル名");

		// フォーマット情報を取得する．
		vidFmt = VideoFormat.getFileFormat(mediaFile);
		audFmt = AudioFormat.getFileFormat(mediaFile);
		if ((vidFmt == null) && (audFmt == null)) {
			warningPause("未対応のファイルフォーマットです．\n");
			return;
		}
		//----------------------------------------------------------------------
		// ■メディアファイルをSegmentIoへ追加
		//----------------------------------------------------------------------
		segIo.configureSourceFile(mediaFile);

		//----------------------------------------------------------------------
		// メディアファイルのフォーマット情報を表示する．
		if (vidFmt != null) {
			print("  %s\n", vidFmt.toString());
		}
		if (audFmt != null) {
			print("  %s\n", audFmt.toString());
		}
		//----------------------------------------------------------------------
		// ■現在の SegmentIo を表示する．
		showSegmentIo();
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void refreshSourcePerformerInformation()
	{
		// ソースホスト名を設定する．
		// ※スペース区切りで複数指定可
		print("\n");
		message("送信ホスト名/IPアドレスを入力して下さい．\n");
		print("  複数指定する場合はスペースで区切って入力して下さい．\n");
		String buf = getLine("送信ホスト名/IPアドレス");
		String[] srcAddrs = buf.split("\\s+");

		StreamInfoManager manager = StreamInfoManager.getInstance();		
		manager.refreshSourceStreamInfoList(srcAddrs);

		// 各々が送信している受信ストリーム情報を設定された
		// 全てのソースホストから収集するための要求パケットを送信する．
		messagePause("受信ストリーム情報要求パケットを送信します．\n");
		manager.refreshSourceStreamInfoList();
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private ConsoleState setupOutput()
	{
		// SetupOutputState へ遷移	
		return this.outStat;
	}
}
