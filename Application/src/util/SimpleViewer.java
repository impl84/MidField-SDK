
package util;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.midfield_system.api.viewer.VideoCanvas;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: SimpleViewer
 *
 * Date Modified: 2021.06.29
 *
 */

//==============================================================================
public class SimpleViewer
{
	//- PUBLIC CONSTANT VALUE --------------------------------------------------
	public static final int
		DEF_WIDTH	= 640,	// ビューワのデフォルトの幅 [pixel]
		DEF_HEIGHT	= 480,	// ビューワのデフォルトの高さ [pixel]
		DEF_ROWS	= 1,	// ビューワのデフォルトの行数
		DEF_COLS	= 1;	// ビューワのデフォルトの列数
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------	
	private JFrame frame = null;

//==============================================================================
//  INSTANCE METHOD:
//==============================================================================
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public SimpleViewer(String title)
	{
		SwingUtilities.invokeLater(
			() -> newSimpleViewerOnEdt(title, DEF_WIDTH, DEF_HEIGHT, DEF_ROWS, DEF_COLS)
		);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public SimpleViewer(String title, VideoCanvas vidCvs)
	{
		SwingUtilities.invokeLater(() -> {
			newSimpleViewerOnEdt(title, DEF_WIDTH, DEF_HEIGHT, DEF_ROWS, DEF_COLS);
			
			if (vidCvs != null) {
				addVideoCanvasOnEdt(vidCvs);
			}
		});
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public SimpleViewer(String title, int width, int height, int rows, int cols)
	{
		SwingUtilities.invokeLater(
			() -> newSimpleViewerOnEdt(title, width, height, rows, cols)
		);
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void addVideoCanvas(VideoCanvas vidCvs)
	{
		if (vidCvs == null) {
			return;
		}
		SwingUtilities.invokeLater(() -> addVideoCanvasOnEdt(vidCvs));
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void removeVideoCanvas(VideoCanvas vidCvs)
	{
		if (vidCvs == null) {
			return;
		}
		SwingUtilities.invokeLater(() -> removeVideoCanvasOnEdt(vidCvs));
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void dispose()
	{
		SwingUtilities.invokeLater(() -> disposeOnEdt());
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void newSimpleViewerOnEdt(String title, int width, int height, int rows, int cols)
	{
		this.frame = new JFrame();
		this.frame.setTitle(title);
		this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		Container container = this.frame.getContentPane();
		container.setPreferredSize(new Dimension(width, height));
		container.setLayout(new GridLayout(rows, cols));
		container.setBackground(Color.BLUE);
	
		this.frame.pack();
		this.frame.setMinimumSize(this.frame.getSize());
		this.frame.setVisible(true);
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void addVideoCanvasOnEdt(VideoCanvas vidCvs)
	{
		Container container = this.frame.getContentPane();
		container.add(vidCvs);
		this.frame.validate();
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void removeVideoCanvasOnEdt(VideoCanvas vidCvs)
	{
		Container container = this.frame.getContentPane();
		container.remove(vidCvs);
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void disposeOnEdt()
	{
		this.frame.dispose();
	}
}
