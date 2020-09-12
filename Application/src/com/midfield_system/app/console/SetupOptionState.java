
package com.midfield_system.app.console;

import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.stream.StreamException;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.api.stream.SegmentIo;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: SetupOptionState
 *
 * Date Modified: 2020.07.15
 *
 */

//==============================================================================
public class SetupOptionState
	extends		IoSetupState
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String STR_OPTION_PROMPT = "opt";
	
	private static final int
		CMD_OPT_SHOW_SEGIO		= 0,

		CMD_OPT_SET_PREVIEWED	= 1,
		CMD_OPT_SET_DESCRIPTION	= 2,
		CMD_OPT_INSERT_VID_EFF	= 3,
		CMD_OPT_INSERT_AUD_EFF	= 4,

		CMD_OPT_NEW_STREAM		= 5,
		CMD_OPT_QUIT			= 9;
	
	private static final String
		CMD_TITLE = "□システム □入力設定 □出力設定 ■オプション設定 □ストリーム操作";
	
	private static final CommandDesc[] CMD_DESCS = new CommandDesc[] {
		new CommandDesc(CMD_OPT_SHOW_SEGIO,			"入出力設定表示"),
		new CommandDesc(CMD_SEPARATOR,				""),
		new CommandDesc(CMD_OPT_SET_PREVIEWED,		"プレビュー ON/OFF"),
		new CommandDesc(CMD_OPT_SET_DESCRIPTION,	"デスクリプション設定"),
		new CommandDesc(CMD_OPT_INSERT_VID_EFF,		"ビデオエフェクタ設定"),
		new CommandDesc(CMD_OPT_INSERT_AUD_EFF,		"オーディオエフェクタ設定"),
		new CommandDesc(CMD_SEPARATOR,				""),
		new CommandDesc(CMD_OPT_NEW_STREAM,			"ストリーム生成 → □ストリーム操作"),
		new CommandDesc(CMD_OPT_QUIT,				"設定キャンセル → □システム")
	};
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private StreamControlState stmStat = null;
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public SetupOptionState(Console console, MfsNode mfs)
	{
		super(console, mfs);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void setStateInstance(
		SystemCommandState sysStat,
		StreamControlState stmStat
	) {
		this.sysStat = sysStat;
		this.stmStat = stmStat;
	}
	
//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------

	//- PROTECTED METHOD -------------------------------------------------------
	//
	@Override
	protected String getPrompt()
	{
		return STR_OPTION_PROMPT;
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	@Override
	protected ConsoleState execute()
	{
		ConsoleState nextStat = this;
		
		int n = getCommandNumber(CMD_TITLE, CMD_DESCS);
		switch (n) {
		case CMD_OPT_SHOW_SEGIO			: showSegmentIo();			break;
		case CMD_OPT_SET_PREVIEWED		: setPreviewed();			break;
		case CMD_OPT_SET_DESCRIPTION	: setDescription();			break;
		case CMD_OPT_INSERT_VID_EFF		: setVideoEffector();		break;
		case CMD_OPT_INSERT_AUD_EFF		: setAudioEffector();		break;
		case CMD_OPT_NEW_STREAM			: nextStat = newStream();	break;
		case CMD_OPT_QUIT				: nextStat = quit();		break;
		default							: warningPause("コマンドをキャンセルしました．\n");
										  nextStat = this;			break;
		}
		return nextStat;
	}

//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	ConsoleState newStream()
	{
		// ストリーム生成処理が正常に行われた場合，次の状態は StreamControl 状態．		
		ConsoleState nextStat = this.stmStat;
		
		//----------------------------------------------------------------------
		SegmentIo segIo = this.console.getSegmentIo();

		//----------------------------------------------------------------------
		// ストリームとビューワを生成する．
		StreamPerformer pfmr = null;
		try {
			pfmr = StreamPerformer.newInstance(segIo);
				// SystemException, StreamException

			StreamViewer viewer = new StreamViewer();
			viewer.setPerformer(pfmr);
			viewer.setVisible(true);
		}
		catch (SystemException | StreamException ex) {
			warning("ストリームを生成できません(%s)．\n", ex.getMessage());
			showSegmentIo();
			// ストリーム生成時にエラー発生．
			// 次の状態は SystemCommand 状態．
			nextStat = this.sysStat;
			return nextStat;
		}
		//----------------------------------------------------------------------
		// ストリームの処理を開始する．
		try {
			pfmr.open();	// StreamException
			pfmr.start();	// StreamException
		}
		catch (StreamException ex) {
			warning("入出力処理を開始できません(%s)．\n", ex.getMessage());
			showSegmentIo();
			// 生成したストリームの処理開始時にエラー発生．
			// 次の状態は SystemCommand 状態．
			nextStat = this.sysStat;
			return nextStat;
		}
		//----------------------------------------------------------------------
		// 生成したストリームの処理が開始された．
		// 生成したストリームのインスタンスを，ExternalConsole に設定しておく．
		this.console.setSelectedActiveStream(pfmr);

		// 次の状態を返す．		
		return nextStat;
	}	

//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setPreviewed()
	{
		boolean useViewer = false;

		printListTitle("プレビュー ON/OFF");
		print("  [0] ON\n");
		print("  [1] OFF\n");

		int nNum = selectNumber("番号");
		switch (nNum) {
		case 0 :	useViewer = true;	break;
		case 1 :	useViewer = false;	break;
		default :	warningPause("コマンドをキャンセルしました．プレビューをONに設定します．\n");
					useViewer = true;	break;
		}

		SegmentIo segIo = this.console.getSegmentIo();
		if (useViewer) {
			segIo.setPreviewer();
		}
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setDescription()
	{
		String desc = getLine("デスクリプション");
		SegmentIo segIo = this.console.getSegmentIo();
		segIo.setDescription(desc);
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setVideoEffector()
	{
		String fltName = getLine("ビデオフィルタ名");
		SegmentIo segIo = this.console.getSegmentIo();
		segIo.setVideoEffector(fltName);
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setAudioEffector()
	{
		String fltName = getLine("オーディオフィルタ名");
		SegmentIo segIo = this.console.getSegmentIo();
		segIo.setAudioEffector(fltName);
	}	
}