
package selector;

import java.io.IOException;

import com.midfield_system.api.stream.StreamException;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.api.system.SystemException;

import performer.ex1.AbstractSampleCode;
import performer.ex1.DeviceToRendererEx1;
import performer.ex1.DeviceToStreamEx1;
import performer.ex1.StreamToRendererEx1;
import performer.ex1.StreamToStreamEx1;
import util.LineReader;
import util.SimpleViewer;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: SampleCodeSelector
 *
 * Date Modified: 2020.10.05
 *
 */

//==============================================================================
public class SampleCodeSelector
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final int
		END_OF_SELECTOR		= -1,
		INDEX_OUT_OF_BOUNDS	= -2;

//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------

	// MidField System
	private MfsNode mfs = null;

	// �r�f�I��\�����邽�߂̃r���[��
	private SimpleViewer viewer = null;

	// �T���v���R�[�h�Ƃ��Ď��s�\�ȃC���X�^���X�̔z��
	private AbstractSampleCode[] samples = null;

//==============================================================================
//  INSTANCE METHOD:
//==============================================================================
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public SampleCodeSelector()
		throws	SystemException
	{
		// MidField System ������������D
		this.mfs = MfsNode.initialize();
			// SystemException

		// MidField System ���N������D
		this.mfs.activate();
			// SystemException

		// SimpleViewer �̃C���X�^���X�𐶐�����D
		this.viewer = new SimpleViewer("Sample Code Selector");
		
		// �T���v���R�[�h�Ƃ��Ď��s�\�ȃC���X�^���X���v�f�ƂȂ�z��𐶐�����D
		this.samples = new AbstractSampleCode[] {
			new DeviceToRendererEx1(this.viewer),
			new DeviceToStreamEx1(this.viewer),
			new StreamToRendererEx1(this.viewer),
			new StreamToStreamEx1(this.viewer)
		};
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public void mainLoop()
	{
		// �T���v���R�[�h��I�����C�J�n�܂��͒�~���J��Ԃ��D
		while (true) {
			// �T���v���R�[�h�̃��X�g��\������D
			printSampleCodeList();
			
			// �T���v���R�[�h��I������D
			int result = selectSampleCode();
			if (result == END_OF_SELECTOR) {
				// �I���ԍ������͂��ꂽ�̂Ń��[�v�𔲂���D
				break;
			}
			else if (result == INDEX_OUT_OF_BOUNDS) {
				// �͈͊O�̔ԍ������͂��ꂽ�̂Ŏ��̃��[�v�̏����ɓ���D
				continue;
			}
			// ���͂��ꂽ�ԍ��̃T���v���R�[�h�̏������J�n�܂��͏I������D
			changeRunningState(result);
		}
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public void cleanup()
	{
		// MidField System �𗘗p���Ă��邩�ǂ������m�F����D
		if (this.mfs == null) {
			return;
		}
		// �������I�����Ă��Ȃ��T���v���R�[�h������ꍇ�͏I��������D
		cleanupAllSampleCodes();
		
		// SimpleViewer ���I������D
		if (this.viewer != null) {
			this.viewer.dispose();
		}
		// MidField System ���V���b�g�_�E������D
		this.mfs.shutdown();
		this.mfs = null;
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void printSampleCodeList()
	{
		// �T���v���R�[�h�̈ꗗ��\������D
		System.out.println();
		System.out.println("���T���v���R�[�h�ꗗ�F");
		
		// �T���v���R�[�h�Ƃ��Ď��s�\�ȃC���X�^���X�̔z��𑖍�����D
		for (int n = 0; n < this.samples.length; n++) {
			// �T���v���R�[�h�̃C���X�^���X���擾����D
			AbstractSampleCode sample = this.samples[n];

			// �����Ԃɉ������o�͕���������߂�D
			String runningState = null;
			if (sample.isRunning()) {
				runningState = "���쒆";
			}
			else {
				runningState = "��~��";
			}
			// �T���v���R�[�h�̔ԍ��E��ԁE�������o�͂���D
			System.out.printf(" [%02d] (%s) %s\n", n,
				runningState,
				sample.getDescription()
			);
		}
	}
		
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private int selectSampleCode()
	{
		int res = 0;
		
		// �T���v���R�[�h��I�����C�J�n/��~����D
		try {
			// �T���v���R�[�h�ԍ����L�[�{�[�h����擾����D
			System.out.printf("> �ԍ�����[0-%d: ���s�C%d: �I��]�F",
				this.samples.length - 1, this.samples.length
			);
			String line = LineReader.readLine();	// IOException
			int n = Integer.parseInt(line);			// NumberFormatException
			
			// ���͂��ꂽ�l�͈̔͂��m�F����D
			if ((n >= 0) && (n < this.samples.length)) {
				// �I�����ꂽ�T���v���R�[�h�̔z��̃C���f�b�N�X�l��
				// ���U���g�R�[�h�ɐݒ肷��D
				res = n;
			}
			else if (n == this.samples.length) {
				// �I�����Ӗ�����ԍ������͂��ꂽ�̂ŁC
				// ���U���g�R�[�h�� END_OF_SELECTOR ��ݒ肷��D
				System.out.printf("  �v���O�������I�����܂��D");
				res = END_OF_SELECTOR;
			}
			else {
				// ���U���g�R�[�h�� INDEX_OUT_OF_BOUNDS ��ݒ肷��D
				System.out.println("  ���K�؂Ȕԍ�����͂��Ă��������D");
				res = INDEX_OUT_OF_BOUNDS;
			}
		}
		catch (NumberFormatException ex) {
			// NumberFormatException �����������ꍇ�́C
			// ���U���g�R�[�h�� INDEX_OUT_OF_BOUNDS ��ݒ肷��D
			System.out.printf("  ���K�؂Ȕԍ�����͂��Ă��������D(%s)\n",
				ex.getMessage()
			);
			res = INDEX_OUT_OF_BOUNDS;
		}
		catch (IOException ex) {
			// IOException �����������ꍇ�́C��O���b�Z�[�W��\�����āC
			// ���U���g�R�[�h�� END_OF_SELECTOR ��ݒ肷��D
			System.out.println("���L�[�{�[�h����̓��͏����ŗ�O���������܂����D");
			ex.printStackTrace();
			res = END_OF_SELECTOR;
		}
		return res;
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void changeRunningState(int idx)
	{
		// �T���v���R�[�h���J�n�܂��͒�~����D
		try {
			// �I�����ꂽ�T���v���R�[�h�̃C���X�^���X���擾����D
			AbstractSampleCode selectedSample = this.samples[idx];
				
			// �T���v���R�[�h�̓����Ԃɂ��C
			// �T���v���R�[�h�̏������J�n�܂��͏I������D
			if (selectedSample.isRunning()) {
				// �T���v���R�[�h�̏������I������D
				selectedSample.cleanup();
			}
			else {
				// �T���v���R�[�h�̏������J�n����D
				selectedSample.startup();
					// SystemException, StreamException
			}			
		}
		catch (SystemException | StreamException ex) {
			// �T���v���R�[�g���s���ɗ�O�����������ꍇ�́C
			// ��O���b�Z�[�W��\�����Ė߂�D
			System.out.println("���T���v���R�[�h���s���ɗ�O���������܂����D");
			ex.printStackTrace();
		}
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void cleanupAllSampleCodes()
	{
		// �T���v���R�[�h�Ƃ��Ď��s�\�ȃC���X�^���X�̔z��𑖍�����D
		for (int n = 0; n < this.samples.length; n++) {
			// �T���v���R�[�h�̃C���X�^���X���擾����D
			AbstractSampleCode sample = this.samples[n];

			// �������I�����Ă��Ȃ��T���v���R�[�h������ꍇ�͏������I��������D
			if (sample.isRunning()) {
				sample.cleanup();
			}
		}		
	}
}