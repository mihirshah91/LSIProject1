package com.sessionModel;

import java.util.Date;

/**
 * 
 * @author mihir POJO class defining session objects
 */

public class SessionModel {

	public String sessionId;
	public String message;
	public Date expiryTime;
	public String intialserverId;
	public boolean sessionNotFound ;

	

	public boolean isSessionNotFound() {
		return sessionNotFound;
	}

	public void setSessionNotFound(boolean sessionNotFound) {
		this.sessionNotFound = sessionNotFound;
	}

	public SessionModel() {
		// TODO Auto-generated constructor stub
	}

	public String getIntialserverId() {
		return intialserverId;
	}

	public void setIntialserverId(String intialserverId) {
		this.intialserverId = intialserverId;
	}

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

	public SessionModel(String id, int version, String message, Date ExpiryTime)

	{
		this.sessionId = id;
		this.versionNumber = version;
		this.message = message;
		this.expiryTime = ExpiryTime;

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
