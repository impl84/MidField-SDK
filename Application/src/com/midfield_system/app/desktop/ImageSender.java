
package com.midfield_system.app.desktop;

import java.util.List;

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
import com.midfield_system.api.stream.ConnectionMode;
import com.midfield_system.api.stream.DeviceInfo;
import com.midfield_system.api.stream.DeviceInfoManager;
import com.midfield_system.api.stream.IoParam;
import com.midfield_system.api.stream.ProtocolType;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamException;
import com.midfield_system.api.stream.StreamFormat;
import com.midfield_system.api.stream.StreamPerformer;
import com.midfield_system.api.stream.VideoFormat;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.util.Log;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: ImageSender 
 *
 * Date Modified: 2020.08.18
 *
 */

//==============================================================================
public class ImageSender
	implements	StreamEventListener
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------

	// デスクトップイメージをキャプチャする際のフレームレート
	// （値は 2.0/4.0/8.0/16.0 のいずれか．）
	private static final double	DEF_FRAME_RATE = 4.0;
	
	private static final String
		STR_CANT_FIND_SRC_FILTER	= "適切なデスクトップキャプチャフィルタがありません．",
		STR_CANT_FIND_CAP_FORMAT	= "適切なキャプチャフォーマットがありません．";

//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private DesktopEventProcessor consumer = null;
	private StreamPerformer pfmr = null;
	private IoParam outMap = null;
	
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
	ImageSender(DesktopEventProcessor remDskProc)
		throws	SystemException,
				StreamException
	{
		this.consumer = remDskProc;

		// デスクトップイメージキャプチャ用 DeviceInfo を取得する．
		DeviceInfo devInf = getDesktopImageSource();
			// SystemException		
		
		// 適切なフォーマットを取得する．
		StreamFormat capFmt = getSuitableCaptureFormat(devInf);
			// SystemException		
		
		// 入力デバイスで SegmentIo の入力を構成する．		
		SegmentIo segIo = new SegmentIo();
		segIo.configureInputDevice(devInf, capFmt, null, null);
		
		// 出力可能なフォーマット情報リストを取得する．
		List<StreamFormat> lsOutFmt = segIo.getOutputVideoFormatList();
		
		// 出力フォーマット情報を取得する．（ここでは最初の要素を選択）
		VideoFormat outFmt = (VideoFormat)lsOutFmt.get(0);
		
		// SegmentIo の出力を設定する．
		ProtocolType type = ProtocolType.TCP;
		ConnectionMode mode = ConnectionMode.PASSIVE;
		
		segIo.configureOutgoingStream(outFmt, null);
		segIo.setTransportProtocol(type, mode);
		segIo.setPrivateOutput(true);
		
		// Stream Performer の生成と入出力処理の開始．
		setupPerformer(segIo);
			// SystemException, StreamException
	}

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	IoParam getOutputParam()
	{
		return this.outMap;
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
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private DeviceInfo getDesktopImageSource()
		throws	SystemException
	{
		DeviceInfoManager devMgr = DeviceInfoManager.getInstance();
		DeviceInfo dskImgSrc = devMgr.getDefaultDesktopImageSource();
		if (dskImgSrc == null) {
			// IOデバイスリストの中にフィルタが無い．
			throw new SystemException(STR_CANT_FIND_SRC_FILTER);
		}
		return dskImgSrc;
	}
		
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private StreamFormat getSuitableCaptureFormat(DeviceInfo devInf)
		throws	SystemException	
	{
		StreamFormat capFmt = null;

		// 取得したフレームレートのフォーマットを探す．
		List<StreamFormat> lsStmFmt = devInf.getOutputFormatList();
		for (StreamFormat stmFmt : lsStmFmt) {
			if ((stmFmt instanceof VideoFormat) == false) {
				continue;
			}
			//------------------------------------------------------------------
			VideoFormat vidFmt = (VideoFormat)stmFmt;
			double frameRate = vidFmt.getFrameRate();
			if (frameRate == DEF_FRAME_RATE) {
				// 適切なフォーマットが存在する場合：
				capFmt = vidFmt;
				break;
			}
		}
		if (capFmt == null) {
			// 適切なフォーマットが存在しない場合：
			throw new SystemException(STR_CANT_FIND_CAP_FORMAT);
		}
		return capFmt;
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setupPerformer(SegmentIo segIo)
		throws	SystemException,
				StreamException
	{
		this.pfmr = null;
		try {
			// SegmentIo をもとに，Stream Performer を生成する．
			this.pfmr = StreamPerformer.newInstance(segIo);
				// SystemException,StreamException 
			
			this.pfmr.addStreamEventListener(this);

			// 入出力処理を開始する．
			this.pfmr.open();			// StreamException
			this.pfmr.start();		// StreamException
			
			// SegmentIo から出力パラメータを取得する．
			segIo = this.pfmr.getSegmentIo();
			List<IoParam> lsOutPrm = segIo.getOutputParamList();
			this.outMap = lsOutPrm.get(0);
		}
		catch (SystemException | StreamException ex) {
			shutdown();
			throw ex;
		}
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD: StreamEventListener
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void evHn_IoStatus(IoStatusEvent ev)
	{
		// Not Implemented.
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void evHn_RendererStatus(RendererStatusEvent ev)
	{
		// Not Implemented.
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
		Log.warning(ev.toString());
	}	

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void evHn_FlowUpdate(FlowUpdateEvent ev)
	{
		if (ev.getPerformerInfo().isActive() == false) {
			// 受信ストリームが縮退したので，
			// デスクトップキャプチャ処理を終了させる．
			this.consumer.stopControl();
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void evHn_StreamExceptionEvent(StreamExceptionEvent ev)
	{
		Log.error(ev.toString());
	}	
}
