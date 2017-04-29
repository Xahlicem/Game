package com.xahlicem.game.helpers.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.xahlicem.game.Game;

public abstract class NetWorker extends Thread{
	protected DatagramSocket socket;
	protected Game game;
	
	public NetWorker(Game game) {
		this.game = game;
	}
	
	public void run() {
		while (true) {
			byte[] data = new byte[4096];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			parsePacket(packet);
		}
	}

	protected abstract void parsePacket(byte[] data, InetSocketAddress address);
	
	protected void parsePacket(DatagramPacket packet) {
		parsePacket(packet.getData(), new InetSocketAddress(packet.getAddress(), packet.getPort()));
	}
	
	public void sendData(byte[] data, InetSocketAddress address) {
		sendData(data, address.getAddress(), address.getPort());
	}

	public void sendData(byte[] data, InetAddress ip, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
