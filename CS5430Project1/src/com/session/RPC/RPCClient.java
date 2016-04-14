package com.session.RPC;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sessionManager.Constants;
import com.sessionManager.StaleSessionCleaner;
import com.sessionModel.SessionModel;

public class RPCClient {

	static List<String> dest = new ArrayList<>();
	static List<String> serverid = new ArrayList<>();
	static List<String> allDests = new ArrayList<>();
	static Map<String, String> serverIdIp = new HashMap<String, String>();

	// int portProj1bRPC = 7111;
	int maxPacketSize = 512;
	static int callNumber = 0;
	public static SessionModel sessionObj = null;
	public static String locationMetdata;

	public static void intializeIPList(String[] ips) {
		System.out.println("intialize ip for read called");

		serverIdIp.clear();
		String ipsnew[] = null;

		if (RPCClient.locationMetdata != null) {

			for (int i = 2; i < ips.length; i++) {
				serverIdIp.put(ips[i], StaleSessionCleaner.serverMap.get(ips[i]));

			}

		}

	}

	public static void initializeIPWqRandlomly() {

		serverIdIp.clear();

		int i = 0;
		while (i < Constants.W) {
			int random = (int) Math.random() * 10 % Constants.N;
			if (!serverIdIp.containsKey(random)) {
				serverIdIp.put(String.valueOf(random), serverid.get(random));
				i++;
			}
		}

	}

	public SessionModel sendRequest(String id, int opcode, SessionModel s) {

		try {
			sessionObj = null;
			locationMetdata = "";
			RPCClientThread.WQAcks = 0;
			System.out.println("inside RPCClient with opcode = " + opcode);

			if (opcode == Constants.SESSIONREAD) {
				callThreads(serverIdIp, Constants.SESSIONREAD, s);

				// if sessionobject is still null, that means no server could
				// found the session
				if (RPCClient.sessionObj != null) {

					// Read the object returned by the servers, do the version
					// increment, replace the message and then call write again

					SessionModel existingSession = RPCClient.sessionObj;
					existingSession.setVersionNumber(existingSession.getVersionNumber() + 1);

					if (s != null && s.message != null && !s.message.equals(""))
						existingSession.setMessage(s.message);

					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.SECOND, Constants.EXPIRYTIME);
					existingSession.setExpiryTime(new Date(cal.getTimeInMillis()));
					locationMetdata = "";
					RPCClientThread.WQAcks = 0;
					callThreads(StaleSessionCleaner.serverMap, Constants.SESSIONWRITE, existingSession);
					return existingSession;
				}

				s.setSessionNotFound(true);
				return s;

			} else if (opcode == Constants.SESSIONWRITE) // first time create
															// session
			{

				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.SECOND, Constants.EXPIRYTIME);
				s.setExpiryTime(new Date(cal.getTimeInMillis()));
				callThreads(StaleSessionCleaner.serverMap, Constants.SESSIONWRITE, s);
				s.setIntialserverId(Constants.INITIALID);

				return s;
			} else {
				callThreads(serverIdIp, Constants.SESSIONLOGOUT, s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sessionObj;
	}

	public void callThreads(Map<String, String> map, int opcode, SessionModel s) {
		Iterator<Map.Entry<String, String>> itr = map.entrySet().iterator();

		while (itr.hasNext()) {

			Map.Entry<String, String> next = itr.next();

			System.out.println("inside RPCClient with server ip =" + next.getValue());

			RPCClientThread thread = null;
			if (opcode == Constants.SESSIONREAD) {
				thread = new RPCClientThread(this, opcode, s, next.getValue());

			} else if (opcode == Constants.SESSIONWRITE) {

				thread = new RPCClientThread(this, opcode, s, next.getValue());

			} else if (opcode == Constants.SESSIONLOGOUT) {

				thread = new RPCClientThread(this, opcode, s, next.getValue());

			}

			thread.setServerid(next.getKey());

			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
