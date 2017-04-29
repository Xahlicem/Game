package com.xahlicem.game.level;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.graphics.SpriteSheet;
import com.xahlicem.game.helpers.audio.BGM;
import com.xahlicem.game.helpers.audio.BGMPlayer;
import com.xahlicem.game.helpers.audio.SFXPlayer;
import com.xahlicem.game.helpers.net.packet.PacketLevelChange;
import com.xahlicem.game.level.tile.Tile;

public class Level {

	private static final int DAY_LIGHT = 0xFFFFFFFF;
	private static final int MORNING_LIGHT = 0xFFC0C0C0;
	private static final int EVENING_LIGHT = 0xFF80C0C0;
	private static final int DARK_LIGHT = 0xFF1F1A1F;
	private static final int NIGHT_LIGHT = 0xFF0A0F09;
	private static final int MIDNIGHT_LIGHT = 0xFF000000;
	public static final int[] LIGHTS = new int[] { MIDNIGHT_LIGHT, NIGHT_LIGHT, DARK_LIGHT, EVENING_LIGHT, MORNING_LIGHT, DAY_LIGHT };

	private static final Random R = new Random();

	public int width, height, wMask, hMask;
	private int[] tiles;
	private List<Tile> tileList = new ArrayList<Tile>();
	private boolean lighted = true;
	private byte[] edges, lights;
	private int time, light;
	private BGM[] bgm = new BGM[] {};
	private int bgmIndex = 0;
	private BGMPlayer midi;
	private SFXPlayer sfx;

