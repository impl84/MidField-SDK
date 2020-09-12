
package com.midfield_system.app.desktop;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.midfield_system.api.event.SystemEvent;
import com.midfield_system.api.event.SystemEventListener;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.system.SystemProperty;
import com.midfield_system.api.util.Log;
import com.midfield_system.app.util.AppUtilities;
import com.midfield_system.gui.misc.MessagePanel;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: RemoteDesktop
 *
 * Date Modified: 2020.09.14
 *
 */

//==============================================================================
@SuppressWarnings("serial")
public class RemoteDesktop
	extends		JFrame
	implements	Runnable,
				ActionListener,
				SystemEventListener
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String
		STR_FRAME_TITLE				= "遠隔デスクトップ操作",
		STR_MSG_PANEL_TITLE			= "メッセージ出力パネル";
	
	private static final String	
		STR_ACCEPTANCE_BUTTON		= "遠隔操作受け入れ",
		STR_CONTROL_BUTTON			= "遠隔操作用ウィンドウ";
	
	private static final String
		MSG_STARTED_MIDFIELD		= "MidField System の処理を開始しました．",
		MSG_LOCAL_IP_ADDRESS		= "(ローカルIPアドレス：%s)",
		MSG_SHUTDOWN_MIDFIELD		= "MidField System を終了します．",
		MSG_SETUP_PROCESSOR			= "遠隔操作の受け入れを開始します．",
		MSG_SHUTDOWN_PROCESSOR		= "遠隔操作の受け入れを終了します．",
		MSG_SETUP_CONTROLLER		= "遠隔操作用ウィンドウによる遠隔操作処理を開始します．",
		MSG_SHUTDOWN_CONTROLLER		= "遠隔操作用ウィンドウによる遠隔操作処理を終了します．";

	private static final String
		ERR_PROCESSOR_IS_ALREADY_IN_USE		= "既に遠隔操作受け入れ処理中です．",
		ERR_PROCESSOR_IS_NOT_RUNNING		= "遠隔操作受け入れ処理は行われていません．",
		ERR_CONTROLLER_IS_ALREADY_IN_USE	= "既に遠隔操作処理中です．",
		ERR_CONTROLLER_IS_NOT_RUNNING		= "遠隔操作処理は行われていません．";
	
	private static final Dimension DIM_MIN_FRAME = new Dimension(640, 480);
	
