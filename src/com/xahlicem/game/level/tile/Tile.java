package com.xahlicem.game.level.tile;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;

public class Tile {
	protected static final Random R = new Random();
	
	protected static enum TileType {
		SPECIAL(0), WATER(0), DIRT(1), PATH(2), GRASS(3), TREE(3);
		public static final int MAX_VARIENTS = 8;
		
		private int height;
		private int index = 0;
		
		private TileType(int height) {
			this.height = height;
		}
		
		public int getIndex() {
			return index;
		}
		
		public int increaseIndex() {
			int i = index + 0;
			index = index + 1;
			if (index >= MAX_VARIENTS) index = MAX_VARIENTS;
			return i;
		}
		
		public int get() {
			return ordinal() << 3;
		}
		
		public int getHeight() {
			return height;
		}
	}

	protected Sprite sprite;
	protected boolean solid = false;
	protected int varient;
	protected TileType type;

	private static final HashMap<Integer, Tile> tileMap = new LinkedHashMap<Integer, Tile>();

	public static final Tile NULL = new Tile(TileType.SPECIAL, Sprite.NULL);

	public static final Tile WATER = new Tile(TileType.WATER, Sprite.WATER);
	public static final Tile WATER_SHINE = new AnimatedTile(TileType.WATER, Sprite.WATER, new int[] { 300, 10, 5, 15 });
	public static final Tile R_WATER_SHINE = new RandomAnimatedTile(TileType.WATER, Sprite.WATER, new int[] { 5, 10, 5, 15 }, 500);

	public static final Tile DIRT = new Tile(TileType.DIRT, Sprite.DIRT);
	public static final Tile DIRT_GRASS = new Tile(TileType.DIRT, Sprite.DIRT_GRASS);
	public static final Tile DIRT_CRACKED = new Tile(TileType.DIRT, Sprite.DIRT_CRACKED);

	public static final Tile PATH = new Tile(TileType.PATH, Sprite.PATH);
	public static final Tile PATH_GRASS = new Tile(TileType.PATH, Sprite.PATH_GRASS);
	public static final Tile PATH_CRACKED = new Tile(TileType.PATH, Sprite.PATH_CRACKED);

	public static final Tile GRASS = new Tile(TileType.GRASS, Sprite.GRASS);
	//public static final Tile GRASS_ROCK = new Tile(TileType.GRASS, Sprite.GRASS_ROCK);
	public static final Tile SMALL_FLOWERS = new Tile(TileType.GRASS, Sprite.SMALL_FLOWERS);
	public static final Tile GRASS_GROWN = new AnimatedTile(TileType.GRASS, Sprite.GRASS_GROWN, new int[] { 150, 30, 45, 15 });
	public static final Tile R_GRASS_GROWN = new RandomAnimatedTile(TileType.GRASS, Sprite.GRASS_GROWN, new int[] { 5, 30, 45, 15 }, 1000);
	public static final Tile GRASS_TALL = new AnimatedTile(TileType.GRASS, Sprite.GRASS_TALL, new int[] { 150, 30, 45, 15 });
	public static final Tile R_GRASS_TALL = new RandomAnimatedTile(TileType.GRASS, Sprite.GRASS_TALL, new int[] { 5, 30, 45, 15 }, 2000);
	public static final Tile FLOWERS = new AnimatedTile(TileType.GRASS, Sprite.FLOWERS, 60);
	public static final Tile R_FLOWERS = new RandomAnimatedTile(TileType.GRASS, Sprite.FLOWERS, 5, 1500);
	public static final Tile TREE = new TallTile(TileType.TREE, Sprite.TREE_TRUNK, Sprite.TREE_MIDDLE, Sprite.TREE_TOP);

	public Tile(TileType type, Sprite sprite) {
		this.type = type;
		this.sprite = sprite;
		varient = type.increaseIndex();
		tileMap.put(type.get() | varient, this);
	}

	public Tile(TileType type, Sprite sprite, boolean solid) {
		this.type = type;
		this.sprite = sprite;
		this.solid = solid;
		varient = type.increaseIndex();
		tileMap.put(type.get() | varient, this);
	}

	public static Tile getTile(int color) {
		int tile = (color >> 8) & 0xFF;
		if (!tileMap.containsKey(tile)) return NULL;
		return tileMap.get(tile);
	}

	public void tick() {}

	public void draw(int x, int y, Screen screen, int l) {
		screen.drawSprite(x, y, sprite, l);
	}

	public void draw(int x, int y, Screen screen) {
		screen.drawSprite(x, y, sprite, 8);
	}

	public boolean solid() {
		return solid;
	}

	public int getColor() {
		return (type.get() | varient) << 8;
	}

	public int getHeight() {
		return type.getHeight();
	}

	public int getBaseColor() {
		return type.get() << 8;
	}
	
	public int getIndex() {
		int i = 0;
		for (int key : tileMap.keySet()) {
			if (tileMap.get(key) == this) break;
			i++;
		}
		return i;
	}

	public int getRandomColor() {
		if (type.getIndex() <= 1) return this.getColor();
		return (type.get() | R.nextInt(type.getIndex())) << 8;
	}

	public static int getRandomColor(int tile) {
		return getTile(tile).getRandomColor();
	}
	
	public static int getTileIndexLength() {
		return tileMap.size();
	}
	
	public static int getTileIndex(Tile tile) {
		int i = 0;
		for (int key : tileMap.keySet()) {
			if (tileMap.get(key) == tile) break;
			i++;
		}
		return i;
	}
	
	public static Tile getTileFromIndex(int num) {
		int i = 0;
		for (int key : tileMap.keySet()) {
			if (i == num) return tileMap.get(key);
			i++;
		}
		return NULL;
	}

	public void draw(int x2, int y2, Screen screen, int p, int t, int b, int l, int r) {
		screen.drawSprite(x2, y2, sprite, p, t, b, l, r);
	}
}