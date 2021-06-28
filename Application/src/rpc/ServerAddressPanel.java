
package rpc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.midfield_system.gui.misc.PopupMessage;

//------------------------------------------------------------------------------
/**
 * ServerAddressPanel
 *
 * Copyright (C) Koji Hashimoto
 *
 * Date Modified: 2020.08.25
 * Koji Hashimoto 
 *
 */

//==============================================================================
@SuppressWarnings("serial")
class ServerAddressPanel
	extends		JPanel
	implements	ActionListener,
				KeyListener,
				InputMethodListener
{
	//- PACKAGE CONSTANT VALUE -------------------------------------------------
	static enum ConnectionState
	{
		IDLE,		// アイドル状態 
		CONNECTING,	// コネクション確立処理状態
		CONTROLLING	// 制御状態
	}	

	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String
		STR_START_CONTROL	= Messages.getString("ServerAddressPanel.0"), //$NON-NLS-1$
		STR_STOP_CONTROL	= Messages.getString("ServerAddressPanel.1"); //$NON-NLS-1$
	
	private static final String
		STR_PLEASE_INPUT	= Messages.getString("ServerAddressPanel.2"), //$NON-NLS-1$
		STR_INVALID_ADDR	= Messages.getString("ServerAddressPanel.3"); //$NON-NLS-1$

	private static final String
		STR_REMOTE_HOST		= Messages.getString("ServerAddressPanel.4"); //$NON-NLS-1$
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private RpcClient rpcClt = null;
	
	private JTextField addrFld = null;
	private JButton ctlBtn = null;

	private ConnectionState stat = ConnectionState.IDLE;
	private boolean isEditing = false;	
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD: IMPLEMENTS: ActionListener
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: ActionListener
	//
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		String appCmd = ev.getActionCommand();
		if (appCmd.equals(STR_START_CONTROL)) {
			evHn_StartControl();
		}
		else if (appCmd.equals(STR_STOP_CONTROL)) {
			evHn_StopControl();
		}	
	}	

//------------------------------------------------------------------------------
//  PUBLIC METHOD: IMPLEMENTS: KeyListener
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: KeyListener
	//
	@Override
	public void keyTyped(KeyEvent ev)
	{
		// Not Implemented.
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: KeyListener
	//
	@Override
	public void keyPressed(KeyEvent ev)
	{
		// Not Implemented.
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: KeyListener
	//
	@Override
	public void keyReleased(KeyEvent ev)
	{
		if (ev.getKeyCode() == KeyEvent.VK_ENTER) {
			if (this.isEditing) {
				this.isEditing = false;
			}
			else {
				this.ctlBtn.doClick();
			}
		}
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: IMPLEMENTS: InputMethodListener
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: InputMethodListener
	//
	@Override
	public void caretPositionChanged(InputMethodEvent ev)
	{
		// Not Implemented.
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: InputMethodListener
	//
	@Override
	public void inputMethodTextChanged(InputMethodEvent ev)
	{
		this.isEditing = true;
	}
	
//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	ServerAddressPanel(RpcClient rpcClt)
	{
		this.rpcClt = rpcClt;
		setupGui();	
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	boolean isControllingState()
	{
		boolean isControlling = false;
		if (this.stat == ConnectionState.CONTROLLING) {
			isControlling = true;
		}
		return isControlling;
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void setState(ConnectionState state)
	{
		switch (state) {
		case IDLE:
			this.addrFld.setEnabled(true);		
			this.ctlBtn.setEnabled(true);
			this.ctlBtn.setText(STR_START_CONTROL);		
			this.stat = ConnectionState.IDLE;
			break;
			
		case CONNECTING:
			this.addrFld.setEnabled(false);
			this.ctlBtn.setEnabled(false);		
			this.stat = ConnectionState.CONNECTING;
			break;
			
		case CONTROLLING: 
			this.addrFld.setEnabled(false);
			this.ctlBtn.setEnabled(true);
			this.ctlBtn.setText(STR_STOP_CONTROL);
			this.stat = ConnectionState.CONTROLLING;
			break;
		}
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------	

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setupGui()
	{
		this.addrFld = new JTextField();
		this.addrFld.setEditable(true);
		this.addrFld.addKeyListener(this);
		this.addrFld.addInputMethodListener(this);
		
		this.ctlBtn = new JButton(STR_START_CONTROL);
		this.ctlBtn.addActionListener(this);
		
		setLayout(new BorderLayout());
		add(new JLabel(STR_REMOTE_HOST), BorderLayout.WEST);
		add(this.addrFld, BorderLayout.CENTER);
		add(this.ctlBtn, BorderLayout.EAST);
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD: actionPerformed()
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	void evHn_StartControl()
	{
		// 接続先アドレスを取得する．
		String address = getSelectedAddress();
		if (address == null) {
			return;
		}
		// コネクションアドレスパネルの状態を変更する．
		setState(ConnectionState.CONNECTING);

		// RPCクライアントの処理を開始する．
		this.rpcClt.startControl(address);
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private String getSelectedAddress()
	{
		String dstAddr = this.addrFld.getText();
		if ((dstAddr == null) || (dstAddr.length() == 0)) {
			// アドレスが入力されていない．
			PopupMessage.warning(this.rpcClt, STR_PLEASE_INPUT);
			dstAddr = null;
			return dstAddr;
		}
		// 入力されたアドレスを確認する．
		try {
			InetAddress.getByName(dstAddr);
							// UnknownHostException
			return dstAddr;
		}
		catch (UnknownHostException ex) {
			// 入力されたアドレスが不正．
			PopupMessage.warning(
				this.rpcClt, String.format(STR_INVALID_ADDR, dstAddr)
			);
			dstAddr = null;
			return dstAddr;
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	void evHn_StopControl()
	{
		// RPCクライアントの処理を停止する．
		this.rpcClt.stopControl();
	}	
}
