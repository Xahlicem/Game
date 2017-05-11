package com.xahlicem.game.thing;

import java.util.Random;

import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.graphics.SpriteSheet;
import com.xahlicem.game.level.Level;

public class Critter extends MobileThing {

	protected static final Random R = new Random();
	
	protected int size = 16;

	protected Sprite[][] sprites;
	protected float moveChance = 0.02f;
	protected int moveTimeMax = 15;
	protected int moveTime = moveTimeMax;
	protected int moveTimeDiv = (moveTimeMax / 3);
	protected int lastX, lastY;
	protected boolean tick = false;

	public Critter(Level level, SpriteSheet spriteSheet, int x, int y) {
		super(level, Sprite.NULL, x, y);
		loadSprites(spriteSheet);
	}

	protected void loadSprites(SpriteSheet spriteSheet) {
		sprites = new Sprite[4][3];
		for (int y = 0; y < 4; y++)
			for (int x = 0; x < 3; x++) {
				sprites[y][x] = new Sprite(size, x, y, spriteSheet);
			}
		sprite = sprites[direction][0];
	}

	public void tick() {
		if (tick) {
			if (moveTime < moveTimeMax) {
				sprite = sprites[direction][moveTime / moveTimeDiv];
				move(lastX, lastY);
				moveTime++;
			} else {
				sprite = sprites[direction][0];
				if (R.nextFloat() <= moveChance) {
					lastX = R.nextInt(3) - 1;
					lastY = R.nextInt(3) - 1;
					if (lastX != 0 || lastY != 0) moveTime = 0;
				}
			}
		}
		tick = !tick;
	}
}
