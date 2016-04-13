package com.session.RPC;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;

import com.sessionManager.Constants;
import com.sessionModel.SessionModel;

public class RPCClientThread extends Thread {

	RPCClient client;
	String id;
	int opcode;
	int localNumber;
	String host;
	static int WQAcks;
	String message = "";
	String serverid="";
	SessionModel session;
	
	public String getServerid() {
		return serverid;
	}

	public void setServerid(String serverid) {
		this.serverid = serverid;
	}


	public void initialize() {
		
		WQAcks = 0;
		System.out.println("WQAcks ="  + WQAcks);
	}


	public RPCClientThread(RPCClient rpc,int opcode,SessionModel s,String ip)
	{
		this.session = s;
		setCommon(rpc, s.getSessionId(), opcode, ip);
		
		
	}
	
	public void setCommon(RPCClient rpc, String id, int opcode, String hostname) {
		this.client = rpc;
		rpc.callNumber++;
		localNumber = rpc.callNumber;
		this.id = id;
		this.opcode = opcode;
		host = hostname;
	}

	public RPCClientThread() {
		// TODO Auto-generated constructor stub
	}

	public void run() {
		DatagramPacket receivePacket = null;
		DatagramSocket clientSocket = null;
		try {
			byte[] outBuf = new byte[client.maxPacketSize];

			System.out.println("inside client thread");
			System.out.println();
			String sendData = localNumber + Constants.DELIMITER + opcode + Constants.DELIMITER + id
					+ Constants.DELIMITER + message;
			outBuf = sendData.getBytes();
			clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(host);
			DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, IPAddress, Constants.RPC_PORT);
			clientSocket.setSoTimeout(10000);
			clientSocket.send(sendPkt);

			byte[] inBuf = new byte[client.maxPacketSize];
			receivePacket = new DatagramPacket(inBuf, inBuf.length);

			int callidReturned = -1;
			String data=null;
			do {
				System.out.println("inside do while client thread");
				receivePacket.setLength(inBuf.length);
				clientSocket.receive(receivePacket);
				
				data = new String(receivePacket.getData());
				int index = data.indexOf("_");
				if(opcode == Constants.SESSIONLOGOUT)
					break;
				callidReturned = Integer.parseInt(data.substring(0, index));

				System.out.println("localNumber=" + localNumber);
				System.out.println("caliid=" + callidReturned);

			} while (callidReturned != localNumber);


			{
				WQAcks++;

			
				
				if (WQAcks <= Constants.WQ && opcode == Constants.SESSIONWRITE)
				{
					System.out.println("inside WQACK check");
					if(RPCClient.locationMetdata.equals(""))
						RPCClient.locationMetdata = serverid;
					else
						RPCClient.locationMetdata = RPCClient.locationMetdata + Constants.DELIMITER + serverid;
				}
				// synchronized (RPCClient.sessionObj) {
				
				// think for read and write
				if (data == null && RPCClient.sessionObj == null && opcode != Constants.SESSIONLOGOUT && opcode!=Constants.SESSIONWRITE) {
						
					System.out.println("inside first time assigning the object value");
					//data = new String(receivePacket.getData());
					String splitData[] = data.split(Constants.DELIMITER);
					RPCClient.sessionObj = new SessionModel(splitData[1], Integer.parseInt(splitData[2]), splitData[4]);
					RPCClient.sessionObj.setIntialserverId(serverid);
					SimpleDateFormat sdfr = new SimpleDateFormat(Constants.dateFormat);
					RPCClient.sessionObj.setExpiryTime(sdfr.parse(splitData[3]));

				}
			}

			// }

			clientSocket.close();

		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
