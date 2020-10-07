
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
		STR_FRAME_TITLE				= "���u�f�X�N�g�b�v����",
		STR_MSG_PANEL_TITLE			= "���b�Z�[�W�o�̓p�l��";
	
	private static final String	
		STR_ACCEPTANCE_BUTTON		= "���u����󂯓���",
		STR_CONTROL_BUTTON			= "���u����p�E�B���h�E";
	
	private static final String
		MSG_STARTED_MIDFIELD		= "MidField System �̏������J�n���܂����D",
		MSG_LOCAL_IP_ADDRESS		= "(���[�J��IP�A�h���X�F%s)",
		MSG_SHUTDOWN_MIDFIELD		= "MidField System ���I�����܂��D",
		MSG_SETUP_PROCESSOR			= "���u����̎󂯓�����J�n���܂��D",
		MSG_SHUTDOWN_PROCESSOR		= "���u����̎󂯓�����I�����܂��D",
		MSG_SETUP_CONTROLLER		= "���u����p�E�B���h�E�ɂ�鉓�u���쏈�����J�n���܂��D",
		MSG_SHUTDOWN_CONTROLLER		= "���u����p�E�B���h�E�ɂ�鉓�u���쏈�����I�����܂��D";

	private static final String
		ERR_PROCESSOR_IS_ALREADY_IN_USE		= "���ɉ��u����󂯓��ꏈ�����ł��D",
		ERR_PROCESSOR_IS_NOT_RUNNING		= "���u����󂯓��ꏈ���͍s���Ă��܂���D",
		ERR_CONTROLLER_IS_ALREADY_IN_USE	= "���ɉ��u���쏈�����ł��D",
		ERR_CONTROLLER_IS_NOT_RUNNING		= "���u���쏈���͍s���Ă��܂���D";
	
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
		// GUI���Z�b�g�A�b�v����D
		setupGui();
		
		// MidField System ���N������D
		setupMidField();

		// �I��������o�^����D
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
			// DesktopEventController ���I�������D
			// ���u����p�E�B���h�E�̃g�O���{�^���̏�Ԃ�K�v�ɉ����ĕς���D
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
		// MessagePanel ���{�[�_�[�t���Ő�������D
		Border border = BorderFactory.createEtchedBorder();		
		MessagePanel panel = new MessagePanel();
		panel.setBorder(new TitledBorder(border, STR_MSG_PANEL_TITLE));

		// ���O�̏o�͐��ݒ肷��D
		Log.setLogPrinter(panel);
		
		// �g�O���{�^���ƃR���g���[���{�b�N�X�𐶐�����D
		this.btnAcceptance = new JToggleButton(STR_ACCEPTANCE_BUTTON);
		this.btnAcceptance.setActionCommand(STR_ACCEPTANCE_BUTTON);
		this.btnAcceptance.addActionListener(this);
		
		this.btnControl = new JToggleButton(STR_CONTROL_BUTTON);
		this.btnControl.setActionCommand(STR_CONTROL_BUTTON);
		this.btnControl.addActionListener(this);

		Box box = Box.createHorizontalBox();
		box.add(this.btnAcceptance);
		box.add(this.btnControl);
		
		// ���� JFrame �̃R���e�i����
		// ���b�Z�[�W�p�l���ƃR���g���[���{�b�N�X��ǉ�����D
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(panel, BorderLayout.CENTER);
		container.add(box, BorderLayout.SOUTH);
		
		// L&F ��ݒ肷��D
		AppUtilities.setLookAndFeel(this);		

		// �^�C�g���ƍŏ��T�C�Y��ݒ肷��D
		setTitle(STR_FRAME_TITLE);
		setMinimumSize(DIM_MIN_FRAME);
		
		// ��ʒ����Ƀt���[���̈ʒu�����킹��D
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getScreenSize();
		setLocation(
			dim.width  / 2 - (int)DIM_MIN_FRAME.getWidth()  / 2,
			dim.height / 2 - (int)DIM_MIN_FRAME.getHeight() / 2
		);
		// ����Ԃɂ���D
		setVisible(true);
	}
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private void setupMidField()
	{
		try {
			// MidField System ������������D
			this.mfs = MfsNode.initialize();
				// SystemException
			
			//  ���̃C���X�^���X�� SystemEventListener �ɒǉ�����D
			this.mfs.addSystemEventListener(this);
			
			// MidField System ���N������D
			this.mfs.activate();
				// SystemException
			
			// MidField System �̊J�n���b�Z�[�W�����O�ɏo�͂���D
			Log.message();
			Log.message(MSG_STARTED_MIDFIELD);

			// ���[�J��IP�A�h���X�����O�֏o�͂���D
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
		// ���u���쒆�C�܂��́C����󂯓��ꒆ�ł���΁C
		// ���ꂼ��̏������I������D
		if (this.controller != null) {
			Log.message(MSG_SHUTDOWN_CONTROLLER);
			this.controller.shutdown();
		}
		if (this.processor != null) {
			Log.message(MSG_SHUTDOWN_PROCESSOR);
			this.processor.shutdown();
		}
		// MidField System ���I������D
		if (this.mfs != null) {
			Log.message(MSG_SHUTDOWN_MIDFIELD);
			this.mfs.shutdown();
		}
		// ���̃t���[���̏I�����������s����D
		dispose();
	}	

//------------------------------------------------------------------------------
//  PRIVATE METHOD: ActionEvent �n���h��
//------------------------------------------------------------------------------
	
	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private void evHn_Acceptance(JToggleButton btn)
	{
		if (btn.isSelected() == true) {
			if (this.processor == null) {
				try {
					// ���u����󂯓���p�g�O���{�^�����I����ԂŁC
					// DesktopEventProcessor �̃C���X�^���X�����݂��Ȃ��ꍇ�C
					// DesktopEventProcessor �̃C���X�^���X�𐶐�����D
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
				// ���u����󂯓���p�g�O���{�^������I����ԂŁC
				// DesktopEventProcessor �̃C���X�^���X�����݂���ꍇ�C
				// DesktopEventProcessor �̃C���X�^���X���������D
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
					// ���u����E�B���h�E�p�g�O���{�^�����I����ԂŁC
					// DesktopEventController �̃C���X�^���X�����݂��Ȃ��ꍇ�C
					// DesktopEventController �̃C���X�^���X�𐶐�����D
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
				// ���u����E�B���h�E�p�g�O���{�^������I����ԂŁC
				// DesktopEventController �̃C���X�^���X�����݂���ꍇ�C
				// DesktopEventController �̃C���X�^���X���������D				
				Log.message(MSG_SHUTDOWN_CONTROLLER);
				this.controller.shutdown();
				this.controller = null;
				
				// ���u����p�E�B���h�E�̃g�O���{�^���̏�Ԃ�K�v�ɉ����ĕς���D
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