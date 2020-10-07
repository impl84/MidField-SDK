
package com.midfield_system.app.console;

import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.system.MfsNode;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: SimpleConsole
 *
 * Date Modified: 2020.03.17
 *
 */

//==============================================================================
public class Console
{
//==============================================================================
//  CLASS METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC STATIC METHOD:
//------------------------------------------------------------------------------

	//======== [MAIN METHOD] ===================================================
	//	
	public static void main(String[] args)
	{
		Console console = new Console();
		console.exec();
	}
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	ConsoleState curStat = null;
	SegmentIo segIo = null;
	StreamPerformer pfmr = null;

//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	public Console()
	{
		this.segIo = new SegmentIo();
	}
		
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void exec()
	{
		//----------------------------------------------------------------------
		// SystemEventViewer �̐����E�\��
		//----------------------------------------------------------------------
		SystemEventViewer sysEvVwr = new SystemEventViewer();
		sysEvVwr.setVisible(true);
		
		//----------------------------------------------------------------------
		// MidField System �̏�����
		//----------------------------------------------------------------------
		MfsNode mfs = null;
		try {
			// MidField System ������������D
			mfs = MfsNode.initialize();
				// SystemException
			
			// MidField System ���N������D
			mfs.activate();
				// SystemException
		}
		catch (SystemException ex) {
			ex.printStackTrace();
			return;
		}
		//----------------------------------------------------------------------
		// ExternalConsole �̏�����
		//----------------------------------------------------------------------
		// ExternalConsole �̊e��Ԃ̃C���X�^���X����		
		SystemCommandState	sysStat = new SystemCommandState(this, mfs);
		SetupInputState		inStat  = new SetupInputState(this, mfs);
		SetupOutputState	outStat = new SetupOutputState(this, mfs);
		SetupOptionState	optStat = new SetupOptionState(this, mfs);
		StreamControlState	stmStat = new StreamControlState(this, mfs);
		
		// ExternalConsole �̊e��Ԃ̏�����
		sysStat.setStateInstance(inStat,  stmStat);
		inStat.setStateInstance( sysStat, outStat);
		outStat.setStateInstanue(sysStat, optStat, stmStat);
		optStat.setStateInstance(sysStat, stmStat);
		stmStat.setStateInstance(sysStat);
		
		// �C�x���g���X�i�o�^�ݒ�
		mfs.addSystemEventListener(sysEvVwr);
		
		//----------------------------------------------------------------------
		// �R�}���h����
		//----------------------------------------------------------------------
		// ������Ԑݒ�
		this.curStat = sysStat;
		System.out.printf("\n\n��ExternalConsole (MidField System �p�e�X�g�v���O����)\n\n");
		// �R�}���h��������я�ԑJ�ڃ��[�v
		while (this.curStat != null) {
			this.curStat = this.curStat.execute();
		}
		//----------------------------------------------------------------------
		// MidField System �̏I������
		//----------------------------------------------------------------------
		// �C�x���g���X�i�ݒ�����CMidField System �̒�~�ƃC���X�^���X�̍폜		
		mfs.removeSystemEventListener(sysEvVwr);
		mfs.shutdown();

		//----------------------------------------------------------------------
		// SystemEventViewer �̏I������
		//----------------------------------------------------------------------
		sysEvVwr.dispose();
	}

//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------

	//- PACKAGE METHOD ---------------------------------------------------------
	//
	SegmentIo getSegmentIo()
	{
		return this.segIo;
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void setSelectedActiveStream(StreamPerformer pfmr)
	{
		this.pfmr = pfmr;
	}

	//- PACKAGE METHOD ---------------------------------------------------------
	//
	StreamPerformer getSelectedActiveStream()
	{
		return this.pfmr;
	}
}
