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
					sessionFound=true;

					break;

				}
			}

			if(!sessionFound)
			{
				// get the new id and call with opcode write
				String id = getUniqueId();
				RPCClient c = new RPCClient();
				SessionModel s = new SessionModel(id, Constants.DEFAULTVERSIONINT, Constants.DEFAULTMESSAGE, new Date());
				c.sendRequest(id, Constants.SESSIONWRITE, s);
				
				
				
			}
			
			if (request.getParameter("replaceButton") != null) {
					
					readLocation(Constants.SESSIONREAD);
					RPCClient c = new RPCClient();
					replace(sessionId,message);
					request.setAttribute("type", "render");
					request.getRequestDispatcher("/index.jsp").forward(request, response);

				} 

				else if (request.getParameter("logoutButton") != null)

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
					refresh(sessionId, message);
					request.setAttribute("type", "refresh");
					// sessionCookie.setMaxAge(Constants.EXPIRYTIME);
					// response.addCookie(sessionCookie);
					request.getRequestDispatcher("/index.jsp").forward(request, response);

				}
			}

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
		sessionTable.remove(sessionId);
		System.out.println("Replace method called");
		RPCClient c = new RPCClient();
		//SessionModel s = c.sendRequest(sessionId, Constants.SESSIONLOGOUT, "");
	}

	public void refresh(String sessionId,String message) throws IOException, ServletException {
		RPCClient c = new RPCClient();
		SessionModel s = c.sendRequest(sessionId, Constants.SESSIONREAD, new SessionModel(sessionId, 1, message , new Date()));
		
	}

	
	public void replace(String sessionId, String message) {

		System.out.println("Replace method called");
		RPCClient c = new RPCClient();
		SessionModel s = c.sendRequest(sessionId, Constants.SESSIONREAD, new SessionModel(sessionId, 1, message , new Date()));
		

	}

	/*public SessionModel retrieveSession(String sessionId) {
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
*/
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

	
}
