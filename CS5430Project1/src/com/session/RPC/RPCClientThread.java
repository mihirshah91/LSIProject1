
/*
 * Order of sending data : id version message expiryTime
 */


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
			//SimpleDateFormat sdfr = new SimpleDateFormat();
			String sendData = localNumber + Constants.DELIMITER + opcode + Constants.DELIMITER + id
					+ Constants.DELIMITER + session.getVersionNumber() + Constants.DELIMITER + session.getMessage() + Constants.DELIMITER + Constants.sdfr.format(session.getExpiryTime()) ;
			outBuf = sendData.getBytes();
			clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(host);
			DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, IPAddress, Constants.RPC_PORT);
			clientSocket.setSoTimeout(Constants.SOCKETTIMEOUT);
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
				String splitData[]  = data.split(Constants.DELIMITER);
				
				if(opcode == Constants.SESSIONLOGOUT)
					break;
				callidReturned = Integer.parseInt(splitData[0]);

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
				
				if (RPCClient.sessionObj == null && !data.contains(Constants.SESSION_NOTFOUND) && opcode != Constants.SESSIONLOGOUT && opcode!=Constants.SESSIONWRITE ) {
						
					System.out.println("inside first time assigning the object value");
				
					String splitData[] = data.split(Constants.DELIMITER);
					RPCClient.sessionObj = new SessionModel(splitData[2], Integer.parseInt(splitData[3]), splitData[4], Constants.sdfr.parse(splitData[5]));
					RPCClient.sessionObj.setIntialserverId(serverid);

				}
			}

		clientSocket.close();

		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