//==============================================================================
//  CLASS METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC STATIC METHOD:
//------------------------------------------------------------------------------

	//======== [MAIN METHOD] ===================================================
	//
	public static void main(String[] args)
	{
		RemoteDesktop remoteDesktop = new RemoteDesktop();
		SwingUtilities.invokeLater(remoteDesktop);
	}
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private MfsNode mfs = null;

	private DesktopEventProcessor processor = null;
	private DesktopEventController controller = null;
	
	private JToggleButton btnAcceptance = null;
	private JToggleButton btnControl= null;
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: Runnable
//------------------------------------------------------------------------------	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: Runnable
	//
	@Override
	public void run()
	{
		// GUIをセットアップする．
		setupGui();
		
		// MidField System を起動する．
		setupMidField();

		// 終了処理を登録する．
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				shutdown();
			}
		});		
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: ActionListener
//------------------------------------------------------------------------------	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: ActionListener
	//
	@Override
	public void actionPerformed(ActionEvent ev)
	{
		String actCmd = ev.getActionCommand();
		Object obj = ev.getSource();
		
		switch (actCmd) {
		case STR_ACCEPTANCE_BUTTON: evHn_Acceptance((JToggleButton)obj);break;
		case STR_CONTROL_BUTTON:	evHn_Control((JToggleButton)obj);	break;
		default:														break;
		}
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: SystemEventListener
//------------------------------------------------------------------------------	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: SystemEventListener
	//
	@Override
	public void update(SystemEvent ev)
	{
			// DesktopEventController が終了した．
			// 遠隔操作用ウィンドウのトグルボタンの状態を必要に応じて変える．
			this.controller = null;
			SwingUtilities.invokeLater(() -> {
				if (this.btnControl.isSelected()) {
					this.btnControl.setSelected(false);
				}
			});
		
	}	
		
//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//	
	RemoteDesktop()
	{
		//
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setupGui()
	{
		// MessagePanel をボーダー付きで生成する．
		Border border = BorderFactory.createEtchedBorder();		
		MessagePanel panel = new MessagePanel();
		panel.setBorder(new TitledBorder(border, STR_MSG_PANEL_TITLE));

		// ログの出力先を設定する．
		Log.setLogPrinter(panel);
		
		// トグルボタンとコントロールボックスを生成する．
		this.btnAcceptance = new JToggleButton(STR_ACCEPTANCE_BUTTON);
		this.btnAcceptance.setActionCommand(STR_ACCEPTANCE_BUTTON);
		this.btnAcceptance.addActionListener(this);
		
		this.btnControl = new JToggleButton(STR_CONTROL_BUTTON);
		this.btnControl.setActionCommand(STR_CONTROL_BUTTON);
		this.btnControl.addActionListener(this);

		Box box = Box.createHorizontalBox();
		box.add(this.btnAcceptance);
		box.add(this.btnControl);
		
		// この JFrame のコンテナ区画に
		// メッセージパネルとコントロールボックスを追加する．
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(panel, BorderLayout.CENTER);
		container.add(box, BorderLayout.SOUTH);
		
		// L&F を設定する．
		AppUtilities.setLookAndFeel(this);		

		// タイトルと最小サイズを設定する．
		setTitle(STR_FRAME_TITLE);
		setMinimumSize(DIM_MIN_FRAME);
		
		// 画面中央にフレームの位置を合わせる．
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getScreenSize();
		setLocation(
			dim.width  / 2 - (int)DIM_MIN_FRAME.getWidth()  / 2,
			dim.height / 2 - (int)DIM_MIN_FRAME.getHeight() / 2
		);
		// 可視状態にする．
		setVisible(true);
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setupMidField()
	{
		try {
			// MidField System を初期化する．
			this.mfs = MfsNode.initialize();
				// SystemException
			
			//  このインスタンスを SystemEventListener に追加する．
			this.mfs.addSystemEventListener(this);
			
			// MidField System を起動する．
			this.mfs.activate();
				// SystemException
			
			// MidField System の開始メッセージをログに出力する．
			Log.message();
			Log.message(MSG_STARTED_MIDFIELD);

			// ローカルIPアドレスをログへ出力する．
			SystemProperty sp = SystemProperty.getCurrentProperty();
			String strAddr = sp.getSysLocalIpAddress();
			Log.message(MSG_LOCAL_IP_ADDRESS, strAddr);
			Log.message();
		}
		catch (Exception ex) {
			Log.error(ex);
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void shutdown()
	{
		// 遠隔操作中，または，操作受け入れ中であれば，
		// それぞれの処理を終了する．
		if (this.controller != null) {
			Log.message(MSG_SHUTDOWN_CONTROLLER);
			this.controller.shutdown();
		}
		if (this.processor != null) {
			Log.message(MSG_SHUTDOWN_PROCESSOR);
			this.processor.shutdown();
		}
		// MidField System を終了する．
		if (this.mfs != null) {
			Log.message(MSG_SHUTDOWN_MIDFIELD);
			this.mfs.shutdown();
		}
		// このフレームの終了処理を実行する．
		dispose();
	}	

//------------------------------------------------------------------------------
//  PRIVATE METHOD: ActionEvent ハンドラ
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_Acceptance(JToggleButton btn)
	{
		if (btn.isSelected() == true) {
			if (this.processor == null) {
				try {
					// 遠隔操作受け入れ用トグルボタンが選択状態で，
					// DesktopEventProcessor のインスタンスが存在しない場合，
					// DesktopEventProcessor のインスタンスを生成する．
					Log.message(MSG_SETUP_PROCESSOR);
					this.processor = new DesktopEventProcessor();
				}
				catch (SystemException ex) {
					Log.error(ex);
				}
			}
			else {
				Log.error(ERR_PROCESSOR_IS_ALREADY_IN_USE);
			}
		}
		else {
			if (this.processor != null) {
				// 遠隔操作受け入れ用トグルボタンが非選択状態で，
				// DesktopEventProcessor のインスタンスが存在する場合，
				// DesktopEventProcessor のインスタンスを解放する．
				Log.message(MSG_SHUTDOWN_PROCESSOR);
				this.processor.shutdown();
				this.processor = null;
			}
			else {
				Log.error(ERR_PROCESSOR_IS_NOT_RUNNING);
			}
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_Control(JToggleButton btn)
	{
		if (btn.isSelected() == true) {
			if (this.controller == null) {
				try {
					// 遠隔操作ウィンドウ用トグルボタンが選択状態で，
					// DesktopEventController のインスタンスが存在しない場合，
					// DesktopEventController のインスタンスを生成する．
					Log.message(MSG_SETUP_CONTROLLER);
					this.controller = new DesktopEventController();
				}
				catch (SystemException ex) {
					Log.error(ex);
				}
			}
			else {
				Log.error(ERR_CONTROLLER_IS_ALREADY_IN_USE);
			}
		}
		else {
			if (this.controller != null) {
				// 遠隔操作ウィンドウ用トグルボタンが非選択状態で，
				// DesktopEventController のインスタンスが存在する場合，
				// DesktopEventController のインスタンスを解放する．				
				Log.message(MSG_SHUTDOWN_CONTROLLER);
				this.controller.shutdown();
				this.controller = null;
				
				// 遠隔操作用ウィンドウのトグルボタンの状態を必要に応じて変える．
				if (this.btnControl.isSelected()) {
					this.btnControl.setSelected(false);
				}
			}
			else {
				Log.error(ERR_CONTROLLER_IS_NOT_RUNNING);
			}
		}
	}
}