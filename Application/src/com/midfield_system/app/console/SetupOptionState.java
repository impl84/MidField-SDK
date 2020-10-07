
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
		CMD_TITLE = "���V�X�e�� �����͐ݒ� ���o�͐ݒ� ���I�v�V�����ݒ� ���X�g���[������";
	
	private static final CommandDesc[] CMD_DESCS = new CommandDesc[] {
		new CommandDesc(CMD_OPT_SHOW_SEGIO,			"���o�͐ݒ�\��"),
		new CommandDesc(CMD_SEPARATOR,				""),
		new CommandDesc(CMD_OPT_SET_PREVIEWED,		"�v���r���[ ON/OFF"),
		new CommandDesc(CMD_OPT_SET_DESCRIPTION,	"�f�X�N���v�V�����ݒ�"),
		new CommandDesc(CMD_OPT_INSERT_VID_EFF,		"�r�f�I�G�t�F�N�^�ݒ�"),
		new CommandDesc(CMD_OPT_INSERT_AUD_EFF,		"�I�[�f�B�I�G�t�F�N�^�ݒ�"),
		new CommandDesc(CMD_SEPARATOR,				""),
		new CommandDesc(CMD_OPT_NEW_STREAM,			"�X�g���[������ �� ���X�g���[������"),
		new CommandDesc(CMD_OPT_QUIT,				"�ݒ�L�����Z�� �� ���V�X�e��")
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
		default							: warningPause("�R�}���h���L�����Z�����܂����D\n");
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
		// �X�g���[����������������ɍs��ꂽ�ꍇ�C���̏�Ԃ� StreamControl ��ԁD		
		ConsoleState nextStat = this.stmStat;
		
		//----------------------------------------------------------------------
		SegmentIo segIo = this.console.getSegmentIo();

		//----------------------------------------------------------------------
		// �X�g���[���ƃr���[���𐶐�����D
		StreamPerformer pfmr = null;
		try {
			pfmr = StreamPerformer.newInstance(segIo);
				// SystemException, StreamException

			StreamViewer viewer = new StreamViewer();
			viewer.setPerformer(pfmr);
			viewer.setVisible(true);
		}
		catch (SystemException | StreamException ex) {
			warning("�X�g���[���𐶐��ł��܂���(%s)�D\n", ex.getMessage());
			showSegmentIo();
			// �X�g���[���������ɃG���[�����D
			// ���̏�Ԃ� SystemCommand ��ԁD
			nextStat = this.sysStat;
			return nextStat;
		}
		//----------------------------------------------------------------------
		// �X�g���[���̏������J�n����D
		try {
			pfmr.open();	// StreamException
			pfmr.start();	// StreamException
		}
		catch (StreamException ex) {
			warning("���o�͏������J�n�ł��܂���(%s)�D\n", ex.getMessage());
			showSegmentIo();
			// ���������X�g���[���̏����J�n���ɃG���[�����D
			// ���̏�Ԃ� SystemCommand ��ԁD
			nextStat = this.sysStat;
			return nextStat;
		}
		//----------------------------------------------------------------------
		// ���������X�g���[���̏������J�n���ꂽ�D
		// ���������X�g���[���̃C���X�^���X���CExternalConsole �ɐݒ肵�Ă����D
		this.console.setSelectedActiveStream(pfmr);

		// ���̏�Ԃ�Ԃ��D		
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

		printListTitle("�v���r���[ ON/OFF");
		print("  [0] ON\n");
		print("  [1] OFF\n");

		int nNum = selectNumber("�ԍ�");
		switch (nNum) {
		case 0 :	useViewer = true;	break;
		case 1 :	useViewer = false;	break;
		default :	warningPause("�R�}���h���L�����Z�����܂����D�v���r���[��ON�ɐݒ肵�܂��D\n");
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
		String desc = getLine("�f�X�N���v�V����");
		SegmentIo segIo = this.console.getSegmentIo();
		segIo.setDescription(desc);
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setVideoEffector()
	{
		String fltName = getLine("�r�f�I�t�B���^��");
		SegmentIo segIo = this.console.getSegmentIo();
		segIo.setVideoEffector(fltName);
	}	
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setAudioEffector()
	{
		String fltName = getLine("�I�[�f�B�I�t�B���^��");
		SegmentIo segIo = this.console.getSegmentIo();
		segIo.setAudioEffector(fltName);
	}	
}