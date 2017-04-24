package com.xahlicem.game.level;

import java.util.Random;

import com.xahlicem.game.graphics.Screen;

public class Level {
	
	private static Random R = new Random();

	private int width, height;
	private int[] tiles;
	
	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		tiles = new int[width*height];
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
		
	}
	
	public void draw(int xScroll, int yScroll, Screen screen) {
		
	}
}
