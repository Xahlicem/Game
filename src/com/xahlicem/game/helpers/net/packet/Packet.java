package com.xahlicem.game.helpers.net.packet;

import com.xahlicem.game.helpers.net.Client;
import com.xahlicem.game.helpers.net.Server;

public abstract class Packet {

	public static enum PacketType {
		INVALID(-1), LOGIN(00), DISCONNECT(01), LEVEL_REQ(02), LEVEL_CHANGE(03);

		private byte id;

		private PacketType(int id) {
			this.id = (byte) id;
		}

		public byte getId() {
			return id;
		}
	}

	public byte packetId;

	public Packet(int id) {
		packetId = (byte) id;
	}
	
	public Packet(PacketType type) {
		packetId = type.id;
	}
	
	public abstract byte[] getData();

	public abstract void writeData(Client client);
	public abstract void writeData(Server server);
	
	public String readData(byte[] data) {
		String message = new String(data).trim();
		return message.substring(0);
	}
	
	public static PacketType getPacketType(int id) {
		for (PacketType type : PacketType.values()) if ((byte)id == type.id) return type;
		return PacketType.INVALID;
	}
}
