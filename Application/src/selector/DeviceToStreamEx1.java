
package selector;

import java.io.IOException;

import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.system.MfsNode;

import util.LineReader;
import util.SimpleViewer;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: DeviceToStreamEx1
 *
 * Date Modified: 2020.10.02
 *
 */

//==============================================================================
public class DeviceToStreamEx1
	extends	AbstractSampleCode
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String
		DESCRIPTION	= "Device -> Outgoing Stream";
	
//==============================================================================
//  CLASS METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC STATIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC STATIC METHOD ---------------------------------------------------
	//
	public static void main(String[] args)
	{
		MfsNode mfs = null;
		SimpleViewer viewer = null;
		AbstractSampleCode sample = null;
		
		try {
			// MidField System �����������C�N������D
			mfs = MfsNode.initialize();	// SystemException
			mfs.activate();				// SystemException
			
			// SimpleViewer �̃C���X�^���X�𐶐�����D
			viewer = new SimpleViewer(DESCRIPTION);
			
			// AbstractSampleCode �̃C���X�^���X�𐶐����ď������J�n����D
			sample = new DeviceToStreamEx1(viewer);
			sample.startup();			// SystemException, StreamException
			
			// Enter�L�[�̓��͂�҂D
			LineReader.readLine("> Enter �L�[�̓��͂�҂��܂��D");	// IOException
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			// AbstractSampleCode �̏������I������D
			if (sample != null) {
				sample.cleanup();
			}
			// SimpleViewer ���I������D
			if (viewer != null) {
				viewer.dispose();
			}
			// MidField System ���I������D
			if (mfs != null) {
				mfs.shutdown();
			}
			// LineReader ���������D
			LineReader.release();
		}
	}
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------	

	//- PUBLIC METHOD ----------------------------------------------------------
	// �R���X�g���N�^
	//
	public DeviceToStreamEx1(SimpleViewer viewer)
	{
		super(viewer);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	// �T���v���R�[�h�̊T�v�������擾����D
	//
	public String getDescription()
	{
		return	DESCRIPTION;
	}	
	
//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------
	
	//- PROTECTED METHOD -------------------------------------------------------
	// SegmentIo �̓��͂��\������D
	//
	protected void configureInput(SegmentIo segIo)
		throws	IOException
	{
		// ���̓f�o�C�X�� SegmentIo �̓��͂��\������D
		this.cfgTool.configureInputDevice(segIo);
			// IOException
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	// SegmentIo �̏o�͂��\������D
	//
	protected void configureOutput(SegmentIo segIo)
		throws	IOException
	{
		// ���M�X�g���[���� SegentIo �̏o�͂��\������D
		this.cfgTool.configureOutgoingStream(segIo);
			// IOException	
	}
}