
package console;

import java.util.List;

import com.midfield_system.api.stream.AudioFormat;
import com.midfield_system.api.stream.ConnectionMode;
import com.midfield_system.api.stream.IoFormat;
import com.midfield_system.api.stream.ProtocolType;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamFormat;
import com.midfield_system.api.stream.VideoFormat;
import com.midfield_system.api.system.MfsNode;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: SetupOutputState
 *
 * Date Modified: 2020.08.18
 *
 */

//==============================================================================
public class SetupOutputState
	extends		IoSetupState
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String STR_OUTPUT_PROMPT = "out";

	private static final int
		CMD_OUT_SHOW_SEGIO		= 0,

		CMD_OUT_SET_VIEWER		= 1,
		CMD_OUT_SET_LIVE_STM	= 2,
		CMD_OUT_SET_MEDIA_FILE	= 3,

		CMD_OUT_NEW_STREAM		= 5,
		CMD_OUT_SETUP_OPTION	= 6,
		CMD_OUT_QUIT			= 9;
	
	private static final String
		CMD_TITLE = "□システム □入力設定 ■出力設定 □オプション設定 □ストリーム操作";
	
	private static final CommandDesc[] CMD_DESCS = new CommandDesc[] {
		new CommandDesc(CMD_OUT_SHOW_SEGIO,		"入出力設定表示"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_OUT_SET_VIEWER,		"再生/表示設定"),
		new CommandDesc(CMD_OUT_SET_LIVE_STM,	"送信ストリーム設定"),
		new CommandDesc(CMD_OUT_SET_MEDIA_FILE,	"メディアファイル設定"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_OUT_NEW_STREAM,		"ストリーム生成 → □ストリーム操作"),
		new CommandDesc(CMD_OUT_SETUP_OPTION,	"オプション設定 → □オプション設定"),
		new CommandDesc(CMD_OUT_QUIT,			"設定キャンセル → □システム")
	};
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private SetupOptionState optStat = null;
//	private StreamControlState stmStat = null;
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public SetupOutputState(Console console, MfsNode mfs)
	{
		super(console, mfs);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void setStateInstanue(
		SystemCommandState sysStat,
		SetupOptionState optStat,
		StreamControlState stmStat
	) {
		this.sysStat = sysStat;
		this.optStat = optStat;
//		this.stmStat = stmStat;
	}
	
//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------

	//- PROTECTED METHOD -------------------------------------------------------
	//
	@Override
	protected String getPrompt()
	{
		return STR_OUTPUT_PROMPT;
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	@Override
	protected ConsoleState execute()
	{
		ConsoleState nextStat = this;
		
		int n = getCommandNumber(CMD_TITLE, CMD_DESCS);
		switch (n) {
		case CMD_OUT_SET_VIEWER		: setViewer();				break;
		case CMD_OUT_SET_LIVE_STM	: setLiveStream();			break;
		case CMD_OUT_SET_MEDIA_FILE	: setMediaFile();			break;
		case CMD_OUT_SHOW_SEGIO		: showSegmentIo();			break;
		case CMD_OUT_NEW_STREAM		: nextStat = newStream();	break;
		case CMD_OUT_SETUP_OPTION	: nextStat = setupOption();	break;
		case CMD_OUT_QUIT			: nextStat = quit();		break;
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
	private void setViewer()
	{
		SegmentIo segIo = this.console.getSegmentIo();

		//----------------------------------------------------------------------
		// ■SegmentIoへビューワを設定
		//----------------------------------------------------------------------
		segIo.configureDefaultRenderer();

		//----------------------------------------------------------------------
		// ライブソースオプションを有効にする．
		segIo.setLiveSource(true);
		
		//----------------------------------------------------------------------
		// SegmentIo を表示する．
		showSegmentIo();
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setLiveStream()
	{
		SegmentIo segIo = this.console.getSegmentIo();

		//----------------------------------------------------------------------
		// ■出力フォーマットの選択
		//----------------------------------------------------------------------
		// 出力フォーマット情報リストを取得する．
		List<StreamFormat> lsOutFmt = segIo.getOutputVideoFormatList();
		lsOutFmt.addAll(segIo.getOutputAudioFormatList());
		int size = lsOutFmt.size();
		if (size <= 0) {
			warningPause("利用可能な出力フォーマットがありません．\n");
			return;
		}
		//----------------------------------------------------------------------
		// 出力フォーマット情報リストの各要素を表示する．
		printStreamFormatList(lsOutFmt);
		
		// 出力フォーマット情報リストにビデオフォーマットが含まれている場合
		// ビデオフォーマットを選択する．
		VideoFormat vidFmt = selectVideoFormat(lsOutFmt);
		
		// 出力フォーマット情報リストにオーディオフォーマットが含まれている場合
		// オーディオフォーマットを選択する．
		// ※選択済みのビデオフォーマットが DV/M2TS の場合は，
		//   オーディオフォーマットを選択しない．
		AudioFormat audFmt = selectAudioFormat(vidFmt, lsOutFmt);
		if ((vidFmt == null) && (audFmt == null)) {
			return;
		}
		//----------------------------------------------------------------------
		// ■トランスポートプロトコルの選択(TCP/UDP)
		//----------------------------------------------------------------------
		boolean useTCP = selectTransportProtocol();

		//----------------------------------------------------------------------
		// ■接続の方向を選択(TCP利用時のみ)
		//----------------------------------------------------------------------
		boolean isRcvCon = false;
		if (useTCP) {
			isRcvCon = selectConnectionDirection();
		}
		//----------------------------------------------------------------------
		// ■送信ストリーム用 IoParam をSegmentIoへ追加
		//----------------------------------------------------------------------
		segIo.configureOutgoingStream(vidFmt, audFmt);
		ProtocolType type = ProtocolType.UDP;
		if (useTCP) {
			type = ProtocolType.TCP;
		}
		ConnectionMode mode = ConnectionMode.ACTIVE;
		if (isRcvCon) {
			mode = ConnectionMode.PASSIVE;
		}
		segIo.setTransportProtocol(type, mode);

		//----------------------------------------------------------------------
		// SegmentIo を表示する．
		showSegmentIo();
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private VideoFormat selectVideoFormat(List<StreamFormat> lsOutFmt)
	{
		VideoFormat vidFmt = null;

		//----------------------------------------------------------------------
		boolean isVid = false;
		for (StreamFormat stmFmt : lsOutFmt) {
			if (stmFmt instanceof VideoFormat) {
				isVid = true;
				break;
			}
		}
		if (isVid == false) {
			// 利用可能なビデオフォーマットが無い．
			return vidFmt;
		}
		//----------------------------------------------------------------------
		int size = lsOutFmt.size();
		int num = selectNumber("ビデオフォーマット番号");
		if (num >= size) {
			warningPause("コマンドをキャンセルしました．\n");
			return vidFmt;
		}
		//----------------------------------------------------------------------
		// 選択した出力フォーマット情報を取得する．
		StreamFormat stmFtm = lsOutFmt.get(num);
		if (stmFtm instanceof VideoFormat) {
			vidFmt = (VideoFormat)stmFtm;
		}
		else {
			warningPause("ビデオフォーマットではありません．\n");
		}
		return vidFmt;
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private AudioFormat selectAudioFormat(
		VideoFormat vidFmt, List<StreamFormat> lsOutFmt
	) {
		AudioFormat audFmt = null;

		//----------------------------------------------------------------------
		if (vidFmt != null) {
			IoFormat ioFmt = vidFmt.getIoFormat();
			if ((ioFmt == IoFormat.DVSD) ||	(ioFmt == IoFormat.M2TS)) {
				// ※選択済みのビデオフォーマットが DV/M2TS の場合は，
				//   オーディオフォーマットを選択しない．
				return audFmt;
			}
		}
		//----------------------------------------------------------------------
		boolean isAud = false;
		for (StreamFormat stmFmt : lsOutFmt) {
			if (stmFmt instanceof AudioFormat) {
				isAud = true;
				break;
			}
		}
		if (isAud == false) {
			// 利用可能なオーディオフォーマットが無い．
			return audFmt;
		}
		//----------------------------------------------------------------------
		int size = lsOutFmt.size();		
		int num = selectNumber("オーディオフォーマット番号");
		if (num >= size) {
			warningPause("コマンドをキャンセルしました．\n");
			return audFmt;
		}
		//----------------------------------------------------------------------
		// 選択した出力フォーマット情報を取得する．
		StreamFormat stmFtm = lsOutFmt.get(num);
		if (stmFtm instanceof AudioFormat) {
			audFmt = (AudioFormat)stmFtm;
		}
		else {
			warningPause("オーディオフォーマットではありません．\n");
		}
		return audFmt;
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private boolean selectTransportProtocol()
	{
		boolean useTCP = false;

		printListTitle("ストリーム転送プロトコル");
		print("  [0] MFSP/TCP\n");
		print("  [1] MFSP/UDP\n");

		int num = selectNumber("プロトコル番号");
		switch (num) {
		case 0 :	useTCP = true;		break;
		case 1 :	useTCP = false;	break;
		default :	warningPause("コマンドをキャンセルしました．UDPを利用します．\n");
					useTCP = false;	break;
		}
		return useTCP;
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private boolean selectConnectionDirection()
	{
		boolean isRcvCon = false;

		printListTitle("コネクション接続方向");
		print("  [0] 受信側から接続をかける\n");
		print("  [1] 送信側から接続をかける\n");

		int num = selectNumber("番号");
		switch (num) {
		case 0 :	isRcvCon = true;	break;
		case 1 :	isRcvCon = false;	break;
		default :	warningPause("コマンドをキャンセルしました．受信側から接続をかけます．\n");
					isRcvCon = true;	break;
		}
		return isRcvCon;
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setMediaFile()
	{
		SegmentIo segIo = this.console.getSegmentIo();

		//----------------------------------------------------------------------
		// ■出力ファイルとフォーマットの設定
		//----------------------------------------------------------------------
		// 出力ファイル名を取得する．
		print("\n");
		String mediaFile = getLine("出力ファイル名");

		// メディアフォーマットを取得する．
		IoFormat ioFmt = IoFormat.UNKNOWN;
		ioFmt = selectMediaFormat();
		if (ioFmt == IoFormat.UNKNOWN) {
			warningPause("コマンドをキャンセルしました．\n");
			return;
		}
		//----------------------------------------------------------------------
		// メディアフォーマットが WMV/WMA の場合，プロファイル名を取得する．
		String wmpName = null;
		if ((ioFmt == IoFormat.WMV) || (ioFmt == IoFormat.WMA)) {
			wmpName = getLine("Windows Media プロファイル名");
		}
		//----------------------------------------------------------------------
		// ■出力ファイル用 IoParam をSegmentIoへ追加
		//----------------------------------------------------------------------
		segIo.configureSinkFile(mediaFile, ioFmt, wmpName);

		//----------------------------------------------------------------------
		// SegmentIo を表示する．
		showSegmentIo();
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private IoFormat selectMediaFormat()
	{
		IoFormat ioFmt = IoFormat.UNKNOWN;

		printListTitle("メディアフォーマット");
		print("  [0] DVSD\n");
		print("  [1] M2TS\n");
		print("  [2] WMV\n");
		print("  [3] WMA\n");
		int num = selectNumber("メディアフォーマット番号");
		switch (num) {
		case 0  : ioFmt = IoFormat.DVSD;	break;
		case 1  : ioFmt = IoFormat.M2TS;		break;
		case 2  : ioFmt = IoFormat.WMV;		break;
		case 3  : ioFmt = IoFormat.WMA;		break;
		default : ioFmt = IoFormat.UNKNOWN;	break;
		}
		return ioFmt;
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private ConsoleState newStream()
	{
		// プレビュー設定
		SegmentIo segIo = this.console.getSegmentIo();
		segIo.setPreviewer();

		// ストリームを生成後，SetupOptionState へ遷移
		ConsoleState conStat = this.optStat.newStream();
		return conStat;
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private ConsoleState setupOption()
	{
		// プレビュー設定
		SegmentIo segIo = this.console.getSegmentIo();
		segIo.setPreviewer();

		// SetupOptionState へ遷移		
		return this.optStat;
	}	
}