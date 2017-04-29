package com.xahlicem.game.helpers.net.packet;

import com.xahlicem.game.helpers.net.Client;
import com.xahlicem.game.helpers.net.Server;

public class PacketLogin extends Packet {

	String username;

	public PacketLogin(byte[] data) {
		super(PacketType.LOGIN);
		username = readData(data);
	}

	public PacketLogin(String username) {
		super(00);
		this.username = username;
	}

	@Override
	public byte[] getData() {
		return ((char)packetId + username).getBytes();
	}

	@Override
	public void writeData(Client client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(Server server) {
		server.sendToAll(getData());
	}
}
