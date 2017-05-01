package com.xahlicem.game.level.tile;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;

public class TallTile extends Tile {
	
	protected Sprite[] sprites;

	public TallTile(TileType type, Sprite...sprites) {
		super(type, sprites[0]);
		this.sprites = sprites;
	}
	
	public void draw(int x, int y, Screen screen, int darkness) {
		screen.drawSprite(x, y, sprite, darkness);
		for (int i = 1; i < sprites.length; i++) screen.drawSprite(x, y - sprite.height * i, sprites[i], darkness);
	}
	
	public void draw(int x, int y, Screen screen, int darkness, int t, int b, int l, int r) {
		screen.drawSprite(x, y, sprite, darkness, t, b, l, r);
		for (int i = 1; i < sprites.length; i++) screen.drawSprite(x, y - sprite.height * i, sprites[i], darkness, t, b ,l, r);
	}

}
