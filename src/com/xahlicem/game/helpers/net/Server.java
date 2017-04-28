package com.xahlicem.game.helpers.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.xahlicem.game.Game;

public class Server extends Thread {
	private DatagramSocket socket;
	private List<SocketAddress> clients = new ArrayList<SocketAddress>();
	private Game game;

	public Server(Game game) {
		this.game = game;
		try {
			socket = new DatagramSocket(1331);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		byte[] data = new byte[4096];
		while (true) {
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (data[0] == '*') {
				String message = new String(packet.getData()).trim().toUpperCase();
				System.out.println("CLIENT > " + message);
				if (message.equals("*PING")) {
					if (!clients.contains(packet.getSocketAddress())) clients.add(packet.getSocketAddress());
					sendData("*PONG".getBytes(), packet.getSocketAddress());
				}
				if (message.equals("*RLVL")) {
					sendData(game.getLevel().getPacket(), packet.getSocketAddress());
				}
			}
			if (data[0] == 'L') sendToAll(data);
		}
	}

	public void sendData(byte[] data, SocketAddress address) {
		DatagramPacket packet = new DatagramPacket(data, data.length, address);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
			clients.remove(address);
		}
	}

	public void sendData(byte[] data, InetAddress ip, int port) {
		sendData(data, new InetSocketAddress(ip, port));
	}

	public void sendToAll(byte[] data) {
		for (SocketAddress client : clients)
			sendData(data, client);
	}
}
