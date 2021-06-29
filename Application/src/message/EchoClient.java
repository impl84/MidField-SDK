
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
	
	// Echo Server �ƂȂ� MidField System ���ғ����Ă���
	// �m�[�h��IP�A�h���X
	private static final String[] IP_ADDRS = new String[] {
//		"172.16.127.164",
		"172.16.126.174",
		"172.16.126.175",
		"172.16.126.176",
		"172.16.126.177",
	};
	// �S����ւ̃p�P�b�g�z����
	private static final int REPEAT_COUNT = 1000;

	// ����M�����R���e���c�f�[�^
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
			// ����ƂȂ� InetAddress �̔z��𐶐�����D
			InetAddress[] dstInetAddrs
				= new InetAddress[EchoClient.IP_ADDRS.length];
			for (int i = 0; i < EchoClient.IP_ADDRS.length; i++) {
				dstInetAddrs[i] = InetAddress.getByName(
					EchoClient.IP_ADDRS[i]
				);
					// UnknownHostException
			}
			// MidField System ������������D
			mfs = MfsNode.initialize();
				// SystemException
			
			// MidField System ���N������D
			mfs.activate();
				// SystemException
			
			// Echo Request �̑��M��ƂȂ� SystemManager �̖��O���擾����D
			EchoClient.systemManagerName = mfs.getSystemManagerName();
			
			// EchoClient �𐶐�����D
			client = new EchoClient();
				// SystemException
			
			// ���҂��鉞���p�P�b�g�̑��������߂�D
			int numResponses = dstInetAddrs.length * REPEAT_COUNT;
			
			// �v���p�̕ϐ������Z�b�g����D
			client.resetMeasures(numResponses);
			
			// �S����ւ̃p�P�b�g�z���Ɖ����̎�M���C�^����ꂽ�񐔌J��Ԃ��D
			client.dispatchRequestToAll(dstInetAddrs, REPEAT_COUNT);

			// �v���p�̕ϐ������Z�b�g����D
			client.resetMeasures(numResponses);

			// �S����ւ̃p�P�b�g�񓯊��z�����C�^����ꂽ�񐔌J��Ԃ��D
			client.dispatchPacketToAll(dstInetAddrs, REPEAT_COUNT);
			
			// Enter �L�[�̓��͂�҂D
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
				// EchoClient ���폜����D
				client.delete();
			}
			if (mfs != null) {
				// MidField System ���V���b�g�_�E������D
				mfs.shutdown();
			}
		}	
	}
	
