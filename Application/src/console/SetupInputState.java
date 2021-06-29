
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
		CMD_TITLE = "���V�X�e�� �����͐ݒ� ���o�͐ݒ� ���I�v�V�����ݒ� ���X�g���[������";
	
	private static final CommandDesc[] CMD_DESCS = new CommandDesc[] {
		new CommandDesc(CMD_IN_SHOW_SEGIO,		"���o�͐ݒ�\��"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_IN_ADD_CAP_DEV,		"���̓f�o�C�X�ǉ�"),
		new CommandDesc(CMD_IN_ADD_LIVE_STM,	"��M�X�g���[���ǉ�"),
		new CommandDesc(CMD_IN_ADD_MEDIA_FILE,	"���f�B�A�t�@�C���ǉ�"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_IN_GATHER_LIVE_STM,	"��M�X�g���[�������W"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_IN_SETUP_OUTPUT,	"�o�͐ݒ�       �� ���o�͐ݒ�"),
		new CommandDesc(CMD_IN_QUIT,			"�ݒ�L�����Z�� �� ���V�X�e��")
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
	private void addCaptureDevice()
	{
		SegmentIo segIo = this.console.getSegmentIo();

		//----------------------------------------------------------------------
		// �����̓f�o�C�X�̑I��
		//----------------------------------------------------------------------
		// ���̓f�o�C�X��񃊃X�g���擾����D
		DeviceInfoManager devMgr = DeviceInfoManager.getInstance();
		List<DeviceInfo> lsDevInf = devMgr.getDeviceInfoList();
		int size = lsDevInf.size();
		if (size <= 0) {
			warningPause("���p�\�ȓ��̓f�o�C�X������܂���D\n");
			return;
		}
		//----------------------------------------------------------------------
		// ���̓f�o�C�X��񃊃X�g�̊e�v�f��\������D
		printDeviceInfoList(lsDevInf);

		// ���̓f�o�C�X��񃊃X�g�̗v�f�ԍ���I������D
		int num = selectNumber("���̓f�o�C�X�ԍ�");
		if (num >= size) {
			warningPause("�R�}���h���L�����Z�����܂����D\n");
			return;
		}
		//----------------------------------------------------------------------
		// �I���������̓f�o�C�X�����擾����D
		DeviceInfo devInf = lsDevInf.get(num);

		//----------------------------------------------------------------------
		// ���o�̓t�H�[�}�b�g�̑I��
		//----------------------------------------------------------------------
		// �o�̓t�H�[�}�b�g��񃊃X�g���擾����D
		List<StreamFormat> lsStmFmt = devInf.getOutputFormatList();
		size = lsStmFmt.size();
		if (size <= 0) {
			warningPause("���p�\�ȏo�̓t�H�[�}�b�g������܂���D\n");
			return;
		}
		//----------------------------------------------------------------------
		// �o�̓t�H�[�}�b�g��񃊃X�g�̊e�v�f��\������D
		printStreamFormatList(lsStmFmt);

		// �o�̓t�H�[�}�b�g��񃊃X�g�̗v�f�ԍ���I������D
		num = selectNumber("�t�H�[�}�b�g�ԍ�");
		if (num >= size) {
			warningPause("�R�}���h���L�����Z�����܂����D\n");
			return;
		}
		//----------------------------------------------------------------------
		// �I�������o�̓t�H�[�}�b�g�����擾����D
		StreamFormat stmFmt = lsStmFmt.get(num);

		//----------------------------------------------------------------------
		// �����̓f�o�C�X�E�o�̓t�H�[�}�b�g���� SegmentIo �֒ǉ�
		//----------------------------------------------------------------------
		segIo.addInputDevice(devInf, stmFmt);

		//----------------------------------------------------------------------
		// �����݂� SegmentIo ��\������D
		showSegmentIo();
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void addLiveStream()
	{
		SegmentIo segIo = this.console.getSegmentIo();

		//----------------------------------------------------------------------
		// ����M�X�g���[���̑I��
		//----------------------------------------------------------------------
		// ��M�X�g���[�����̃��X�g���擾����D
		StreamInfoManager manager = StreamInfoManager.getInstance();
		List<StreamInfo> lsOutInf = manager.getSourceStreamInfoList();
		int size = lsOutInf.size();
		if (size <= 0) {
			warningPause("��M�\�ȃX�g���[��������܂���D\n");
			return;
		}
		//----------------------------------------------------------------------
		// ��M�X�g���[�����̔z��̊e�v�f��\������D
		printOutputInformationList("��M�X�g���[���ꗗ", lsOutInf);

		// ��M�X�g���[�����̔z��v�f�ԍ���I������D
		int num = selectNumber("��M�X�g���[���ԍ�");
		if (num >= size) {
			warningPause("�R�}���h���L�����Z�����܂����D\n");
			return;
		}
		//----------------------------------------------------------------------
		List<IoParam> lsInPrm = lsOutInf.get(num).restoreIoParamList();
		IoParam inPrm = lsInPrm.get(0);

/*
		//----------------------------------------------------------------------
		// ���}���`�L���X�g�Ŏ�M����ꍇ�̐ݒ�
		// �i����M�X�g���[����UDP�𗘗p���Ă���ꍇ�̂݁j
		//----------------------------------------------------------------------
		String mcAddr = null;
		String xportProt = inPrm.getTransportProtocol();
		if (xportProt.equals("UDP")) {
			String strBuf = getLine("�}���`�L���X�g�𗘗p���܂����H[Y/N]");
			if (strBuf.startsWith("Y") || strBuf.startsWith("y")) {
				// �}���`�L���X�g�𗘗p����ꍇ
				mcAddr = getLine("�}���`�L���X�g�A�h���X");
			}
		}
*/		
		//----------------------------------------------------------------------
		// ����M�X�g���[������ SegmentIo �֒ǉ�
		//----------------------------------------------------------------------
		segIo.addIncomingStream(inPrm);
		//----------------------------------------------------------------------
		// �����݂� SegmentIo ��\������D
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
		// �����f�B�A�t�@�C�����ƃt�H�[�}�b�g���̎擾
		//----------------------------------------------------------------------
		// ���f�B�A�t�@�C�������擾����D
		print("\n");
		String mediaFile = getLine("���f�B�A�t�@�C����");

		// �t�H�[�}�b�g�����擾����D
		vidFmt = VideoFormat.getFileFormat(mediaFile);
		audFmt = AudioFormat.getFileFormat(mediaFile);
		if ((vidFmt == null) && (audFmt == null)) {
			warningPause("���Ή��̃t�@�C���t�H�[�}�b�g�ł��D\n");
			return;
		}
		//----------------------------------------------------------------------
		// �����f�B�A�t�@�C����SegmentIo�֒ǉ�
		//----------------------------------------------------------------------
		segIo.configureSourceFile(mediaFile);

		//----------------------------------------------------------------------
		// ���f�B�A�t�@�C���̃t�H�[�}�b�g����\������D
		if (vidFmt != null) {
			print("  %s\n", vidFmt.toString());
		}
		if (audFmt != null) {
			print("  %s\n", audFmt.toString());
		}
		//----------------------------------------------------------------------
		// �����݂� SegmentIo ��\������D
		showSegmentIo();
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void refreshSourcePerformerInformation()
	{
		// �\�[�X�z�X�g����ݒ肷��D
		// ���X�y�[�X��؂�ŕ����w���
		print("\n");
		message("���M�z�X�g��/IP�A�h���X����͂��ĉ������D\n");
		print("  �����w�肷��ꍇ�̓X�y�[�X�ŋ�؂��ē��͂��ĉ������D\n");
		String buf = getLine("���M�z�X�g��/IP�A�h���X");
		String[] srcAddrs = buf.split("\\s+");

		StreamInfoManager manager = StreamInfoManager.getInstance();		
		manager.refreshSourceStreamInfoList(srcAddrs);

		// �e�X�����M���Ă����M�X�g���[������ݒ肳�ꂽ
		// �S�Ẵ\�[�X�z�X�g������W���邽�߂̗v���p�P�b�g�𑗐M����D
		messagePause("��M�X�g���[�����v���p�P�b�g�𑗐M���܂��D\n");
		manager.refreshSourceStreamInfoList();
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private ConsoleState setupOutput()
	{
		// SetupOutputState �֑J��	
		return this.outStat;
	}
}
