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
import com.sessionModel.SessionModel;

public class RPCClient {

	static List<String> dest = new ArrayList<>();
	static List<String> serverid = new ArrayList<>();
	static List<String> allDests = new ArrayList<>();
	static Map<String, String> serverIdIp = new HashMap<String, String>();
	
	
	
	static Map<String,String> serverMap = new LinkedHashMap<>();
	//int portProj1bRPC = 7111;
	int maxPacketSize = 512;
	static int callNumber = 0;
	public static SessionModel sessionObj = null;
	public static String locationMetdata = "";

	public static void intializeIPList(String[] ips)
	{
		System.out.println("intialize ip for read called");
		//dest.clear();
		serverIdIp.clear();
		for(int i=2; i<ips.length;i++)
		{
			
			//dest.add(serverMap.get(ips[i]));
			//serverid.add(ips[i]);
			serverIdIp.put(ips[i], serverMap.get(ips[i]));
			
		}
		
	}
	
	public static void intializeIPList()
	{
		System.out.println("intialize ip for write called");
		allDests.clear();
		serverid.clear();
		Iterator<Map.Entry<String,String>> itr = serverMap.entrySet().iterator();
		
		while(itr.hasNext())
		{
			
			Map.Entry<String, String> next = itr.next();
			allDests.add(next.getValue());
			
		}
		
	}
	
	
	
	
	public SessionModel sendRequest(String id, int opcode, String message) {

		try {
			sessionObj = null;
			locationMetdata = "";
			RPCClientThread.WQAcks = 0;
			
			// following method builds the map
			callJsonParser();
			
			Iterator<Map.Entry<String,String>> itr = null;
			//Iterator<String> itr = null;
			if(opcode == Constants.SESSIONREAD)
				//itr = dest.iterator();
				itr = serverIdIp.entrySet().iterator();
			else
				//itr = allDests.iterator();
				itr = serverMap.entrySet().iterator();
			
			System.out.println("SESSION ID : " + id);
			int index = 0;
			while(itr.hasNext()) {
				
				Map.Entry<String, String> next = itr.next();
				
				RPCClientThread thread = null;
				if (opcode == Constants.SESSIONREAD)
				{
					//thread = new RPCClientThread(this, id, opcode, next);
					thread = new RPCClientThread(this, id, opcode, next.getValue());
					
				}
				else if (opcode == Constants.SESSIONWRITE)
				{
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
	
	public static void callJsonParser()
	{
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(Constants.filePath));
			 StringBuilder sb = new StringBuilder();
			 String line = "";
			 
		        while ( (line=br.readLine()) != null) {
		            sb.append(line);
		            sb.append("\n");
		           
		        }
			
			System.out.println("json = " + sb);
			
			String json = new String(sb);
			
			JSONObject obj = new JSONObject(json);
			//String pageName = obj.getJSONObject("pageInfo").getString("pageName");

			JSONArray arr = obj.getJSONArray("Items");
			for (int i = 0; i < arr.length(); i++)
			{
			    JSONArray ipArray = arr.getJSONObject(i).getJSONArray("Attributes");
			    String ip = ipArray.getJSONObject(0).getString("Value");
			    String serverid = arr.getJSONObject(i).getString("Name");
			    serverMap.put(serverid, ip);
			 //   System.out.println("ip = " + ip);
			    
			}

			System.out.println("server map = " + serverMap);
			
			
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
