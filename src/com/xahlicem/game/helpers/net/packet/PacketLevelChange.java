package com.xahlicem.game.helpers.net.packet;

import java.net.InetSocketAddress;

import com.xahlicem.game.helpers.net.Client;
import com.xahlicem.game.helpers.net.Server;

public class PacketLevelChange extends Packet {
	private byte[] data;

	public PacketLevelChange(byte[] data) {
		super(PacketType.LEVEL_CHANGE);
		this.data = data;
		data[0] = packetId;
	}

	@Override
	public byte[] getData() {
		return data;
	}

	@Override
	public void writeData(Client client) {
		client.sendData(data);
	}

	@Override
	public void writeData(Server server) {
		server.sendToAll(getData());
	}
	
	public void writeSingleData(Server server, InetSocketAddress address) {
		server.sendData(getData(), address);
	}

}
