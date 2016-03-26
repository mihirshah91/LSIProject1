package com.sessionManager;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sessionModel.SessionModel;

/**
 * @author mihir
 * Servlet implementation class SessionManagerServlet.
 * It has doPost method implemented and also helper methods
 */
@WebServlet("/SessionManagerServlet")
public class SessionManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Map<String,SessionModel> sessionTable =new ConcurrentHashMap<String,SessionModel>();
    public int expiryTimeinSec = 180; // always written in seconds   
	
	
    public SessionManagerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	
	/*
	 * Post method of this servlet is called on form submisison. 
	 * It first identifies the button pressed with help of request.getParameter() and takes the respective actions 
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		Cookie[] cookies = request.getCookies();
		String sessionId="";
		String message="";
		
		//handle the case for session time-out here
		
		for(Cookie c: cookies)
		{
			if(c.getName().equals("CS5300Project1SessionId"))
			{
				String cookieValue = c.getValue();
				int index = cookieValue.indexOf("_");
				sessionId = cookieValue.substring(0, index);
				message = request.getParameter("userMessage");
			

			}
		}
		
		if(request.getParameter("replaceButton")!=null)
		{
			//call replace method only if sessionid found else ignore - this can happen if session is expired and refresh is called
			if(sessionId!="")
				replace(sessionId,message);
			response.sendRedirect(request.getContextPath() + "/index.jsp");
		}
		else if(request.getParameter("refreshButton")!=null)
		{
			//call refresh method
			refresh(sessionId);
			response.sendRedirect(request.getContextPath() + "/index.jsp");
			
		}
		else if(request.getParameter("logoutButton")!=null)

		{
			//call logout method
			logout(sessionId);
			response.sendRedirect(request.getContextPath() + "/logout.html");
		}
			
		
	}
	
	/*
	 * Removes the entry from sessionTable
	 */
	
	public void logout(String sessionId)
	{
		sessionTable.remove(sessionId);
	}
	
	
	public void refresh(String sessionId)
	{
		System.out.println("Refresh Method called");
	}
	
	
	/*
	 * Replaces the message in hashmap corresponding to the sessionid
	 */
	public void replace(String sessionId,String message)
	{
		
		System.out.println("Replace method called");
		SessionModel s = sessionTable.get(sessionId);
		s.setMessage(message);
		sessionTable.put(sessionId, s);
		
		
		
	}
	
	/*
	 * updates the versionNumber and expiryTime for the corresponding sessionId
	 */
	
	public SessionModel updateSesion(String sessionId)
	{
		SessionModel s = sessionTable.get(sessionId);
		if(s!=null)
		{
		int version = s.getVersionNumber();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND,expiryTimeinSec);
		s.setExpiryTime(cal.getTime());
		
		s.setVersionNumber(version+1);
		sessionTable.put(sessionId, s);
		
		return s;
		
		}
		return null;
	}
	
	public SessionModel retrieveSession(String sessionId)
	{
		System.out.println("inside retrieve sesison");
		
		SessionModel s = updateSesion(sessionId);
		if(s!=null)
			return s;
	
		
		return null;
		
	}
	
	/*
	 * Generates the uniqueId if session not found
	 */
	
	public String getUniqueId()
	{
		return UUID.randomUUID().toString();
	}
	
	
	/*
	 * Creates the newEntry in the hashMap if session is not found for user request
	 */
	
	public String createSession(String uniqueID,HttpServletRequest request)
	{
		System.out.println("inside create session");
		System.out.println("sessionTable= " +sessionTable);
		//String uniqueID = UUID.randomUUID().toString();
		
		//long requestDate = request.getDateHeader("If-Modified-Since");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND,expiryTimeinSec);
		
		
		
		
		SessionModel s = new SessionModel(uniqueID, 1,"Hello user");
		s.setExpiryTime(cal.getTime());
		sessionTable.put(uniqueID, s);
		System.out.println("unique id generated is " + uniqueID);
		return uniqueID;
		
	}
	
	
	

}
