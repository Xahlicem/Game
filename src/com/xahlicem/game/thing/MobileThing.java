package com.xahlicem.game.thing;

import java.awt.Rectangle;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.level.Level;

public class MobileThing extends Thing {

	protected Sprite sprite;
	protected int levelWidth, levelHeight;
	protected int direction = 2;

	public MobileThing(Level level, Sprite sprite, int x, int y) {
		super(level);
		this.sprite = sprite;
		this.x = level.getX(x);
		this.y = level.getY(y);
		init();
	}

	public void init() {
		levelWidth = level.width << 4;
		levelHeight = level.height << 4;
		bounds = new Rectangle(x + xOffset, y + yOffset, sprite.width + widthOffset, sprite.height + heightOffset);
	}

	@Override
	public void tick() {}

	public void move(int x, int y) {
		y = moveY(y);
		x = moveX(x);
		if (Math.abs(y) >= Math.abs(x)) {
			if (y > 0) direction = 2;
			else direction = 0;
		} else {
			if (x > 0) direction = 1;
			else direction = 3;
		}
	}

	protected int moveY(int i) {
		moveBounds(0, i);
		if (level.intersects(this)) {
			moveBounds(0, -i);
			return 0;
		}
		y = level.getY(y + i);
		return i;
	}

	protected int moveX(int i) {
		moveBounds(i, 0);
		if (level.intersects(this)) {
			moveBounds(-i, 0);
			return 0;
		}
		x = level.getX(x + i);
		return i;
	}

	@Override
	public void draw(Screen screen, int... darkness) {
		screen.drawSprite(x, y, sprite, darkness);
		screen.drawSprite(x - levelWidth, y - levelHeight, sprite, darkness);
		screen.drawSprite(x - levelWidth, y, sprite, darkness);
		screen.drawSprite(x - levelWidth, y + levelHeight, sprite, darkness);
		screen.drawSprite(x + levelWidth, y - levelHeight, sprite, darkness);
		screen.drawSprite(x + levelWidth, y, sprite, darkness);
		screen.drawSprite(x + levelWidth, y + levelHeight, sprite, darkness);
		screen.drawSprite(x, y - levelHeight, sprite, darkness);
		screen.drawSprite(x, y + levelHeight, sprite, darkness);

		// Draw bounds
		// screen.drawSprite(bounds.x, bounds.y, new Sprite(sprite.width +
		// widthOffset, sprite.height + heightOffset, 0x0), darkness);
	}

}
