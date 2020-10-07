
package com.midfield_system.app.performer.ex1;

import java.io.IOException;

import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamException;
import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.viewer.VideoCanvas;
import com.midfield_system.app.util.SimpleViewer;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: AbstractSampleCode
 *
 * Date Modified: 2020.10.02
 *
 */

//==============================================================================
public abstract class AbstractSampleCode
{
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PROTECTED VARIABLE -----------------------------------------------------
	
	// ���̃N���X���g�������N���X���ŗ��p�������o�͍\���c�[��
	protected ConfigTool cfgTool = null;
	
	//- PRIVATE VARIABLE -------------------------------------------------------
	
	// StreamPerformer 
	private StreamPerformer pfmr = null;
	
	// StreamPerformer ���f���\���ɗ��p���� VideoCanvas
	private VideoCanvas vidCvs = null;
	
	// VideoCanvas ��z�u���ĉ�ʂɕ\�����邽�߂̃r���[��
	// ���C���X�^���X�������ɊO������^������D
	private SimpleViewer viewer = null;
	
	// �T���v���R�[�h�̓�����(true:���쒆�Cfalse:��~��)
	private boolean isRunning = false;	
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	// �R���X�g���N�^
	//
	public AbstractSampleCode(SimpleViewer viewer)
	{
		// ���̃N���X���g�������N���X���ŗ��p�������o�͍\���c�[���𐶐�����D
		this.cfgTool = new ConfigTool();
		
		// �^����ꂽ SimpleViewer �C���X�^���X��
		// �r���[���Ƃ��ė��p���邽�߂ɕێ�����D
		this.viewer = viewer;
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	// �T���v���R�[�h�̓�����(true:���쒆�Cfalse:��~��)��Ԃ��D
	//
	public boolean isRunning()
	{
		return this.isRunning;
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	// �T���v���R�[�h�̏������J�n����D
	//
	public void startup()
		throws SystemException, StreamException
	{
		// �����Ԃ��m�F����D
		if (this.isRunning) {
			return;
		}
		// Stream Performer �𐶐����C���o�͏��������s����D
		try {
			// SegmentIo �𐶐�����D�D
			SegmentIo segIo = new SegmentIo();
			
			// SegmentIo �̓��o�͂��\������D�D
			configureInput(segIo);	// IOException
			configureOutput(segIo);	// IOException

			// SegmentIo �����ƂɁCStreamPerformer �𐶐�����D
			this.pfmr = StreamPerformer.newInstance(segIo);
				// SystemException, StreamException

			// StreamPerformer ���� VideoCanvas ���擾���C
			// �r���[���ɒǉ�����D
			this.vidCvs = this.pfmr.getVideoCanvas();
			addVideoCanvasToViewer(this.vidCvs);

			// ���o�͏������J�n����D
			this.pfmr.open();		// StreamException
			this.pfmr.start();		// StreamException
			
			// �����Ԃ� true �ɂ���D
			this.isRunning = true;
		}
		catch (IOException ex) {
			// ��O�������̃��b�Z�[�W���o�͂���D
			System.out.println(ex.getMessage());
		}
		catch (SystemException | StreamException ex) {
			// ���̃C���X�^���X�̏I������������D
			delete();
			
			// ��O���Ăяo�����֓�����D
			throw ex;
		}
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	// �T���v���R�[�h�̏������I������D
	//		
	public void cleanup()
	{
		// �����Ԃ��m�F����D
		if (this.isRunning == false) {
			return;
		}
		try {
			// ���o�͏������I������D
			this.pfmr.stop();	// StreamException
			this.pfmr.close();
		}
		catch (StreamException ex) {
			// ��O�������̃X�^�b�N�g���[�X���o�͂���D
			ex.printStackTrace();
		}
		finally {
			// ���̃C���X�^���X�̏I������������D
			delete();
			
			// �����Ԃ� false �ɂ���D
			this.isRunning = false;
		}
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	// �T���v���R�[�h�̊T�v�������擾����D
	//
	public abstract String getDescription();
	
//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------
		
	//- PROTECTED METHOD -------------------------------------------------------
	// SegmentIo �̓��͂��\������D
	//
	protected abstract void configureInput(SegmentIo segIo)
		throws IOException;
	
	//- PROTECTED METHOD -------------------------------------------------------
	// SegmentIo �̏o�͂��\������D
	//
	protected abstract void configureOutput(SegmentIo segIo)
		throws IOException;
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	// VideoCanvas ���r���[���ɒǉ�����D
	//
	private void addVideoCanvasToViewer(VideoCanvas vidCvs)
	{
		// �r���[���̃C���X�^���X���L��ꍇ�C
		// VideoCanvas ���r���[���ɒǉ�����D 
		if (this.viewer != null) {
			this.viewer.addVideoCanvas(this.vidCvs);
		}
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	// ���̃C���X�^���X�̏I������������D
	//
	private void delete()
	{
		// �r���[���̃C���X�^���X���L��ꍇ�C
		// �r���[������ VideoCanvas ���폜����D
		if (this.viewer != null) {
			this.viewer.removeVideoCanvas(this.vidCvs);
		}
		// StreamPerformer �̃C���X�^���X���L��ꍇ�C
		// StreamPerformer ���I������D
		if (this.pfmr != null) {
			this.pfmr.delete();
			this.pfmr = null;
		}
	}
}
