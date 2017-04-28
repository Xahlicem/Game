package com.xahlicem.game.helpers.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.xahlicem.game.Game;

public class Client extends Thread {

	private InetAddress ip;
	private DatagramSocket socket;
	private Game game;
	
	public Client(Game game, String ip) {
		this.game = game;
		try {
			socket = new DatagramSocket();
			this.ip = InetAddress.getByName(ip);
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
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
			//System.out.println("SERVER > " + new String(data));
			if (data[0] == 'L') game.getLevel().addPacket(data);
		}
	}
	
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ip, 1331);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendData(String data) {
		sendData(data.getBytes());
	}
	
	public void requestLevel() {
		sendData("*RLVL");
	}
	
	public void sendLevel() {
		sendData(game.getLevel().getPacket());
	}
}
