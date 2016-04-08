package com.session.RPC;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import com.sessionManager.Constants;
import com.sessionManager.SessionManagerServlet;
import com.sessionModel.SessionModel;

public class RPCServer extends Thread {

	int portProj1bRPC = 1900;
	int maxPacketSize = 512;
	DatagramSocket rpcSocket = null;

	public RPCServer() {
		try {
			System.out.println("Constructor called");
			rpcSocket = new DatagramSocket(portProj1bRPC);
			System.out.println("rpcSocket=" + rpcSocket);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] sessionLogOut(String callId, String sessionId) {
		System.out.println("sessionid = " + sessionId + "callid=" + callId);
		// System.out.println(sessionTable);
		SessionModel s = SessionManagerServlet.sessionTable.get(sessionId.trim());
		System.out.println("inside session read");
		System.out.println("s=" + s);
		if (s != null) {

			SessionManagerServlet.sessionTable.remove(sessionId.trim());

		}
		String data = callId;
		return data.getBytes();
	}

	public byte[] sessionRead(String callId, String sessionId) {
		Map<String, SessionModel> sessionTable = SessionManagerServlet.sessionTable;
		System.out.println("sessionid = " + sessionId + "callid=" + callId);
		System.out.println(sessionTable);
		SessionModel s = SessionManagerServlet.sessionTable.get(sessionId.trim());
		System.out.println("inside session read");
		System.out.println("s=" + s);
		if (s != null) {
			int version = s.getVersionNumber();
			System.out.println("inside session read if loop");
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, Constants.EXPIRYTIME + Constants.delta);
			s.setExpiryTime(cal.getTime());

			s.setVersionNumber(version + 1);

			String tempid = s.getSessionId();
			String tempSplitData[] = tempid.split(Constants.DEFAULTVERSIONNUMBER);

			//String tempnewkey = tempSplitData[0] + Constants.DELIMITERVERSION + s.getVersionNumber();
			//String tempnewkey = tempSplitData[0] ;
			//System.out.println("new key= " + tempnewkey);

			sessionTable.put(s.getSessionId(), s);

			SimpleDateFormat sdfr = new SimpleDateFormat();

			String data = callId + Constants.DELIMITER + s.sessionId + Constants.DELIMITER + s.versionNumber
					+ Constants.DELIMITER + sdfr.format(s.expiryTime) + Constants.DELIMITER + s.message;

			return data.getBytes();
		} else {
			// create session
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, Constants.EXPIRYTIME + Constants.delta);
			SimpleDateFormat sdfr = new SimpleDateFormat();
			SessionModel ses = new SessionModel(sessionId.trim(), 1, "Hello user");
			ses.setExpiryTime(cal.getTime());
			String outputString = callId + Constants.DELIMITER + sessionId.trim() + Constants.DELIMITER + "1"
					+ Constants.DELIMITER + sdfr.format(ses.expiryTime) + Constants.DELIMITER + ses.message;
			sessionTable.put(sessionId.trim(), ses);

			return outputString.getBytes();
		}
		// return null;
	}

	public byte[] sessionWrite(String callId, String sessionId, String message) {
		Map<String, SessionModel> sessionTable = SessionManagerServlet.sessionTable;
		SessionModel s = SessionManagerServlet.sessionTable.get(sessionId.trim());

		if (s != null) {
			int version = s.getVersionNumber();

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, Constants.EXPIRYTIME + Constants.delta);
			//cal.add(Calendar.SECOND, amount);
			s.setExpiryTime(cal.getTime() );
			s.setVersionNumber(version + 1);
			s.setMessage(message);
			String tempid = s.getSessionId();

			String tempSplitData[] = tempid.split(Constants.DEFAULTVERSIONNUMBER);
			//String tempnewkey = tempSplitData[0] + Constants.DELIMITERVERSION + s.getVersionNumber();
			//String tempnewkey = tempSplitData[0] ;
			//System.out.println("new key= " + tempnewkey);
			sessionTable.put(s.getSessionId(), s);

			SimpleDateFormat sdfr = new SimpleDateFormat();
			String data = callId + Constants.DELIMITER + s.sessionId + Constants.DELIMITER + s.versionNumber
					+ Constants.DELIMITER + sdfr.format(s.expiryTime) + Constants.DELIMITER + s.message;

			return data.getBytes();

		}

		String data = "";
		return data.getBytes();

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
					outBuf = sessionRead(splitData[0], splitData[2]);
					break;

				}

				case Constants.SESSIONWRITE: {
					System.out.println("inside sesison write switch statement");
					outBuf = sessionWrite(splitData[0], splitData[2], splitData[3]);
					break;
				}

				case Constants.SESSIONLOGOUT: {
					System.out.println("Inside switch session logout");
					outBuf = sessionLogOut(splitData[0], splitData[2]);
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
