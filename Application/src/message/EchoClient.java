
package message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import com.midfield_system.api.system.CommObject;
import com.midfield_system.api.system.CommPacket;
import com.midfield_system.api.system.SystemException;
import com.midfield_system.api.system.MfsNode;
import com.midfield_system.api.system.PacketIoException;
import com.midfield_system.api.system.RemoteException;
import com.midfield_system.protocol.MessageType;

//------------------------------------------------------------------------------
/**
 * Sample code of MidField System API: EchoClient 
 *
 * Date Modified: 2020.03.17
 *
 */

//==============================================================================
public class EchoClient
	extends CommObject
{
	//- PRIVATE CONSTANT VALUE -------------------------------------------------
	private static final String
		COMM_OBJECT_NAME = "EchoClient";
	
	// Echo Server となる MidField System が稼働している
	// ノードのIPアドレス
	private static final String[] IP_ADDRS = new String[] {
//		"172.16.127.164",
		"172.16.126.174",
		"172.16.126.175",
		"172.16.126.176",
		"172.16.126.177",
	};
	// 全宛先へのパケット配送回数
	private static final int REPEAT_COUNT = 1000;

	// 送受信されるコンテンツデータ
	private static final byte[] CONTENT = new byte[1000000];

//==============================================================================
//  CLASS VARIABLE: 
//==============================================================================

	//- PRIVATE STATIC VARIABLE ------------------------------------------------
	private static String systemManagerName = null;

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
		MfsNode mfs = null;
		EchoClient client = null;
		
		try {
			// 宛先となる InetAddress の配列を生成する．
			InetAddress[] dstInetAddrs
				= new InetAddress[EchoClient.IP_ADDRS.length];
			for (int i = 0; i < EchoClient.IP_ADDRS.length; i++) {
				dstInetAddrs[i] = InetAddress.getByName(
					EchoClient.IP_ADDRS[i]
				);
					// UnknownHostException
			}
			// MidField System を初期化する．
			mfs = MfsNode.initialize();
				// SystemException
			
			// MidField System を起動する．
			mfs.activate();
				// SystemException
			
			// Echo Request の送信先となる SystemManager の名前を取得する．
			EchoClient.systemManagerName = mfs.getSystemManagerName();
			
			// EchoClient を生成する．
			client = new EchoClient();
				// SystemException
			
			// 期待する応答パケットの総数を求める．
			int numResponses = dstInetAddrs.length * REPEAT_COUNT;
			
			// 計測用の変数をリセットする．
			client.resetMeasures(numResponses);
			
			// 全宛先へのパケット配送と応答の受信を，与えられた回数繰り返す．
			client.dispatchRequestToAll(dstInetAddrs, REPEAT_COUNT);

			// 計測用の変数をリセットする．
			client.resetMeasures(numResponses);

			// 全宛先へのパケット非同期配送を，与えられた回数繰り返す．
			client.dispatchPacketToAll(dstInetAddrs, REPEAT_COUNT);
			
			// Enter キーの入力を待つ．
			System.out.println("> (Enter Key)");
			System.in.read();
				// IOException
		}
		catch (UnknownHostException ex) {
			ex.printStackTrace();
		}
		catch (SystemException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			if (client != null) {
				// EchoClient を削除する．
				client.delete();
			}
			if (mfs != null) {
				// MidField System をシャットダウンする．
				mfs.shutdown();
			}
		}	
	}
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	
	// 受信すべき応答パケットの数と，受信したコンテンツの総バイト数
	private int numResponses = 0;
	private long numTotalRcvd = 0;
	
	// 計測開始時刻
	private long startTime_ms = 0;	
	
//==============================================================================
//  INSTANCE METHOD:
//==============================================================================

//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------

	//- CONSTRUCTOR ------------------------------------------------------------
	//
	EchoClient()
		throws SystemException
	{
		super(COMM_OBJECT_NAME);
	}
	
