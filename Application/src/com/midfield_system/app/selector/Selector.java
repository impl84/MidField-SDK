
package com.midfield_system.app.selector;

import com.midfield_system.api.system.SystemException;
import com.midfield_system.app.util.LineReader;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: Selector
 *
 * Date Modified: 2020.10.02
 *
 */

//==============================================================================
public class Selector
{
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
		// SampleCodeSelector �𐶐����C
		// �T���v���R�[�h�̎��s�ƏI�����J��Ԃ��D
		SampleCodeSelector selector = null;
		try {
			// SampleCodeSelector �𐶐�����D
			selector = new SampleCodeSelector();
				// SystemException
			
			// �T���v���R�[�h�̎��s�ƏI�����J��Ԃ��D
			selector.mainLoop();
		}
		catch (SystemException ex) {
			// SampleCodeSelector �������ɗ�O�����������D
			System.out.println("��SampleCodeSelector �������ɗ�O���������܂����D");
			ex.printStackTrace();
		}
		catch (Exception ex) {
			// SampleCodeSelector �̓��쒆�ɗ�O���������܂����D
			System.out.println("��SampleCodeSelector �̓��쒆�ɗ�O���������܂����D");
			ex.printStackTrace();
		}
		finally {
			// SampleCodeSelector ���I������D
			if (selector != null) {
				selector.cleanup();
			}
			// �W�����͂����1�s�ǂݍ��݂ɗ��p���� LineReader ���������D
			LineReader.release();
		}
	}
}