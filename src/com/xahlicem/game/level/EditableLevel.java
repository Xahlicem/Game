package com.xahlicem.game.level;

import java.io.File;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.graphics.SpriteSheet;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.helpers.audio.BGM;
import com.xahlicem.game.level.tile.RandomAnimatedTile;
import com.xahlicem.game.level.tile.Tile;
import com.xahlicem.game.thing.Mouse;
import com.xahlicem.game.thing.Rabbit;

public class EditableLevel extends TimeLevel {
	protected static enum State {
		TILE, LIGHT, THING
	}

	protected State currentState = State.TILE;
	protected boolean click = false;
	protected boolean edit = true;
	protected int index = 0;
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
			switch (currentState) {
				case TILE:
					index = (Tile.getTileIndexLength() * 2 + index + input.getWheel()) % Tile.getTileIndexLength();
					break;
				case LIGHT:
					index = (-DAY * 2 + index + input.getWheel()) % -DAY;
					break;
				case THING:
					index = (4 + index + input.getWheel()) % 2;
					break;
			}
		}

		pointX = (pointX / Screen.SCALE) + x >> 4;
		pointY = (pointY / Screen.SCALE) + y >> 4;
		xPos = getTileX(pointX);
		yPos = getTileY(pointY);

		if (edit && input.isKeyPressed(Input.KEY_PRESS)) {
			if (xPos != lastX || yPos != lastY) {
				if (corner) {
					switch (currentState) {
						case TILE:
							lighted = false;

							currentState = State.LIGHT;
							break;
						case LIGHT:
							lighted = true;

							currentState = State.THING;
							break;
						case THING:
							lighted = true;

							currentState = State.TILE;
							break;
					}
					index = 0;
				} else {
					switch (currentState) {
						case TILE:
							changeTile(xPos, yPos, Tile.getTileFromIndex(index).getColor(), input.isKeyPressed(Input.KEY_SHIFT));
							break;
						case LIGHT:
							changeLight(xPos, yPos, index);
							break;
						case THING:
							switch (index) {
								case 0:
									addThing(new Rabbit(this, xPos << 4, yPos << 4));
									break;
								case 1:
									addThing(new Mouse(this, xPos << 4, yPos << 4));
									break;
							}
							break;
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
	protected void tickThings() {
		if (!edit) super.tickThings();
	}

	@Override
	protected void drawThings(Screen screen) {
		super.drawThings(screen);
		if (edit) {
			screen.drawSprite(x, y, Sprite.CONTAINER, 8);
			String s = String.valueOf(index);
			switch (currentState) {
				case TILE:
					Tile.getTileFromIndex(index).draw(x + 2, y + 2, screen);
					if (Tile.getTileFromIndex(index).getClass().equals(RandomAnimatedTile.class)) s += "A";
					break;
				case LIGHT:
					getTile(xPos, yPos).draw(x + 2, y + 2, screen, index);
					break;
				case THING:
					switch (index) {
						case 0:
							screen.drawSprite(x + 2, y + 2, new Sprite(16, 0, 2, SpriteSheet.THING_RABBIT), 8);
							break;
						case 1:
							screen.drawSprite(x + 2, y + 2, new Sprite(16, 0, 2, SpriteSheet.THING_MOUSE), 8);
							break;
					}
					break;
			}
			screen.drawString(x + 3, y + 12, s, Sprite.FONT_TINY, (lighted()) ? 0xFF000000 : 0xFFFFFFFF);

		}
	}

}
