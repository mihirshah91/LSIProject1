package com.session.RPC;


/*
 * callId opcode sessionid version message expiry
 * 
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import com.sessionManager.Constants;
import com.sessionManager.SessionManagerServlet;
import com.sessionModel.SessionModel;

public class RPCServer extends Thread {

	// int portProj1bRPC = 7111;
	int maxPacketSize = 512;
	DatagramSocket rpcSocket = null;

	public RPCServer() {
		try {
			System.out.println("Constructor called");
			rpcSocket = new DatagramSocket(Constants.RPC_PORT);
			System.out.println("rpcSocket=" + rpcSocket);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] sessionLogOut(String splitData[]) {
		
		
		SessionModel s = SessionManagerServlet.sessionTable.get(splitData[2]);
		System.out.println("inside logout");
		System.out.println("s=" + s);
		if (s != null) {

			SessionManagerServlet.sessionTable.remove(splitData[2]);

		}
		String data = splitData[0] ;
		return data.getBytes();
	}

	public byte[] sessionRead(String splitData[]) {
		
		
		Map<String, SessionModel> sessionTable = SessionManagerServlet.sessionTable;
		SessionModel s = SessionManagerServlet.sessionTable.get(splitData[2]);
		
		System.out.println("sessionid = " + splitData[2] + "callid=" + splitData[1]);
		System.out.println("inside session read");
		System.out.println("s=" + s);
		System.out.println(sessionTable);
		
		if (s != null) {
			SimpleDateFormat sdfr = new SimpleDateFormat(Constants.dateFormat);
			String data = splitData[0] + Constants.DELIMITER + splitData[1]+ Constants.DELIMITER +  s.sessionId + Constants.DELIMITER + s.versionNumber
					+ Constants.DELIMITER +  s.message + Constants.DELIMITER + sdfr.format(s.expiryTime); 

			return data.getBytes();
		} else {
			
			return  (splitData[0] + Constants.DELIMITER + Constants.SESSION_NOTFOUND).getBytes();
		}
		
	}

	public byte[] sessionWrite(String splitData[]) {
		Map<String, SessionModel> sessionTable = SessionManagerServlet.sessionTable;
	
		try {	
		SessionModel s = new SessionModel();
		s.setSessionId(splitData[2]);
		s.setVersionNumber(Integer.parseInt(splitData[3]));
		s.setMessage(splitData[4]);
		s.setExpiryTime(Constants.sdfr.parse(splitData[5]));
		sessionTable.put(s.getSessionId(), s);
		String returnString = splitData[0] + Constants.DELIMITER + Constants.SUCCESSFUL;
		return returnString.getBytes();
		} 
		
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String returnString = splitData[0] + Constants.DELIMITER + Constants.FAILURE;
			return returnString.getBytes();
		}
		
	}

	public void run() {

		while (true) {
			try {

				System.out.println("Server Started");
				byte[] inBuf = new byte[maxPacketSize];
				DatagramPacket receivedPacket = new DatagramPacket(inBuf, inBuf.length);

				rpcSocket.receive(receivedPacket);
				InetAddress returnAddr = receivedPacket.getAddress();
				int returnPort = receivedPacket.getPort();

				// here inBuf contains the callID and operationCode
				String receivedData = new String(receivedPacket.getData());
				String splitData[] = receivedData.split("_");

				int operationCode = Integer.parseInt(splitData[1]);
				byte[] outBuf = null;
				switch (operationCode) {

				case Constants.SESSIONREAD: {// SessionRead accepts call args
												// and returns call results
					System.out.println("inside sesison read switch statement");
					outBuf = sessionRead(splitData);
					break;

				}

				case Constants.SESSIONWRITE: {
					System.out.println("inside sesison write switch statement");
					outBuf = sessionWrite(splitData);
					break;
				}

				case Constants.SESSIONLOGOUT: {
					System.out.println("Inside switch session logout");
					outBuf = sessionLogOut(splitData);
				}

				}

				// here outBuf should contain the callID and results of the call
				DatagramPacket sendPkt;
				if (outBuf != null) {
					sendPkt = new DatagramPacket(outBuf, outBuf.length, returnAddr, returnPort);
					rpcSocket.send(sendPkt);
				} else {
					outBuf = "asfdd_sfsdf_23_dffsd_dfdfgg".getBytes();
					sendPkt = new DatagramPacket(outBuf, outBuf.length, returnAddr, returnPort);
					rpcSocket.send(sendPkt);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
