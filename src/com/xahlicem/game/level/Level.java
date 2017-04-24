package com.xahlicem.game.level;

import java.util.Random;

import com.xahlicem.game.graphics.Screen;

public class Level {

	private static Random R = new Random();

	private int width, height, wMask, hMask;
	private int[] tiles;

	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		wMask = width - 1;
		hMask = height - 1;
		tiles = new int[width * height];
		generateLevel();
	}

	public Level(String path) {
		loadLevel(path);
	}

	private void loadLevel(String path) {

	}

	private void generateLevel() {
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) {
				tiles[x + y * width] = R.nextInt(3);
			}
	}

	public void tick() {
		AnimatedTile.WATER.tick();
		AnimatedTile.GRASS.tick();
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
		switch (tiles[i]) {
			case 0:
				return AnimatedTile.GRASS;
			case 1:
				return AnimatedTile.GRASS;
			case 2:
				return AnimatedTile.WATER;
			default:
				return Tile.NULL;
		}
	}
}
