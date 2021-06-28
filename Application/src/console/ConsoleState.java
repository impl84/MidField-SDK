
package console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.midfield_system.api.stream.IoParam;
import com.midfield_system.api.stream.SegmentIo;
import com.midfield_system.api.stream.StreamFormat;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.protocol.StreamInfo;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: ConsoleState
 *
 * Date Modified: 2018.08.07
 *
 */

//==============================================================================
public abstract class ConsoleState
{
	//- PROTECTED CONSTANT VALUE -----------------------------------------------
	protected static final String
		STR_ITEM_COMMAND	= "Å° ",
		STR_ITEM_LIST		= "Å•",
		STR_ITEM_MESSAGE	= "Å|",
		STR_ITEM_WARNING	= "Å¶";
	
	protected static final int CMD_SEPARATOR = -1;
	
//==============================================================================
//  CLASS VARIABLE:
//==============================================================================	
	
	//- PROTECTED CLASS --------------------------------------------------------
	protected static class CommandDesc
	{
		private int cmdId = 0;
		private String desc = null;
		
		protected CommandDesc(int cmdId, String desc)	{
			this.cmdId = cmdId; this.desc = desc;
		}
		protected int getCommandID()		{ return this.cmdId; }
		protected String getDescription()	{ return this.desc; }
	}

//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PROTECTED VARIABLE -----------------------------------------------------
	protected Console console = null;
	protected MfsNode mfs = null;

	//- PRIVATE VARIABLE -------------------------------------------------------
	private BufferedReader bufRdr = null;
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PUBLIC METHOD:
//------------------------------------------------------------------------------

	//- PUBLIC METHOD ----------------------------------------------------------
	//
	public ConsoleState(Console console, MfsNode mfs)
	{
		this.console = console;
		this.mfs = mfs;
		
		InputStreamReader isr = new InputStreamReader(System.in);
		this.bufRdr = new BufferedReader(isr);		
	}	
	
//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected abstract String getPrompt();

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected abstract ConsoleState execute();
	
//------------------------------------------------------------------------------

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void print(String format, Object ... args)
	{
		System.out.printf("[%s] ", getPrompt());
		System.out.printf(format, args);
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void message(String format, Object ... args)
	{
		System.out.printf("[%s] %s", getPrompt(), STR_ITEM_MESSAGE);
		System.out.printf(format, args);
	}

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void messagePause(String format, Object ... args)
	{
		message(format, args);
		pause();
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void warning(String format, Object ... args)
	{
		System.out.printf("[%s] %s", getPrompt(), STR_ITEM_WARNING);
		System.out.printf(format, args);
	}

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void warningPause(String format, Object ... args)
	{
		warning(format, args);
		pause();
	}
	
//------------------------------------------------------------------------------
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void printSeparator()
	{
		System.out.printf("\n");
		//                           1         2         3         4
		//                 01234567890123456789012345678901234567890
		System.out.printf("Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\");
		System.out.printf("Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\Å\\n");
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void printShortSeparator()
	{
		//                           1         2         3         4
		//                 01234567890123456789012345678901234567890
		print(            "- - - - - - - - - - - - - - - - - - - - ");
		System.out.printf("- - - - - - - - - - - - - - - - \n");
	}
	
//------------------------------------------------------------------------------

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected int selectNumber(String msg)
	{
		int num = -1;

		print("\n");
		print("  [è„ãLà»äOÇÃî‘çÜ] ÉLÉÉÉìÉZÉã\n");
		
		while (num < 0) {
			try {
				print("\n");
				print("ÅE%sÅF", msg);
				String line = this.bufRdr.readLine();	// IOException
				if (line == null) {
					print("\n");
					continue;
				}
				//------------------------------------------------------
				num = Integer.parseInt(line);	// NumberFormatException
			}
			catch (NumberFormatException ex) {
				continue;
			}
			catch (IOException ex) {
				warning(ex.getMessage());
				break;
			}
		}
		printShortSeparator();

		return num;
	}

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected String getLine(String msg)
	{
		String line = null;
		int len = 0;

		while (len == 0) {
			try {
				print("ÅE%sÅF", msg);
				line = this.bufRdr.readLine();	// IOException
				if (line == null) {
					print("\n");
					continue;
				}
				//------------------------------------------------------
				len = msg.length();
			}
			catch (IOException ex) {
				warning(ex.getMessage());
				break;
			}
		}
		return line;
	}

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void pause()
	{
		print("  (PUSH ENTER KEY)");
		try {
			this.bufRdr.readLine();
		}
		catch (IOException ex) {
			warning(ex.getMessage());
		}
	}
	
//------------------------------------------------------------------------------
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void printListTitle(String title)
	{
		print("%s%s\n", STR_ITEM_LIST, title);
	}

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void printSegmentIo(SegmentIo segIo)
	{
		print("\n");
		print("Åñì¸èoóÕê›íË  Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`\n");
		printIoParamList("ì¸óÕê›íË", segIo.getInputParamList());
		printIoParamList("èoóÕê›íË", segIo.getOutputParamList());
		print("Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`Å`\n");
		pause();		
	}

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void printIoParamList(String msg, List<IoParam> lsIOMap)
	{
		printListTitle(msg);
		int size = lsIOMap.size();
		for (int i = 0; i < size; i++) {
			print("  [%s] ", Integer.toString(i));
			IoParam ioMap = lsIOMap.get(i);
			printIoParam(ioMap);
		}		
	}

	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void printIoParam(IoParam ioMap)
	{
		String desc = ioMap.getDescription();
		if (desc == null) {
			desc = ioMap.getStreamName();
		}
		String sender = ioMap.getSourceAddress();

		StreamFormat stmFmt = ioMap.getStreamFormat();

		String prot = null;
		System.out.printf("%s\n", desc);
		if (sender != null) {
			print( "    ëóêMÉzÉXÉg  : %s\n", sender);
			prot = ioMap.getConnectionInfo();
		}
		print( "    ÉtÉHÅ[É}ÉbÉg: %s\n", stmFmt.toString());
		if (prot != null) {
			print( "    ÉvÉçÉgÉRÉã  : %s\n", prot);
		}
	}
	
	//- PROTECTED METHOD -------------------------------------------------------
	//
	protected void printOutputInformationList(String msg, List<StreamInfo> lsOutInf)
	{
		printListTitle(msg);
		int size = lsOutInf.size();
		for (int i = 0; i < size; i++) {
			print("  [%s] ", Integer.toString(i));
			StreamInfo outInf = lsOutInf.get(i);
			print("    OutputInformation : %s\n", outInf.toString());
		}		
	}
	
//------------------------------------------------------------------------------

	//- PROTECTED METHOD -------------------------------------------------------
	//
	int getCommandNumber(String title, CommandDesc[] cmdDescs)
	{
		printSeparator();
		print("%s\n", title);
		printShortSeparator();
		print("\n");

		for (CommandDesc cmdDesc : cmdDescs) {
			if (cmdDesc.getCommandID() == CMD_SEPARATOR) {
				print("\n");
			}
			else {
				print("  [%s] %s\n",
					Integer.toString(cmdDesc.getCommandID()),
					cmdDesc.getDescription()
				);
			}
		}
		int num = selectNumber("ÉRÉ}ÉìÉhî‘çÜ");
		return num;
	}
}
