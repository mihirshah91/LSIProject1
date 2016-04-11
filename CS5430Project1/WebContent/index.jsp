
<%@page import="org.apache.catalina.tribes.group.RpcCallback"%>
<%@page import="com.session.RPC.RPCClient"%>
<%@page import="com.sessionManager.Constants"%>
<%@page import="com.sessionManager.SessionManagerServlet"%>
<%@page import="com.sessionModel.SessionModel"%>
<%@page import="com.sessionManager.*"%>


<%@ page import="java.io.*,java.util.*"%>



<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CS5300</title>
</head>
<body>

	<%-- Declaring general variables used for display on page --%>

	<%!String sessionId;
	int versionNumber;
	String message;
	String cookie;
	// always in seconds
	Date expiryTime;
	String serverId;

	// Initializing variables which are displayed on screen for first request of new session

	public void initialize(String id, String cookieTemp) {
		sessionId = id;
		versionNumber = 1;
		message = "Hello user";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, Constants.EXPIRYTIME);

		expiryTime = cal.getTime();
		cookie = cookieTemp;
	}%>

	<%-- Below code identifies if the cookie is set in request or not.
 	  If not then create the session, seth the cookie in response with expiry time
 	  If found, then update the seeion parameters	
  --%>

	<%
		Cookie sessioCookie = null;
		Cookie[] cookies = request.getCookies();
		SessionManagerServlet s = new SessionManagerServlet();
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		//response.setDateHeader("Expires", -1);
		//response.setDateHeader("Last-Modified", new Date().getTime());

		boolean sessionFound = false;

		if (cookies != null) {
			for (Cookie c : cookies) {
				System.out.println("cookie " + c.getName());
				if (c.getName().equals("CS5300Project1SessionId")) {

					String cookieValue = c.getValue();
					/* int index = cookieValue.indexOf("_");
					sessionId = cookieValue.substring(0, index); */
					String splitData[] = cookieValue.split("_");
					SessionModel sessionObj = null;

					String type = (String) request.getAttribute("type");
					System.out.println("type in jsp=" + type);

					if (type == null)

					{

						//sessionObj = s.retrieveSession(splitData[0] + Constants.DELIMITERVERSION + splitData[1]);
						sessionObj = s.retrieveSession(splitData[0]);
						//c.setMaxAge(Constants.EXPIRYTIME+ Constants.delta);
					} else

					{
						int temp = Integer.parseInt(splitData[1]);
						System.out.println("inside type=replcae");
						/* sessionObj = SessionManagerServlet.sessionTable
							.get(splitData[0]);  */
						sessionObj = RPCClient.sessionObj;
					}

					if (sessionObj != null) {
						versionNumber = sessionObj.getVersionNumber();
						message = sessionObj.getMessage();
						sessionId = sessionObj.getSessionId();
						/* cookie = sessionId + Constants.DELIMITER + versionNumber + Constants.DELIMITER
								+ splitData[2] + RPCClient.locationMetdata ; */
						cookie = sessionId + Constants.DELIMITER + versionNumber + Constants.DELIMITER
								+ RPCClient.locationMetdata;
						c.setMaxAge(Constants.EXPIRYTIME);
						c.setValue(cookie);
						c.setDomain(Constants.DOMAIN_NAME);
						c.setPath("/");
						response.addCookie(c);
						expiryTime = sessionObj.getExpiryTime();
						serverId = RPCClient.sessionObj.getIntialserverId();

						sessionFound = true;
					}

				}

			}

		}

		if (!sessionFound) {
			String sessionID = s.getUniqueId();
			s.createSession(sessionID, request);
			String temp = sessionID + Constants.DELIMITER + Constants.DEFAULTVERSIONNUMBER + Constants.DELIMITER
					+ RPCClient.locationMetdata;

			Cookie sessionIdCookie = new Cookie("CS5300Project1SessionId", temp);
			sessionIdCookie.setMaxAge(Constants.EXPIRYTIME);
			sessionIdCookie.setDomain(Constants.DOMAIN_NAME);
			
			sessionIdCookie.setPath("/");
			response.addCookie(sessionIdCookie);
			serverId = RPCClient.sessionObj.getIntialserverId();
			initialize(sessionID, temp);
		}
	%>




	Net id:mgs275 &nbsp;&nbsp; Session :
	<%
		out.print(sessionId);
	%>


	&nbsp;&nbsp; Version:<%
		out.print(versionNumber);
	%>
	&nbsp;&nbsp;

	<%=new java.util.Date()%>

	<br />
	<br />


	<form action="${pageContext.request.contextPath}/SessionManagerServlet"
		method="post">

		<p>
			<%
				out.print(message);
			%>
		</p>

		<input type="submit" name="replaceButton" value="Replace"></input>
		&nbsp;&nbsp; <input type="text" name="userMessage" value=""
			required="required" maxLength="512"></input> <br /> <input
			type="submit" name="refreshButton" value="Refresh" formnovalidate></input>
		<br /> <input type="submit" name="logoutButton" value="Logout"
			formnovalidate></input> <br />


	</form>

	<br />
	<br /> Cookie :
	<%
		out.print(cookie);
	%>
	&nbsp;&nbsp; Expires :

	<%=expiryTime%>

	Session found server :
	<%=serverId%>


</body>
</html>