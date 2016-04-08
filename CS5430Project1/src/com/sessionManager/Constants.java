package com.sessionManager;

public interface Constants {
	 int EXPIRYTIME = 180; // in seconds
	 String DELIMITER = "_"; 
	 String DELIMITERVERSION = "$";
	 int SESSIONREAD = 1;
	 int SESSIONWRITE = 2;
	 String DEFAULTMESSAGE = "HELLO USER";
	 String DEFAULTVERSIONNUMBER = "1";
	 int SESSIONLOGOUT = 3; 
	 int F = 1;
	 int R =  F + 1;
	 int WQ = F + 1;
	 int W = 2*F + 1;
	 int N = W;
	 int delta = 2 ; // extra seconds in discard time (delta)
	 String filePath = "/Users/Shiva/git/LSIProject1/CS5430Project1/IPTable" ;
	 String LOCALDATA_PATH = "/Users/Shiva/Documents/tmp/localdata";
	 int RPC_PORT = 7111;
	 
	 
}