//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void dispatchRequestToAll(InetAddress[] dstInetAddrs, int repeatCount)
	{
		// 全宛先へのパケット配送と応答の受信を，与えられた回数繰り返す．
		for (int i = 0; i < repeatCount; i++) {
			dispatchRequestToAll(dstInetAddrs);
		}
	}

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void dispatchRequestToAll(InetAddress[] dstInetAddrs)
	{
		// 与えられた宛先アドレスを走査する．
		for (InetAddress dstInetAddr : dstInetAddrs) {
			try {
				// エコー要求パケットを生成する．
				CommPacket request = new CommPacket(
					MessageType.ECHO_REQUEST,		// メッセージタイプ
					EchoClient.systemManagerName,	// 宛先 CommObject 名
					dstInetAddr	// 宛先となるノードのIPアドレス
				);
				// エコー要求パケットにコンテンツを設定する．
				request.setSerializedPayload(EchoClient.CONTENT);
				
				// エコー要求パケットを送信し，応答を待つ．
				CommPacket response = dispatchRequest(
					request,						// エコー要求パケット
					MessageType.ECHO_RESPONSE		// 期待するメッセージタイプ
				);
					// PacketIoException
					// RemoteException
					// TimeoutException
					// InterruptedException
				
				// 応答パケットを処理する．
				processReceivedResponse(response);
			}
			catch (PacketIoException ex) {
				ex.printStackTrace();
			}
			catch (RemoteException ex) {
				ex.printStackTrace();
			}
			catch (TimeoutException ex) {
				ex.printStackTrace();
			}
			catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	
//------------------------------------------------------------------------------
//  PACKAGE METHOD:
//------------------------------------------------------------------------------
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void dispatchPacketToAll(InetAddress[] dstInetAddrs, int repeatCount)
	{
		// 全宛先へのパケット非同期配送を，与えられた回数繰り返す．
		for (int i = 0; i < repeatCount; i++) {
			dispatchPacketToAll(dstInetAddrs);
		}
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void dispatchPacketToAll(InetAddress[] dstInetAddrs)
	{
		// 与えられた宛先アドレスを走査する．
		for (InetAddress dstInetAddr : dstInetAddrs) {
			// エコー要求パケットを生成する．
			CommPacket request = new CommPacket(
				MessageType.ECHO_REQUEST,		// メッセージタイプ
				EchoClient.systemManagerName,	// 宛先 CommObject 名
				dstInetAddr	// 宛先となるノードのIPアドレス
			);
			// エコー要求パケットにコンテンツを設定する．
			request.setSerializedPayload(EchoClient.CONTENT);
						
			// エコー要求パケットを配送する．
			dispatchPacket(request);
		}
	}

//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------
	
	//- PROTECTED METHOD -------------------------------------------------------
	// ■受信パケットの処理
	//
	@Override
	protected boolean incomingPacketHandler(CommPacket response)
	{
		boolean wasHandled = false;

		// メッセージタイプを取得・確認する．
		String msgType = response.getMessageType();
		if (msgType.equals(MessageType.ECHO_RESPONSE)) {
			// 応答パケットのメッセージタイプがエコー応答：
			
			// 応答パケットを処理する．
			processReceivedResponse(response);

			// 入力パケットが処理されたことを示すフラグを true にする．
			wasHandled = true;
		}			
		// 入力パケットが処理されたことを示すフラグを返す．
		return wasHandled;
	}
		
	//- PROTECTED METHOD -------------------------------------------------------
	// ■パケット送信時の例外処理
	//
	@Override
	protected void packetIoExceptionHandler(PacketIoException ex)
	{
		ex.printStackTrace();
	}

	//- PROTECTED METHOD -------------------------------------------------------
	// ■未到達パケットの処理
	//
	@Override
	protected void unreachablePacketHandler(CommPacket inPkt)
	{
		// 未到達パケットを取得する．
		CommPacket unreachable = inPkt.getPayload(
			CommPacket.class
		);
		// 未到達パケットに関する情報を出力する．
		System.out.println("* Unreachable Packet:");
		String[] lines = unreachable.getMessageHeaderLines();
		for (String line : lines) {
			if (line != null) {
				System.out.println("  - " + line);
			}
		}
		System.out.println();
	}

//------------------------------------------------------------------------------
//  PRIVATE METHOD:
//------------------------------------------------------------------------------

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private synchronized void resetMeasures(int numResponses)
	{
		// 受信すべき応答パケットの数を設定する．
		this.numResponses = numResponses;
		
		// 受信したコンテンツの総バイト数をクリアする．
		this.numTotalRcvd = 0;
		
		// 計測開始時刻をクリアする．
		this.startTime_ms = System.currentTimeMillis();
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private synchronized void processReceivedResponse(CommPacket response)
	{
		if (this.numResponses <= 0) {
			return;
		}
		// 受信したコンテンツのバイト数を加算する．
		this.numTotalRcvd += response.getPayloadLength();
		
		// 応答パケットの受信カウントをデクリメントする．
		this.numResponses--;
		
		// 応答パケットの受信カウント値を確認する．
		if (this.numResponses == 0) {
			// 受信すべき応答パケットを全て受信したので，
			// 計測結果を出力する．
			
			// 計測終了時刻を取得し，計測時間を求める．
			long endTime_ms = System.currentTimeMillis();
			double erapsedTime_sec = (endTime_ms - this.startTime_ms) / 1000.0;

			// 受信したコンテンツの総バイト数から，
			// 受信スループットを求める．
			double throughput_mbps
				= (this.numTotalRcvd / 1000.0 / 1000.0) * 8 / erapsedTime_sec;
			
			// 受信スループットを出力する．
			System.out.println();
			System.out.printf("> %d byte received, %.2f sec, %.2f Mbps\n",
				Long.valueOf(this.numTotalRcvd),
				Double.valueOf(erapsedTime_sec),
				Double.valueOf(throughput_mbps)
			);
		}
		else if ((this.numResponses % 100) == 0) {
			// 受信した応答パケット数が 100 で割り切れる場合，
			// 途中経過を出力する．
			System.out.printf("(%d) ", Integer.valueOf(this.numResponses));
			
			// 受信した応答パケット数が 1000 で割り切れる場合，
			// 改行する．
			if ((this.numResponses % 1000) == 0) {
				System.out.println();
			}
		}
	}
}
