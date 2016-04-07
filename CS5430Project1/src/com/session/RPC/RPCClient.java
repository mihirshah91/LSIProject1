package com.session.RPC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sessionManager.Constants;
import com.sessionModel.SessionModel;

public class RPCClient {

	List<String> dest = new ArrayList<String>(Arrays.asList("10.132.2.77","10.148.3.215")); // list
																			// of
																			// ips
																			// of
																			// tomcat
																			// servers
	int portProj1bRPC = 1800;
	int maxPacketSize = 512;
	static int callNumber = 0;
	static SessionModel sessionObj = null;
	public static String locationMetdata = "";

	public SessionModel sendRequest(String id, int opcode, String message) {

		try {
			sessionObj = null;
			locationMetdata = "";
			RPCClientThread.WQAcks = 0;
			System.out.println("SESSION ID : " + id);
			for (int i = 0; i < dest.size(); i++) {

				RPCClientThread thread = null;
				if (opcode == Constants.SESSIONREAD)
					thread = new RPCClientThread(this, id, opcode, dest.get(i));
				else if (opcode == Constants.SESSIONWRITE)
					thread = new RPCClientThread(this, id, opcode, dest.get(i), message);
				else if (opcode == Constants.SESSIONLOGOUT) {
					thread = new RPCClientThread(this, id, opcode, dest.get(i));

				}

				//thread.initialize();
				// RPCClientThread thread = new RPCClientThread();
				thread.start();
				thread.join();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sessionObj;
	}

}
