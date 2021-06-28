
package console;

import com.midfield_system.api.stream.MixerController;
import com.midfield_system.api.stream.PerformerController;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamException;
import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.system.MfsNode;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: StreamControlState
 *
 * Date Modified: 2019.03.15
 *
 */

//==============================================================================
public class StreamControlState
	extends		IoSetupState
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String STR_STREAM_PROMPT = "stm";
	
	private static final int
		CMD_STM_SHOW_SEGIO	= 0,

		CMD_STM_START		= 1,
		CMD_STM_STOP		= 2,
		CMD_STM_DELETE		= 3,

		CMD_STM_RENDERER	= 5,
		CMD_STM_QUIT		= 9;
	
	private static final String
		CMD_TITLE = "□システム □入力設定 □出力設定 □オプション設定 ■ストリーム操作";
	
	private static final CommandDesc[] CMD_DESCS_MAIN = new CommandDesc[] {
		new CommandDesc(CMD_STM_SHOW_SEGIO,	"入出力設定表示"),
		new CommandDesc(CMD_SEPARATOR,		""),
		new CommandDesc(CMD_STM_START,		"入出力処理開始/再開"),
		new CommandDesc(CMD_STM_STOP,		"入出力処理停止"),
		new CommandDesc(CMD_STM_DELETE,		"ストリーム削除"),
		new CommandDesc(CMD_SEPARATOR,		""),
		new CommandDesc(CMD_STM_RENDERER,	"→ □再生/表示操作（ストリーム操作）"),
		new CommandDesc(CMD_STM_QUIT,		"→ □システム")
	};
	
