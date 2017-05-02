package com.xahlicem.game.helpers.net.packet;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import com.xahlicem.game.helpers.net.Client;
import com.xahlicem.game.helpers.net.Server;

public class PacketLevelTime extends Packet{

	private int time;
	
	public PacketLevelTime(byte[] data) {
		super(PacketType.LEVEL_TIME);
		this.data = data;
		data[0] = packetId;

		populateData();
	}
	
	public PacketLevelTime(int time) {
		super(PacketType.LEVEL_TIME);
		this.time = time;
		
		ByteBuffer bytes = ByteBuffer.allocate(5);
		bytes.put(packetId);
		bytes.putInt(time);
		
		data = bytes.array();
	}
	
	private void populateData() {
		ByteBuffer bytes = ByteBuffer.wrap(data);
		bytes.get();
		time = bytes.getInt();
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

	public int getTime() {
		return time;
	}

}
