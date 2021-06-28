
package desktop;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.util.Constants;
import com.midfield_system.api.util.Log;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: Dialog 
 *
 * Date Modified: 2017.09.01
 *
 */

//==============================================================================
public class Dialog
{
	//- PACKAGE CONSTANT VALUE -------------------------------------------------
	static final String
		STR_TITLE_MESSAGE		= "Message",
		STR_TITLE_WARNING		= "Warning",
		STR_TITLE_ERROR			= "Error",
		STR_TITLE_CONFIRMATION	= "Confirmation";
	
//==============================================================================
//  CLASS METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC STATIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public static void message(Component parent, String msg)
	{
		popup(parent, STR_TITLE_MESSAGE, msg);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public static void warning(Component parent, Throwable throwable)
	{
		String msg = SystemException.getMessageWithCause(throwable);		
		warning(parent, msg);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public static void warning(Component parent, String msg)
	{
		popup(parent, STR_TITLE_WARNING, msg);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public static void error(Component parent, Throwable throwable)
	{
		String msg = SystemException.getMessageWithCause(throwable);		
		error(parent, msg);
	}

	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public static void error(Component parent, String msg)
	{
		popup(parent, STR_TITLE_ERROR, msg);
	}
	
	//- PUBLIC METHOD ----------------------------------------------------------
	//	
	public static boolean confirmation(Component parent, String msg)
	{
		boolean isOk = false;
		
		// 確認用ダイアログを生成する．
		final ConfirmationDialog dialog = new ConfirmationDialog();
		
		// ダイアログを表示する．
		if (SwingUtilities.isEventDispatchThread()) {
			dialog.showDialog(parent, msg);
		}
		else {
			final Component _parent = parent;
			final String _msg = msg;
			
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						dialog.showDialog(_parent, _msg);
					}
				});
			}
			catch (Exception ex) {
				Log.error(ex);
			}
		}
		// 結果を取得する．
		isOk = dialog.getResult();
		return isOk;
	}
	
//------------------------------------------------------------------------------
//  PACKAGE STATIC CLASS:
//------------------------------------------------------------------------------

	//======== [PACKAGE CLASS] =================================================
	//	
	static class ConfirmationDialog
	{
		private boolean result = false;
		
		boolean getResult()
		{
			return this.result;
		}
		
		void showDialog(Component parent, String msg)
		{
			Object[] options = { Constants.STR_OK, Constants.STR_CANCEL }; 
			int res = JOptionPane.showOptionDialog(
				parent,
				msg,
				Dialog.STR_TITLE_CONFIRMATION,
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]
			);
			if (res == 0) {
				this.result = true;
			}
		}
	}
	
//------------------------------------------------------------------------------
//  PACKAGE STATIC METHOD:
//------------------------------------------------------------------------------

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	static void showDialog(
		Component parent, String title, int msgType, String msg
	) {
		Object[] options = { Constants.STR_OK }; 
		JOptionPane.showOptionDialog(
			parent,
			msg,
			title,
			JOptionPane.DEFAULT_OPTION,
			msgType,
			null,
			options,
			options[0]
		);
	}

//------------------------------------------------------------------------------
//  PRIVATE STATIC METHOD:
//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//	
	private static void popup(Component parent, String title, String msg)
	{
		// ダイアログのタイプを決める．
		int type = JOptionPane.PLAIN_MESSAGE;
		if (title.equals(STR_TITLE_MESSAGE) == true) {
			type = JOptionPane.INFORMATION_MESSAGE;
		}
		else if (title.equals(STR_TITLE_WARNING) == true) {
			type = JOptionPane.WARNING_MESSAGE;
		}
		else if (title.equals(STR_TITLE_ERROR) == true) {
			type = JOptionPane.ERROR_MESSAGE;
		}
		// ダイアログを表示する．
		if (SwingUtilities.isEventDispatchThread()) {
			showDialog(parent, title, type, msg);
		}
		else {
			final Component _parent = parent;
			final String _title = title;
			final int _type = type;
			final String _msg = msg;
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showDialog(_parent, _title, _type, _msg);
				}
			});
		}
	}
}
