package com.session.RPC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.standard.Destination;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sessionManager.Constants;
import com.sessionManager.StaleSessionCleaner;
import com.sessionModel.SessionModel;

public class RPCClient {

	static List<String> dest = new ArrayList<>();
	static List<String> serverid = new ArrayList<>();
	static List<String> allDests = new ArrayList<>();
	static Map<String, String> serverIdIp = new HashMap<String, String>();
	
	
	
	
	//int portProj1bRPC = 7111;
	int maxPacketSize = 512;
	static int callNumber = 0;
	public static SessionModel sessionObj = null;
	public static String locationMetdata ;

	public static void intializeIPList(String[] ips)
	{
		System.out.println("intialize ip for read called");
		//dest.clear();
		serverIdIp.clear();
		
		String ipsnew[] = null;
		
		if(RPCClient.locationMetdata !=null)
		{
			
		for(int i=2; i<ips.length;i++)
		{
			
			//dest.add(serverMap.get(ips[i]));
			//serverid.add(ips[i]);
			serverIdIp.put(ips[i], StaleSessionCleaner.serverMap.get(ips[i]));
			
		}
		
		}
		
		
	}
	

	public SessionModel sendRequest(String id, int opcode, SessionModel s) {

		try {
			sessionObj = null;
			//locationMetdata = "";
			RPCClientThread.WQAcks = 0;
			System.out.println("inside RPCClient with opcode = " + opcode);
			
			if(opcode == Constants.SESSIONREAD)
				{
				callThreads(serverIdIp,Constants.SESSIONREAD,s);
				
				// Read the object returned by the servers, do the version increment, replace the message and then call write again
				SessionModel existingSession = RPCClient.sessionObj;
				existingSession.setVersionNumber(existingSession.getVersionNumber() + 1);
				existingSession.setMessage(s.message);
				existingSession.setExpiryTime( new Date((new Date()).getTime() + Constants.EXPIRYTIME));
				callThreads(StaleSessionCleaner.serverMap,Constants.SESSIONWRITE,existingSession);
				return existingSession;
				
				}
			else if(opcode == Constants.SESSIONWRITE)  // first time create session
			{
				
				s.setExpiryTime(new Date((new Date()).getTime() + Constants.EXPIRYTIME));
				locationMetdata="";
				callThreads(StaleSessionCleaner.serverMap,Constants.SESSIONWRITE,s);
			}
			
//			System.out.println("SESSION ID : " + id);
//			int index = 0;
//			
//			if(opcode == Constants.SESSIONWRITE)
//				locationMetdata="";
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sessionObj;
	}
	
	
	
	public void callThreads(Map<String, String> map,int opcode, SessionModel s)
	{
		Iterator<Map.Entry<String,String>> itr = map.entrySet().iterator();
		
		
		while(itr.hasNext()) {
			
			
			Map.Entry<String, String> next = itr.next();
			
			System.out.println("inside RPCClient with server ip =" + next.getValue());
			
			RPCClientThread thread = null;
			if (opcode == Constants.SESSIONREAD)
			{
				thread = new RPCClientThread(this, opcode,s, next.getValue());
				
			}
			else if (opcode == Constants.SESSIONWRITE)
			{
				
				thread = new RPCClientThread(this, opcode, s,next.getValue());
				
			}
			else if (opcode == Constants.SESSIONLOGOUT) {
			
				thread = new RPCClientThread(this, opcode, s,next.getValue());
				

			}
			thread.setServerid(next.getKey());
			
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
	}
	
	

}
