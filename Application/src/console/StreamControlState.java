
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
		CMD_TITLE = "���V�X�e�� �����͐ݒ� ���o�͐ݒ� ���I�v�V�����ݒ� ���X�g���[������";
	
	private static final CommandDesc[] CMD_DESCS_MAIN = new CommandDesc[] {
		new CommandDesc(CMD_STM_SHOW_SEGIO,	"���o�͐ݒ�\��"),
		new CommandDesc(CMD_SEPARATOR,		""),
		new CommandDesc(CMD_STM_START,		"���o�͏����J�n/�ĊJ"),
		new CommandDesc(CMD_STM_STOP,		"���o�͏�����~"),
		new CommandDesc(CMD_STM_DELETE,		"�X�g���[���폜"),
		new CommandDesc(CMD_SEPARATOR,		""),
		new CommandDesc(CMD_STM_RENDERER,	"�� ���Đ�/�\������i�X�g���[������j"),
		new CommandDesc(CMD_STM_QUIT,		"�� ���V�X�e��")
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
		CMD_TITLE_RND = "���V�X�e�� �����͐ݒ� ���o�͐ݒ� ���I�v�V�����ݒ� ���Đ�/�\������";	

	private static final CommandDesc[] CMD_DESCS_RND = new CommandDesc[] {
		new CommandDesc(CMD_RND_IS_SEEKABLE,	"�V�[�N�@�\ ���p��/�s�� �m�F"),
		new CommandDesc(CMD_RND_TOTAL_TIME,		"���f�B�A�f�[�^�̃g�[�^�����Ԏ擾"),
		new CommandDesc(CMD_RND_GET_POS,		"�Đ�/�\���ʒu�擾"),
		new CommandDesc(CMD_RND_SET_POS,		"�Đ�/�\���ʒu�ݒ�"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_RND_HAS_VID_SRC,	"�r�f�I�\�[�X �L/�� �m�F"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_RND_HAS_VID_MIXER,	"�r�f�I�~�L�V���O�@�\ ���p��/�s�� �m�F"),
		new CommandDesc(CMD_RND_VID_SRC,		"�r�f�I�\�[�X���X�g�ꗗ"),
		new CommandDesc(CMD_RND_SET_MAIN_VID,	"���C���r�f�I�ݒ�"),
		new CommandDesc(CMD_RND_SET_SUB_ALPHA,	"�r�f�I�~�L�V���O�{�b�N�X�����x�ݒ�"),
		new CommandDesc(CMD_RND_SET_SUB_TYPE,	"�r�f�I�~�L�V���O�{�b�N�X�z�u�^�C�v�ݒ�"),
		new CommandDesc(CMD_RND_SET_SUB_SIZE,	"�r�f�I�~�L�V���O�{�b�N�X�T�C�Y�ݒ�"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_RND_HAS_AUD_SRC,	"�I�[�f�B�I�\�[�X �L/�� �m�F"),
		new CommandDesc(CMD_RND_GET_AUD_VOL,	"�I�[�f�B�I�{�����[���擾"),
		new CommandDesc(CMD_RND_SET_AUD_VOL,	"�I�[�f�B�I�{�����[���ݒ�"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_RND_HAS_AUD_MIXER,	"�I�[�f�B�I�~�L�V���O�@�\ ���p��/�s�� �m�F"),
		new CommandDesc(CMD_RND_AUD_SRC,		"�I�[�f�B�I�\�[�X���X�g�ꗗ"),
		new CommandDesc(CMD_RND_SET_AUD_MVOL,	"�~�L�V���O�I�[�f�B�I�{�����[���ݒ�"),
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
		default					: warningPause("�R�}���h���L�����Z�����܂����D\n");
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
			warningPause("���o�͏������J�n�ł��܂���(%s)�D\n", ex.getMessage());
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
			warningPause("���o�͏������~�ł��܂���(%s)�D\n", ex.getMessage());
		}
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private ConsoleState deleteStream()
	{
		this.pfmr.delete();

		// SystemCommandState �֑J��
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
									  warningPause("�R�}���h���L�����Z�����܂����D\n");
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
			messagePause("�V�[�N�@�\ ���p��\n");
		}
		else {
			messagePause("�V�[�N�@�\ ���p�s��\n");
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndTotalTime()
	{
		long total = this.pfmrCtlr.getMediaTotalTime();
		messagePause("���f�B�A�f�[�^�̃g�[�^�����ԁF%s[msec]\n", Long.toString(total));
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndGetPosision()
	{
		long pos = this.pfmrCtlr.getMediaPosition();
		messagePause("�Đ�/�\���ʒu�F%s[msec]\n", Long.toString(pos));
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndSetPosision()
	{
		String strPos = getLine("�Đ�/�\���ʒu[msec]");
		this.pfmrCtlr.setMediaPosition(Long.parseLong(strPos));
	}

//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndHasRenderableVideo()
	{
		boolean has = this.pfmrCtlr.hasRenderableVideo();
		if (has) {
			messagePause("�r�f�I�\�[�X �L\n");
		}
		else {
			messagePause("�r�f�I�\�[�X ��\n");
		}
	}

//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndIsVideoMixerSource()
	{
		boolean has = this.pfmrCtlr.isVideoMixerSource();
		if (has) {
			messagePause("�r�f�I�~�L�V���O�@�\ ���p��\n");
		}
		else {
			messagePause("�r�f�I�~�L�V���O�@�\ ���p�s��\n");
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndShowVideoSourceList()
	{
/*		
		List<IoParam> lsIOMap = this.pfmrCtlr.getVideoSourceList();
		printIoParamList("�r�f�I�\�[�X���X�g�ꗗ", lsIOMap);
*/		
print("�r�f�I�\�[�X���X�g�ꗗ (������ 2009/10/09)");
		pause();
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndSetMainVideo()
	{
/*		
		String strNo = getLine("���C���r�f�I �\�[�X�ԍ�");
		this.pfmrCtlr.setVideoMixerTile(Integer.parseInt(strNo), null);
*/		
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndSetSubAlpha()
	{
		String strAlpha = getLine("�r�f�I�~�L�V���O�{�b�N�X�����x[0.0-1.0]");
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

		printListTitle("�r�f�I�~�L�V���O�{�b�N�X�̔z�u");
		print("  [0] ����\n");
		print("  [1] ����\n");

		int num = selectNumber("�z�u�ԍ�");
		switch (num) {
		case 0 :	type = PerformerController.TYPE_HORIZONTAL;	break;
		case 1 :	type = PerformerController.TYPE_VERTICAL;		break;
		default :	warningPause("�R�}���h���L�����Z�����܂����D�����ɔz�u���܂��D\n");
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
		String strSize = getLine("�r�f�I�~�L�V���O�{�b�N�X�T�C�Y[0.0-1.0]");
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
			messagePause("�I�[�f�B�I�\�[�X �L\n");
		}
		else {
			messagePause("�I�[�f�B�I�\�[�X ��\n");
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndGetAudioVolume()
	{
		double vol = this.pfmrCtlr.getAudioVolume();
		messagePause("�I�[�f�B�I�{�����[���F%s\n", Double.toString(vol));
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndSetAudioVolume()
	{
		String strVol = getLine("�I�[�f�B�I�{�����[��[0.0-1.0]");
		this.pfmrCtlr.setAudioVolume(Double.parseDouble(strVol));
	}

//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndIsAudioMixerSource()
	{
		boolean  has = this.pfmrCtlr.isAudioMixerSource();
		if (has) {
			messagePause("�I�[�f�B�I�~�L�V���O�@�\ ���p��\n");
		}
		else {
			messagePause("�I�[�f�B�I�~�L�V���O�@�\ ���p�s��\n");
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndShowAudioSourceList()
	{
//		List<IoParam> lsIOMap = this.pfmrCtlr.getAudioSourceList();
//		printIoParamList("�I�[�f�B�I�\�[�X���X�g�ꗗ", lsIOMap);
print("�I�[�f�B�I�\�[�X���X�g�ꗗ (������ 2009/10/09)");
		pause();
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	void rndSetMixingAudioVolume()
	{
print("�I�[�f�B�I�{�����[���̒��� (������ 2019/03/15)");
/*
		String strNo = getLine("�I�[�f�B�I �\�[�X�ԍ�");
		String strVol = getLine("�I�[�f�B�I�{�����[��[0.0-1.0]");

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