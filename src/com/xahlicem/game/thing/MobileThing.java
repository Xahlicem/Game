package com.xahlicem.game.thing;

import java.awt.Rectangle;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.level.Level;

public class MobileThing extends Thing {

	protected Sprite sprite;
	protected int levelWidth, levelHeight;
	protected int direction = 0;

	public MobileThing(Level level, Sprite sprite, int x, int y) {
		super(level);
		this.sprite = sprite;
		this.x = level.getX(x);
		this.y = level.getY(y);
		levelWidth = level.width << 4;
		levelHeight = level.height << 4;
		bounds = new Rectangle(this.x, this.y, sprite.width, sprite.height);
	}

	@Override
	public void tick() {}

	public void move(int x, int y) {
		if (Math.abs(y) >= Math.abs(x)) {
			if (y > 0) direction = 2;
			else direction = 0;
		} else {
			if (x > 0) direction = 1;
			else direction = 3;
		}
		
		bounds.setLocation(level.getX(this.x + x), level.getY(this.y + y));

		if (!level.intersects(bounds, this)) {
			this.x = level.getX(this.x + x);
			this.y = level.getY(this.y + y);
		}
		bounds.setLocation(this.x, this.y);
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
	}

}