	public static final Level TITLE = new Level("/level/TITLE", BGM.BGM_TITLE);

	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		wMask = width - 1;
		hMask = height - 1;
		tiles = new int[width * height];
		generateLevel();
	}

	public Level(int width, int height, BGM... bgm) {
		this.width = width;
		this.height = height;
		wMask = width - 1;
		hMask = height - 1;
		this.bgm = bgm;
		tiles = new int[width * height];
		generateLevel();
	}

	public Level(String path) {
		loadLevel(path);
	}

	public Level(String path, BGM... bgm) {
		loadLevel(path);
		this.bgm = bgm;
	}

	private void loadLevel(String path) {
		try {
			BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path + ".PNG"));
			width = image.getWidth();
			height = image.getHeight();
			wMask = width - 1;
			hMask = height - 1;
			tiles = new int[width * height];
			image.getRGB(0, 0, width, height, tiles, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path + "_L.PNG"));
			width = image.getWidth();
			height = image.getHeight();
			wMask = width - 1;
			hMask = height - 1;
			lights = new byte[width * height];
			int[] light = new int[width * height];
			image.getRGB(0, 0, width, height, light, 0, width);
			convertLights(light);
		} catch (IOException e) {
			e.printStackTrace();
			lights = new byte[width * height];
			for (int i = 0; i < lights.length; i++)
				lights[i] = 0;
		}

		edges = new byte[width * height];

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				int i = x + y * width;
				calculateEdges(x, y);
				tiles[i] = Tile.getRandomColor(tiles[i]);
				if (!tileList.contains(Tile.getTile(tiles[i]))) tileList.add(Tile.getTile(tiles[i]));
			}
	}

	private void convertLights(int[] light) {
		for (int i = 0; i < light.length; i++) {
			switch (light[i]) {
			case DAY_LIGHT:
				lights[i] = 5;
				break;
			case MORNING_LIGHT:
				lights[i] = 4;
				break;
			case EVENING_LIGHT:
				lights[i] = 3;
				break;
			case DARK_LIGHT:
				lights[i] = 2;
				break;
			case NIGHT_LIGHT:
				lights[i] = 1;
				break;
			default:
				lights[i] = 0;
				break;
			}
		}
	}

	private void calculateEdges(int x, int y) {
		int i = x + y * width;
		edges[i] = 0;
		int color = tiles[i] & 0xFFFFFF;
		if ((tiles[((x - 1) & wMask) + (y & hMask) * width] & 0xFFFFFF) != color) edges[i] |= 0b1;
		if ((tiles[((x + 1) & wMask) + (y & hMask) * width] & 0xFFFFFF) != color) edges[i] |= 0b10;
		if ((tiles[(x & wMask) + ((y - 1) & hMask) * width] & 0xFFFFFF) != color) edges[i] |= 0b100;
		if ((tiles[(x & wMask) + ((y + 1) & hMask) * width] & 0xFFFFFF) != color) edges[i] |= 0b1000;
	}

	private void generateLevel() {
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				tiles[x + y * width] = R.nextInt(10);
			}
	}

	public void init(BGMPlayer midi, SFXPlayer sfx) {
		this.midi = midi;
		this.sfx = sfx;
	}

	public void tick() {
		if (!midi.isPlaying()) {
			midi.setSound(bgm[bgmIndex++ % bgm.length]);
			midi.play();
		}
		time();
		// light = DARK_LIGHT;
		for (Tile tile : tileList)
			tile.tick();
	}

	private void time() {
		time++;
		if (time > 2400) time = 0;

		if (time >= 2250) light = 1;
		else if (time >= 2100) light = 2;
		else if (time >= 1900) light = 3;
		else if (time >= 800) light = 5;
		else if (time >= 550) light = 4;
		else if (time >= 400) light = 2;
		else if (time >= 300) light = 1;
		else light = 0;
	}

	public void draw(int xScroll, int yScroll, Screen screen) {
		screen.setOffset(xScroll, yScroll);
		for (int y = yScroll >> 4; y <= (yScroll + screen.height + 32) >> 4; y++)
			for (int x = xScroll >> 4; x <= (xScroll + screen.width) >> 4; x++) {
				int i = (x & wMask) + (y & hMask) * width;
				int x2 = x << 4;
				int y2 = y << 4;
				int l = getLight(i);
				getTile(i).draw(x2, y2, screen, l);
				drawEdges(x, y, x2, y2, l, screen);
			}
	}

	private int getLight(int i) {
		if (!lighted) return LIGHTS[lights[i]];
		int l = (lights[i] + light);
		if (l >= LIGHTS.length) l = 5;
		return LIGHTS[l];
	}

	private void drawEdges(int x, int y, int x2, int y2, int l, Screen screen) {
		int loc = (x & wMask) + (y & hMask) * width;
		int[] increaseX = new int[] { -1, 1, 0, 0 };
		int[] increaseY = new int[] { 0, 0, -1, 1 };
		int[] edge = new int[] { 0, 2, 1, 3 };
		int h = getTile(loc).getHeight();

		for (int i = 0; i < 4; i++) {
			if ((edges[loc] >> i & 0x1) == 0x1) {
				Sprite[] draw = Sprite.INVISIBLE_EDGE;
				Tile t = getTile((x + increaseX[i] & wMask) + (y + increaseY[i] & hMask) * width);
				if (t.getBaseColor() == Tile.DIRT_COLOR && t.getHeight() > h) draw = Sprite.DIRT_EDGE;
				if (t.getBaseColor() == Tile.PATH_COLOR && t.getHeight() > h) draw = Sprite.PATH_EDGE;
				if (t.getBaseColor() == Tile.GRASS_COLOR && t.getHeight() > h) draw = Sprite.GRASS_EDGE;
				screen.drawSprite(x2, y2, draw[edge[i]], l);
			}
		}
	}

	public Tile getTile(int x, int y) {
		return getTile(x + y * width);
	}

	public Tile getTile(int i) {
		return Tile.getTile(tiles[i]);
	}

	public void changeTile(int x, int y, int tile, boolean random) {
		if (random) tiles[x + y * width] = Tile.getRandomColor(tile | 0xFF000000);
		else tiles[x + y * width] = tile;
		sfx.sound(127, 1);
		calculateEdges(x, y);
		calculateEdges((x - 1) & wMask, y);
		calculateEdges((x + 1) & wMask, y);
		calculateEdges(x, (y - 1) & hMask);
		calculateEdges(x, (y + 1) & hMask);
	}

	public void changeTile(int x, int y, int tile) {
		changeTile(x, y, tile, false);
	}

	public void changeLight(int i, int light) {
		this.lights[i] = (byte) light;
	}

	public void changeLight(int x, int y, int light) {
		changeLight(x + y * width, light);
	}

	public PacketLevelChange getPacket() {

		ByteBuffer bytes = ByteBuffer.allocate(4096);
		bytes.put((byte) 0);
		// b.putInt(time);
		bytes.put((byte) (time >> 24));
		bytes.put((byte) (time >> 16));
		bytes.put((byte) (time >> 8));
		bytes.put((byte) time);

		for (int i = 0; i < tiles.length; i++) {
			bytes.put((byte) Tile.list.indexOf(getTile(i)));

			byte b = edges[i];
			b |= lights[i] << 4;
			bytes.put(b);
		}
		return new PacketLevelChange(bytes.array());
	}

	public void addPacket(byte[] packet) {
		int index = 1;
		int b = 0;
		b |= ((packet[index++] & 0xff) << 24);
		b |= ((packet[index++] & 0xff) << 16);
		b |= ((packet[index++] & 0xff) << 8);
		b |= packet[index++] & 0xff;

		time = b;

		// time = ByteBuffer.wrap(packet).getInt(1);

		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = Tile.list.get(packet[index++]).getColor();

			b = packet[index++];
			edges[i] = (byte) (b & 0xF);

			lights[i] = (byte) (b >> 4);
		}
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

	public void save(String name) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		try {
			System.arraycopy(tiles, 0, pixels, 0, pixels.length);
			ImageIO.write(image, "PNG", new File(name.toUpperCase() + ".PNG"));
			System.arraycopy(lights, 0, pixels, 0, pixels.length);
			ImageIO.write(image, "PNG", new File(name.toUpperCase() + "_L.PNG"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
