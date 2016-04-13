package com.sessionManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;

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
		if (cookies != null) {
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

			if (sessionCookie == null) {
				request.getRequestDispatcher("/index.jsp").forward(request, response);
			}

			else {
				if (request.getParameter("replaceButton") != null) {
					// call replace method only if sessionid found else ignore -
					// this
					// can happen if session is expired and refresh is called
					readLocation(Constants.SESSIONWRITE);
					if (sessionId != "")
						replace(sessionId, message);
					request.setAttribute("type", "replace");
					// sessionCookie.setMaxAge(expiry);
					// sessionCookie.setMaxAge(Constants.EXPIRYTIME);
					// response.addCookie(sessionCookie);
					request.getRequestDispatcher("/index.jsp").forward(request, response);

				} /*else if (request.getParameter("refreshButton") != null) {
					// call refresh method

					readLocation(Constants.SESSIONREAD);
					refresh(sessionId);
					request.setAttribute("type", "refresh"); //
					//sessionCookie.setMaxAge(Constants.EXPIRYTIME); //
					//response.addCookie(sessionCookie);
					request.getRequestDispatcher("/index.jsp").forward(request, response);

				}*/ else if (request.getParameter("logoutButton") != null)

				{
					// call logout method
					readLocation(Constants.SESSIONWRITE);
					request.setAttribute("type", Constants.LOGOUTYPE);
					logout(sessionId);
					sessionCookie.setMaxAge(0);
					sessionCookie.setPath("/");
					sessionCookie.setValue("");
					response.addCookie(sessionCookie);
					request.getRequestDispatcher("/logout.html").forward(request, response);
					System.out.println("AFTER DELETING FROM THE COOKIE");

				} else {
					// if( sessionId == null && sessionId=="")
					
					readLocation(Constants.SESSIONREAD);
					refresh(sessionId, request,response);
					request.setAttribute("type", "refresh");
					// sessionCookie.setMaxAge(Constants.EXPIRYTIME);
					// response.addCookie(sessionCookie);
					request.getRequestDispatcher("/index.jsp").forward(request, response);

				}
			}

		}
	}

	public void readLocation(int opcode) {
		String splitData[] = null;

		if (sessionCookie == null)
			opcode = Constants.SESSIONWRITE;
		else {
			String cookieValue = sessionCookie.getValue();
			splitData = cookieValue.split(Constants.DELIMITER);
		}

		if (opcode == Constants.SESSIONREAD)
			RPCClient.intializeIPList(splitData);

	}

	/*
	 * Removes the entry from sessionTable
	 */

	public void logout(String sessionId) {
		sessionTable.remove(sessionId);
		System.out.println("Replace method called");
		RPCClient c = new RPCClient();
		SessionModel s = c.sendRequest(sessionId, Constants.SESSIONLOGOUT, "");
	}

	public void refresh(String sessionId, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		RPCClient c = new RPCClient();
		if (sessionTable == null || !sessionTable.containsKey(sessionId)) {
//			sessionId = getUniqueId();
//			c.sendRequest(sessionId, Constants.SESSIONWRITE, "");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		} else {

			SessionModel s = c.sendRequest(sessionId, Constants.SESSIONREAD, "");
		}
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
		SessionModel s = null;

		if (sessionCookie == null)
			s = c.sendRequest(sessionId, Constants.SESSIONWRITE, "");
		else
			s = c.sendRequest(sessionId, Constants.SESSIONREAD, "");
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
			String filepath = this.getClass().getResource("/").getPath();

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

	/*
	 * Creates the newEntry in the hashMap if session is not found for user
	 * request
	 */

	public String createSession(String uniqueID, HttpServletRequest request) {
		System.out.println("inside create session");
		System.out.println("sessionTable= " + sessionTable);

		RPCClient c = new RPCClient();
		SessionModel s = null;
		if (sessionCookie == null)
			s = c.sendRequest(uniqueID, Constants.SESSIONWRITE, "");
		else
			s = c.sendRequest(uniqueID, Constants.SESSIONREAD, "");
		System.out.println("unique id generated is " + uniqueID);
		return uniqueID;

	}

}
