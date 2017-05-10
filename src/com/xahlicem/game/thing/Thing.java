package com.xahlicem.game.thing;

import java.awt.Rectangle;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.level.Level;

public abstract class Thing {
	protected int x, y;
	protected Rectangle bounds;
	protected int boundsOffset;
	protected int xOffset, yOffset;
	protected Level level;

	public Thing(Level level) {
		this.level = level;
		this.bounds = new Rectangle(0, 0, 0, 0);
	}

	protected void moveBounds(int x, int y) {
		bounds.setLocation(level.getX(this.x + x + xOffset), level.getY(this.y + y + yOffset));
	}

	protected void setBounds(int x, int y) {
		bounds.setLocation(x + xOffset, y + yOffset);
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
