
package com.midfield_system.app.console;

import java.util.List;

import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.system.MessageConnectionStatus;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.protocol.ResourceStatus;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: SystemCommandState
 *
 * Date Modified: 2020.08.14
 *
 */

//==============================================================================
public class SystemCommandState
	extends		ConsoleState
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String STR_SYSTEM_PROMPT = "sys";

	private static final int
		CMD_SYS_SETUP_STM		= 0,
		CMD_SYS_SELECT_STM		= 1,

		CMD_SYS_SHOW_STM_LIST	= 2,
		CMD_SYS_CON_SOCK_LIST	= 3,
		CMD_SYS_RESOURCES		= 4,

		CMD_SYS_EXIT			= 9;
	
	private static final String
		CMD_TITLE = "���V�X�e�� �����͐ݒ� ���o�͐ݒ� ���I�v�V�����ݒ� ���X�g���[������";
	
	private static final CommandDesc[] CMD_DESCS = new CommandDesc[] {
		new CommandDesc(CMD_SYS_SETUP_STM,		"�X�g���[�����o�͐ݒ� �� �����͐ݒ�"),
		new CommandDesc(CMD_SYS_SELECT_STM,		"�X�g���[���I��       �� ���X�g���[������"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_SYS_SHOW_STM_LIST,	"�X�g���[���ꗗ�\��"),
		new CommandDesc(CMD_SYS_CON_SOCK_LIST,	"���b�Z�[�W�R�l�N�V�����ꗗ�\��"),
		new CommandDesc(CMD_SYS_RESOURCES,		"CPU���p�󋵕\��"),
		new CommandDesc(CMD_SEPARATOR,			""),
		new CommandDesc(CMD_SYS_EXIT,			"�I��")
	};
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private SetupInputState inStat = null;
	private StreamControlState stmStat = null;	
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public SystemCommandState(Console console, MfsNode mfs)
	{
		super(console, mfs);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void setStateInstance(SetupInputState inStat, StreamControlState stmStat)
	{
		this.inStat = inStat;
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
		return STR_SYSTEM_PROMPT;
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	@Override
	protected ConsoleState execute()
	{
		ConsoleState nextStat = this;

		int n = getCommandNumber(CMD_TITLE, CMD_DESCS);
		switch (n) {
		case CMD_SYS_SETUP_STM		: nextStat = setupStream();		break;
		case CMD_SYS_SELECT_STM		: nextStat = selectStream();	break;
		case CMD_SYS_SHOW_STM_LIST	: showStreamList();				break;
		case CMD_SYS_CON_SOCK_LIST	: showMsgConList();			break;
		case CMD_SYS_RESOURCES		: getResourceStatus();			break;
		case CMD_SYS_EXIT			: nextStat = null;				break;		
		default						: warningPause("�R�}���h���L�����Z�����܂����D\n");
									  nextStat = this;				break;
		}
		return nextStat;
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private ConsoleState setupStream()
	{
		SegmentIo segIo = this.console.getSegmentIo();
		segIo.removeInputParams();
		segIo.removeOutputParams();
		
		// SetupInputState �֑J��		
		return this.inStat;
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private ConsoleState selectStream()
	{
		ConsoleState nextStat = this.stmStat;
						// �K�؂ȃX�g���[�����I�����ꂽ�ꍇ�C
						// StreamControlState �֑J�ڂ���D

		List<StreamPerformer> lsMFStm = StreamPerformer.getStreamPerformerList();
		int size = lsMFStm.size();
		if (size > 0) {
			printStreamList(lsMFStm);
			int num = selectNumber("�X�g���[���ԍ�");
			if (num < size) {
				StreamPerformer pfmr = lsMFStm.get(num);
				this.console.setSelectedActiveStream(pfmr);
			}
			else {
				warningPause("�R�}���h���L�����Z�����܂����D\n");
				// ���� SystemCommandState
				nextStat = this;
			}
		}
		else {
			warningPause("�I���ł���X�g���[��������܂���D\n");
			// ���� SystemCommandState
			nextStat = this;
		}
		// ���̏�Ԃ�Ԃ��D
		return nextStat;
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void printStreamList(List<StreamPerformer> lsMFStm)
	{
		int size = lsMFStm.size();
		for (int i = 0; i < size; i++) {
			StreamPerformer pfmr = lsMFStm.get(i);
			SegmentIo segIo = pfmr.getSegmentIo();

			print("\n");
			print("���X�g���[��[%s] �`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`\n",
				Integer.toString(i)
			);
			printIoParamList("���͐ݒ�", segIo.getInputParamList());
			printIoParamList("�o�͐ݒ�", segIo.getOutputParamList());
			print("�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`�`\n");
		}
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void showStreamList()
	{
		List<StreamPerformer> lsMFStm = StreamPerformer.getStreamPerformerList();
		int size = lsMFStm.size();
		if (size <= 0) {
			warningPause("�X�g���[��������܂���D\n");
			return;
		}
		//----------------------------------------------------------------------
		printStreamList(lsMFStm);
		pause();
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void showMsgConList()
	{
		printListTitle("���b�Z�[�W�R�l�N�V�����ꗗ");
		this.mfs.getMessageConnectionStatusList();
		List<MessageConnectionStatus> lsMsgConStat = this.mfs.getMessageConnectionStatusList();
		int size = lsMsgConStat.size();
		if (size <= 0) {
			warningPause("�ڑ����̃��b�Z�[�W�R�l�N�V����������܂���D\n");
			return;
		}
		//----------------------------------------------------------------------
		for (MessageConnectionStatus stat : lsMsgConStat) {
			print("  %s\n", stat.toString());
		}
		pause();
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void getResourceStatus()
	{
		ResourceStatus resStat = this.mfs.getResourceStatus();
		double fCPURate = resStat.getCpuUsage();
		
		//--------------------------------------------------------------------------
		if (fCPURate >= 0.0) {
			print("  CPU���p���F%s[��]\n", Double.toString(fCPURate));
		}
		//--------------------------------------------------------------------------
		if (fCPURate < 0.0) {
			warning("CPU���p�����擾�ł��܂���D\n");
		}
		//--------------------------------------------------------------------------
		pause();
	}
}