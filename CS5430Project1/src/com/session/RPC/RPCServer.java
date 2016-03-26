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
	
	int portProj1bRPC = 1234;
	int maxPacketSize = 512;
	DatagramSocket rpcSocket = null;
	
	public RPCServer()
	{
		try {
			rpcSocket = new DatagramSocket(portProj1bRPC);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public byte[] sessionRead(String callId,String sessionId)
	{
		Map<String,SessionModel> sessionTable = SessionManagerServlet.sessionTable;
		System.out.println("sessionid = " + sessionId + "callid=" + callId);
		System.out.println(sessionTable);
		SessionModel s = SessionManagerServlet.sessionTable.get(sessionId.trim());
		System.out.println("inside session read");
		System.out.println("s="+ s);
		if(s!=null)
		{
		int version = s.getVersionNumber();
		System.out.println("inside session read if loop");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND,Constants.EXPIRYTIME);
		s.setExpiryTime(cal.getTime());
		
		s.setVersionNumber(version+1);
		sessionTable.put(sessionId, s);
		
		 SimpleDateFormat sdfr = new SimpleDateFormat();
		
		String data = callId + Constants.DELIMITER + s.sessionId + Constants.DELIMITER + s.versionNumber + Constants.DELIMITER + sdfr.format(s.expiryTime)
					 + Constants.DELIMITER + s.message	;
		
		return data.getBytes();
		}
		return null;
	}
	
	
	
	
	public void run()
	{
		
		
		
		
		  while(true) {
			  try
				{
				  
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
		    switch( operationCode ) {
		    	
		    	case Constants.SESSIONREAD:
		    		// SessionRead accepts call args and returns call results 
		    		outBuf = sessionRead(splitData[0],splitData[2]);
		    		break;
		    
		    }
		    // here outBuf should contain the callID and results of the call
		    DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length,returnAddr, returnPort);
		    rpcSocket.send(sendPkt);
		  }
			  catch(Exception e)
				{
					e.printStackTrace();
				}
		}
		
		
	}
	
	
	
	
}
