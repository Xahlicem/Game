package com.xahlicem.game.level.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;

public class Tile {
	protected static final Random R = new Random();

	protected Sprite sprite;
	protected boolean solid = false;
	protected int color, baseColor;
	protected int height;

	private static final HashMap<Integer, Tile> tileColor = new HashMap<Integer, Tile>();
	public static List<Tile> list = new ArrayList<Tile>();

	private static final int INCREASE_BY = 0x01000000;
	
	public static final int WATER_COLOR = 0xE00000FF;
	public static int waterIndex = 0;
	public static final int DIRT_COLOR = 0xE0964b00;
	public static int dirtIndex = 0;
	public static final int GRASS_COLOR = 0xE000FF00;
	public static int grassIndex = 0;

	public static final Tile NULL = new Tile(Sprite.NULL, 0xFFFF66FF);
	public static final Tile WATER = new Tile(Sprite.WATER, WATER_COLOR);
	public static final Tile WATER_SHINE = new AnimatedTile(new int[] { 300, 10, 5, 15 }, Sprite.WATER, WATER_COLOR);
	public static final Tile R_WATER_SHINE = new RandomAnimatedTile(500, new int[] { 5, 10, 5, 15 }, Sprite.WATER, WATER_COLOR);
	public static final Tile DIRT = new Tile(Sprite.DIRT, DIRT_COLOR);
	public static final Tile GRASS = new Tile(Sprite.GRASS, GRASS_COLOR);
	public static final Tile SMALL_FLOWERS = new Tile(Sprite.SMALL_FLOWERS, GRASS_COLOR);
	public static final Tile GRASS_GROWN = new AnimatedTile(new int[] { 150, 30, 45, 15 }, Sprite.GRASS_GROWN, GRASS_COLOR);
	public static final Tile R_GRASS_GROWN = new RandomAnimatedTile(1000, new int[] { 5, 30, 45, 15 }, Sprite.GRASS_GROWN, GRASS_COLOR);
	public static final Tile GRASS_TALL = new AnimatedTile(new int[] { 150, 30, 45, 15 }, Sprite.GRASS_TALL, GRASS_COLOR);
	public static final Tile R_GRASS_TALL = new RandomAnimatedTile(2000, new int[] { 5, 30, 45, 15 }, Sprite.GRASS_TALL, GRASS_COLOR);
	public static final Tile FLOWERS = new AnimatedTile(60, Sprite.FLOWERS, GRASS_COLOR);
	public static final Tile R_FLOWERS = new RandomAnimatedTile(1500, 5, Sprite.FLOWERS, GRASS_COLOR);
	public static final Tile GRASS_TREE = new TallTile(GRASS_COLOR, Sprite.TREE_TRUNK, Sprite.TREE_MIDDLE, Sprite.TREE_TOP);

	public Tile(Sprite sprite, int color) {
		this.sprite = sprite;
		this.color = color;
		baseColor = color;
		init();
		tileColor.put(this.color, this);
		list.add(this);
	}

	private void init() {
		height = 0;
		
		switch (color) {
		case WATER_COLOR:
			height = 0;
			this.color += waterIndex;
			waterIndex += INCREASE_BY;
			break;
		case DIRT_COLOR:
			height = 1;
			this.color += dirtIndex;
			dirtIndex += INCREASE_BY;
			break;
		case GRASS_COLOR:
			height = 2;
			this.color += grassIndex;
			grassIndex += INCREASE_BY;
			break;
		}
	}

	public Tile(Sprite sprite, int color, boolean solid) {
		this.sprite = sprite;
		this.solid = solid;
		this.color = color;
		tileColor.put(color, this);
		list.add(this);
	}

	public static Tile getTile(int color) {
		if (!tileColor.containsKey(color)) return NULL;
		return tileColor.get(color);
	}

	public void tick() {}

	public void draw(int x, int y, Screen screen, int darkness) {
		screen.drawSprite(x, y, sprite, darkness);
	}

	public void draw(int x, int y, Screen screen) {
		screen.drawSprite(x, y, sprite, 0xFFFFFF);
	}

	public boolean solid() {
		return solid;
	}

	public int getColor() {
		return color;
	}

	public static int getRandomColor(int color) {
		switch (color - 0x1F000000) {
		case Tile.WATER_COLOR:
			return Tile.WATER_COLOR + (R.nextInt(Tile.waterIndex >> 24) << 24);
		case Tile.DIRT_COLOR:
			return Tile.DIRT_COLOR + (R.nextInt(Tile.dirtIndex >> 24) << 24);
		case Tile.GRASS_COLOR:
			return Tile.GRASS_COLOR + (R.nextInt(Tile.grassIndex >> 24) << 24);
		}
		return color;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getBaseColor() {
		return baseColor;
	}
}