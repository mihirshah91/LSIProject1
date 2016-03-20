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

<%!				              
   String sessionId;
   int versionNumber;
   String message;
   String cookie;
   int expiryTimeinSec = 15; // always in seconds
   Date expiryTime;
 
   public void initialize(String id)
   {
	   sessionId=id;
  		versionNumber=1;
  		message = "Hello user";
  		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND,expiryTimeinSec);
		
  		expiryTime = cal.getTime();
  		cookie=id + "_1" ;
   }
   
   
%>
 
<% Cookie[] cookies= request.getCookies();
SessionManagerServlet s= new SessionManagerServlet();
response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
response.setHeader("Pragma", "no-cache");
//response.setDateHeader("Expires", -1);
//response.setDateHeader("Last-Modified", new Date().getTime());


boolean sessionFound = false;

		for(Cookie c: cookies)
		{
			
			if(c.getName().equals("CS5430Project1SessionId"))
			{
				
				sessionId = c.getValue();
				SessionModel sessionObj = s.retrieveSession(c);
				if(sessionObj!=null)
				{
				versionNumber = sessionObj.getVersionNumber();
				message = sessionObj.getMessage();
				cookie = sessionId + "_" + versionNumber;
				expiryTime = sessionObj.getExpiryTime();
				sessionFound = true;
				}
				
				
			}
			
		}
	
	
	
	if(!sessionFound)
	{
		String sessionID = s.getUniqueId();
		s.createSession(sessionID, request);
		
		Cookie sessionIdCookie = new Cookie("CS5430Project1SessionId",sessionID);
		sessionIdCookie.setMaxAge(15);
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

<input type="text" name="userMessage" value=""></input> <br/> <br/>
<input type="submit" name="replaceButton" value="Replace"></input>
<input type="submit" name="refreshButton" value="Refresh"></input>
<input type="submit" name="logoutButton" value="Logout"></input>


</form>

<br/> <br/>
Cookie : <% out.print(cookie); %>  &nbsp;&nbsp; Expires :   

<%= expiryTime %>

</body>
</html>