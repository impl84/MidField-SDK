
package com.midfield_system.app.performer.ex0;

import com.midfield_system.api.stream.ConnectionMode;
import com.midfield_system.api.stream.DeviceInfo;
import com.midfield_system.api.stream.DeviceInfoManager;
import com.midfield_system.api.stream.ProtocolType;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.app.util.LineReader;
import com.midfield_system.app.util.SimpleViewer;

// Sample code of MidField System API
// Date Modified: 2020.10.01
//
public class DeviceToStreamEx0
{
	public static void main(String[] args)
	{
		MfsNode mfs = null;
		StreamPerformer pfmr = null;
		SimpleViewer viewer = null;
		
		try {
			// MidField System �����������C�N������D
			mfs = MfsNode.initialize();		// SystemException
			mfs.activate();					// SystemException
			
			// �r�f�I�ƃI�[�f�B�I�̓��̓f�o�C�X��񃊃X�g���擾���C
			// ���p������̓f�o�C�X��I������D�i�����ł͍ŏ��̗v�f��I������D�j
			DeviceInfoManager devInfMgr = DeviceInfoManager.getInstance();
			DeviceInfo vidDev = devInfMgr.getInputVideoDeviceInfoList().get(0);
			DeviceInfo audDev = devInfMgr.getInputAudioDeviceInfoList().get(0);
			
			// ���̓f�o�C�X�� SegmentIo �̓��͂��\������D
			SegmentIo segIo = new SegmentIo();
			segIo.configureInputDevice(vidDev, audDev);
			
			// SegmentIo �̏o�͂𑗐M�X�g���[���Ƃ��č\�����C
			// �g�����X�|�[�g�v���g�R���̐ݒ���s���D
			segIo.configureOutgoingStream(
				segIo.getOutputVideoFormatList().get(0),
				segIo.getOutputAudioFormatList().get(0)
			);
			segIo.setTransportProtocol(
				ProtocolType.TCP,		// TCP�𗘗p����D
				ConnectionMode.PASSIVE	// �R�l�N�V�����ڑ��v�����󂯓����D
			);
			// �I�v�V�����̐ݒ������D
			segIo.setPreviewer();		// �v���r���[���\�𗘗p����D
			segIo.setLiveSource(true);	// ���C�u�\�[�X�I�v�V������L���ɂ���D	

			// SegmentIo �����Ƃ� StreamPerformer �𐶐�����D
			pfmr = StreamPerformer.newInstance(segIo);	// SystemException, StreamException

			// StreamPerformer ���� VideoCanvas ���擾���CSimpleViewer �ɒǉ�����D
			viewer = new SimpleViewer("DeviceToStream", pfmr.getVideoCanvas());

			// ���o�͏������J�n����D
			pfmr.open();	// StreamException
			pfmr.start();	// StreamException
			
			// Enter�L�[�̓��͂�҂D
			LineReader.readLine("> Enter �L�[�̓��͂�҂��܂��D");	// IOException
			
			// ���o�͏������I������D
			pfmr.stop();	// StreamException
			pfmr.close();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			// SimpleViewer, StreamPerformer, MidField System ���I������D
			if (viewer != null) { viewer.dispose(); }
			if (pfmr != null) { pfmr.delete(); }
			if (mfs != null) { mfs.shutdown(); }
			
			// LineReader ���������D
			LineReader.release();
		}
	}
}