package com.xahlicem.game.level;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;

public class Tile {

	protected int x, y;
	protected Sprite sprite;
	protected boolean solid = false;
	
	public static final Tile NULL = new Tile(Sprite.NULL);
	public static final Tile DIRT = new Tile(Sprite.DIRT);
	
	public Tile(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public Tile(Sprite sprite, boolean solid) {
		this.sprite = sprite;
		this.solid = solid;
	}
	
	public void draw(int x, int y, Screen screen) {
		screen.drawSprite(x, y, sprite);
	}
	
	public boolean solid() {
		return solid;
	}
}