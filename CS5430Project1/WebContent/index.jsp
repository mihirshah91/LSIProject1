

<%@page import="com.session.RPC.RPCClient"%>
<%@page import="com.sessionManager.Constants"%>
<%@page import="com.sessionModel.SessionModel"%>
<%@ page import="java.io.*,java.util.*"%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CS5300</title>
</head>
<body>

	<%-- Declaring general variables used for display on page --%>

	<%!
	String sessionId;
	int versionNumber;
	String message;
	String cookie;
	// always in seconds
	//Date expiryTime;
	String serverId;

	// Initializing variables which are displayed on screen for first request of new session

	/* public void initialize(String id, String cookieTemp) {
		sessionId = id;
		versionNumber = 1;
		message = "Hello user";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, Constants.EXPIRYTIME );

		expiryTime = cal.getTime();
		cookie = cookieTemp;
	} */
	%>

	<%-- Below code identifies if the cookie is set in request or not.
 	  If not then create the session, seth the cookie in response with expiry time
 	  If found, then update the seeion parameters	
  --%>

	<%

	String type = (String) request.getAttribute("type");
	SessionModel s = null;
	String cookie = null;

// 	if (type == null)

// 	{
		
// 	response.sendRedirect("/SessionManagerServlet");
	
// 	}
// 	else{
		
	System.out.println("else");
	s = (SessionModel) request.getAttribute("SessionData");
	cookie = s.getSessionId() + Constants.DELIMITER + s.getVersionNumber() + Constants.DELIMITER + RPCClient.locationMetdata ;
	System.out.println("type in jsp=" + type);
		
	


	%>




	Net id:mgs275 &nbsp;&nbsp; Session :
	<%
		out.print(s.getSessionId());
	%>


	&nbsp;&nbsp; Version:<%
		out.print(s.getVersionNumber());
	%>
	&nbsp;&nbsp;

	<%=new java.util.Date()%>

	<br />
	<br />


	<form action="${pageContext.request.contextPath}/SessionManagerServlet"
		method="post">

		<p>
			<%
				out.print(s.getMessage());
			%>
		</p>

		<input type="submit" name="replaceButton" value="Replace"></input>
		&nbsp;&nbsp; 
		<input type="text" name="userMessage" value="" required="required" maxLength="512"></input> <br />
	<input type="submit" name="refreshButton" value="Refresh" formnovalidate></input>
		<br /> 
		<input type="submit" name="logoutButton" value="Logout" formnovalidate></input> <br />


	</form>

	<br />
	<br /> Cookie :
	<%
		out.print(cookie);
	%>
	&nbsp;&nbsp; Expires :

	<%= s.getExpiryTime().toString() %>

	Session found server :
	<%=s.getIntialserverId()%>


</body>
</html>