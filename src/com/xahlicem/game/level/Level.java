package com.xahlicem.game.level;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;

import com.xahlicem.game.Game;
import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.graphics.SpriteSheet;
import com.xahlicem.game.helpers.audio.BGM;
import com.xahlicem.game.helpers.audio.BGMPlayer;
import com.xahlicem.game.helpers.audio.SFXPlayer;
import com.xahlicem.game.helpers.net.packet.PacketLevelChange;
import com.xahlicem.game.level.tile.Tile;

public class Level {
	/**
	 * Bits that will indicate how dark a something will get
	 */
	private static final int BIT_LIGHT = 	0b11100000000000000000000000000000; // 8
	/**
	 * Player start flag
	 * 
	 * player will start on random tile with flag
	 */
	private static final int BIT_PLAYER = 	0b00010000000000000000000000000000; // 1
	/**
	 * Next level flag
	 */
	private static final int BIT_NEXT =		0b00001000000000000000000000000000; // 1
	/**
	 * Previous level flag
	 */
	private static final int BIT_PREV = 	0b00000100000000000000000000000000; // 1
	/**
	 * Random mob flag
	 */
	private static final int BIT_RANDOM_M = 0b00000010000000000000000000000000; // 1
	/**
	 * Random tile flag
	 */
	private static final int BIT_RANDOM_T = 0b00000001000000000000000000000000; // 1
	/**
	 * Which mob set mob is using
	 */
	private static final int BIT_MOBSET = 	0b00000000111000000000000000000000; // 8
	/**
	 * Which mob out of mob set
	 */
	private static final int BIT_MOB = 		0b00000000000111100000000000000000; // 16
	/**
	 * What item is contained within mob/chest
	 */
	private static final int BIT_ITEM = 	0b00000000000000011111000000000000; // 32
	/**
	 * Which tile type
	 * 
	 * 0 - Special
	 * 1 - Water
	 * 2 - Dirt
	 * 3 - Path
	 * 4 - Grass
	 * 5 - Tree
	 * TODO Finish tile types
	 */
	private static final int BIT_TILETYPE = 0b00000000000000000000111110000000; // 32
	/**
	 * Which variant of tile to show
	 * 
	 * if BIT_RANDOM_T is set ignore
	 */
	private static final int BIT_TILEVAR = 	0b00000000000000000000000001110000; // 8
	/**
	 * Which edge to show
	 */
	private static final int BIT_EDGE = 	0b00000000000000000000000000001111; // 8
	
	private static final Random R = new Random();

	public static final int MAX_BRIGHTNESS = 8;

	public int width, height, wMask, hMask;
	private int[] tiles;
	private boolean lighted = true;
	private int time, darkness;
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

