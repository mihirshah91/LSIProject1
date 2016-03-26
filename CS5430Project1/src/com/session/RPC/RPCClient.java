package com.session.RPC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sessionModel.SessionModel;

public class RPCClient {

		
		
	List<String> dest = new ArrayList<String>(Arrays.asList("localhost")); // list of ips of tomcat servers
	int portProj1bRPC = 1234;
	int maxPacketSize = 512;
	static int callNumber = 0;
	static SessionModel sessionObj=null;

	
public SessionModel sendRequest(String id, int opcode) {

		try {
			sessionObj = null;
			for (int i=0; i < dest.size(); i++) {
				RPCClientThread thread = new RPCClientThread(this, id, opcode,dest.get(i));
				//RPCClientThread thread = new RPCClientThread();
				thread.start();
				thread.join();
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	return sessionObj;	
	}
}


