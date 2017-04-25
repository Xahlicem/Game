package com.xahlicem.game.level.tile;

import java.util.ArrayList;
import java.util.List;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;

public class Tile {

	protected Sprite sprite;
	protected boolean solid = false;
	protected int color;
	
	public static List<Tile> tileList = new ArrayList<Tile>();

	public static final int WATER_COLOR = 0x0000FF;
	public static final int DIRT_COLOR = 0x964b00;
	public static final int GRASS_COLOR = 0x00FF00;

	public static final Tile NULL = new Tile(Sprite.NULL, 0xFF66FF);
	public static final Tile WATER = new AnimatedTile(new int[] { 120, 10, 5, 15 }, Sprite.WATER, 0x0000FE);
	public static final Tile R_WATER = new RandomAnimatedTile(10, new int[] { 5, 10, 5, 15 }, Sprite.WATER, 0x0000FD);
	public static final Tile DIRT = new Tile(Sprite.DIRT, 0x964b01);
	public static final Tile GRASS = new Tile(Sprite.GRASS, 0x00FE00);
	public static final Tile SMALL_FLOWERS = new Tile(Sprite.SMALL_FLOWERS, 0x00FD00);
	public static final Tile GRASS_GROWN = new AnimatedTile(new int[] { 150, 30, 45, 15 }, Sprite.GRASS_GROWN, 0x00FC00);
	public static final Tile R_GRASS_GROWN = new RandomAnimatedTile(50, new int[] { 5, 30, 45, 15 }, Sprite.GRASS_GROWN, 0x00FB00);
	public static final Tile GRASS_TALL = new AnimatedTile(new int[] { 150, 30, 45, 15 }, Sprite.GRASS_TALL, 0x00FA00);
	public static final Tile R_GRASS_TALL = new RandomAnimatedTile(50, new int[] { 5, 30, 45, 15 }, Sprite.GRASS_TALL, 0x00F900);
	public static final Tile FLOWERS = new AnimatedTile(60, Sprite.FLOWERS, 0x00F800);
	public static final Tile R_FLOWERS = new RandomAnimatedTile(100, 5, Sprite.FLOWERS, 0x00F700);

	public Tile(Sprite sprite, int color) {
		this.sprite = sprite;
		tileList.add(this);
		this.color = color;
	}

	public Tile(Sprite sprite, int color, boolean solid) {
		this.sprite = sprite;
		this.solid = solid;
		tileList.add(this);
		this.color = color;
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
}