	public Level(File file) {
		try {
			loadLevel(file.toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public Level(String path, BGM... bgm) {
		loadLevel(path);
		this.bgm = bgm;
	}

	private void loadLevel(String path) {
		loadLevel(SpriteSheet.class.getResource(path + ".PNG"));
	}
	
	private void loadLevel(URL url) {
		try {
			BufferedImage image = ImageIO.read(url);
			width = image.getWidth();
			height = image.getHeight();
			wMask = width - 1;
			hMask = height - 1;
			tiles = new int[width * height];
			image.getRGB(0, 0, width, height, tiles, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				if ((tiles[x + y * width] & BIT_RANDOM_T) != 0) changeTile(x, y, tiles[x + y * width], true);
	}

	private void calculateEdges(int x, int y) {
		int i = x + y * width;
		tiles[i] &= ~(BIT_EDGE);
		int color = tiles[i] & 0xF8;
		if ((tiles[((x - 1) & wMask) + (y & hMask) * width] & 0xF8) != color) tiles[i] |= 0b0001;
		if ((tiles[((x + 1) & wMask) + (y & hMask) * width] & 0xF8) != color) tiles[i] |= 0b0010;
		if ((tiles[(x & wMask) + ((y - 1) & hMask) * width] & 0xF8) != color) tiles[i] |= 0b0100;
		if ((tiles[(x & wMask) + ((y + 1) & hMask) * width] & 0xF8) != color) tiles[i] |= 0b1000;
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
		if (bgm.length != 0 && !midi.isPlaying()) {
			midi.setSound(bgm[bgmIndex++ % bgm.length]);
			midi.play();
		}
		time();
		for (int i = 0; i < Tile.getTileIndexLength(); i++)
			Tile.getTileFromIndex(i).tick();
	}

	private void time() {
		time++;
		if (time > 2400) time = 0;

		if (time >= 2250) darkness = -1;
		else if (time >= 2100) darkness = -4;
		else if (time >= 1900) darkness = -6;
		else if (time >= 800) darkness = -8;
		else if (time >= 550) darkness = -5;
		else if (time >= 400) darkness = -4;
		else if (time >= 300) darkness = -1;
		else darkness = 0;
	}

	public void draw(int xScroll, int yScroll, Screen screen) {
		screen.setOffset(xScroll, yScroll);
		for (int y = yScroll >> 4; y <= (yScroll + screen.height + 32) >> 4; y++)
			for (int x = xScroll >> 4; x <= (xScroll + screen.width) >> 4; x++) {
				int i = (x & wMask) + (y & hMask) * width;
				int x2 = x << 4;
				int y2 = y << 4;

				int[] lights = getLights(x, y);

				getTile(i).draw(x2, y2, screen, lights);
				drawEdges(x, y, x2, y2, screen, lights);
				if (Game.edit && (tiles[i] & BIT_RANDOM_T) != 0) screen.drawSprite(x2+7, y2+6, Sprite.FONT['R'], 8);
			}
	}

	private int[] getLights(int x, int y) {
		int[] lights = new int[5];
		lights[0] = getLight((x & wMask) + (y & hMask) * width);
		lights[1] = getLight((x & wMask) + (y - 1 & hMask) * width);
		lights[2] = getLight((x & wMask) + (y + 1 & hMask) * width);
		lights[3] = getLight((x - 1 & wMask) + (y & hMask) * width);
		lights[4] = getLight((x + 1 & wMask) + (y & hMask) * width);
		return lights;
	}

	private void drawEdges(int x, int y, int x2, int y2, Screen screen, int... lights) {
		int loc = (x & wMask) + (y & hMask) * width;
		int[] increaseX = new int[] { -1, 1, 0, 0 };
		int[] increaseY = new int[] { 0, 0, -1, 1 };
		int[] edge = new int[] { 0, 2, 1, 3 };
		int h = getTile(loc).getHeight();

		for (int i = 0; i < 4; i++) {
			if ((tiles[loc] >> i & 0x1) == 0x1) {
				Sprite[] draw = Sprite.INVISIBLE_EDGE;
				Tile t = getTile((x + increaseX[i] & wMask) + (y + increaseY[i] & hMask) * width);
				if (t.getBaseColor() == Tile.DIRT.getBaseColor() && t.getHeight() > h) draw = Sprite.DIRT_EDGE;
				if (t.getBaseColor() == Tile.PATH.getBaseColor() && t.getHeight() > h) draw = Sprite.PATH_EDGE;
				if (t.getBaseColor() == Tile.GRASS.getBaseColor() && t.getHeight() > h) draw = Sprite.GRASS_EDGE;
				if (t.getBaseColor() == Tile.TREE.getBaseColor() && t.getHeight() > h) draw = Sprite.GRASS_EDGE;
				screen.drawSprite(x2, y2, draw[edge[i]], lights);
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
		int i = x + y * width;
		tiles[i] &= ~(BIT_TILETYPE | BIT_TILEVAR);

		if (random) tiles[i] |= Tile.getRandomColor(tile) | BIT_RANDOM_T;
		else {
			tiles[i] &= ~(BIT_RANDOM_T);
			tiles[i] |= tile;
		}

		if (sfx != null) sfx.sound(127, 1);

		calculateEdges(x, y);
	}

	public void changeTile(int x, int y, int tile) {
		changeTile(x, y, tile, false);
	}

	public int getLight(int i) {
		int l = ((tiles[i] >> 29) & 0x7);
		if (lighted) l -= darkness;
		// else l -= MAX_DARKNESS;
		return l;
	}

	public int getLight(int x, int y) {
		return getLight(x + y * width);
	}

	public void changeLight(int i, int light) {
		tiles[i] &= ~(BIT_LIGHT);
		tiles[i] |= light << 29;
		System.out.println(getLight(i));
		// System.out.println(String.format("%d", tiles[i] >> 29));
	}

	public void changeLight(int x, int y, int light) {
		changeLight(x + y * width, light);
	}

	public PacketLevelChange getPacket() {
		return new PacketLevelChange(width, height, time, tiles);
	}

	public void addPacket(PacketLevelChange packet) {
		width = packet.getWidth();
		height = packet.getHeight();
		time = packet.getTime();
		tiles = packet.getTiles();
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
			for (int i = 0; i < pixels.length; i++) {
				if ((pixels[i] & BIT_RANDOM_T) != 0) pixels[i] &= ~(BIT_TILEVAR);
			}
			ImageIO.write(image, "PNG", new File("save/" + name.toUpperCase() + ".PNG"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
