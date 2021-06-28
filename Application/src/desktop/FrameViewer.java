
package desktop;

import static desktop.DesktopEvent.ActionType.KEY_PRESS;
import static desktop.DesktopEvent.ActionType.KEY_RELEASE;
import static desktop.DesktopEvent.ActionType.MOUSE_MOVE;
import static desktop.DesktopEvent.ActionType.MOUSE_PRESS;
import static desktop.DesktopEvent.ActionType.MOUSE_RELEASE;
import static desktop.DesktopEvent.ActionType.MOUSE_WHEEL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.midfield_system.api.stream.IoParam;
import com.midfield_system.api.stream.StreamException;
import com.midfield_system.api.stream.StreamFormat;
import com.midfield_system.api.system.PacketIoException;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.util.Constants;
import com.midfield_system.api.util.Log;
import com.midfield_system.api.viewer.VideoCanvas;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: FrameViewer 
 *
 * Date Modified: 2020.07.15
 *
 */

//==============================================================================
@SuppressWarnings("serial")
public class FrameViewer
	extends		Frame
	implements	KeyListener,
				MouseListener,
				MouseMotionListener,
				MouseWheelListener,
				ActionListener
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String
		TITLE_REMOTE_DESKTOP	= "遠隔デスクトップ",
		STR_TITLE_FORMAT		= TITLE_REMOTE_DESKTOP + " (%s)";
	
	private static final String
		STR_FRAME_RATE		= "  再生フレームレート : %.2f [fps] ",
		STR_BIT_RATE		= "  受信ビットレート : %.2f [Mbps] ",
		STR_PKT_LOSS_RATE	= "  受信パケットロス率 : %.2f [%%] ";

	private static final String	
		STR_CONTROL_REFUSED	= "遠隔デスクトップ操作要求が拒否されました．",
		STR_FAILED_CAUSE	= "%s\n%s",
		STR_PLEASE_CHECK	= "\\n遠隔/ローカルホストの設定を確認して下さい．";
	
	private static final String
		STR_DESKTOP_FORMAT	= "遠隔デスクトップフォーマット : %s";
			
	private static final int
		PRE_FRAME_WIDTH		= 640,
		PRE_FRAME_HEIGHT	= 480,
		MIN_FRAME_WIDTH		= 640,
		MIN_FRAME_HEIGHT	= 480;
	
	private static final Dimension
		DIM_MIN = new Dimension(MIN_FRAME_WIDTH / 2, MIN_FRAME_HEIGHT / 2);

//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------	
	private DesktopEventController controller = null;
	private ImageReceiver imgReciever = null;
	
	private HostAddressPanel pnlConAddr = null;
	private JComponent desktopPane = null;
	
	private JLabel
		labFrameRate	= null,
		labBitRate		= null,
		labPktLossRate	= null;
	
	private Container dummyComponent = new Container();

