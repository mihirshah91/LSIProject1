package com.sessionManager;

public interface Constants {
	 int EXPIRYTIME = 180; // in seconds
	 String DELIMITER = "_"; 
	 String DELIMITERVERSION = "$";
	 int SESSIONREAD = 1;
	 int SESSIONWRITE = 2;
	 String DEFAULTMESSAGE = "HELLO USER";
	 String DEFAULTVERSIONNUMBER = "1";
	
	 int F = 1;
	 int R =  F + 1;
	 int WQ = F + 1;
	 int W = 2*F + 1;
	 int N = W;
	 
}
