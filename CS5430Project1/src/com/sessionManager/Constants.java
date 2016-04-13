package com.sessionManager;

public interface Constants {
	 int EXPIRYTIME = 60; // in seconds
	 String DELIMITER = "_"; 
	 String DELIMITERVERSION = "$";
	 int SESSIONREAD = 1;
	 int SESSIONWRITE = 2;
	 String DEFAULTMESSAGE = "HELLO USER";
	 String DEFAULTVERSIONNUMBER = "1";
	 int DEFAULTVERSIONINT = 1;
	 String DUMMYMESSAGE= "dummy";
	 String NOTFOUND = "sessionNotFound";
	 String dateFormat = "dd-M-yyyy hh:mm:ss";
	 int SESSIONLOGOUT = 3; 
	 int F = 1;
	 int R =  F + 1;
	 int WQ = F + 1;
	 int W = 2*F + 1;
	 int N = W;
	 int delta = 2 ; // extra seconds in discard time (delta)
	 //String filePath = "/Users/Shiva/git/LSIProject1/CS5430Project1/IPTable" ;
	 //String filePath = "IPTable" ;
	 //String LOCALDATA_PATH = "localdata";
	String filePath = "/Users/mihir/Desktop/IPTable.json" ;
	String REBOOT_DATA_PATH = "/Users/mihir/Desktop/reboot.txt";
	String SERVER_ID_PATH = "/Users/mihir/Desktop/ami-launch-index";
	
	//String filePath = "/home/ec2-user/IPTable.json" ;
	//String REBOOT_DATA_PATH = "/home/ec2-user/reboot.txt";
	//String SERVER_ID_PATH = "/home/ec2-user/ami-launch-index";
	 int RPC_PORT = 7111;
	 //String DOMAIN_NAME = ".bigdata.systems";
	 String DOMAIN_NAME = "localhost"
			 ;
	 String LOGOUTYPE = "logout";
	 
}
