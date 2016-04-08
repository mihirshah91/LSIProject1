package com.sessionManager;

import java.io.BufferedReader;
import java.io.FileReader;
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

import com.session.RPC.RPCClient;
import com.sessionModel.SessionModel;

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
	static int sessionNumber = 0;
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

		for (Cookie c : cookies) {

			if (c.getName().equals("CS5300Project1SessionId")) {
				String cookieValue = c.getValue();
				/*
				 * int index = cookieValue.indexOf("_"); sessionId =
				 * cookieValue.substring(0, index);
				 */
				String tempData[] = cookieValue.split(Constants.DELIMITER);
				sessionId = tempData[0];
				message = request.getParameter("userMessage");
				sessionCookie = c;
				break;

			}
		}

		if (request.getParameter("replaceButton") != null) {
			// call replace method only if sessionid found else ignore - this
			// can happen if session is expired and refresh is called
			if (sessionId != "")
				replace(sessionId, message);
			request.setAttribute("type", "replace");
			response.addCookie(sessionCookie);
			request.getRequestDispatcher("/index.jsp").forward(request, response);
			

		} else if (request.getParameter("refreshButton") != null) {
			// call refresh method
			refresh(sessionId);
			request.setAttribute("type", "refresh");
			response.addCookie(sessionCookie);
			request.getRequestDispatcher("/index.jsp").forward(request, response);

		} else if (request.getParameter("logoutButton") != null)

		{
			// call logout method
			request.setAttribute("type", "logout");
			logout(sessionId);
			sessionCookie.setMaxAge(0);
			response.addCookie(sessionCookie);
			request.getRequestDispatcher("/logout.html").forward(request, response);
		} else {
			// if( sessionId == null && sessionId=="")
			refresh(sessionId);
			request.setAttribute("type", "refresh");
			response.addCookie(sessionCookie);
			request.getRequestDispatcher("/index.jsp").forward(request, response);

		}

	}

	/*
	 * Removes the entry from sessionTable
	 */

	public void logout(String sessionId) {
		sessionTable.remove(sessionId);
	}

	public void refresh(String sessionId) {
		RPCClient c = new RPCClient();
		SessionModel s = c.sendRequest(sessionId, Constants.SESSIONREAD, "");
	}

	/*
	 * Replaces the message in hashmap corresponding to the sessionid
	 */
	public void replace(String sessionId, String message) {

		System.out.println("Replace method called");
		RPCClient c = new RPCClient();
		SessionModel s = c.sendRequest(sessionId, Constants.SESSIONWRITE, message);
		/*
		 * SessionModel s = sessionTable.get(sessionId); s.setMessage(message);
		 * sessionTable.put(sessionId, s);
		 */

	}

	public SessionModel retrieveSession(String sessionId) {
		System.out.println("inside retrieve sesison");
		RPCClient c = new RPCClient();

		SessionModel s = c.sendRequest(sessionId, Constants.SESSIONREAD, "");
		if (s != null)
			return s;

		return null;
	}

	/*
	 * Generates the uniqueId if session not found
	 */

	public String getUniqueId() {
		String sessionId = null;
		try {

			BufferedReader br = new BufferedReader(new FileReader(Constants.LOCALDATA_PATH));

			sessionId = br.readLine();
			sessionId = sessionId + Constants.DELIMITERVERSION + br.readLine() +Constants.DELIMITERVERSION+ String.valueOf(++sessionNumber);

			br.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return sessionId;
	}

	/*
	 * Creates the newEntry in the hashMap if session is not found for user
	 * request
	 */

	public String createSession(String uniqueID, HttpServletRequest request) {
		System.out.println("inside create session");
		System.out.println("sessionTable= " + sessionTable);

		RPCClient c = new RPCClient();
		SessionModel s = c.sendRequest(uniqueID, Constants.SESSIONREAD, "");
		System.out.println("unique id generated is " + uniqueID);
		return uniqueID;

	}

}
