package com.xahlicem.game.thing;

import java.awt.Rectangle;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.level.Level;

public abstract class Thing {
	protected int x, y;
	protected Rectangle bounds;
	protected Level level;

	public Thing(Level level) {
		this.level = level;
		this.bounds = new Rectangle(0, 0, 0, 0);
	}

	public abstract void tick();
	
	public void draw(Screen screen) {
		draw(screen, 8);
	}

	public abstract void draw(Screen screen, int... darkness);
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public int getPos() {
		return level.getPos(x, y);
	}

	public int getTilePos() {
		return level.getTilePos(x >> 4, y >> 4);
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
}
