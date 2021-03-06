
package selector;

import java.io.IOException;

import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.system.MfsNode;

import util.LineReader;
import util.SimpleViewer;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: StreamToRendererEx1
 *
 * Date Modified: 2020.10.02
 *
 */

//==============================================================================
public class StreamToRendererEx1
	extends	AbstractSampleCode
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String
		DESCRIPTION	= "Incoming Stream -> Renderer";
	
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
			// MidField System を初期化し，起動する．
			mfs = MfsNode.initialize();	// SystemException
			mfs.activate();				// SystemException
			
			// SimpleViewer のインスタンスを生成する．
			viewer = new SimpleViewer(DESCRIPTION);
			
			// AbstractSampleCode のインスタンスを生成して処理を開始する．
			sample = new StreamToRendererEx1(viewer);
			sample.startup();			// SystemException, StreamException
			
			// Enterキーの入力を待つ．
			LineReader.readLine("> Enter キーの入力を待ちます．");	// IOException
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			// AbstractSampleCode の処理を終了する．
			if (sample != null) {
				sample.cleanup();
			}
			// SimpleViewer を終了する．
			if (viewer != null) {
				viewer.dispose();
			}
			// MidField System を終了する．
			if (mfs != null) {
				mfs.shutdown();
			}
			// LineReader を解放する．
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
	// コンストラクタ
	//
	public StreamToRendererEx1(SimpleViewer viewer)
	{
		super(viewer);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	// サンプルコードの概要説明を取得する．
	//
	public String getDescription()
	{
		return	DESCRIPTION;
	}	
	
//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------
	
	//- PROTECTED METHOD -------------------------------------------------------
	// SegmentIo の入力を構成する．
	//
	protected void configureInput(SegmentIo segIo)
		throws	IOException
	{
		// 受信ストリームで SegmentIo の入力を構成する．
		this.cfgTool.configureIncomingStream(segIo);
			// IOException	
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	// SegmentIo の出力を構成する．
	//
	protected void configureOutput(SegmentIo segIo)
		throws	IOException
	{
		// デフォルトレンダラで SegmentIo の出力を構成する．
		this.cfgTool.configureDefaultRenderer(segIo);		
			// IOException	
	}
}