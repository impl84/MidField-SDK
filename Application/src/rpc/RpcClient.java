
package rpc;

import static com.midfield_system.gui.misc.GuiConstants.BDR_EMPTY_8;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Set;

import javax.swing.JFrame;

import com.midfield_system.gui.misc.MessagePanel;
import com.midfield_system.gui.misc.PopupMessage;

import rpc.ServerAddressPanel.ConnectionState;
import util.AppUtilities;

//------------------------------------------------------------------------------
/**
 * RpcClient 
 *
 * Copyright (C) Koji Hashimoto
 *
 * Date Modified: 2020.08.25
 * Koji Hashimoto 
 *
 */

//==============================================================================
@SuppressWarnings("serial")
public class RpcClient
	extends		JFrame
	implements	Runnable
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String
		STR_PROGRAM_NAME	= Messages.getString("RpcClient.0"),	//$NON-NLS-1$
		STR_TITLE_FORMAT	= STR_PROGRAM_NAME + " (%s)",			//$NON-NLS-1$
		STR_THREAD_NAME		= STR_PROGRAM_NAME;
	
	private static final String
		STR_CANT_CONNECT	= Messages.getString("RpcClient.2"),	//$NON-NLS-1$
		STR_CON_ERROR		= Messages.getString("RpcClient.3");	//$NON-NLS-1$
	
	public static final String
		LINE_SEPARATOR		= Messages.getString("RpcClient.4"),	 //$NON-NLS-1$
		LINE_HEADER			= Messages.getString("RpcClient.5");	 //$NON-NLS-1$
	
	private static final int
		RPC_PORT_NUMBER		= 60202;
	
	private static final int
		PRE_FRAME_WIDTH		= 840,
		PRE_FRAME_HEIGHT	= 540,
		MIN_FRAME_WIDTH		= 640,
		MIN_FRAME_HEIGHT	= 480;
	
	private static final int
		ROW_RESULT_MESSAGES = 160;
	
//==============================================================================
//  CLASS METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC STATIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC STATIC METHOD ---------------------------------------------------
	//
	public static void main(String[] args)
	{
		new RpcClient();
	}
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	private String strSvrAddr = null;
	private Socket socket = null;
	private CommandParser parser = null;
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	private Thread thread = null;

	private CommandListPanel cmdLstPnl = null;
	private ServerAddressPanel svrAddrPnl = null;
	private CommandBox cmdBox = null;
	private MessagePanel msgPnl = null;
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	public RpcClient()
	{
		// コマンドハンドラを生成する．
		this.parser = new CommandParser();

		// GUIをセットアップする．
		setupGui();
	}
	
//------------------------------------------------------------------------------
//  PUBLIC METHOD: IMPLEMENTS: Runnable
//------------------------------------------------------------------------------	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: Runnable
	//
	@Override
	public void run()
	{
		// コネクションをセットアップする．
		boolean isOk = setupConnection(this.strSvrAddr);
		if (isOk == false) {
			return;
		}
		// RPC応答の受信と表示を繰り返す．
		do {
			try {
				String result = this.reader.readLine();
				if (result != null) {
					this.msgPnl.println(result);
				}
				else {
					isOk = false;
				}
			}
			catch (IOException ex) {
				isOk = false;
			}
		} while (isOk);
	}
	
