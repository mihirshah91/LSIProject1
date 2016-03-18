package com.sessionManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;

import com.sessionModel.SessionModel;


public class SessionManager {

	public static Map<String,SessionModel> sessionTable =new ConcurrentHashMap<String,SessionModel>();
	
	public void createHashTable()
	{
		System.out.println("inside create table");
		//sessionTable = new ConcurrentHashMap<String,SessionModel>();
		
	}
	
	public SessionModel retrieveSession(Cookie c)
	{
		System.out.println("inside retrieve sesison");
		
		String sessionId = c.getValue();
		
		SessionModel s = sessionTable.get(sessionId);
		if(s!=null)
		{
		int version = s.getVersionNumber();
		s.setVersionNumber(version+1);
		sessionTable.put(sessionId, s);
		
		return s;
		
		}
		
		return null;
		
	}
	
	public String createSession()
	{
		System.out.println("inside create session");
		System.out.println("sessionTable= " +sessionTable);
		String uniqueID = UUID.randomUUID().toString();
		SessionModel s = new SessionModel(uniqueID, 0,"Hello user");
		sessionTable.put(uniqueID, s);
		System.out.println("unique id generated is " + uniqueID);
		return uniqueID;
		
	}
	
}
