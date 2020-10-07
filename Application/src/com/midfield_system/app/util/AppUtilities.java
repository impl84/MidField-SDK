package com.midfield_system.app.util;

import static com.midfield_system.gui.misc.GuiConstants.FONT_GUI_DEFAULT;

import java.awt.Component;

import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.midfield_system.gui.misc.PopupMessage;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: AppUtilities
 *
 * Date Modified: 2020.09.18
 *
 */

//==============================================================================
public class AppUtilities
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------	
	private static final String
		LF_CLASS_NAME = "javax.swing.plaf.nimbus.NimbusLookAndFeel",	//$NON-NLS-1$
		LF_KEY_DEFAULT_FONT		= "defaultFont";						//$NON-NLS-1$
	
//==============================================================================
//  CLASS METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC STATIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC STATIC METHOD ---------------------------------------------------
	//
	public static void setLookAndFeel(Component cmp)
	{
		try {
			// L&F ��ݒ肷��D
			UIManager.setLookAndFeel(LF_CLASS_NAME);
				// ClassNotFoundException, InstantiationException,
				// IllegalAccessException, UnsupportedLookAndFeelException
			
			// L&F �ŗ��p�����f�t�H���g�t�H���g��ݒ肷��D
			UIDefaults uiDef = UIManager.getLookAndFeelDefaults();
			uiDef.put(LF_KEY_DEFAULT_FONT, FONT_GUI_DEFAULT);
			
			// Swing �̃R���|�[�l���g�c���[�ɐݒ�𔽉f������D
			SwingUtilities.updateComponentTreeUI(cmp);
			
		} catch (Exception ex) {
			PopupMessage.warning(cmp, ex);
		}		
	}
}
