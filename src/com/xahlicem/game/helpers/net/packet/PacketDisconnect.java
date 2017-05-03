package com.xahlicem.game.helpers.net.packet;

import com.xahlicem.game.helpers.net.Client;
import com.xahlicem.game.helpers.net.Server;

public class PacketDisconnect extends Packet {

	String username;

	public PacketDisconnect(byte[] data) {
		super(PacketType.DISCONNECT);
		username = readData(data);
	}

	public PacketDisconnect(String username) {
		super(PacketType.DISCONNECT);
		this.username = username;
	}

	@Override
	public byte[] getData() {
		return ((char) packetId + username).getBytes();
	}

	@Override
	public void writeData(Client client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(Server server) {
		server.sendToAll(getData());
	}

	public String getUsername() {
		return username;
	}
}
