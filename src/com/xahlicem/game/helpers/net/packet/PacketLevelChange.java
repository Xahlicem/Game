package com.xahlicem.game.helpers.net.packet;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import com.xahlicem.game.helpers.net.Client;
import com.xahlicem.game.helpers.net.Server;

public class PacketLevelChange extends Packet {
	private int width, height;
	private int[] tiles;

	public PacketLevelChange(byte[] data) {
		super(PacketType.LEVEL_CHANGE);
		this.data = data;
		data[0] = packetId;

		populateData();
	}

	private void populateData() {
		ByteBuffer bytes = ByteBuffer.wrap(data);
		bytes.get();
		width = bytes.getInt();
		height = bytes.getInt();

		tiles = new int[width * height];

		for (int i = 0; i < tiles.length; i++)
			tiles[i] = bytes.getInt();
	}

	public PacketLevelChange(int width, int height, int[] tiles) {
		super(PacketType.LEVEL_CHANGE);
		this.width = width;
		this.height = height;
		this.tiles = tiles;
		
		ByteBuffer bytes = ByteBuffer.allocate(10240);
		bytes.put(packetId);
		bytes.putInt(width);
		bytes.putInt(height);

		for (int i = 0; i < tiles.length; i++)
			bytes.putInt(tiles[i]);

		data = bytes.array();
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getTiles() {
		return tiles;
	}
}
