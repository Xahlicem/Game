package com.xahlicem.game.level;

import java.awt.image.BufferedImage;
import java.io.IOException;
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
import com.xahlicem.game.level.tile.Tile;

public class Level {

	private static final int DAY_LIGHT = 0xFFFFFF;
	private static final int MORNING_LIGHT = 0xC0C0C0;
	private static final int EVENING_LIGHT = 0x80C0C0;
	private static final int DARK_LIGHT = 0x1F1A1F;
	private static final int NIGHT_LIGHT = 0x0A0F09;
	private static final int MIDNIGHT_LIGHT = 0x000000;
	private static final Random R = new Random();

	public int width, height, wMask, hMask;
	private int[] tiles, darkness, edges;
	private List<Tile> tileList = new ArrayList<Tile>();
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
			darkness = new int[width * height];
			image.getRGB(0, 0, width, height, darkness, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
			darkness = new int[width * height];
			for (int i = 0; i < darkness.length; i++)
				darkness[i] = 0xFFFFFF;
		}

		edges = new int[width * height];

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				int i = x + y * width;
				calculateEdges(x, y);
				darkness[i] = darkness[i] & 0xFFFFFF;
				tiles[i] = Tile.getRandomColor(tiles[i]);
				if (!tileList.contains(Tile.getTile(tiles[i]))) tileList.add(Tile.getTile(tiles[i]));
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
		//light = DARK_LIGHT;
		for (Tile tile : tileList)
			tile.tick();
	}

	private void time() {
		time++;
		if (time > 2400) time = 0;

		if (time >= 2250) light = NIGHT_LIGHT;
		else if (time >= 2100) light = DARK_LIGHT;
		else if (time >= 1900) light = EVENING_LIGHT;
		else if (time >= 800) light = DAY_LIGHT;
		else if (time >= 550) light = MORNING_LIGHT;
		else if (time >= 400) light = DARK_LIGHT;
		else if (time >= 300) light = NIGHT_LIGHT;
		else light = MIDNIGHT_LIGHT;
	}

	public void draw(int xScroll, int yScroll, Screen screen) {
		screen.setOffset(xScroll, yScroll);
		for (int y = yScroll >> 4; y <= (yScroll + screen.height + 32) >> 4; y++)
			for (int x = xScroll >> 4; x <= (xScroll + screen.width) >> 4; x++) {
				int i = (x & wMask) + (y & hMask) * width;
				int x2 = x << 4;
				int y2 = y << 4;
				int l = darkness[i];
				if (l < light) l = light;
				getTile(i).draw(x2, y2, screen, l);
				drawEdges(x, y, x2, y2, l, screen);
			}
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

	public void changeTile(int x, int y, int tile) {
		tiles[x + y * width] = tile;
		sfx.sound(127, 1);
		calculateEdges(x, y);
		calculateEdges((x - 1) & wMask, y);
		calculateEdges((x + 1) & wMask, y);
		calculateEdges(x, (y - 1) & hMask);
		calculateEdges(x, (y + 1) & hMask);
	}
}
