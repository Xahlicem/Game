package com.xahlicem.game.level;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.SpriteSheet;
import com.xahlicem.game.level.tile.Tile;

public class Level {

	private static final int DAY_LIGHT = 0xFFFFFF;
	private static final int MORNING_LIGHT = 0xC0C0C0;
	private static final int EVENING_LIGHT = 0x80A0A0;
	private static final int TWI_LIGHT = 0x306080;
	private static final int NIGHT_LIGHT = 0x203040;
	private static final Random R = new Random();

	private int width, height, wMask, hMask;
	private int[] tiles, darkness;
	private int time, light;
	public String[] bgm = new String[]{};

	public static final Level TITLE = new Level("/level/TITLE");

	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		wMask = width - 1;
		hMask = height - 1;
		tiles = new int[width * height];
		generateLevel();
	}

	public Level(int width, int height, String... bgm) {
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
	
	public Level(String path, String... bgm) {
		loadLevel(path);
		this.bgm = bgm;
	}

	private void loadLevel(String path) {
		try {
			BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path + ".png"));
			width = image.getWidth();
			height = image.getHeight();
			wMask = width - 1;
			hMask = height - 1;
			tiles = new int[width*height];
			image.getRGB(0, 0, width, height, tiles, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path + "_L.png"));
			width = image.getWidth();
			height = image.getHeight();
			wMask = width - 1;
			hMask = height - 1;
			darkness = new int[width*height];
			image.getRGB(0, 0, width, height, darkness, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
			darkness = new int[width*height];
			for (int i = 0; i < darkness.length; i++) darkness[i] = 0xFFFFFF;
		}
		
		for (int i = 0; i < tiles.length; i++) {
			tiles[i] = tiles[i] & 0xFFFFFF;
			darkness[i] = darkness[i] & 0xFFFFFF;
			switch (tiles[i] & 0xFFFFFF) {
			case 0x00FF00:
				tiles[i] = R.nextInt(7) + 4;
				break;
			case 0x00FE00:
				tiles[i] = 4;
				break;
			}
		}
	}

	private void generateLevel() {
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				tiles[x + y * width] = R.nextInt(10);
			}
	}

	public void tick() {
		time();
		for (Tile tile : Tile.animated)
			tile.tick();
	}
	
	private void time() {
		time++;
		if (time > 1000) time = 0;
		
		if (time < 150) light = NIGHT_LIGHT;
		else if (time < 250) light = TWI_LIGHT;
		else if (time < 300) light = MORNING_LIGHT;
		else if (time < 600) light = DAY_LIGHT;
		else if (time < 700) light = EVENING_LIGHT;
		else if (time < 750) light = TWI_LIGHT;
		else light = NIGHT_LIGHT;
		System.out.println(time + " " + light);
	}

	public void draw(int xScroll, int yScroll, Screen screen) {
		screen.setOffset(xScroll, yScroll);
		for (int y = yScroll >> 4; y <= (yScroll + screen.height) >> 4; y++)
			for (int x = xScroll >> 4; x <= (xScroll + screen.width) >> 4; x++) {
				int l = darkness[(x&wMask) + (y&hMask) * width];
				if (l < light) l = light;
				getTile(x & wMask, y & hMask).draw(x << 4, y << 4, screen, l);
			}
	}

	public Tile getTile(int x, int y) {
		return getTile(x + y * width);
	}

	public Tile getTile(int i) {
		if (tiles[i] >= Tile.tileList.size() || tiles[i] < 0) return Tile.NULL;
		return Tile.tileList.get(tiles[i]);
	}
}
