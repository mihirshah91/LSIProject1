package com.sessionManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.session.RPC.RPCClient;
import com.sessionModel.SessionModel;
import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

/**
 * @author mihir Servlet implementation class SessionManagerServlet. It has
 *         doPost method implemented and also helper methods
 */

// TODO
// Add cookie back to each response
@WebServlet("/SessionManagerServlet")
public class SessionManagerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Map<String, SessionModel> sessionTable = new ConcurrentHashMap<String, SessionModel>();
	// always written in seconds
	public static int sessionNumber = 1;
	Cookie sessionCookie;

	public SessionManagerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/*
	 * Post method of this servlet is called on form submisison. It first
	 * identifies the button pressed with help of request.getParameter() and
	 * takes the respective actions
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		Cookie[] cookies = request.getCookies();
		String sessionId = "";
		String message = "";

		// handle the case for session time-out here
		sessionCookie = null;
		boolean sessionFound = false;
		if (cookies != null) {
			for (Cookie c : cookies) {

				if (c.getName().equals(Constants.COOKIENAME)) {
					String cookieValue = c.getValue();
					/*
					 * int index = cookieValue.indexOf("_"); sessionId =
					 * cookieValue.substring(0, index);
					 */
					String tempData[] = cookieValue.split(Constants.DELIMITER);
					sessionId = tempData[0];
					message = request.getParameter("userMessage");
					sessionCookie = c;
					sessionFound=true;

					break;

				}
			}
		}
			if(!sessionFound)
			{
				// get the new id and call with opcode write
				System.out.println("sesion not found with ami launch index 0");
				String id = getUniqueId();
				RPCClient c = new RPCClient();
				SessionModel s = new SessionModel(id, Constants.DEFAULTVERSIONINT, Constants.DEFAULTMESSAGE, new Date());
				SessionModel newSession = c.sendRequest(id, Constants.SESSIONWRITE, s);
				request.setAttribute("type", "create");
				setRequestAttribute(request, newSession);
				String cookieValue = id + Constants.DELIMITER + Constants.DEFAULTVERSIONNUMBER + Constants.DELIMITER + RPCClient.locationMetdata ; 
				Cookie cookie = new Cookie(Constants.COOKIENAME, cookieValue);
				cookie.setMaxAge(Constants.EXPIRYTIME);
				setCookieAttributes(request, response, cookie);
				request.getRequestDispatcher("/index.jsp").forward(request, response);
				
				
			}
			
			else if (request.getParameter("replaceButton") != null) {
					
					readLocation(Constants.SESSIONREAD);
					RPCClient c = new RPCClient();
					SessionModel s = replace(sessionId,message,request);
					request.setAttribute("type", "replace");
					sessionCookie.setMaxAge(Constants.EXPIRYTIME);
					setCookieAttributes(request,response,sessionCookie);
					
					if(!s.isSessionNotFound())
						request.getRequestDispatcher("/index.jsp").forward(request, response);
					else
						request.getRequestDispatcher("/error.html").forward(request, response);

				} 

				else if (request.getParameter("logoutButton") != null)

				{
					
					readLocation(Constants.SESSIONREAD);
					request.setAttribute("type", Constants.LOGOUTYPE);
					logout(sessionId);
					sessionCookie.setMaxAge(0);
					setCookieAttributes(request, response, sessionCookie);
					request.getRequestDispatcher("/logout.html").forward(request, response);
					System.out.println("AFTER DELETING FROM THE COOKIE");

				} else {
				
					
					readLocation(Constants.SESSIONREAD);
					SessionModel s = refresh(sessionId, message,request);
					request.setAttribute("type", "refresh");
					sessionCookie.setMaxAge(Constants.EXPIRYTIME);
					setCookieAttributes(request, response, sessionCookie);
					if(!s.isSessionNotFound())
						request.getRequestDispatcher("/index.jsp").forward(request, response);
					else
						request.getRequestDispatcher("/error.html").forward(request, response);

				}
			

		}

	
	public void setCookieAttributes (HttpServletRequest request, HttpServletResponse response,Cookie sessionCookie)
	{
		sessionCookie.setDomain(Constants.DOMAIN_NAME);
		sessionCookie.setPath(Constants.DOMAINPATH);
		
		response.addCookie(sessionCookie);
		
		
	}

	
	public void setRequestAttribute(HttpServletRequest request, SessionModel s)
	{
		request.setAttribute("SessionData", s);
		
		
		
	}
	
	
	public void readLocation(int opcode) {
		String splitData[] = null;
		String cookieValue = sessionCookie.getValue();
		splitData = cookieValue.split(Constants.DELIMITER);
		
		if (opcode == Constants.SESSIONREAD)
			RPCClient.intializeIPList(splitData);

	}

	/*
	 * Removes the entry from sessionTable
	 */

	// TODO LOGOUT
	public void logout(String sessionId) {
		
		System.out.println("Replace method called");
		RPCClient c = new RPCClient();
		SessionModel s = c.sendRequest(sessionId, Constants.SESSIONLOGOUT, new SessionModel());
		
	}

	public SessionModel refresh(String sessionId,String message,HttpServletRequest request) throws IOException, ServletException {
		RPCClient c = new RPCClient();
		SessionModel s = c.sendRequest(sessionId, Constants.SESSIONREAD, new SessionModel(sessionId, 1, message , new Date()));
		setRequestAttribute(request, s);
		return s;
	}

	
	public SessionModel replace(String sessionId, String message, HttpServletRequest request) {

		System.out.println("Replace method called");
		RPCClient c = new RPCClient();
		SessionModel s = c.sendRequest(sessionId, Constants.SESSIONREAD, new SessionModel(sessionId, 1, message , new Date()));
		setRequestAttribute(request, s);
		return s;

	}

	
	public String getUniqueId() {
		String sessionId = null;
		try {
			String filepath = this.getClass().getResource("/").getPath();
			System.out.println("sesion not found with ami launch index 0 in method");
			filepath = filepath.replace("WEB-INF/classes/", "");
			System.out.println("LOCAL DATA FILE " + filepath);
			
			//BufferedReader br = new BufferedReader(new FileReader(filepath + Constants.LOCALDATA_PATH));

			BufferedReader br = new BufferedReader(new FileReader(Constants.REBOOT_DATA_PATH));
			BufferedReader br_reboot = new BufferedReader(new FileReader(Constants.SERVER_ID_PATH));
			sessionId = br_reboot.readLine();
			sessionId = sessionId + Constants.DELIMITERVERSION + br.readLine() + Constants.DELIMITERVERSION
					+ String.valueOf(sessionNumber);
			sessionNumber++;

			br.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return sessionId;
	}

	
}
