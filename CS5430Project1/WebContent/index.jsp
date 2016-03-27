<%@page import="com.sessionManager.Constants"%>
<%@page import="com.sessionManager.SessionManagerServlet"%>
<%@page import="com.sessionModel.SessionModel"%>
<%@page import="com.sessionManager.SessionManager"%>
<%@ page import="java.io.*,java.util.*" %>


 
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
   int expiryTimeinSec = 180; // always in seconds
   Date expiryTime;
 
   
   // Initializing variables which are displayed on screen for first request of new session
   
   public void initialize(String id)
   {
	   sessionId=id;
  		versionNumber=1;
  		message = "Hello user";
  		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND,expiryTimeinSec);
		
  		expiryTime = cal.getTime();
  		cookie=id + "_1" + "_s1" ;
   }
   
   
%>
 
 <%-- Below code identifies if the cookie is set in request or not.
 	  If not then create the session, seth the cookie in response with expiry time
 	  If found, then update the seeion parameters	
  --%>
 
<% Cookie[] cookies= request.getCookies();
SessionManagerServlet s= new SessionManagerServlet();
response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
response.setHeader("Pragma", "no-cache");
//response.setDateHeader("Expires", -1);
//response.setDateHeader("Last-Modified", new Date().getTime());


boolean sessionFound = false;

		for(Cookie c: cookies)
		{
			System.out.println("cookie " + c.getName());
			if(c.getName().equals("CS5300Project1SessionId"))
			{
				String cookieValue = c.getValue();
				/* int index = cookieValue.indexOf("_");
				sessionId = cookieValue.substring(0, index); */
				String splitData[] = cookieValue.split("_");
				
				SessionModel sessionObj = s.retrieveSession(splitData[0] + Constants.DELIMITER + splitData[1]);
				if(sessionObj!=null)
				{
				versionNumber = sessionObj.getVersionNumber();
				message = sessionObj.getMessage();
				cookie = sessionId + "_" + versionNumber + "_s1";
				expiryTime = sessionObj.getExpiryTime();
				sessionFound = true;
				}
				
				
			}
			
		}
	
	
	
	if(!sessionFound)
	{
		String sessionID = s.getUniqueId();
		s.createSession(sessionID, request);
		String temp = sessionID + "_1_s1" ;
		
		Cookie sessionIdCookie = new Cookie("CS5300Project1SessionId",temp);
		sessionIdCookie.setMaxAge(expiryTimeinSec);
		response.addCookie(sessionIdCookie);
		initialize(sessionID);
	}
	
	
%> 




Net id:mgs275  &nbsp;&nbsp;  Session : <% out.print(sessionId); %>


&nbsp;&nbsp; Version:<% out.print(versionNumber); %> &nbsp;&nbsp;

<%=new java.util.Date() %>

<br/> <br/>


<form action="${pageContext.request.contextPath}/SessionManagerServlet" method="post">

<p> <% out.print(message); %> </p>

<input type="submit" name="replaceButton" value="Replace"></input>  &nbsp;&nbsp; <input type="text" name="userMessage" value="" required="required" maxLength="512"></input>  <br/>
<input type="submit" name="refreshButton" value="Refresh" formnovalidate></input> <br/>
<input type="submit" name="logoutButton" value="Logout" formnovalidate></input> <br/>


</form>

<br/> <br/>
Cookie : <% out.print(cookie); %>  &nbsp;&nbsp; Expires :   

<%= expiryTime %>

</body>
</html>