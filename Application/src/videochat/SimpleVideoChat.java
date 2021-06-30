
package videochat;

import java.io.IOException;

import com.midfield_system.api.stream.StreamException;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.api.system.SystemException;

import selector.AbstractSampleCode;
import selector.DeviceToStreamEx1;
import selector.StreamToRendererEx1;
import util.LineReader;
import util.SimpleViewer;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: SimpleVideoChat
 *
 * Date Modified: 2020.10.02
 *
 */

//==============================================================================
public class SimpleVideoChat
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
		MfsNode mfs = null;
		SimpleViewer viewer = null;
		AbstractSampleCode sender = null;
		AbstractSampleCode receiver = null;
	
		try {
			// MidField System ������������D
			mfs = MfsNode.initialize();		// SystemException
			
			// MidField System ���N������D
			mfs.activate();					// SystemException
			
			// SimpleViewer �𐶐�����D
			viewer = new SimpleViewer("Simple Video Chat", 640, 240, 0, 2);

			// �r�f�I���M�p�̃T���v���R�[�h�����s����D
			sender = new DeviceToStreamEx1(viewer);
			sender.startup();			// SystemException, StreamException
			
			// �r�f�I��M�p�̃T���v���R�[�h�����s����D
			receiver = new StreamToRendererEx1(viewer);
			receiver.startup();		// SystemException, StreamException
			
			// Enter�L�[�̓��͂�҂D
			LineReader.readLine("> Enter �L�[�̓��͂�҂��܂��D");	// IOException
		}
		catch (SystemException | StreamException | IOException ex) {
			// ��O�������̃X�^�b�N�g���[�X��\������D
			ex.printStackTrace();
		}
		finally {
			// �T���v���R�[�h�̏������I������D
			if (receiver != null) {
				receiver.cleanup();
			}
			if (sender != null) {
				sender.cleanup();
			}
			// SimpleViewer ���I������D
			if (viewer != null) {
				viewer.dispose();
			}
			// MidField System ���I������D
			if (mfs != null) {
				mfs.shutdown();
			}
			// �W�����͂����1�s�ǂݍ��݂ɗ��p���� LineReader ���������D
			LineReader.release();
		}
	}
}