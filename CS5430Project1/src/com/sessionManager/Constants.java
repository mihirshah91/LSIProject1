package com.sessionManager;

import java.text.SimpleDateFormat;

public class Constants {
	 public static int EXPIRYTIME = 900; // in seconds
	 public static String DELIMITER = "_"; 
	 public static String DELIMITERVERSION = "$";
	 public static final int SESSIONREAD = 1;
	 public static final int SESSIONWRITE = 2;
	 public static String DEFAULTMESSAGE = "HELLO USER";
	 public static String DEFAULTVERSIONNUMBER = "1";
	 public static int DEFAULTVERSIONINT = 1;
	 public static String DUMMYMESSAGE= "dummy";
	 public static String SESSION_NOTFOUND = "sessionNotFound";
	 public static String SUCCESSFUL = "success";
	 public static String FAILURE = "failure";
	 public static String dateFormat = "dd-M-yyyy HH:mm:ss";
	 public static String COOKIENAME = "CS5300Project1";
	 public static final int SESSIONLOGOUT = 3; 
	 
	 public static int F = 1;
	 public static int R =  F + 1;
	 public static int WQ = F + 1;
	 public static int W = 2*F + 1;
	 public static int N = W;
	 public static int delta = 2 ; // extra seconds in discard time (delta)

	public static String filePath = "/Users/Shiva/Desktop/IPTable.json" ;
	public static String REBOOT_DATA_PATH = "/Users/Shiva/Desktop/reboot.txt";
	public static String SERVER_ID_PATH = "/Users/Shiva/Desktop/ami-launch-index";
	 public static String F_FILEPATH = "/Users/Shiva/Desktop/fcount.txt";
	public static String N_FILEPATH = "/Users/Shiva/Desktop/servercount.txt";
	 public static String DOMAINPATH="/";
	 public static int SOCKETTIMEOUT = 25000;
	 public static String INITIALID = "N/A";
	
	 public static SimpleDateFormat sdfr = new SimpleDateFormat(Constants.dateFormat);
	
//	 public static String filePath = "/home/ec2-user/IPTable.json" ;
//	 public static String REBOOT_DATA_PATH = "/home/ec2-user/reboot.txt";
//	 public static String SERVER_ID_PATH = "/home/ec2-user/ami-launch-index";
//	 public static String F_FILEPATH = "/var/tmp/fcount.txt";
//	 public static String N_FILEPATH = "/var/tmp/servercount.txt";
	 public static int RPC_PORT = 7111;
	 //public static String DOMAIN_NAME = ".bigdata.systems";
	public static String DOMAIN_NAME = "localhost";
	 public static String LOGOUTYPE = "logout";
	 
}
