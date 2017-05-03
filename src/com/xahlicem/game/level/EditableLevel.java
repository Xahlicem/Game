package com.xahlicem.game.level;

import java.io.File;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.helpers.audio.BGM;
import com.xahlicem.game.level.tile.RandomAnimatedTile;
import com.xahlicem.game.level.tile.Tile;

public class EditableLevel extends TimeLevel {
	protected boolean click = false;
	protected boolean edit = true;
	protected int color = 0;
	protected int xPos, yPos;
	protected int lastX = -1, lastY = -1;

	public EditableLevel(int width, int height, BGM... bgm) {
		super(width, height, bgm);
	}

	public EditableLevel(String path, BGM... bgm) {
		super(path, bgm);
	}

	public EditableLevel(File file) {
		super(file);
	}

	public void tick(Input input) {
		super.tick(input);

		edit = input.isKeyPressed(Input.KEY_X);

		int pointX = input.getPoint()[0];
		int pointY = input.getPoint()[1];

		boolean corner = (pointX < 74 && pointY < 74);
		if (input.getWheel() != 0) {
			if (lighted()) color = (Tile.getTileIndexLength() * 2 + color + input.getWheel()) % Tile.getTileIndexLength();
			else color = (-DAY * 2 + color + input.getWheel()) % -DAY;
		}

		pointX = (pointX / Screen.SCALE) + x >> 4;
		pointY = (pointY / Screen.SCALE) + y >> 4;
		xPos = getTileX(pointX);
		yPos = getTileY(pointY);

		if (edit && input.isKeyPressed(Input.KEY_PRESS)) {
			if (xPos != lastX || yPos != lastY) {
				if (corner) {
					toggleLight();
					color = 0;
				} else {
					if (lighted()) {
						changeTile(xPos, yPos, Tile.getTileFromIndex(color).getColor(), input.isKeyPressed(Input.KEY_SHIFT));
					} else {
						changeLight(xPos, yPos, color);
					}
				}

				if (game.getClient() != null) sendChange(game.getClient());
				lastX = getTileX(pointX);
				lastY = getTileY(pointY);
			}
		}

		if (!click && input.isKeyPressed(Input.KEY_PRESS)) {
			click = true;
		}
		if (click && !input.isKeyPressed(Input.KEY_PRESS)) {
			click = false;
			lastX = -1;
			lastY = -1;
		}
	}
	
	@Override
	protected void drawThings(Screen screen) {
		super.drawThings(screen);
		if (edit) {
			screen.drawSprite(x, y, Sprite.CONTAINER, 8);
			String s = String.valueOf(color);
			if (lighted()) {
				Tile.getTileFromIndex(color).draw(x + 2, y + 2, screen);
				if (Tile.getTileFromIndex(color).getClass().equals(RandomAnimatedTile.class)) s += "A";
			} else {
				getTile(xPos, yPos).draw(x + 2, y + 2, screen, color);
			}
			screen.drawString(x + 3, y + 12, s, Sprite.FONT_TINY, (lighted()) ? 0xFF000000 : 0xFFFFFFFF);

		}
	}

}
