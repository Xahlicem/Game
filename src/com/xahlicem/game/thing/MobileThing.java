package com.xahlicem.game.thing;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.level.Level;

public class MobileThing extends Thing{
	
	protected Sprite sprite;
	protected int levelWidth, levelHeight;

	public MobileThing(Level level, Sprite sprite, int x, int y) {
		super(level);
		this.sprite = sprite;
		this.x = x;
		this.y = y;
		levelWidth = level.width << 4;
		levelHeight = level.height << 4;
		//level.addMob(this);
	}

	@Override
	public void tick() {
		move(1, 0);
	}
	
	public int getPos() {
		return level.getPos(x, y);
	}
	
	public int getTilePos() {
		return level.getPos(x>>4, y>>4);
	}
	
	public void move(int x, int y) {
		this.x = level.getX(this.x + x);
		this.y = level.getY(this.y + y);
	}

	@Override
	public void draw(Screen screen) {
		screen.drawSprite(x, y, sprite, 8);
		screen.drawSprite(x-levelWidth, y-levelHeight, sprite, 8);
		screen.drawSprite(x-levelWidth, y, sprite, 8);
		screen.drawSprite(x-levelWidth, y+levelHeight, sprite, 8);
		screen.drawSprite(x+levelWidth, y-levelHeight, sprite, 8);
		screen.drawSprite(x+levelWidth, y, sprite, 8);
		screen.drawSprite(x+levelWidth, y+levelHeight, sprite, 8);
		screen.drawSprite(x, y-levelHeight, sprite, 8);
		screen.drawSprite(x, y+levelHeight, sprite, 8);
	}

}
