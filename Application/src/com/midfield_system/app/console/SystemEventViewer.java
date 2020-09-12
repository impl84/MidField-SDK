
package com.midfield_system.app.console;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.midfield_system.api.event.SystemEvent;
import com.midfield_system.api.event.SystemEventListener;
import com.midfield_system.api.event.ResourceStatusEvent;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: SystemEventViewer
 *
 * Date Modified: 2017.08.25
 *
 */

//==============================================================================
@SuppressWarnings("serial")
public class SystemEventViewer
	extends		JFrame
	implements	SystemEventListener
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final Dimension DIM_MIN_FRAME = new Dimension(400, 300);
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private PrintOutPanel pnlSysEv = null;
	private PrintOutPanel pnlStat  = null;

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public SystemEventViewer()
	{
		Border bdSysEv = BorderFactory.createEtchedBorder();		
		this.pnlSysEv = new PrintOutPanel();
		this.pnlSysEv.setBorder(new TitledBorder(bdSysEv, "SystemEvent"));

		Border bdStat = BorderFactory.createEtchedBorder();		
		this.pnlStat = new PrintOutPanel();
		this.pnlStat.setBorder(new TitledBorder(bdStat, "ResourceStatusEvent"));
		
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(this.pnlSysEv, BorderLayout.CENTER);
		container.add(this.pnlStat,  BorderLayout.SOUTH);

		setTitle("SystemEventViewer");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		setMinimumSize(DIM_MIN_FRAME);		
	}

//------------------------------------------------------------------------------
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: SystemEventListener	
	//
	@Override
	public void update(SystemEvent ev)
	{
		String strEv = ev.toString();
		
		if (ev instanceof ResourceStatusEvent) {
			this.pnlStat.println(strEv);
		}
		else {
			this.pnlSysEv.println(strEv);
		}
	}
}