package com.sessionManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.json.JSONArray;
import org.json.JSONObject;

import com.session.RPC.RPCClient;
import com.session.RPC.RPCServer;
import com.sessionModel.SessionModel;

/**
 * 
 * @author mihir
 * Listener class which will be initialized on server startup and also contains GarbageCollector class 
 * which extends Java TimerTask class so that it will be scheduled at regular intervals for session clearance
 */


@WebListener
public class StaleSessionCleaner implements ServletContextListener {

	long timeInterval = 15; // in seconds - time interval after which it should be run
	long delay = 15;  // in seconds initial delay in seconds
	public static Map<String,String> serverMap = new LinkedHashMap<>();
	static public  String filepath = null;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Context destroyed");
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
		filepath = this.getClass().getResource("/").getPath();
		System.out.println("listener started");
		
		TimerTask timerTask = new GarbageCollector();
		Timer timer = new Timer();
		timer.schedule(timerTask,delay*1000,timeInterval*1000);
		
		
		RPCServer server = new RPCServer();
		callJsonParser();
		server.start();
		
	}

	public static void callJsonParser()
	{
		try {
			
			//filepath = filepath.replace("WEB-INF/classes/", "");
			
			//BufferedReader br = new BufferedReader(new FileReader( filepath+ Constants.filePath));
			BufferedReader br = new BufferedReader(new FileReader( Constants.filePath));
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


class GarbageCollector extends TimerTask
{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("timer called");
		Map<String,SessionModel> sessionTable = SessionManagerServlet.sessionTable;
		System.out.println(sessionTable);
		
		// iterate the map and delete the expired entry
		
		Iterator<Map.Entry<String, SessionModel>> itr = sessionTable.entrySet().iterator();
		
		
		while(itr.hasNext())
		{
			
			Map.Entry<String,SessionModel> s = itr.next();
			String id = s.getKey();
			SessionModel sm = s.getValue();
			Calendar cal = Calendar.getInstance();
			
			if(sm.getExpiryTime().getTime() < cal.getTimeInMillis())
			{
				//remove entry from map
				sessionTable.remove(id);
				System.out.println("Entry with id " + id + "removed from sesison table");
			}
			
			
			
		}
		
		
		
		
	}
	
	
	
	
	
}