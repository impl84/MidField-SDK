
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
		// SampleCodeSelector を生成し，
		// サンプルコードの実行と終了を繰り返す．
		SampleCodeSelector selector = null;
		try {
			// SampleCodeSelector を生成する．
			selector = new SampleCodeSelector();
				// SystemException
			
			// サンプルコードの実行と終了を繰り返す．
			selector.mainLoop();
		}
		catch (SystemException ex) {
			// SampleCodeSelector 生成時に例外が発生した．
			System.out.println("※SampleCodeSelector 生成時に例外が発生しました．");
			ex.printStackTrace();
		}
		catch (Exception ex) {
			// SampleCodeSelector の動作中に例外が発生しました．
			System.out.println("※SampleCodeSelector の動作中に例外が発生しました．");
			ex.printStackTrace();
		}
		finally {
			// SampleCodeSelector を終了する．
			if (selector != null) {
				selector.cleanup();
			}
			// 標準入力からの1行読み込みに利用した LineReader を解放する．
			LineReader.release();
		}
	}
}