//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void startControl(String address)
	{
		// 接続先アドレスを保持する．
		this.strSvrAddr = address;
		if (this.strSvrAddr == null) {
			return;
		}
		// コネクション確立およびコマンド実行結果受信用スレッドを生成・開始する．
		this.thread = new Thread(this, STR_THREAD_NAME);
		this.thread.start();
	}

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void stopControl()
	{
		// ソケットを閉じる．
		try {
			if (this.socket != null) {
				this.socket.close();
					// IOException
			}
		}
		catch (IOException ex) {
			PopupMessage.warning(this, ex);
		}
		// RPC応答受信用の Reader を閉じる．
		try {
			if (this.reader != null) {
				this.reader.close();
					// IOException
			}
		}
		catch (IOException ex) {
			PopupMessage.warning(this, ex);
		}
		// RPC要求送信用の Writer を閉じる．
		if (this.writer != null) {
			this.writer.close();
		}
		// スレッドの終了を待つ．
		try {
			if (this.thread != null) {
				this.thread.join();
					// InterruptedException
			}
		}
		catch (InterruptedException ex) {
			PopupMessage.warning(this, ex);
		}
		// 各変数をクリアしておく．
		this.reader = null;
		this.writer = null;
		this.socket = null;
		this.thread = null;

		// サーバアドレスパネルの状態を変更する．
		this.svrAddrPnl.setState(ConnectionState.IDLE);		
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void setSelectedCommand(String command)
	{
		this.cmdBox.setSelectedCommand(command);
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void sendRpcRequest(String command) 
	{
		if (this.writer == null) {
			return;
		}
		// 遠隔コマンドをJSON文字列(RPC要求)へ変換する．
		String request = null;
		try {
			request = this.parser.parseCommand(command);
			if (request == null) {
				return;
			}
		}
		catch (Exception ex) {
			PopupMessage.warning(this, ex);
			return;
		}
		// RPC要求送信用 PrintWriter にエラーが発生しているか確認する．
		boolean hasError = this.writer.checkError(); 
		if (hasError == false) {
			// 遠隔コマンドを表示する．
			this.msgPnl.println(LINE_SEPARATOR);
			this.msgPnl.println(LINE_HEADER + command);
			this.msgPnl.println(LINE_SEPARATOR);
			
			// RPC要求を表示する．
			this.msgPnl.println(request);

			// RPC要求をサーバへ送信する．
			this.writer.println(request);
		}
		else {
			// エラーが発生している．警告メッセージをポップアップする．
			String msg = String.format(STR_CON_ERROR, this.strSvrAddr);
			PopupMessage.warning(this, msg);
			
			// RPCクライアントの処理を停止する．
			stopControl();
		}
	}	

//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setupGui()
	{
		// コマンドリストパネルを生成する．
		Set<String> nameSet = this.parser.getMethodNameSet();
		String[] cmdArray = nameSet.toArray(new String[0]);
		this.cmdLstPnl = new CommandListPanel(this, cmdArray);

		// サーバアドレス入力用コンポーネントを生成する．
		this.svrAddrPnl = new ServerAddressPanel(this);

		// コマンド入力・送信用コンポーネントを生成する．
		this.cmdBox = new CommandBox(this);
	
		// 出力用コンポーネントを生成する．
		this.msgPnl = new MessagePanel(ROW_RESULT_MESSAGES);
		this.msgPnl.setBorder(BDR_EMPTY_8);

		// コンポーネントをレイアウトする．
		this.msgPnl.add(this.svrAddrPnl, BorderLayout.NORTH);
		this.msgPnl.add(this.cmdBox, BorderLayout.SOUTH);
		setLayout(new BorderLayout());
		add(this.cmdLstPnl, BorderLayout.WEST);
		add(this.msgPnl, BorderLayout.CENTER);
		
		// フレームサイズを設定する．
		setMinimumSize(new Dimension(MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT));
		setPreferredSize(new Dimension(PRE_FRAME_WIDTH, PRE_FRAME_HEIGHT));
		setSize(PRE_FRAME_WIDTH, PRE_FRAME_HEIGHT);
		
		// フレームのタイトルを設定する．
		setTitle(STR_PROGRAM_NAME);
		
		// L&F を設定する．
		AppUtilities.setLookAndFeel(this);

		// 初期表示位置を設定する．
		pack();
		Dimension curSize = getSize();
		setPreferredSize(curSize);		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimScr = toolkit.getScreenSize();
		setLocation(
			(dimScr.width  - curSize.width)  / 2,
			(dimScr.height - curSize.height) / 2
		);
		// 終了処理を登録する．
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				RpcClient.this.cleanup();
			}
		});
		// 可視状態にする．
		setVisible(true);
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void cleanup()
	{
		// 制御中であればRPCクライアントの処理を停止する．
		if (this.svrAddrPnl.isControllingState()) {
			stopControl();
		}
		// このフレームの終了処理を実行する．
		dispose();
	}

//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private boolean setupConnection(String strAddr) 
	{	
		boolean isOk = true;
		
		InetAddress inetAddr = null;
		int portNum = RPC_PORT_NUMBER;
		try {
			inetAddr = InetAddress.getByName(strAddr);
				// 	UnknownHostException

			// コネクションをセットアップする．
			this.socket = new Socket(inetAddr, portNum);
				// UnknownHostException
				
			InputStream in = this.socket.getInputStream();
				// IOException
			OutputStream out = this.socket.getOutputStream();
				// IOException
			this.reader = new BufferedReader(new InputStreamReader(in));
			this.writer = new PrintWriter(out, true);
				// Exception

			// サーバアドレスパネルの状態を変更する．
			this.svrAddrPnl.setState(ConnectionState.CONTROLLING);
			
			// フレームのタイトルを変更する．
			setTitle(String.format(STR_TITLE_FORMAT, strAddr));
		}
		catch (Exception ex) {
			String msg = String.format(STR_CANT_CONNECT,
				strAddr, Integer.valueOf(portNum), ex.getMessage()
			);
			PopupMessage.warning(this, msg);
			
			// サーバアドレスパネルの状態を変更する．
			this.svrAddrPnl.setState(ConnectionState.IDLE);
			
			isOk = false;
		}
		return isOk;
	}	
}
