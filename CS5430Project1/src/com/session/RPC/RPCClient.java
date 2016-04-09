package com.session.RPC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
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
			ipsnew = RPCClient.locationMetdata.split(Constants.DELIMITER);
		for(int i=0; i<ipsnew.length;i++)
		{
			
			//dest.add(serverMap.get(ips[i]));
			//serverid.add(ips[i]);
			serverIdIp.put(ips[i], StaleSessionCleaner.serverMap.get(ips[i]));
			
		}
		
		}
		
		
	}
	

	
	
	
	public SessionModel sendRequest(String id, int opcode, String message) {

		try {
			sessionObj = null;
			//locationMetdata = "";
			RPCClientThread.WQAcks = 0;
			
			// following method builds the map
			System.out.println("inside RPCClient with opcode = " + opcode);
			
			Iterator<Map.Entry<String,String>> itr = null;
			//Iterator<String> itr = null;
			if(opcode == Constants.SESSIONREAD)
				//itr = dest.iterator();
				itr = serverIdIp.entrySet().iterator();
			else
				//itr = allDests.iterator();
				itr = StaleSessionCleaner.serverMap.entrySet().iterator();
			
			System.out.println("SESSION ID : " + id);
			int index = 0;
			while(itr.hasNext()) {
				
				
				Map.Entry<String, String> next = itr.next();
				
				System.out.println("inside RPCClient with server ip =" + next.getValue());
				
				RPCClientThread thread = null;
				if (opcode == Constants.SESSIONREAD)
				{
					//thread = new RPCClientThread(this, id, opcode, next);
					thread = new RPCClientThread(this, id, opcode, next.getValue());
					
				}
				else if (opcode == Constants.SESSIONWRITE)
				{
					locationMetdata="";
					thread = new RPCClientThread(this, id, opcode, next.getValue(), message);
					
				}
				else if (opcode == Constants.SESSIONLOGOUT) {
				
					thread = new RPCClientThread(this, id, opcode, next.getValue());
					

				}
				thread.setServerid(next.getKey());
				//thread.initialize();
				// RPCClientThread thread = new RPCClientThread();
				index++; 
				thread.start();
				thread.join();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sessionObj;
	}
	
	

}
