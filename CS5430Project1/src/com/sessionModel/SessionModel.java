package com.sessionModel;

import java.util.Date;

public class SessionModel {

	public String sessionId;
	public String message;
	public Date expiryTime;
	
	public Date getExpiryTime() {
		return expiryTime;
	}




	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}




	public String getMessage() {
		return message;
	}




	public void setMessage(String message) {
		this.message = message;
	}
	public int versionNumber;
	
	
	public SessionModel(String id, int version,String message )

	{
		this.sessionId = id;
		this.versionNumber = version;
		this.message = message;
		
	}
	
	
	

	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public int getVersionNumber() {
		return versionNumber;
	}
	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}
	
	
	
	
}
