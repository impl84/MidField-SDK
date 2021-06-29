
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
		// �R�}���h�n���h���𐶐�����D
		this.parser = new CommandParser();

		// GUI���Z�b�g�A�b�v����D
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
		// �R�l�N�V�������Z�b�g�A�b�v����D
		boolean isOk = setupConnection(this.strSvrAddr);
		if (isOk == false) {
			return;
		}
		// RPC�����̎�M�ƕ\�����J��Ԃ��D
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
		// �ڑ���A�h���X��ێ�����D
		this.strSvrAddr = address;
		if (this.strSvrAddr == null) {
			return;
		}
		// �R�l�N�V�����m������уR�}���h���s���ʎ�M�p�X���b�h�𐶐��E�J�n����D
		this.thread = new Thread(this, STR_THREAD_NAME);
		this.thread.start();
	}

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void stopControl()
	{
		// �\�P�b�g�����D
		try {
			if (this.socket != null) {
				this.socket.close();
					// IOException
			}
		}
		catch (IOException ex) {
			PopupMessage.warning(this, ex);
		}
		// RPC������M�p�� Reader �����D
		try {
			if (this.reader != null) {
				this.reader.close();
					// IOException
			}
		}
		catch (IOException ex) {
			PopupMessage.warning(this, ex);
		}
		// RPC�v�����M�p�� Writer �����D
		if (this.writer != null) {
			this.writer.close();
		}
		// �X���b�h�̏I����҂D
		try {
			if (this.thread != null) {
				this.thread.join();
					// InterruptedException
			}
		}
		catch (InterruptedException ex) {
			PopupMessage.warning(this, ex);
		}
		// �e�ϐ����N���A���Ă����D
		this.reader = null;
		this.writer = null;
		this.socket = null;
		this.thread = null;

		// �T�[�o�A�h���X�p�l���̏�Ԃ�ύX����D
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
		// ���u�R�}���h��JSON������(RPC�v��)�֕ϊ�����D
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
		// RPC�v�����M�p PrintWriter �ɃG���[���������Ă��邩�m�F����D
		boolean hasError = this.writer.checkError(); 
		if (hasError == false) {
			// ���u�R�}���h��\������D
			this.msgPnl.println(LINE_SEPARATOR);
			this.msgPnl.println(LINE_HEADER + command);
			this.msgPnl.println(LINE_SEPARATOR);
			
			// RPC�v����\������D
			this.msgPnl.println(request);

			// RPC�v�����T�[�o�֑��M����D
			this.writer.println(request);
		}
		else {
			// �G���[���������Ă���D�x�����b�Z�[�W���|�b�v�A�b�v����D
			String msg = String.format(STR_CON_ERROR, this.strSvrAddr);
			PopupMessage.warning(this, msg);
			
			// RPC�N���C�A���g�̏������~����D
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
		// �R�}���h���X�g�p�l���𐶐�����D
		Set<String> nameSet = this.parser.getMethodNameSet();
		String[] cmdArray = nameSet.toArray(new String[0]);
		this.cmdLstPnl = new CommandListPanel(this, cmdArray);

		// �T�[�o�A�h���X���͗p�R���|�[�l���g�𐶐�����D
		this.svrAddrPnl = new ServerAddressPanel(this);

		// �R�}���h���́E���M�p�R���|�[�l���g�𐶐�����D
		this.cmdBox = new CommandBox(this);
	
		// �o�͗p�R���|�[�l���g�𐶐�����D
		this.msgPnl = new MessagePanel(ROW_RESULT_MESSAGES);
		this.msgPnl.setBorder(BDR_EMPTY_8);

		// �R���|�[�l���g�����C�A�E�g����D
		this.msgPnl.add(this.svrAddrPnl, BorderLayout.NORTH);
		this.msgPnl.add(this.cmdBox, BorderLayout.SOUTH);
		setLayout(new BorderLayout());
		add(this.cmdLstPnl, BorderLayout.WEST);
		add(this.msgPnl, BorderLayout.CENTER);
		
		// �t���[���T�C�Y��ݒ肷��D
		setMinimumSize(new Dimension(MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT));
		setPreferredSize(new Dimension(PRE_FRAME_WIDTH, PRE_FRAME_HEIGHT));
		setSize(PRE_FRAME_WIDTH, PRE_FRAME_HEIGHT);
		
		// �t���[���̃^�C�g����ݒ肷��D
		setTitle(STR_PROGRAM_NAME);
		
		// L&F ��ݒ肷��D
		AppUtilities.setLookAndFeel(this);

		// �����\���ʒu��ݒ肷��D
		pack();
		Dimension curSize = getSize();
		setPreferredSize(curSize);		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimScr = toolkit.getScreenSize();
		setLocation(
			(dimScr.width  - curSize.width)  / 2,
			(dimScr.height - curSize.height) / 2
		);
		// �I��������o�^����D
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				RpcClient.this.cleanup();
			}
		});
		// ����Ԃɂ���D
		setVisible(true);
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void cleanup()
	{
		// ���䒆�ł����RPC�N���C�A���g�̏������~����D
		if (this.svrAddrPnl.isControllingState()) {
			stopControl();
		}
		// ���̃t���[���̏I�����������s����D
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

			// �R�l�N�V�������Z�b�g�A�b�v����D
			this.socket = new Socket(inetAddr, portNum);
				// UnknownHostException
				
			InputStream in = this.socket.getInputStream();
				// IOException
			OutputStream out = this.socket.getOutputStream();
				// IOException
			this.reader = new BufferedReader(new InputStreamReader(in));
			this.writer = new PrintWriter(out, true);
				// Exception

			// �T�[�o�A�h���X�p�l���̏�Ԃ�ύX����D
			this.svrAddrPnl.setState(ConnectionState.CONTROLLING);
			
			// �t���[���̃^�C�g����ύX����D
			setTitle(String.format(STR_TITLE_FORMAT, strAddr));
		}
		catch (Exception ex) {
			String msg = String.format(STR_CANT_CONNECT,
				strAddr, Integer.valueOf(portNum), ex.getMessage()
			);
			PopupMessage.warning(this, msg);
			
			// �T�[�o�A�h���X�p�l���̏�Ԃ�ύX����D
			this.svrAddrPnl.setState(ConnectionState.IDLE);
			
			isOk = false;
		}
		return isOk;
	}	
}
