package com.session.RPC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sessionManager.Constants;
import com.sessionModel.SessionModel;

public class RPCClient {

	List<String> dest = new ArrayList<String>(Arrays.asList("10.132.2.77")); 
	
	Map<String,String> serverMap = new LinkedHashMap<>();
	int portProj1bRPC = 1800;
	int maxPacketSize = 512;
	static int callNumber = 0;
	public static SessionModel sessionObj = null;
	public static String locationMetdata = "";

	public SessionModel sendRequest(String id, int opcode, String message) {

		try {
			sessionObj = null;
			locationMetdata = "";
			RPCClientThread.WQAcks = 0;
			
			// following method builds the map
			callJsonParser();
			
			Iterator<Map.Entry<String,String>> itr = serverMap.entrySet().iterator();
			
			System.out.println("SESSION ID : " + id);
			while(itr.hasNext()) {
				
				Map.Entry<String, String> next = itr.next();
				
				RPCClientThread thread = null;
				if (opcode == Constants.SESSIONREAD)
					thread = new RPCClientThread(this, id, opcode, next.getValue());
				else if (opcode == Constants.SESSIONWRITE)
					thread = new RPCClientThread(this, id, opcode, next.getValue(), message);
				else if (opcode == Constants.SESSIONLOGOUT) {
					thread = new RPCClientThread(this, id, opcode, next.getValue());

				}

				//thread.initialize();
				// RPCClientThread thread = new RPCClientThread();
				thread.start();
				thread.join();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sessionObj;
	}
	
	public void callJsonParser()
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
