package com.xahlicem.game.helpers.net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.xahlicem.game.Game;
import com.xahlicem.game.helpers.net.packet.Packet;
import com.xahlicem.game.helpers.net.packet.PacketLevelChange;
import com.xahlicem.game.helpers.net.packet.PacketLogin;

public class Client extends NetWorker {

	private InetAddress ip;
	
	public Client(Game game, String ip) {
		super(game);
		try {
			socket = new DatagramSocket();
			this.ip = InetAddress.getByName(ip);
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void parsePacket(byte[] data, InetSocketAddress address) {
		Packet packet;
		switch(Packet.getPacketType(data[0])) {
		default:
		case INVALID:
			System.out.println("Invalid..." + data[0]);
			break;
		case LOGIN:
			packet = new PacketLogin(data);
			System.out.println(packet.readData(data) + " has started playing");
			break;
		case DISCONNECT:
			System.out.println("Disconnect");
			break;
		case LEVEL_REQ:
			game.getLevel().getPacket().writeData(this);
			break;
		case LEVEL_CHANGE:
			game.getLevel().addPacket(new PacketLevelChange(data));
			break;
		}
	}
	
	public void sendData(byte[] data) {
		sendData(data, ip, 1331);
	}
	
	public void sendData(String data) {
		sendData(data.getBytes());
	}
}
