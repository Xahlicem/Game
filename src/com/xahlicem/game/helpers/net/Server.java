package com.xahlicem.game.helpers.net;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.xahlicem.game.Game;
import com.xahlicem.game.helpers.net.packet.Packet;
import com.xahlicem.game.helpers.net.packet.PacketLevelChange;
import com.xahlicem.game.helpers.net.packet.PacketLogin;
import com.xahlicem.game.helpers.net.packet.PacketDisconnect;

public class Server extends NetWorker {
	private List<InetSocketAddress> clients = new ArrayList<InetSocketAddress>();
	private List<String> names = new ArrayList<String>();

	public Server(Game game) {
		super(game);
		try {
			socket = new DatagramSocket(1331);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void parsePacket(byte[] data, InetSocketAddress address) {
		Packet packet = null;
		switch (Packet.getPacketType(data[0])) {
			default:
			case INVALID:
				System.out.println("Invalid..." + data[0]);
				break;
			case LOGIN:
				packet = new PacketLogin(data);
				if (!clients.contains(address)) {
					for (String name : names)
						sendData(new PacketLogin(name).getData(), address);
					clients.add(address);
					names.add(packet.readData(data));
					game.getLevel().sendChangeTo(this, address);
				}
				System.out.println(packet.readData(data) + " has logged in from " + address);
				break;
			case DISCONNECT:
				packet = new PacketDisconnect(data);
				clients.remove(address);
				names.remove(((PacketDisconnect) packet).getUsername());
				System.out.println(packet.readData(data) + " has disconnected in from " + address);
				break;
			case LEVEL_REQ:
				game.getLevel().sendChangeTo(this, address);
				break;
			case LEVEL_CHANGE:
				packet = new PacketLevelChange(data);
				break;
		}
		if (packet != null) packet.writeData(this);
	}

	public void sendToAll(byte[] data) {
		for (InetSocketAddress client : clients)
			sendData(data, client);
	}

	public void close() {
		for (String name : names)
			new PacketDisconnect(name).writeData(this);
		super.close();
	}
	
	public String getIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "";
	}
}
