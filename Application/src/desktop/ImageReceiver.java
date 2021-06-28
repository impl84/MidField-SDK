
package desktop;

import javax.swing.SwingUtilities;

import com.midfield_system.api.event.FlowUpdateEvent;
import com.midfield_system.api.event.IoStatusEvent;
import com.midfield_system.api.event.MixerStatusEvent;
import com.midfield_system.api.event.PerformerStateEvent;
import com.midfield_system.api.event.RendererStatusEvent;
import com.midfield_system.api.event.SegmentEvent;
import com.midfield_system.api.event.StreamEvent;
import com.midfield_system.api.event.StreamEventListener;
import com.midfield_system.api.event.StreamExceptionEvent;
import com.midfield_system.api.stream.IoParam;
import com.midfield_system.api.stream.RendererStatus;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamException;
import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.util.Log;
import com.midfield_system.api.viewer.VideoCanvas;
import com.midfield_system.protocol.IoStatus;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: ImageReceiver 
 *
 * Date Modified: 2020.08.27
 *
 */

//==============================================================================
public class ImageReceiver
	implements	StreamEventListener
{
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private StreamPerformer pfmr = null;
	
	private FrameViewer viewer = null;
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: StreamEventListener	
	//
	@Override
	public void update(StreamEvent ev)
	{
		// EDT対応
		SwingUtilities.invokeLater(() -> { updateOnEdt(ev); });
	}

	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void updateOnEdt(StreamEvent ev)
	{
		if (ev instanceof IoStatusEvent) {
			evHn_IoStatus((IoStatusEvent)ev);
		}
		else if (ev instanceof RendererStatusEvent) {
			evHn_RendererStatus((RendererStatusEvent)ev);
		}
		else if (ev instanceof MixerStatusEvent) {
			// 特に何もしない．(2016-06-08)
		}		
		else if (ev instanceof PerformerStateEvent) {
			evHn_PerformerState((PerformerStateEvent)ev);
		}
		else if (ev instanceof SegmentEvent) {
			evHn_Segment((SegmentEvent)ev);
		}
		else if (ev instanceof FlowUpdateEvent) {
			evHn_FlowUpdate((FlowUpdateEvent)ev);
		}
		else if (ev instanceof StreamExceptionEvent) {
			evHn_StreamExceptionEvent((StreamExceptionEvent)ev);
		}
		else {
			Log.error(ev.toString());
		}		
	}	

//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	ImageReceiver(FrameViewer viewer)
	{
		this.viewer = viewer;
	}

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	VideoCanvas createVideoCanvas(IoParam inPrm)
		throws	SystemException,
				StreamException
	{
		// SegmentIo の入出力を設定する．
		SegmentIo segIo = new SegmentIo();
		segIo.addIncomingStream(inPrm);
		segIo.configureDefaultRenderer();

		// Stream Performer を生成する．
		this.pfmr = StreamPerformer.newInstance(segIo);
			// SystemException, StreamException
		
		this.pfmr.addStreamEventListener(this);
		
		// VideoCanvas を取得する．
		VideoCanvas vidCvs = this.pfmr.getVideoCanvas();

		// VideoCanvas を返す．
		return vidCvs;
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void start()
		throws	StreamException	
	{
		try {
			// 入出力処理を開始する．
			this.pfmr.open();
				// StreamException
			
			this.pfmr.start();
				// StreamException
		}
		catch (StreamException ex) {
			shutdown();
			throw ex;
		}
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void shutdown()
	{
		if (this.pfmr != null) {
			this.pfmr.removeStreamEventListener(this);
			this.pfmr.delete();
			this.pfmr = null;
		}
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD: StreamEventListener
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void evHn_IoStatus(IoStatusEvent ev)
	{
		IoStatus stat = ev.getIoStatus();
		this.viewer.updateConnectionStatus(
			stat.getBitRate(),
			stat.getPacketLossRate()
		);
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void evHn_RendererStatus(RendererStatusEvent ev)
	{
		RendererStatus stat = ev.getRendererStatus();
		this.viewer.updatePlayoutStatus(
			stat.getVidFrameRate()
		);
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void evHn_PerformerState(PerformerStateEvent ev)
	{
		// Not Implemented.
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void evHn_Segment(SegmentEvent ev)
	{
		Dialog.warning(this.viewer, ev.toString());
	}	

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void evHn_FlowUpdate(FlowUpdateEvent ev)
	{
		// Not Implemented.
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void evHn_StreamExceptionEvent(StreamExceptionEvent ev)
	{
		Dialog.error(this.viewer, ev.toString());
	}
}
