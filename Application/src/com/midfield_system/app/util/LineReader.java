
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
	
	// 標準入力からの1行読み込みに利用する BufferedReader
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
		// 引数で与えられた文字列が null では無い場合，その文字列を表示する．
		System.out.print(description);
		
		// 引数無しの readLine() を呼び出す．
		String line = readLine();
			// IOException
		
		// 読み込んだ1行を返す．
		return line;
	}
	
	//- PUBLIC STATIC METHOD ---------------------------------------------------
	//
	public static String readLine()
		throws	IOException
	{
		// 標準入力からの1行読み込みに利用する BufferedReader のインスタンスの
		// 有無を確認する．
		if (LineReader.reader == null) {
			// インスタンスが存在しない場合は，
			// 標準入力からの1行読み込みに利用する BufferedReader を生成する．
			LineReader.reader = new BufferedReader(new InputStreamReader(System.in));
		}
		// 標準入力から1行読み込む．
		String line = LineReader.reader.readLine();
			// IOException
		
		// 読み込んだ1行を返す．
		return line;
	}
	
	//- PUBLIC STATIC METHOD ---------------------------------------------------
	//
	public static void release()
	{
		// BufferedReader のインスタンスの有無を確認する．
		if (LineReader.reader == null) {
			// インスタンスが存在しないので，何もせずに戻る．
			return;
		}
		try {
			// BufferedReader を閉じる．
			LineReader.reader.close();
				// IOException
				
			// BufferedReader のインスタンスを格納するための
			// 変数を初期化しておく．
			LineReader.reader = null;
		}
		catch (IOException ex) {
			// 例外発生時のスタックトレースを出力する．
			ex.printStackTrace();
		}
	}
}
