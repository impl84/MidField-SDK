
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
		CMD_TITLE = "���V�X�e�� �����͐ݒ� ���o�͐ݒ� ���I�v�V�����ݒ� ���X�g���[������";
	
	private static final CommandDesc[] CMD_DESCS = new CommandDesc[] {
		new CommandDesc(CMD_OUT_SHOW_SEGIO,		"���o�͐ݒ�\��"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_OUT_SET_VIEWER,		"�Đ�/�\���ݒ�"),
		new CommandDesc(CMD_OUT_SET_LIVE_STM,	"���M�X�g���[���ݒ�"),
		new CommandDesc(CMD_OUT_SET_MEDIA_FILE,	"���f�B�A�t�@�C���ݒ�"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_OUT_NEW_STREAM,		"�X�g���[������ �� ���X�g���[������"),
		new CommandDesc(CMD_OUT_SETUP_OPTION,	"�I�v�V�����ݒ� �� ���I�v�V�����ݒ�"),
		new CommandDesc(CMD_OUT_QUIT,			"�ݒ�L�����Z�� �� ���V�X�e��")
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
		default						: warningPause("�R�}���h���L�����Z�����܂����D\n");
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
		// ��SegmentIo�փr���[����ݒ�
		//----------------------------------------------------------------------
		segIo.configureDefaultRenderer();

		//----------------------------------------------------------------------
		// ���C�u�\�[�X�I�v�V������L���ɂ���D
		segIo.setLiveSource(true);
		
		//----------------------------------------------------------------------
		// SegmentIo ��\������D
		showSegmentIo();
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setLiveStream()
	{
		SegmentIo segIo = this.console.getSegmentIo();

		//----------------------------------------------------------------------
		// ���o�̓t�H�[�}�b�g�̑I��
		//----------------------------------------------------------------------
		// �o�̓t�H�[�}�b�g��񃊃X�g���擾����D
		List<StreamFormat> lsOutFmt = segIo.getOutputVideoFormatList();
		lsOutFmt.addAll(segIo.getOutputAudioFormatList());
		int size = lsOutFmt.size();
		if (size <= 0) {
			warningPause("���p�\�ȏo�̓t�H�[�}�b�g������܂���D\n");
			return;
		}
		//----------------------------------------------------------------------
		// �o�̓t�H�[�}�b�g��񃊃X�g�̊e�v�f��\������D
		printStreamFormatList(lsOutFmt);
		
		// �o�̓t�H�[�}�b�g��񃊃X�g�Ƀr�f�I�t�H�[�}�b�g���܂܂�Ă���ꍇ
		// �r�f�I�t�H�[�}�b�g��I������D
		VideoFormat vidFmt = selectVideoFormat(lsOutFmt);
		
		// �o�̓t�H�[�}�b�g��񃊃X�g�ɃI�[�f�B�I�t�H�[�}�b�g���܂܂�Ă���ꍇ
		// �I�[�f�B�I�t�H�[�}�b�g��I������D
		// ���I���ς݂̃r�f�I�t�H�[�}�b�g�� DV/M2TS �̏ꍇ�́C
		//   �I�[�f�B�I�t�H�[�}�b�g��I�����Ȃ��D
		AudioFormat audFmt = selectAudioFormat(vidFmt, lsOutFmt);
		if ((vidFmt == null) && (audFmt == null)) {
			return;
		}
		//----------------------------------------------------------------------
		// ���g�����X�|�[�g�v���g�R���̑I��(TCP/UDP)
		//----------------------------------------------------------------------
		boolean useTCP = selectTransportProtocol();

		//----------------------------------------------------------------------
		// ���ڑ��̕�����I��(TCP���p���̂�)
		//----------------------------------------------------------------------
		boolean isRcvCon = false;
		if (useTCP) {
			isRcvCon = selectConnectionDirection();
		}
		//----------------------------------------------------------------------
		// �����M�X�g���[���p IoParam ��SegmentIo�֒ǉ�
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
		// SegmentIo ��\������D
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
			// ���p�\�ȃr�f�I�t�H�[�}�b�g�������D
			return vidFmt;
		}
		//----------------------------------------------------------------------
		int size = lsOutFmt.size();
		int num = selectNumber("�r�f�I�t�H�[�}�b�g�ԍ�");
		if (num >= size) {
			warningPause("�R�}���h���L�����Z�����܂����D\n");
			return vidFmt;
		}
		//----------------------------------------------------------------------
		// �I�������o�̓t�H�[�}�b�g�����擾����D
		StreamFormat stmFtm = lsOutFmt.get(num);
		if (stmFtm instanceof VideoFormat) {
			vidFmt = (VideoFormat)stmFtm;
		}
		else {
			warningPause("�r�f�I�t�H�[�}�b�g�ł͂���܂���D\n");
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
				// ���I���ς݂̃r�f�I�t�H�[�}�b�g�� DV/M2TS �̏ꍇ�́C
				//   �I�[�f�B�I�t�H�[�}�b�g��I�����Ȃ��D
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
			// ���p�\�ȃI�[�f�B�I�t�H�[�}�b�g�������D
			return audFmt;
		}
		//----------------------------------------------------------------------
		int size = lsOutFmt.size();		
		int num = selectNumber("�I�[�f�B�I�t�H�[�}�b�g�ԍ�");
		if (num >= size) {
			warningPause("�R�}���h���L�����Z�����܂����D\n");
			return audFmt;
		}
		//----------------------------------------------------------------------
		// �I�������o�̓t�H�[�}�b�g�����擾����D
		StreamFormat stmFtm = lsOutFmt.get(num);
		if (stmFtm instanceof AudioFormat) {
			audFmt = (AudioFormat)stmFtm;
		}
		else {
			warningPause("�I�[�f�B�I�t�H�[�}�b�g�ł͂���܂���D\n");
		}
		return audFmt;
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private boolean selectTransportProtocol()
	{
		boolean useTCP = false;

		printListTitle("�X�g���[���]���v���g�R��");
		print("  [0] MFSP/TCP\n");
		print("  [1] MFSP/UDP\n");

		int num = selectNumber("�v���g�R���ԍ�");
		switch (num) {
		case 0 :	useTCP = true;		break;
		case 1 :	useTCP = false;	break;
		default :	warningPause("�R�}���h���L�����Z�����܂����DUDP�𗘗p���܂��D\n");
					useTCP = false;	break;
		}
		return useTCP;
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private boolean selectConnectionDirection()
	{
		boolean isRcvCon = false;

		printListTitle("�R�l�N�V�����ڑ�����");
		print("  [0] ��M������ڑ���������\n");
		print("  [1] ���M������ڑ���������\n");

		int num = selectNumber("�ԍ�");
		switch (num) {
		case 0 :	isRcvCon = true;	break;
		case 1 :	isRcvCon = false;	break;
		default :	warningPause("�R�}���h���L�����Z�����܂����D��M������ڑ��������܂��D\n");
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
		// ���o�̓t�@�C���ƃt�H�[�}�b�g�̐ݒ�
		//----------------------------------------------------------------------
		// �o�̓t�@�C�������擾����D
		print("\n");
		String mediaFile = getLine("�o�̓t�@�C����");

		// ���f�B�A�t�H�[�}�b�g���擾����D
		IoFormat ioFmt = IoFormat.UNKNOWN;
		ioFmt = selectMediaFormat();
		if (ioFmt == IoFormat.UNKNOWN) {
			warningPause("�R�}���h���L�����Z�����܂����D\n");
			return;
		}
		//----------------------------------------------------------------------
		// ���f�B�A�t�H�[�}�b�g�� WMV/WMA �̏ꍇ�C�v���t�@�C�������擾����D
		String wmpName = null;
		if ((ioFmt == IoFormat.WMV) || (ioFmt == IoFormat.WMA)) {
			wmpName = getLine("Windows Media �v���t�@�C����");
		}
		//----------------------------------------------------------------------
		// ���o�̓t�@�C���p IoParam ��SegmentIo�֒ǉ�
		//----------------------------------------------------------------------
		segIo.configureSinkFile(mediaFile, ioFmt, wmpName);

		//----------------------------------------------------------------------
		// SegmentIo ��\������D
		showSegmentIo();
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private IoFormat selectMediaFormat()
	{
		IoFormat ioFmt = IoFormat.UNKNOWN;

		printListTitle("���f�B�A�t�H�[�}�b�g");
		print("  [0] DVSD\n");
		print("  [1] M2TS\n");
		print("  [2] WMV\n");
		print("  [3] WMA\n");
		int num = selectNumber("���f�B�A�t�H�[�}�b�g�ԍ�");
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
		// �v���r���[�ݒ�
		SegmentIo segIo = this.console.getSegmentIo();
		segIo.setPreviewer();

		// �X�g���[���𐶐���CSetupOptionState �֑J��
		ConsoleState conStat = this.optStat.newStream();
		return conStat;
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private ConsoleState setupOption()
	{
		// �v���r���[�ݒ�
		SegmentIo segIo = this.console.getSegmentIo();
		segIo.setPreviewer();

		// SetupOptionState �֑J��		
		return this.optStat;
	}	
}