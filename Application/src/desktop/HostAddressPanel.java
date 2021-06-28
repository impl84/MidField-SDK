
package desktop;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: HostAddressPanel 
 *
 * Date Modified: 2017.09.06
 *
 */

//==============================================================================
@SuppressWarnings("serial")
public class HostAddressPanel
	extends		JPanel
	implements	KeyListener,
				InputMethodListener
{
	//- PUBLIC CONSTANT VALUE --------------------------------------------------
	public static final String
		STR_START_CONTROL	= "開始",
		STR_STOP_CONTROL	= "停止";
	
	public static final String
		STR_PLEASE_INPUT	= "ホスト名 / IPアドレスを入力してください．",
		STR_INVALID_ADDR	= "%s への接続に失敗しました．";

	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String
		STR_REMOTE_HOST		= "  ホスト名/IPアドレス : ";

	private static final Dimension PREF_DIM_ADDRESS_FIELD = new Dimension(160, 24);
	
	private static enum ConnectionState
	{
		IDLE,		// アイドル状態 
		CONNECTING,	// コネクション確立処理状態
		CONTROLLING	// 制御状態
	}
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private Component baseComp = null;
	private JTextField txtAddr = null;
	private JButton btnControl = null;

	private ConnectionState stat = ConnectionState.IDLE;
	private boolean isEditing = false;	
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public HostAddressPanel(Component baseComp, ActionListener listener)
	{
		this.baseComp = baseComp;
		setup(listener);	
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public boolean isControllingState()
	{
		boolean isControlling = false;
		if (this.stat == ConnectionState.CONTROLLING) {
			isControlling = true;
		}
		return isControlling;
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public boolean isIdleState()
	{
		boolean isIdle = false;
		if (this.stat == ConnectionState.IDLE) {
			isIdle = true;
		}
		return isIdle;
	}	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public String getSelectedAddress()
	{
		String dstAddr = this.txtAddr.getText();
		if ((dstAddr == null) || (dstAddr.length() == 0)) {
			// アドレスが入力されていない．
			Dialog.warning(this.baseComp, STR_PLEASE_INPUT);
			dstAddr = null;
			return dstAddr;
		}
		//----------------------------------------------------------------------
		try {
			// 入力されているアドレスの確認．
			InetAddress.getByName(dstAddr);
							// UnknownHostException
			return dstAddr;
		}
		catch (UnknownHostException ex) {
			// 入力されているアドレスが不正．
			Dialog.warning(
				this.baseComp, String.format(STR_INVALID_ADDR, dstAddr)
			);
			dstAddr = null;
			return dstAddr;
		}
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void changeToControllingState()
	{
		this.txtAddr.setEnabled(false);
		this.btnControl.setEnabled(true);
		this.btnControl.setText(STR_STOP_CONTROL);
		
		this.stat = ConnectionState.CONTROLLING;
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void changeToIdleState()
	{
		this.txtAddr.setEnabled(true);		
		this.btnControl.setEnabled(true);
		this.btnControl.setText(STR_START_CONTROL);		

		this.stat = ConnectionState.IDLE;
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public void changeToConnectingState()
	{
		this.txtAddr.setEnabled(false);
		this.btnControl.setEnabled(false);		

		this.stat = ConnectionState.CONNECTING;
	}
	
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
				this.btnControl.doClick();
			}
		}
	}
	
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
//  PRIVATE METHOD:
//------------------------------------------------------------------------------	

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setup(ActionListener listener)
	{
		this.txtAddr = new JTextField();
		this.txtAddr.setPreferredSize(PREF_DIM_ADDRESS_FIELD);
		this.txtAddr.setEditable(true);
		
		this.btnControl = new JButton(STR_START_CONTROL);
		this.btnControl.addActionListener(listener);
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(new JLabel(STR_REMOTE_HOST));
		add(this.txtAddr);
		add(this.btnControl);
	}
}
