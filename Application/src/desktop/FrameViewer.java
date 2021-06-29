
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
		TITLE_REMOTE_DESKTOP	= "���u�f�X�N�g�b�v",
		STR_TITLE_FORMAT		= TITLE_REMOTE_DESKTOP + " (%s)";
	
	private static final String
		STR_FRAME_RATE		= "  �Đ��t���[�����[�g : %.2f [fps] ",
		STR_BIT_RATE		= "  ��M�r�b�g���[�g : %.2f [Mbps] ",
		STR_PKT_LOSS_RATE	= "  ��M�p�P�b�g���X�� : %.2f [%%] ";

	private static final String	
		STR_CONTROL_REFUSED	= "���u�f�X�N�g�b�v����v�������ۂ���܂����D",
		STR_FAILED_CAUSE	= "%s\n%s",
		STR_PLEASE_CHECK	= "\\n���u/���[�J���z�X�g�̐ݒ���m�F���ĉ������D";
	
	private static final String
		STR_DESKTOP_FORMAT	= "���u�f�X�N�g�b�v�t�H�[�}�b�g : %s";
			
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
		// �R�l�N�V�����A�h���X�p�l���𐶐�����D
		this.pnlConAddr = new HostAddressPanel(this, this);
		
		// ���b�Z�[�W�o�̓R���|�[�l���g�𐶐�����D
		Container msgComp = setupMessageComponents();

		// ���̃t���[���ɓ��o�̓R���|�[�l���g��ǉ�����D
		setLayout(new BorderLayout());
		add(this.pnlConAddr, BorderLayout.NORTH);
		add(msgComp, BorderLayout.SOUTH);

		// �t���[���T�C�Y��ݒ肷��D
		setMinimumSize(new Dimension(MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT));
		setPreferredSize(new Dimension(PRE_FRAME_WIDTH, PRE_FRAME_HEIGHT));
		setSize(PRE_FRAME_WIDTH, PRE_FRAME_HEIGHT);

		// �t���[���̔w�i�F�ƃ^�C�g����ݒ肷��D
		setBackground(Color.BLUE);
		setTitle(TITLE_REMOTE_DESKTOP);

		// �I��������o�^����D
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				shutdown();
			}
		});
		// ��ʒ����Ƀt���[���̈ʒu�����킹��D
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getScreenSize();
		setLocation(
			dim.width  / 2 - PRE_FRAME_WIDTH  / 2,
			dim.height / 2 - PRE_FRAME_HEIGHT / 2
		);		
		// ����Ԃɂ���D
		setVisible(true);
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void shutdown()
	{
		if (this.pnlConAddr.isControllingState()) {
			// ���䒆�F
			
			// �����~�C�x���g��z������D 
			this.controller.stopControl();
		
			// ImageReceiver �̏I�����������s����D
			this.imgReciever.shutdown();
		}
		// RemoteDesktopController ���폜����D
		this.controller.delete();
		
		// ���̃t���[���̏I�����������s����D
		dispose();
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//
	void controlAccepted(IoParam inPrm)
	{
		try {
			// ImageReceiver �𐶐����āCVideoCanvas ���擾����D
			this.imgReciever = new ImageReceiver(this);
			VideoCanvas vidCvs = this.imgReciever.createVideoCanvas(inPrm);
				// SystemException, StreamException
			
			// VideoCanvas �̃Z�b�g�A�b�v�D
			vidCvs.enableInputMethods(false);

			vidCvs.addKeyListener(this);
			vidCvs.addMouseListener(this);
			vidCvs.addMouseMotionListener(this);
			vidCvs.addMouseWheelListener(this);
			
			// �f�X�N�g�b�v���̃Z�b�g�A�b�v�D
			newDesktopPane(vidCvs, inPrm);
			
			// �R�l�N�V�����A�h���X�p�l���̏�Ԃ�ύX����D
			this.pnlConAddr.changeToControllingState();
			
			// �t���[���̃^�C�g����ύX����D
			setTitle(String.format(
				STR_TITLE_FORMAT, this.pnlConAddr.getSelectedAddress()
			));

			// �f�X�N�g�b�v�C���[�W�̎�M�Đ��������J�n����D
			this.imgReciever.start();
				// StreamException
			
			// �t�H�[�}�b�g���o��
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
			// �A�C�h����Ԃł���Ζ߂�D
			if (this.pnlConAddr.isIdleState()) {
				return;
			}
			// ImageReceiver �̏I�����������s����D
			if (this.pnlConAddr.isControllingState()) {
				// ���䒆�F
			
				this.imgReciever.shutdown();
				
				// �f�X�N�g�b�v�����폜����D
				deleteDesktopPane();
			}
			// �t���[���̃^�C�g����ύX����D
			setTitle(TITLE_REMOTE_DESKTOP);

			// �R�l�N�V�����A�h���X�p�l���̏�Ԃ�ύX����D
			this.pnlConAddr.changeToIdleState();
		
			// ���b�Z�[�W�p�l���̒l������������D
			updatePlayoutStatus(0.0);
			updateConnectionStatus(0, 0.0);

			// ���b�Z�[�W���M��O�Ɋւ��郁�b�Z�[�W���|�b�v�A�b�v����D
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
		// ������ۂɊւ��郁�b�Z�[�W���|�b�v�A�b�v����D
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
		
		// �R�l�N�V�����A�h���X�p�l���̏�Ԃ�ύX����D
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
	//	2017/9/6 KeyEvent �𐶐��������D
	//	KeyEvent �� Serializable ���������Ă��邪�CKeyEvent �̒��Ɋ܂܂�Ă���
	//  �\�[�X�R���|�[�l���g�� VideoCanvas �ɂ��C�V���A���C�Y����ہC
	//  ��O����������D
	//  VideoCanvas �� Serializable ���������Ă��邪�C�����ɃV���A���C�Y�ł��Ȃ�
	//  �C���X�^���X�ւ̎Q�Ƃ�ێ����Ă�����̂Ǝv����D
	//  �����C�\�[�X�R���|�[�l���g�� VideoCanvas �ł���K�v�͖����̂ŁC
	//  �_�~�[�̃R���|�[�l���g���\�[�X�R���|�[�l���g�Ƃ���C�x���g�𐶐����C
	//  ������V���A���C�Y���ĉ��u�֑��M���邱�Ƃɂ����D
	//
	//  MouseEvent �ł����l�ɔ�������ƍl�����邪�C
	//  MouseEvent �ł͔������Ȃ��DKeyEvent �ł� MouseEvent �ł��C
	//  �\�[�X�R���|�[�l���g�͓����� VideoCanvas �Ȃ̂����C
	//  �����炭�C�����ŕێ�����C���X�^���X�ւ̎Q�ƂɈႢ������Ǝv����D
	//  
	//  ���̌��ۂ́C�ȑO�� KeyEvent �ł��������Ȃ������D
	//  ����CMouseEvent �ł���������\�����l�����āCMouseEvent �ł�
	//  �_�~�[�̃R���|�[�l���g���\�[�X�R���|�[�l���g�Ƃ���悤�C�����������D
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
	//  KeyEvent ���l�C�_�~�[�̃R���|�[�l���g���\�[�X�R���|�[�l���g�Ƃ���
	//  ���p����l�C���D
	//
	//	2009/1/20 MouseEvent �𐶐��������D
	//	Windows Vista �ł́CVideoCanvas �̃}�E�X���W�擾�ɕs�������D
	//	mousePressed() �� mouseMoved() �̈����ƂȂ� MouseEvent ����擾
	//	�ł��鑊�΍��W�l�Ɛ�΍��W�l�������Ă��Ȃ��D��΍��W�p�̕ϐ���
	//	���΍��W�l���i�[����Ă���D���΍��W�p�̕ϐ��ɂ͕s���Ȓl���i�[
	//	����Ă���D���̃��X�i�[���\�b�h�ł͖��Ȃ��D
	//	Windows XP �ł͔������Ȃ��D
	//	Java �̃o�[�W������ 1.6.0 update 7�D
	//	Java �̕s��ł͂Ȃ����Ǝv���C��������P����邩�Ƃ��v�����C
	//	���̂܂܂ł͎g���Ȃ��D
	//	�K�v�ƂȂ鑊�΍��W�l�́C���L�̕��@�ł��擾�ł���D
	//	���ʁC���̃��\�b�h���g���������������}�E�X�C�x���g�𗘗p����D
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

		// �X�N���[�����𐶐�����D
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setWheelScrollingEnabled(false);
		scrollPane.setSize(1, 1); // ����ʕ\�����̃t���b�V���Ή��D

		// VideoCanvas �̍��E�ɉ��т�Ђ�t����D
		Box horBox = Box.createHorizontalBox();
		horBox.add(Box.createHorizontalGlue());
		horBox.add(scrollPane);
		horBox.add(Box.createHorizontalGlue());

		// horBox �̏㉺�ɉ��т�Ђ�t���C�f�X�N�g�b�v���Ƃ���D
		this.desktopPane = Box.createVerticalBox();
		this.desktopPane.add(Box.createVerticalGlue());
		this.desktopPane.add(horBox);
		this.desktopPane.add(Box.createVerticalGlue());
		
		// �f�X�N�g�b�v�������̃t���[���ɒǉ�����D
		add(this.desktopPane, BorderLayout.CENTER);
		validate(); // �����ŃX�N���[���o�[�̕��ƍ��������܂�D

		// �X�N���[�����̐����E�ő�T�C�Y���Z�o����D
		int width  = scrollPane.getVScrollbarWidth();
		int height = scrollPane.getHScrollbarHeight();
		width  /= 4;
		height /= 4;
		Dimension dim = new Dimension(vidWidth + width, vidHeight + height);
		
		// �X�N���[�����̊e�T�C�Y��ݒ肷��D
		scrollPane.setMinimumSize(DIM_MIN);
		scrollPane.setPreferredSize(dim);
		scrollPane.setMaximumSize(dim);
		
		// �X�N���[������ VideoCanvas ��ǉ�����D
		scrollPane.add(vidCvs);
		validate(); // �X�N���[�����̃T�C�Y�ɉ����ă��C�A�E�g�𒲐�����D
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void deleteDesktopPane()
	{
		// �f�X�N�g�b�v�������̃t���[������폜����D
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
			// �ڑ���A�h���X���擾����D
			String strAddr = this.pnlConAddr.getSelectedAddress();
			if (strAddr == null) {
				return;
			}
			// �J�n�C�x���g��z������D
			this.controller.startControl(strAddr);
				// SystemException
			
			// �R�l�N�V�����A�h���X�p�l���̏�Ԃ�ύX����D
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
		// ImageReceiver �̏I�����������s����D
		this.imgReciever.shutdown();
		
		// �t���[���̃^�C�g����ύX����D
		setTitle(TITLE_REMOTE_DESKTOP);

		// �R�l�N�V�����A�h���X�p�l���̏�Ԃ�ύX����D
		this.pnlConAddr.changeToIdleState();		
		
		// ���b�Z�[�W�p�l���̒l������������D
		updatePlayoutStatus(0.0);
		updateConnectionStatus(0, 0.0);

		// �f�X�N�g�b�v�����폜����D
		deleteDesktopPane();

		// ��~�C�x���g��z������D 
		this.controller.stopControl();
	}
}