//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD: KeyListener
//------------------------------------------------------------------------------	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: KeyListener
	//
	@Override
	public void keyPressed(KeyEvent ev)
	{
		ev = reCreateKeyEvent(ev);
		this.controller.dispatchControlMessage(KEY_PRESS, ev);		
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: KeyListener
	//
	@Override
	public void keyReleased(KeyEvent ev)
	{
		ev = reCreateKeyEvent(ev);		
		this.controller.dispatchControlMessage(KEY_RELEASE, ev);		
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: KeyListener
	//
	@Override
	public void keyTyped(KeyEvent ev)
	{
		// Not Implemented.		
	}

//------------------------------------------------------------------------------
//  PUBLIC METHOD: MouseListener
//------------------------------------------------------------------------------	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: MouseListener
	//
	@Override
	public void mouseClicked(MouseEvent ev)
	{
		// Not Implemented.		
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: MouseListener
	//
	@Override
	public void mouseEntered(MouseEvent ev)
	{
		// Not Implemented.		
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: MouseListener
	//
	@Override
	public void mouseExited(MouseEvent ev)
	{
		// Not Implemented.		
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: MouseListener
	//
	@Override
	public void mousePressed(MouseEvent ev)
	{
		ev = reCreateMouseEvent(ev);
		this.controller.dispatchControlMessage(MOUSE_PRESS, ev);		
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: MouseListener
	//
	@Override
	public void mouseReleased(MouseEvent ev)
	{
		ev = reCreateMouseEvent(ev);
		this.controller.dispatchControlMessage(MOUSE_RELEASE, ev);
	}

//------------------------------------------------------------------------------
//  PUBLIC METHOD: MouseMotionListener
//------------------------------------------------------------------------------	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: MouseMotionListener
	//
	@Override
	public void mouseDragged(MouseEvent ev)
	{
		ev = reCreateMouseEvent(ev);
		this.controller.dispatchControlMessage(MOUSE_MOVE, ev);		
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: MouseMotionListener
	//
	@Override
	public void mouseMoved(MouseEvent ev)
	{
		ev = reCreateMouseEvent(ev);
		this.controller.dispatchControlMessage(MOUSE_MOVE, ev);
	}

//------------------------------------------------------------------------------
//  PUBLIC METHOD: MouseWheelListener
//------------------------------------------------------------------------------	
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//- IMPLEMENTS: MouseWheelListener
	//
	@Override
	public void mouseWheelMoved(MouseWheelEvent ev)
	{
		this.controller.dispatchControlMessage(MOUSE_WHEEL, ev);
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
		String appCmd = ev.getActionCommand();
		if (appCmd.equals(HostAddressPanel.STR_START_CONTROL)) {
			evHn_StartControl();
		}
		else if (appCmd.equals(HostAddressPanel.STR_STOP_CONTROL)) {
			evHn_StopControl();
		}
	}
		
//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//	
	FrameViewer(DesktopEventController dskCtlr)
	{
		this.controller = dskCtlr;
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void setupGui()
	{
		// コネクションアドレスパネルを生成する．
		this.pnlConAddr = new HostAddressPanel(this, this);
		
		// メッセージ出力コンポーネントを生成する．
		Container msgComp = setupMessageComponents();

		// このフレームに入出力コンポーネントを追加する．
		setLayout(new BorderLayout());
		add(this.pnlConAddr, BorderLayout.NORTH);
		add(msgComp, BorderLayout.SOUTH);

		// フレームサイズを設定する．
		setMinimumSize(new Dimension(MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT));
		setPreferredSize(new Dimension(PRE_FRAME_WIDTH, PRE_FRAME_HEIGHT));
		setSize(PRE_FRAME_WIDTH, PRE_FRAME_HEIGHT);

		// フレームの背景色とタイトルを設定する．
		setBackground(Color.BLUE);
		setTitle(TITLE_REMOTE_DESKTOP);

		// 終了処理を登録する．
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				shutdown();
			}
		});
		// 画面中央にフレームの位置を合わせる．
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getScreenSize();
		setLocation(
			dim.width  / 2 - PRE_FRAME_WIDTH  / 2,
			dim.height / 2 - PRE_FRAME_HEIGHT / 2
		);		
		// 可視状態にする．
		setVisible(true);
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void shutdown()
	{
		if (this.pnlConAddr.isControllingState()) {
			// 制御中：
			
			// 制御停止イベントを配送する． 
			this.controller.stopControl();
		
			// ImageReceiver の終了処理を実行する．
			this.imgReciever.shutdown();
		}
		// RemoteDesktopController を削除する．
		this.controller.delete();
		
		// このフレームの終了処理を実行する．
		dispose();
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void controlAccepted(IoParam inPrm)
	{
		try {
			// ImageReceiver を生成して，VideoCanvas を取得する．
			this.imgReciever = new ImageReceiver(this);
			VideoCanvas vidCvs = this.imgReciever.createVideoCanvas(inPrm);
				// SystemException, StreamException
			
			// VideoCanvas のセットアップ．
			vidCvs.enableInputMethods(false);

			vidCvs.addKeyListener(this);
			vidCvs.addMouseListener(this);
			vidCvs.addMouseMotionListener(this);
			vidCvs.addMouseWheelListener(this);
			
			// デスクトップ区画のセットアップ．
			newDesktopPane(vidCvs, inPrm);
			
			// コネクションアドレスパネルの状態を変更する．
			this.pnlConAddr.changeToControllingState();
			
			// フレームのタイトルを変更する．
			setTitle(String.format(
				STR_TITLE_FORMAT, this.pnlConAddr.getSelectedAddress()
			));

			// デスクトップイメージの受信再生処理を開始する．
			this.imgReciever.start();
				// StreamException
			
			// フォーマット情報出力
			StreamFormat stmFmt = inPrm.getStreamFormat();
			Log.message(String.format(STR_DESKTOP_FORMAT, stmFmt.toString()));
		}
		catch (SystemException | StreamException ex) {
			StreamFormat stmFmt = inPrm.getStreamFormat();
			Dialog.warning(this, String.format(STR_FAILED_CAUSE,
				ex.getMessage(), stmFmt.toString()
			));
		}
	}

	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void failedMessageHandler(PacketIoException ex)
	{
		synchronized (this.pnlConAddr) {
			// アイドル状態であれば戻る．
			if (this.pnlConAddr.isIdleState()) {
				return;
			}
			// ImageReceiver の終了処理を実行する．
			if (this.pnlConAddr.isControllingState()) {
				// 制御中：
			
				this.imgReciever.shutdown();
				
				// デスクトップ区画を削除する．
				deleteDesktopPane();
			}
			// フレームのタイトルを変更する．
			setTitle(TITLE_REMOTE_DESKTOP);

			// コネクションアドレスパネルの状態を変更する．
			this.pnlConAddr.changeToIdleState();
		
			// メッセージパネルの値を初期化する．
			updatePlayoutStatus(0.0);
			updateConnectionStatus(0, 0.0);

			// メッセージ送信例外に関するメッセージをポップアップする．
			String failMsg = null;
			Throwable cause = ex.getCause();
			if (cause != null) {
				failMsg = String.format(STR_FAILED_CAUSE,
					ex.getMessage(), cause.getMessage()
				);
				failMsg += STR_PLEASE_CHECK;
			}
			else {
				failMsg = ex.getMessage();
				failMsg += STR_PLEASE_CHECK;
			}
			Dialog.warning(this, failMsg);
		}
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void controlRefused(SystemException ex)
	{
		// 受入拒否に関するメッセージをポップアップする．
		String message = null;
		if (ex != null) {
			message = String.format(STR_FAILED_CAUSE,
				STR_CONTROL_REFUSED, ex.getMessage()
			);
		}
		else {
			message = STR_CONTROL_REFUSED;
		}
		Dialog.warning(this, message);
		
		// コネクションアドレスパネルの状態を変更する．
		this.pnlConAddr.changeToIdleState();
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void updatePlayoutStatus(double frameRate)
	{
		this.labFrameRate.setText(String.format(STR_FRAME_RATE,
			Double.valueOf(frameRate)
		));
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void updateConnectionStatus(int bitRate, double lossRate)
	{
		this.labBitRate.setText(String.format(STR_BIT_RATE,
			Double.valueOf(bitRate / Constants.MEGA)
		));
		
		this.labPktLossRate.setText(String.format(STR_PKT_LOSS_RATE,
			Double.valueOf(lossRate)
		));
	}
	
//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------
		
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	//	2017/9/6 KeyEvent を生成し直す．
	//	KeyEvent は Serializable を実装しているが，KeyEvent の中に含まれている
	//  ソースコンポーネントの VideoCanvas により，シリアライズする際，
	//  例外が発生する．
	//  VideoCanvas も Serializable を実装しているが，内部にシリアライズできない
	//  インスタンスへの参照を保持しているものと思われる．
	//  動作上，ソースコンポーネントが VideoCanvas である必要は無いので，
	//  ダミーのコンポーネントをソースコンポーネントとするイベントを生成し，
	//  それをシリアライズして遠隔へ送信することにした．
	//
	//  MouseEvent でも同様に発生すると考えられるが，
	//  MouseEvent では発生しない．KeyEvent でも MouseEvent でも，
	//  ソースコンポーネントは同じく VideoCanvas なのだが，
	//  おそらく，内部で保持するインスタンスへの参照に違いがあると思われる．
	//  
	//  この現象は，以前は KeyEvent でも発生しなかった．
	//  今後，MouseEvent でも発生する可能性を考慮して，MouseEvent でも
	//  ダミーのコンポーネントをソースコンポーネントとするよう修正を加えた．
	//	
	private KeyEvent reCreateKeyEvent(KeyEvent ev)
	{
		@SuppressWarnings("deprecation")
		KeyEvent kev = new KeyEvent(
			dummyComponent,
			ev.getID(),
			ev.getWhen(),
			ev.getModifiers(),
			ev.getKeyCode(),
			ev.getKeyChar(),
			ev.getKeyLocation()
		);
		return kev;
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	//	2017/9/6 
	//  KeyEvent 同様，ダミーのコンポーネントをソースコンポーネントとして
	//  利用する様修正．
	//
	//	2009/1/20 MouseEvent を生成し直す．
	//	Windows Vista では，VideoCanvas のマウス座標取得に不具合がある．
	//	mousePressed() と mouseMoved() の引数となる MouseEvent から取得
	//	できる相対座標値と絶対座標値があっていない．絶対座標用の変数に
	//	相対座標値が格納されている．相対座標用の変数には不明な値が格納
	//	されている．他のリスナーメソッドでは問題ない．
	//	Windows XP では発生しない．
	//	Java のバージョンは 1.6.0 update 7．
	//	Java の不具合ではないかと思われ，いずれ改善されるかとも思うが，
	//	このままでは使えない．
	//	必要となる相対座標値は，下記の方法でも取得できる．
	//	当面，このメソッドを使い生成し直したマウスイベントを利用する．
	//	
	private MouseEvent reCreateMouseEvent(MouseEvent ev)
	{
		PointerInfo inf = MouseInfo.getPointerInfo();
		Point point = inf.getLocation();
		SwingUtilities.convertPointFromScreen(point, ev.getComponent());
		
		@SuppressWarnings("deprecation")
		MouseEvent mev = new MouseEvent(
			dummyComponent,
			ev.getID(),
			ev.getWhen(),
			ev.getModifiers(),
			point.x,
			point.y,
			ev.getClickCount(),
			ev.isPopupTrigger(),
			ev.getButton()
		);
		return mev;
	}
	
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private Container setupMessageComponents()
	{
		this.labFrameRate	= new JLabel();
		this.labBitRate		= new JLabel();
		this.labPktLossRate	= new JLabel();
		
		updateConnectionStatus(0, 0.0);
		updatePlayoutStatus(0.0);

		JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));
		container.add(this.labFrameRate);
		container.add(this.labBitRate);
		container.add(this.labPktLossRate);
		return container;
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void newDesktopPane(VideoCanvas vidCvs, IoParam inPrm)
	{
		int vidWidth  =  vidCvs.getWidth();
		int vidHeight =  vidCvs.getHeight();

		// スクロール区画を生成する．
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setWheelScrollingEnabled(false);
		scrollPane.setSize(1, 1); // ※画面表示時のフラッシュ対応．

		// VideoCanvas の左右に延びる糊を付ける．
		Box horBox = Box.createHorizontalBox();
		horBox.add(Box.createHorizontalGlue());
		horBox.add(scrollPane);
		horBox.add(Box.createHorizontalGlue());

		// horBox の上下に延びる糊を付け，デスクトップ区画とする．
		this.desktopPane = Box.createVerticalBox();
		this.desktopPane.add(Box.createVerticalGlue());
		this.desktopPane.add(horBox);
		this.desktopPane.add(Box.createVerticalGlue());
		
		// デスクトップ区画をこのフレームに追加する．
		add(this.desktopPane, BorderLayout.CENTER);
		validate(); // ここでスクロールバーの幅と高さが決まる．

		// スクロール区画の推奨・最大サイズを算出する．
		int width  = scrollPane.getVScrollbarWidth();
		int height = scrollPane.getHScrollbarHeight();
		width  /= 4;
		height /= 4;
		Dimension dim = new Dimension(vidWidth + width, vidHeight + height);
		
		// スクロール区画の各サイズを設定する．
		scrollPane.setMinimumSize(DIM_MIN);
		scrollPane.setPreferredSize(dim);
		scrollPane.setMaximumSize(dim);
		
		// スクロール区画に VideoCanvas を追加する．
		scrollPane.add(vidCvs);
		validate(); // スクロール区画のサイズに応じてレイアウトを調整する．
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void deleteDesktopPane()
	{
		// デスクトップ区画をこのフレームから削除する．
		remove(this.desktopPane);
		validate();
		repaint();
	}
	
//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	void evHn_StartControl()
	{
		try {
			// 接続先アドレスを取得する．
			String strAddr = this.pnlConAddr.getSelectedAddress();
			if (strAddr == null) {
				return;
			}
			// 開始イベントを配送する．
			this.controller.startControl(strAddr);
				// SystemException
			
			// コネクションアドレスパネルの状態を変更する．
			this.pnlConAddr.changeToConnectingState();
		}
		catch (Exception ex) {
			Dialog.warning(this, ex);
		}
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	void evHn_StopControl()
	{
		// ImageReceiver の終了処理を実行する．
		this.imgReciever.shutdown();
		
		// フレームのタイトルを変更する．
		setTitle(TITLE_REMOTE_DESKTOP);

		// コネクションアドレスパネルの状態を変更する．
		this.pnlConAddr.changeToIdleState();		
		
		// メッセージパネルの値を初期化する．
		updatePlayoutStatus(0.0);
		updateConnectionStatus(0, 0.0);

		// デスクトップ区画を削除する．
		deleteDesktopPane();

		// 停止イベントを配送する． 
		this.controller.stopControl();
	}
}
