
package com.midfield_system.app.console;

import java.util.List;

import com.midfield_system.api.stream.DeviceInfo;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamFormat;
import com.midfield_system.api.system.MfsNode;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: IoSetupState
 *
 * Date Modified: 2017.08.25
 *
 */

//==============================================================================
public abstract class IoSetupState
	extends		ConsoleState
{
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PROTECTED VARIABLE -----------------------------------------------------
	protected SystemCommandState sysStat = null;
	
//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected IoSetupState(Console console, MfsNode mfs)
	{
		super(console, mfs);
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void showSegmentIo()
	{
		SegmentIo segIo = this.console.getSegmentIo();
		printSegmentIo(segIo);
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected ConsoleState quit()
	{
		// SystemCommandState へ遷移		
		return this.sysStat;
	}

//------------------------------------------------------------------------------
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void printDeviceInfoList(List<DeviceInfo> lsDevInf)
	{
		printListTitle("入力デバイス一覧");
		int size = lsDevInf.size();
		for (int i = 0; i < size; i++) {
			DeviceInfo devInf = lsDevInf.get(i);
			print("  [%s] %s\n", Integer.toString(i), devInf.getIoName());
		}
	}

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void printStreamFormatList(List<StreamFormat> lsStmFmt)
	{
		printListTitle("フォーマット一覧");
		int size = lsStmFmt.size();
		for (int i = 0; i < size; i++) {
			StreamFormat stmFmt = lsStmFmt.get(i);
			print("  [%s] %s\n", Integer.toString(i), stmFmt.getDescription());
		}
	}	
}
