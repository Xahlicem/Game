package com.xahlicem.game.level;

import java.io.File;

import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.helpers.audio.BGM;
import com.xahlicem.game.helpers.net.packet.Packet;
import com.xahlicem.game.helpers.net.packet.PacketLevelChange;

public class TimeLevel extends Level {

	public TimeLevel(int width, int height, BGM... bgm) {
		super(width, height, bgm);
		time = 550;
	}

	public TimeLevel(String path, BGM... bgm) {
		super(path, bgm);
		time = 550;
	}

	public TimeLevel(File file) {
		super(file);
		time = 550;
	}

	protected static final int DAY = -8;
	protected static final int MORNING = -6;
	protected static final int EVENING = -5;
	protected static final int TWILIGHT = -4;
	protected static final int NIGHT = -1;
	protected static final int PITCH = 0;

	private int time, darkness;
	protected boolean lighted = true;

	private void time() {
		time++;
		if (time > 2400) time = 0;

		if (time >= 2250) darkness = NIGHT;
		else if (time >= 2100) darkness = TWILIGHT;
		else if (time >= 1900) darkness = EVENING;
		else if (time >= 800) darkness = DAY;
		else if (time >= 550) darkness = MORNING;
		else if (time >= 400) darkness = TWILIGHT;
		else if (time >= 300) darkness = NIGHT;
		else darkness = PITCH;
	}

	public void tick(Input input) {
		super.tick(input);
		time();
	}

	public int getLight(int i) {
		int l = ((tiles[i] >> 29) & 0x7);
		if (lighted) l -= darkness;
		return l;
	}

	public void toggleLight() {
		setLight(!lighted);
	}

	public void setLight(boolean light) {
		lighted = light;
	}

	public boolean lighted() {
		return lighted;
	}

	protected PacketLevelChange getPacket() {
		return new PacketLevelChange(time, width, height, tiles);
	}

	public void addPacket(Packet packet) {
		switch (packet.gePacketType()) {
			case LEVEL_CHANGE:
				tiles = ((PacketLevelChange) packet).getTiles();
				width = ((PacketLevelChange) packet).getWidth();
				height = ((PacketLevelChange) packet).getHeight();
				time = ((PacketLevelChange) packet).getTime();
				break;
			default:
				break;
		}
	}
}
