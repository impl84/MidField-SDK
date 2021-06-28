
package rpc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JTextField;

//------------------------------------------------------------------------------
/**
 * CommandBox
 *
 * Copyright (C) Koji Hashimoto
 *
 * Date Modified: 2020.08.29
 * Koji Hashimoto 
 *
 */

//==============================================================================
@SuppressWarnings("serial")
class CommandBox
	extends		Box
	implements	ActionListener,
				KeyListener,
				InputMethodListener	
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------	
	private static final String
		STR_SEND_COMMAND	= Messages.getString("CommandBox.0"); //$NON-NLS-1$

//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private RpcClient rpcClt = null;	
	
	private JTextField cmdFld = null;
	private JButton sndBtn = null;
	
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
		if (appCmd.equals(STR_SEND_COMMAND)) {
			evHn_Send();
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
				evHn_Send();
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
	CommandBox(RpcClient rpcClt)
	{
		super(BoxLayout.X_AXIS);
		this.rpcClt = rpcClt;
		
		setupGui();
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void setSelectedCommand(String command)
	{
		this.cmdFld.setText(command);
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------	

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setupGui()
	{
		this.cmdFld = new JTextField();
		this.cmdFld.setEditable(true);
		this.cmdFld.addKeyListener(this);
		this.cmdFld.addInputMethodListener(this);

		this.sndBtn = new JButton(STR_SEND_COMMAND);
		this.sndBtn.addActionListener(this);
		
		add(this.cmdFld);
		add(this.sndBtn);		
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD: actionPerformed()
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void evHn_Send()
	{
		// コマンドを取得する．
		String command = this.cmdFld.getText();
		if (command == null) {
			return;
		}
		// 取得したコマンドをRPC要求に変換してサーバへ送信する．
		this.rpcClt.sendRpcRequest(command);
	}	
}
