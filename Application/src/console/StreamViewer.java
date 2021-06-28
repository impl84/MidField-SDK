
package console;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.midfield_system.api.event.StreamEvent;
import com.midfield_system.api.event.StreamEventListener;
import com.midfield_system.api.event.RendererStatusEvent;
import com.midfield_system.api.event.IoStatusEvent;
import com.midfield_system.api.event.PerformerStateEvent;
import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.stream.RendererStatus;
import com.midfield_system.api.stream.PerformerState;
import com.midfield_system.api.viewer.VideoCanvas;
import com.midfield_system.protocol.IoStatus;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: StreamViewer
 *
 * Date Modified: 2017.08.25
 *
 */

//==============================================================================
@SuppressWarnings("serial")
public class StreamViewer
	extends		JFrame
	implements	StreamEventListener,
				WindowListener
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final Dimension DIM_MIN_FRAME_WITH_VIDEO = new Dimension(400, 600);
	private static final Dimension DIM_MIN_FRAME_NO_VIDEO   = new Dimension(400, 300);
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private StreamPerformer pfmr = null;
	
	private PrintOutPanel pnlStmEv = null;
	private PrintOutPanel pnlStat  = null;

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public StreamViewer()
	{
		Border bdStmEv = BorderFactory.createEtchedBorder();		
		this.pnlStmEv = new PrintOutPanel();
		this.pnlStmEv.setBorder(new TitledBorder(bdStmEv, "StreamEvent"));

		Border bdStat = BorderFactory.createEtchedBorder();		
		this.pnlStat = new PrintOutPanel();
		this.pnlStat.setBorder(new TitledBorder(bdStat, "RendererStatusEvent/IoStatusEvent"));
		
		JPanel pnlEv = new JPanel(new BorderLayout());
		pnlEv.add(this.pnlStmEv, BorderLayout.CENTER);
		pnlEv.add(this.pnlStat,  BorderLayout.SOUTH);
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(pnlEv, BorderLayout.SOUTH);

		setTitle("StreamViewer");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(this);
		addNotify();
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void setPerformer(StreamPerformer pfmr)
	{
		this.pfmr = pfmr;
		this.pfmr.addStreamEventListener(this);
		
		VideoCanvas vidCvs = this.pfmr.getVideoCanvas();
		if (vidCvs != null) {
			setMinimumSize(DIM_MIN_FRAME_WITH_VIDEO);

			JPanel pnlVidCvs = new JPanel(new BorderLayout());
			pnlVidCvs.add(vidCvs, BorderLayout.CENTER);

			Border border = BorderFactory.createEtchedBorder();		
			pnlVidCvs.setBorder(new TitledBorder(border, "VideoCanvas"));
			
			Container container = getContentPane();
			container.add(pnlVidCvs, BorderLayout.CENTER);
		}
		else {
			setMinimumSize(DIM_MIN_FRAME_NO_VIDEO);
		}
		pack();
	}

//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: StreamEventListener	
	//
	@Override
	public void update(StreamEvent ev)
	{
		if (ev instanceof RendererStatusEvent) {
			// 再生表示に関する状態情報を表示する．
			RendererStatusEvent rsEv = (RendererStatusEvent)ev;
			RendererStatus rndStat = rsEv.getRendererStatus();
			this.pnlStat.println("[表示再生] " + rndStat.toString());
		}
		else if (ev instanceof IoStatusEvent) {
			// コネクションに関する状態情報を表示する．
			IoStatusEvent scsEv = (IoStatusEvent)ev;
			IoStatus stat = scsEv.getIoStatus();
			this.pnlStat.println("[接続状況] " + stat.toString());
		}
		else {
			// 上記以外の StreamEvent を表示する．
			this.pnlStmEv.println(ev.toString());

			// ストリームの状態が FINAL へ遷移したか調べる．
			// FINAL へ遷移していた場合，この SimpleViewer を終了する．
			if (ev instanceof PerformerStateEvent) {
				PerformerStateEvent ssEv = (PerformerStateEvent)ev;
				PerformerState curStat = ssEv.getCurrentState();
				if (curStat == PerformerState.FINAL) {
					dispose();
				}
			}
		}
	}

//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: WindowListener	
	//
	@Override
	public void windowActivated(WindowEvent ev)
	{
		//		
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: WindowListener	
	//
	@Override
	public void windowClosed(WindowEvent ev)
	{
		//
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: WindowListener	
	//
	@Override
	public void windowClosing(WindowEvent ev)
	{
		if (this.pfmr != null) {
			this.pfmr.removeStreamEventListener(this);
			this.pfmr.delete();
			this.pfmr = null;
		}
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: WindowListener	
	//
	@Override
	public void windowDeactivated(WindowEvent ev)
	{
		//
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: WindowListener	
	//
	@Override
	public void windowDeiconified(WindowEvent ev)
	{
		//
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: WindowListener	
	//
	@Override
	public void windowIconified(WindowEvent ev)
	{
		//
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: WindowListener	
	//
	@Override
	public void windowOpened(WindowEvent ev)
	{
		//
	}
}