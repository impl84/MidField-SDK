
package com.midfield_system.app.console;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.LinkedList;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: PrintOutPanel
 *
 * Date Modified: 2017.08.25
 *
 */

//==============================================================================
@SuppressWarnings("serial")
public class PrintOutPanel
	extends		JPanel
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final int DEF_MAX_ROWS = 40;
	private static final Font DEF_FONT = new Font("ÇlÇr ÉSÉVÉbÉN", Font.PLAIN, 12); 

//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private int maxRows = 0;
	private JList<String> lstLog = null;
	private LinkedList<String> lnkLog = null;

//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	public PrintOutPanel()
	{
		this.maxRows = DEF_MAX_ROWS;
		this.lnkLog = new LinkedList<String>();
		this.lstLog = new JList<String>();
		this.lstLog.setFont(DEF_FONT);

		setLayout(new BorderLayout());
		add(new JScrollPane(this.lstLog), BorderLayout.CENTER);

		for (int i = 0; i < this.maxRows; i++) {
			this.lnkLog.add(" ");
		}
		int size = this.lnkLog.size();
		String[] logs = new String[size];
		this.lnkLog.toArray(logs);
		
		this.lstLog.setListData(logs);
		this.lstLog.ensureIndexIsVisible(this.lnkLog.size() - 1);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public synchronized void println(String line)
	{
		if (line == null) {
			return;
		}
		//----------------------------------------------------------------------
		if (line.length() > 0) {
			this.lnkLog.add(line);
		}
		else {
			this.lnkLog.add(" ");
		}
		int rows = this.lnkLog.size();
		int numOver = rows - this.maxRows;
		while (numOver-- > 0) {
			this.lnkLog.removeFirst();
		}
		int size = this.lnkLog.size();
		String[] logs = new String[size];
		this.lnkLog.toArray(logs);

		this.lstLog.setListData(logs);
	}
}