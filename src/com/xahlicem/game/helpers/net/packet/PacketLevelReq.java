package com.xahlicem.game.helpers.net.packet;

import com.xahlicem.game.helpers.net.Client;
import com.xahlicem.game.helpers.net.Server;

public class PacketLevelReq extends Packet {

	public PacketLevelReq() {
		super(PacketType.LEVEL_REQ);
	}

	@Override
	public byte[] getData() {
		return new byte[] { packetId };
	}

	@Override
	public void writeData(Client client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(Server server) {
		//TODO
	}

}
