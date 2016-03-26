package com.sessionManager;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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

	long timeInterval = 60; // in seconds - time interval after which it should be run
	long delay = 180;  // in seconds initial delay in seconds
	
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Context destroyed");
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("listener started");
		
		TimerTask timerTask = new GarbageCollector();
		Timer timer = new Timer();
		//timer.schedule(timerTask,delay*1000,timeInterval*1000);
		
		
		RPCServer server = new RPCServer();
		server.start();
		
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

			
			if(sm.getExpiryTime().getTime() < (new Date()).getTime())
			{
				//remove entry from map
				sessionTable.remove(id);
				System.out.println("Entry with id " + id + "removed from sesison table");
			}
			
			
			
		}
		
		
		
		
	}
	
}