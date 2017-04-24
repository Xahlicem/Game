package com.xahlicem.game.level;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;

public class Tile {

	private int x, y;
	private Sprite sprite;
	private boolean solid = false;
	
	public Tile(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public Tile(Sprite sprite, boolean solid) {
		this.sprite = sprite;
		this.solid = solid;
	}
	
	public void draw(int x, int y, Screen screen) {
		
	}
	
	public boolean solid() {
		return solid;
	}
}