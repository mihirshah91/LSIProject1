package com.session.RPC;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sessionManager.Constants;
import com.sessionModel.SessionModel;

public class RPCClientThread extends Thread {

	RPCClient client;
	String id;
	int opcode;
	int localNumber;
	String host;
	static int WQAcks;
	String message = "";

	SessionModel session;

	public void initialize() {
		WQAcks = 0;
	}

	public RPCClientThread(RPCClient rpc, String id, int opcode, String hostname) {

		setCommon(rpc, id, opcode, hostname);

	}

	public RPCClientThread(RPCClient rpc, String id, int opcode, String hostname, String message) {
		setCommon(rpc, id, opcode, hostname);
		this.message = message;

	}

	public void setCommon(RPCClient rpc, String id, int opcode, String hostname) {
		this.client = rpc;
		rpc.callNumber++;
		localNumber = rpc.callNumber;
		this.id = id;
		this.opcode = opcode;
		host = hostname;
	}

	public RPCClientThread() {
		// TODO Auto-generated constructor stub
	}

	public void run() {
		DatagramPacket receivePacket = null;
		DatagramSocket clientSocket = null;
		try {
			byte[] outBuf = new byte[client.maxPacketSize];

			System.out.println("inside client thread");
			System.out.println();
			String sendData = localNumber + Constants.DELIMITER + opcode + Constants.DELIMITER + id
					+ Constants.DELIMITER + message;
			outBuf = sendData.getBytes();
			clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(host);
			DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, IPAddress, client.portProj1bRPC);
			clientSocket.send(sendPkt);

			byte[] inBuf = new byte[client.maxPacketSize];
			receivePacket = new DatagramPacket(inBuf, inBuf.length);

			int callidReturned = -1;
			do {
				System.out.println("inside do while client thread");
				receivePacket.setLength(inBuf.length);
				clientSocket.receive(receivePacket);
				String data = new String(receivePacket.getData());
				int index = data.indexOf("_");

				callidReturned = Integer.parseInt(data.substring(0, index));

				System.out.println("localNumber=" + localNumber);
				System.out.println("caliid=" + callidReturned);

			} while (callidReturned != localNumber);

//			if (opcode == Constants.SESSIONLOGOUT) {
//
//			} else
			{
				WQAcks++;

				// synchronized (RPCClient.sessionObj ) {

				if (WQAcks <= Constants.WQ)
					RPCClient.locationMetdata = RPCClient.locationMetdata + Constants.DELIMITER + host;

				// synchronized (RPCClient.sessionObj) {
				if (RPCClient.sessionObj == null && opcode != Constants.SESSIONLOGOUT) {

					String data = new String(receivePacket.getData());
					String splitData[] = data.split(Constants.DELIMITER);
					RPCClient.sessionObj = new SessionModel(splitData[1], Integer.parseInt(splitData[2]), splitData[4]);

				}
			}

			// }

			clientSocket.close();

		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
