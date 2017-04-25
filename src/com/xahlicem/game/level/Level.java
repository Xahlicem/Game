package com.xahlicem.game.level;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.SpriteSheet;
import com.xahlicem.game.level.tile.Tile;

public class Level {

	private static Random R = new Random();

	private int width, height, wMask, hMask;
	private int[] tiles;
	public String[] bgm = new String[]{};

	public static final Level TITLE = new Level("/level/TITLE.png", "BGM0");

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
			BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path));
			width = image.getWidth();
			height = image.getHeight();
			wMask = width - 1;
			hMask = height - 1;
			tiles = new int[width*height];
			image.getRGB(0, 0, width, height, tiles, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < tiles.length; i++) {
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
		for (Tile tile : Tile.animated)
			tile.tick();
	}

	public void draw(int xScroll, int yScroll, Screen screen) {
		screen.setOffset(xScroll, yScroll);
		for (int y = yScroll >> 4; y <= (yScroll + screen.height) >> 4; y++)
			for (int x = xScroll >> 4; x <= (xScroll + screen.width) >> 4; x++)
				getTile(x & wMask, y & hMask).draw(x << 4, y << 4, screen);
	}

	public Tile getTile(int x, int y) {
		return getTile(x + y * width);
	}

	public Tile getTile(int i) {
		if (tiles[i] >= Tile.tileList.size() || tiles[i] < 0) return Tile.NULL;
		return Tile.tileList.get(tiles[i]);
		/*switch (tiles[i]) {
		case 0:
			return Tile.GRASS;
		case 1:
			return Tile.GRASS_GROWN;
		case 2:
			return Tile.R_GRASS_GROWN;
		case 3:
			return Tile.GRASS_TALL;
		case 4:
			return Tile.R_GRASS_TALL;
		case 5:
			return Tile.FLOWERS;
		case 6:
			return Tile.R_FLOWERS;
		default:
			return Tile.NULL;
		}*/
	}
}
