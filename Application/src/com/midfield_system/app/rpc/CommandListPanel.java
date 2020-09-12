
package com.midfield_system.app.rpc;

import static com.midfield_system.gui.misc.GuiConstants.BDR_EMPTY_8;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

//------------------------------------------------------------------------------
/**
 * CommandList 
 *
 * Copyright (C) Koji Hashimoto
 *
 * Date Modified: 2020.08.25
 * Koji Hashimoto 
 *
 */

//==============================================================================
@SuppressWarnings("serial")
class CommandListPanel
	extends		JPanel
	implements	ListSelectionListener
{
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private RpcClient rpcClt = null;
	
	private JList<String> cmdLst = null;
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: IMPLEMENTS: ListSelectionListener
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: ListSelectionListener
	//
	@Override
	public void valueChanged(ListSelectionEvent ev)
	{
		if (ev.getValueIsAdjusting()) {
			return;
		}
		String command = this.cmdLst.getSelectedValue();
		this.rpcClt.setSelectedCommand(command);
	}
	
//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------

	//- PACKAGE METHOD ---------------------------------------------------------
	//		
	CommandListPanel(RpcClient rpcClt, String[] cmdArray)
	{
		this.rpcClt = rpcClt;
		setupGui(cmdArray);
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------	

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setupGui(String[] cmdArray)
	{
		this.cmdLst = new JList<String>(cmdArray);
		this.cmdLst.addListSelectionListener(this);
		this.cmdLst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(this.cmdLst);
		
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		setBorder(BDR_EMPTY_8);
	}
}