//==============================================================================
//  INSTANCE VARIABLE:
//==============================================================================

	//- PRIVATE VARIABLE -------------------------------------------------------
	
	// ��M���ׂ������p�P�b�g�̐��ƁC��M�����R���e���c�̑��o�C�g��
	private int numResponses = 0;
	private long numTotalRcvd = 0;
	
	// �v���J�n����
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
		// �S����ւ̃p�P�b�g�z���Ɖ����̎�M���C�^����ꂽ�񐔌J��Ԃ��D
		for (int i = 0; i < repeatCount; i++) {
			dispatchRequestToAll(dstInetAddrs);
		}
	}

	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void dispatchRequestToAll(InetAddress[] dstInetAddrs)
	{
		// �^����ꂽ����A�h���X�𑖍�����D
		for (InetAddress dstInetAddr : dstInetAddrs) {
			try {
				// �G�R�[�v���p�P�b�g�𐶐�����D
				CommPacket request = new CommPacket(
					MessageType.ECHO_REQUEST,		// ���b�Z�[�W�^�C�v
					EchoClient.systemManagerName,	// ���� CommObject ��
					dstInetAddr	// ����ƂȂ�m�[�h��IP�A�h���X
				);
				// �G�R�[�v���p�P�b�g�ɃR���e���c��ݒ肷��D
				request.setSerializedPayload(EchoClient.CONTENT);
				
				// �G�R�[�v���p�P�b�g�𑗐M���C������҂D
				CommPacket response = dispatchRequest(
					request,						// �G�R�[�v���p�P�b�g
					MessageType.ECHO_RESPONSE		// ���҂��郁�b�Z�[�W�^�C�v
				);
					// PacketIoException
					// RemoteException
					// TimeoutException
					// InterruptedException
				
				// �����p�P�b�g����������D
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
		// �S����ւ̃p�P�b�g�񓯊��z�����C�^����ꂽ�񐔌J��Ԃ��D
		for (int i = 0; i < repeatCount; i++) {
			dispatchPacketToAll(dstInetAddrs);
		}
	}
	
	//- PACKAGE METHOD ---------------------------------------------------------
	//	
	void dispatchPacketToAll(InetAddress[] dstInetAddrs)
	{
		// �^����ꂽ����A�h���X�𑖍�����D
		for (InetAddress dstInetAddr : dstInetAddrs) {
			// �G�R�[�v���p�P�b�g�𐶐�����D
			CommPacket request = new CommPacket(
				MessageType.ECHO_REQUEST,		// ���b�Z�[�W�^�C�v
				EchoClient.systemManagerName,	// ���� CommObject ��
				dstInetAddr	// ����ƂȂ�m�[�h��IP�A�h���X
			);
			// �G�R�[�v���p�P�b�g�ɃR���e���c��ݒ肷��D
			request.setSerializedPayload(EchoClient.CONTENT);
						
			// �G�R�[�v���p�P�b�g��z������D
			dispatchPacket(request);
		}
	}

//------------------------------------------------------------------------------
//  PROTECTED METHOD:
//------------------------------------------------------------------------------
	
	//- PROTECTED METHOD -------------------------------------------------------
	// ����M�p�P�b�g�̏���
	//
	@Override
	protected boolean incomingPacketHandler(CommPacket response)
	{
		boolean wasHandled = false;

		// ���b�Z�[�W�^�C�v���擾�E�m�F����D
		String msgType = response.getMessageType();
		if (msgType.equals(MessageType.ECHO_RESPONSE)) {
			// �����p�P�b�g�̃��b�Z�[�W�^�C�v���G�R�[�����F
			
			// �����p�P�b�g����������D
			processReceivedResponse(response);

			// ���̓p�P�b�g���������ꂽ���Ƃ������t���O�� true �ɂ���D
			wasHandled = true;
		}			
		// ���̓p�P�b�g���������ꂽ���Ƃ������t���O��Ԃ��D
		return wasHandled;
	}
		
	//- PROTECTED METHOD -------------------------------------------------------
	// ���p�P�b�g���M���̗�O����
	//
	@Override
	protected void packetIoExceptionHandler(PacketIoException ex)
	{
		ex.printStackTrace();
	}

	//- PROTECTED METHOD -------------------------------------------------------
	// �������B�p�P�b�g�̏���
	//
	@Override
	protected void unreachablePacketHandler(CommPacket inPkt)
	{
		// �����B�p�P�b�g���擾����D
		CommPacket unreachable = inPkt.getPayload(
			CommPacket.class
		);
		// �����B�p�P�b�g�Ɋւ�������o�͂���D
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
		// ��M���ׂ������p�P�b�g�̐���ݒ肷��D
		this.numResponses = numResponses;
		
		// ��M�����R���e���c�̑��o�C�g�����N���A����D
		this.numTotalRcvd = 0;
		
		// �v���J�n�������N���A����D
		this.startTime_ms = System.currentTimeMillis();
	}

	//- PRIVATE METHOD ---------------------------------------------------------
	//
	private synchronized void processReceivedResponse(CommPacket response)
	{
		if (this.numResponses <= 0) {
			return;
		}
		// ��M�����R���e���c�̃o�C�g�������Z����D
		this.numTotalRcvd += response.getPayloadLength();
		
		// �����p�P�b�g�̎�M�J�E���g���f�N�������g����D
		this.numResponses--;
		
		// �����p�P�b�g�̎�M�J�E���g�l���m�F����D
		if (this.numResponses == 0) {
			// ��M���ׂ������p�P�b�g��S�Ď�M�����̂ŁC
			// �v�����ʂ��o�͂���D
			
			// �v���I���������擾���C�v�����Ԃ����߂�D
			long endTime_ms = System.currentTimeMillis();
			double erapsedTime_sec = (endTime_ms - this.startTime_ms) / 1000.0;

			// ��M�����R���e���c�̑��o�C�g������C
			// ��M�X���[�v�b�g�����߂�D
			double throughput_mbps
				= (this.numTotalRcvd / 1000.0 / 1000.0) * 8 / erapsedTime_sec;
			
			// ��M�X���[�v�b�g���o�͂���D
			System.out.println();
			System.out.printf("> %d byte received, %.2f sec, %.2f Mbps\n",
				Long.valueOf(this.numTotalRcvd),
				Double.valueOf(erapsedTime_sec),
				Double.valueOf(throughput_mbps)
			);
		}
		else if ((this.numResponses % 100) == 0) {
			// ��M���������p�P�b�g���� 100 �Ŋ���؂��ꍇ�C
			// �r���o�߂��o�͂���D
			System.out.printf("(%d) ", Integer.valueOf(this.numResponses));
			
			// ��M���������p�P�b�g���� 1000 �Ŋ���؂��ꍇ�C
			// ���s����D
			if ((this.numResponses % 1000) == 0) {
				System.out.println();
			}
		}
	}
}
