
package com.midfield_system.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: LineReader
 *
 * Date Modified: 2020.10.01
 *
 */

//==============================================================================
public class LineReader
{
//==============================================================================
//  CLASS VARIABLE:
//==============================================================================

	//- PRIVATE STATIC VARIABLE ------------------------------------------------
	
	// �W�����͂����1�s�ǂݍ��݂ɗ��p���� BufferedReader
	private static BufferedReader reader = null;
	
//==============================================================================
//  CLASS METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC STATIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC STATIC METHOD ---------------------------------------------------
	//
	public static String readLine(String description)
		throws	IOException
	{
		// �����ŗ^����ꂽ������ null �ł͖����ꍇ�C���̕������\������D
		System.out.print(description);
		
		// ���������� readLine() ���Ăяo���D
		String line = readLine();
			// IOException
		
		// �ǂݍ���1�s��Ԃ��D
		return line;
	}
	
	//- PUBLIC STATIC METHOD ---------------------------------------------------
	//
	public static String readLine()
		throws	IOException
	{
		// �W�����͂����1�s�ǂݍ��݂ɗ��p���� BufferedReader �̃C���X�^���X��
		// �L�����m�F����D
		if (LineReader.reader == null) {
			// �C���X�^���X�����݂��Ȃ��ꍇ�́C
			// �W�����͂����1�s�ǂݍ��݂ɗ��p���� BufferedReader �𐶐�����D
			LineReader.reader = new BufferedReader(new InputStreamReader(System.in));
		}
		// �W�����͂���1�s�ǂݍ��ށD
		String line = LineReader.reader.readLine();
			// IOException
		
		// �ǂݍ���1�s��Ԃ��D
		return line;
	}
	
	//- PUBLIC STATIC METHOD ---------------------------------------------------
	//
	public static void release()
	{
		// BufferedReader �̃C���X�^���X�̗L�����m�F����D
		if (LineReader.reader == null) {
			// �C���X�^���X�����݂��Ȃ��̂ŁC���������ɖ߂�D
			return;
		}
		try {
			// BufferedReader �����D
			LineReader.reader.close();
				// IOException
				
			// BufferedReader �̃C���X�^���X���i�[���邽�߂�
			// �ϐ������������Ă����D
			LineReader.reader = null;
		}
		catch (IOException ex) {
			// ��O�������̃X�^�b�N�g���[�X���o�͂���D
			ex.printStackTrace();
		}
	}
}