//------------------------------------------------------------------------------

	private static final int	
		CMD_RND_IS_SEEKABLE		= 10,
		CMD_RND_TOTAL_TIME		= 11,
		CMD_RND_GET_POS			= 12,
		CMD_RND_SET_POS			= 13,

		CMD_RND_HAS_VID_SRC		= 20,

		CMD_RND_HAS_VID_MIXER	= 30,
		CMD_RND_VID_SRC			= 31,
		CMD_RND_SET_MAIN_VID	= 32,
		CMD_RND_SET_SUB_ALPHA	= 34,
		CMD_RND_SET_SUB_TYPE	= 35,
		CMD_RND_SET_SUB_SIZE	= 36,

		CMD_RND_HAS_AUD_SRC		= 40,
		CMD_RND_GET_AUD_VOL		= 41,
		CMD_RND_SET_AUD_VOL		= 42,

		CMD_RND_HAS_AUD_MIXER	= 50,
		CMD_RND_AUD_SRC			= 51,
		CMD_RND_SET_AUD_MVOL	= 52;
	
	private static final String	
		CMD_TITLE_RND = "□システム □入力設定 □出力設定 □オプション設定 ■再生/表示操作";	

	private static final CommandDesc[] CMD_DESCS_RND = new CommandDesc[] {
		new CommandDesc(CMD_RND_IS_SEEKABLE,	"シーク機能 利用可/不可 確認"),
		new CommandDesc(CMD_RND_TOTAL_TIME,		"メディアデータのトータル時間取得"),
		new CommandDesc(CMD_RND_GET_POS,		"再生/表示位置取得"),
		new CommandDesc(CMD_RND_SET_POS,		"再生/表示位置設定"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_RND_HAS_VID_SRC,	"ビデオソース 有/無 確認"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_RND_HAS_VID_MIXER,	"ビデオミキシング機能 利用可/不可 確認"),
		new CommandDesc(CMD_RND_VID_SRC,		"ビデオソースリスト一覧"),
		new CommandDesc(CMD_RND_SET_MAIN_VID,	"メインビデオ設定"),
		new CommandDesc(CMD_RND_SET_SUB_ALPHA,	"ビデオミキシングボックス透明度設定"),
		new CommandDesc(CMD_RND_SET_SUB_TYPE,	"ビデオミキシングボックス配置タイプ設定"),
		new CommandDesc(CMD_RND_SET_SUB_SIZE,	"ビデオミキシングボックスサイズ設定"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_RND_HAS_AUD_SRC,	"オーディオソース 有/無 確認"),
		new CommandDesc(CMD_RND_GET_AUD_VOL,	"オーディオボリューム取得"),
		new CommandDesc(CMD_RND_SET_AUD_VOL,	"オーディオボリューム設定"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_RND_HAS_AUD_MIXER,	"オーディオミキシング機能 利用可/不可 確認"),
		new CommandDesc(CMD_RND_AUD_SRC,		"オーディオソースリスト一覧"),
		new CommandDesc(CMD_RND_SET_AUD_MVOL,	"ミキシングオーディオボリューム設定"),
	};	
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private StreamPerformer pfmr = null;
	private PerformerController pfmrCtlr = null;
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public StreamControlState(Console console, MfsNode mfs)
	{
		super(console, mfs);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void setStateInstance(
		SystemCommandState sysStat
	) {
		this.sysStat = sysStat;
	}
	
//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------

	//- PROTECTED METHOD -------------------------------------------------------
	//
	@Override
	protected String getPrompt()
	{
		return STR_STREAM_PROMPT;
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	@Override
	protected ConsoleState execute()
	{
		ConsoleState nextStat = this;
		this.pfmr = this.console.getSelectedActiveStream();
		this.pfmrCtlr = this.pfmr.getPerformerController();
		
		int n = getCommandNumber(CMD_TITLE, CMD_DESCS_MAIN);
		switch (n) {
		case CMD_STM_SHOW_SEGIO	: showStmSegmentIo();			break;
		case CMD_STM_START		: startStream();				break;
		case CMD_STM_STOP		: stopStream();					break;
		case CMD_STM_DELETE		: nextStat = deleteStream();	break;
		case CMD_STM_RENDERER	: rendererControl();			break;
		case CMD_STM_QUIT		: nextStat = quit();			break;
		default					: warningPause("コマンドをキャンセルしました．\n");
								  nextStat = this;				break;
		}
		return nextStat;
	}

//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void showStmSegmentIo()
	{
		SegmentIo segIo = this.pfmr.getSegmentIo();
		printSegmentIo(segIo);
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void startStream()
	{
		try {
			this.pfmr.start();
		}
		catch (StreamException ex) {
			warningPause("入出力処理を開始できません(%s)．\n", ex.getMessage());
		}
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void stopStream()
	{
		try {
			this.pfmr.stop();
		}
		catch (StreamException ex) {
			warningPause("入出力処理を停止できません(%s)．\n", ex.getMessage());
		}
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private ConsoleState deleteStream()
	{
		this.pfmr.delete();

		// SystemCommandState へ遷移
		return this.sysStat;
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void rendererControl()
	{
		boolean isQuit = false;

		while (isQuit == false) {
			isQuit = executeRendererControl();
		}
	}
	
//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	boolean executeRendererControl()
	{
		boolean isQuit = false;

		int n = getCommandNumber(CMD_TITLE_RND, CMD_DESCS_RND);
		switch (n) {
		case CMD_RND_IS_SEEKABLE	: rndIsSeekable();				break;
		case CMD_RND_TOTAL_TIME		: rndTotalTime();				break;
		case CMD_RND_GET_POS		: rndGetPosision();				break;
		case CMD_RND_SET_POS		: rndSetPosision();				break;

		case CMD_RND_HAS_VID_SRC	: rndHasRenderableVideo();		break;

		case CMD_RND_HAS_VID_MIXER	: rndIsVideoMixerSource();		break;
		case CMD_RND_VID_SRC		: rndShowVideoSourceList();		break;
		case CMD_RND_SET_MAIN_VID	: rndSetMainVideo();			break;
		case CMD_RND_SET_SUB_ALPHA	: rndSetSubAlpha();				break;
		case CMD_RND_SET_SUB_TYPE	: rndSetSubType();				break;
		case CMD_RND_SET_SUB_SIZE	: rndSetSubSize();				break;

		case CMD_RND_HAS_AUD_SRC	: rndHasRenderableAudio();		break;
		case CMD_RND_GET_AUD_VOL	: rndGetAudioVolume();			break;
		case CMD_RND_SET_AUD_VOL	: rndSetAudioVolume();			break;

		case CMD_RND_HAS_AUD_MIXER	: rndIsAudioMixerSource();	break;
		case CMD_RND_AUD_SRC		: rndShowAudioSourceList();		break;
		case CMD_RND_SET_AUD_MVOL	: rndSetMixingAudioVolume();	break;

		default						: isQuit = true;
									  warningPause("コマンドをキャンセルしました．\n");
																	break;
		}
		return isQuit;
	}

//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndIsSeekable()
	{
		boolean isSeekable = this.pfmrCtlr.isSeekable();
		if (isSeekable) {
			messagePause("シーク機能 利用可\n");
		}
		else {
			messagePause("シーク機能 利用不可\n");
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndTotalTime()
	{
		long total = this.pfmrCtlr.getMediaTotalTime();
		messagePause("メディアデータのトータル時間：%s[msec]\n", Long.toString(total));
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndGetPosision()
	{
		long pos = this.pfmrCtlr.getMediaPosition();
		messagePause("再生/表示位置：%s[msec]\n", Long.toString(pos));
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndSetPosision()
	{
		String strPos = getLine("再生/表示位置[msec]");
		this.pfmrCtlr.setMediaPosition(Long.parseLong(strPos));
	}

//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndHasRenderableVideo()
	{
		boolean has = this.pfmrCtlr.hasRenderableVideo();
		if (has) {
			messagePause("ビデオソース 有\n");
		}
		else {
			messagePause("ビデオソース 無\n");
		}
	}

//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndIsVideoMixerSource()
	{
		boolean has = this.pfmrCtlr.isVideoMixerSource();
		if (has) {
			messagePause("ビデオミキシング機能 利用可\n");
		}
		else {
			messagePause("ビデオミキシング機能 利用不可\n");
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndShowVideoSourceList()
	{
/*		
		List<IoParam> lsIOMap = this.pfmrCtlr.getVideoSourceList();
		printIoParamList("ビデオソースリスト一覧", lsIOMap);
*/		
print("ビデオソースリスト一覧 (未実装 2009/10/09)");
		pause();
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndSetMainVideo()
	{
/*		
		String strNo = getLine("メインビデオ ソース番号");
		this.pfmrCtlr.setVideoMixerTile(Integer.parseInt(strNo), null);
*/		
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndSetSubAlpha()
	{
		String strAlpha = getLine("ビデオミキシングボックス透明度[0.0-1.0]");
		MixerController mixCtlr = this.pfmrCtlr.getMixerController();
		if (mixCtlr != null) {
			mixCtlr.setVideoBoxAlphaValue(Double.parseDouble(strAlpha));
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndSetSubType()
	{
/*		
		String type = null;

		printListTitle("ビデオミキシングボックスの配置");
		print("  [0] 水平\n");
		print("  [1] 垂直\n");

		int num = selectNumber("配置番号");
		switch (num) {
		case 0 :	type = PerformerController.TYPE_HORIZONTAL;	break;
		case 1 :	type = PerformerController.TYPE_VERTICAL;		break;
		default :	warningPause("コマンドをキャンセルしました．水平に配置します．\n");
					type = PerformerController.TYPE_HORIZONTAL;	break;
		}
		this.pfmrCtlr.setVideoBoxType(type);
*/		
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndSetSubSize()
	{
/*		
		String strSize = getLine("ビデオミキシングボックスサイズ[0.0-1.0]");
		this.pfmrCtlr.setVideoBoxRatio(Double.parseDouble(strSize));
*/		
	}

//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndHasRenderableAudio()
	{
		boolean  has = this.pfmrCtlr.hasRenderableAudio();
		if (has) {
			messagePause("オーディオソース 有\n");
		}
		else {
			messagePause("オーディオソース 無\n");
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndGetAudioVolume()
	{
		double vol = this.pfmrCtlr.getAudioVolume();
		messagePause("オーディオボリューム：%s\n", Double.toString(vol));
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndSetAudioVolume()
	{
		String strVol = getLine("オーディオボリューム[0.0-1.0]");
		this.pfmrCtlr.setAudioVolume(Double.parseDouble(strVol));
	}

//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndIsAudioMixerSource()
	{
		boolean  has = this.pfmrCtlr.isAudioMixerSource();
		if (has) {
			messagePause("オーディオミキシング機能 利用可\n");
		}
		else {
			messagePause("オーディオミキシング機能 利用不可\n");
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndShowAudioSourceList()
	{
//		List<IoParam> lsIOMap = this.pfmrCtlr.getAudioSourceList();
//		printIoParamList("オーディオソースリスト一覧", lsIOMap);
print("オーディオソースリスト一覧 (未実装 2009/10/09)");
		pause();
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndSetMixingAudioVolume()
	{
print("オーディオボリュームの調整 (未実装 2019/03/15)");
/*
		String strNo = getLine("オーディオ ソース番号");
		String strVol = getLine("オーディオボリューム[0.0-1.0]");

		MixerController mixCtlr = this.pfmrCtlr.getMixerController();
		if (mixCtlr != null) {
			mixCtlr.setAudioMixerVolume(
				Integer.parseInt(strNo),
				Double.parseDouble(strVol)
			);
		}
*/
	}
}