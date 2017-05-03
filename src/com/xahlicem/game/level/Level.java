package com.xahlicem.game.level;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.xahlicem.game.Game;
import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.helpers.audio.BGM;
import com.xahlicem.game.helpers.audio.BGMPlayer;
import com.xahlicem.game.helpers.audio.SFXPlayer;
import com.xahlicem.game.helpers.net.Client;
import com.xahlicem.game.helpers.net.Server;
import com.xahlicem.game.helpers.net.packet.Packet;
import com.xahlicem.game.helpers.net.packet.PacketLevelChange;
import com.xahlicem.game.level.tile.Tile;
import com.xahlicem.game.thing.Thing;

public class Level {

	/**
	 * Bits that will indicate how dark a something will get
	 */
	protected static final int BIT_LIGHT = 0b11100000000000000000000000000000; // 8
	/**
	 * Player start flag
	 * 
	 * player will start on random tile with flag
	 */
	protected static final int BIT_PLAYER = 0b00010000000000000000000000000000; // 1
	/**
	 * Next level flag
	 */
	protected static final int BIT_NEXT = 0b00001000000000000000000000000000; // 1
	/**
	 * Previous level flag
	 */
	protected static final int BIT_PREV = 0b00000100000000000000000000000000; // 1
	/**
	 * Random mob flag
	 */
	protected static final int BIT_RANDOM_M = 0b00000010000000000000000000000000; // 1
	/**
	 * Random tile flag
	 */
	protected static final int BIT_RANDOM_T = 0b00000001000000000000000000000000; // 1
	/**
	 * Which mob set mob is using
	 */
	protected static final int BIT_MOBSET = 0b00000000111000000000000000000000; // 8
	/**
	 * Which mob out of mob set
	 */
	protected static final int BIT_MOB = 0b00000000000111100000000000000000; // 16
	/**
	 * What item is contained within mob/chest
	 */
	protected static final int BIT_ITEM = 0b00000000000000011111000000000000; // 32
	/**
	 * Which tile type
	 * 
	 * TODO Finish
	 */
	protected static final int BIT_TILETYPE = 0b00000000000000000000111110000000; // 32
	/**
	 * Which variant of tile to show
	 * 
	 * if BIT_RANDOM_T is set ignore
	 */
	protected static final int BIT_TILEVAR = 0b00000000000000000000000001110000; // 8
	/**
	 * Which edge to show
	 * 
	 * 0b0001 = Top 0b0010 = Bottom 0b0100 = Left 0b1000 = Right
	 */
	protected static final int BIT_EDGE = 0b00000000000000000000000000001111; // 8

	protected static final Random R = new Random();

	public int width, height;
	protected int[] tiles;
	protected int x = 0, y = 0;
	private BGM[] bgm = new BGM[] {};
	private int bgmIndex = 0;
	protected BGMPlayer midi;
	protected SFXPlayer sfx;
	protected Game game;
	protected boolean up, down, enter, esc;
	protected List<Thing> things = new ArrayList<Thing>();

	public static final Level TITLE = new TimeLevel("/level/TITLE", BGM.BGM_TITLE);
	public static final Level MAIN_MENU = new MenuLevel(Game.TITLE, "Start Game", "Load", "Options", "Quit");

	public Level(int width, int height, BGM... bgm) {
		this.width = width;
		this.height = height;
		this.bgm = bgm;
		tiles = new int[width * height];
		generateLevel();
	}

	public Level(String path, BGM... bgm) {
		loadLevel(path);
		this.bgm = bgm;
	}

	public Level(File file) {
		try {
			loadLevel(file.toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void loadLevel(String path) {
		loadLevel(Level.class.getResource(path + ".PNG"));
	}

	private void loadLevel(URL url) {
		try {
			BufferedImage image = ImageIO.read(url);
			width = image.getWidth();
			height = image.getHeight();
			tiles = new int[width * height];
			image.getRGB(0, 0, width, height, tiles, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				if ((tiles[x + y * width] & BIT_RANDOM_T) != 0) changeTile(x, y, tiles[x + y * width], true);
	}

	private void calculateEdge(int x, int y) {
		int i = getTilePos(x, y);
		tiles[i] &= ~(BIT_EDGE);
		int color = tiles[i] & BIT_TILETYPE;
		if ((tiles[getTilePos(x, y + 1)] & BIT_TILETYPE) != color) tiles[i] |= 0b0001;
		if ((tiles[getTilePos(x, y - 1)] & BIT_TILETYPE) != color) tiles[i] |= 0b0010;
		if ((tiles[getTilePos(x + 1, y)] & BIT_TILETYPE) != color) tiles[i] |= 0b0100;
		if ((tiles[getTilePos(x - 1, y)] & BIT_TILETYPE) != color) tiles[i] |= 0b1000;
	}

	private void calculateEdges(int x, int y) {
		calculateEdge(x, y);
		calculateEdge(x, y - 1);
		calculateEdge(x, y + 1);
		calculateEdge(x - 1, y);
		calculateEdge(x + 1, y);
	}

	private void generateLevel() {
		for (int i = 0; i < tiles.length; i++)
			tiles[i] = (R.nextInt(4) + 1) << 7;
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				calculateEdge(x, y);
	}

	public void init(Game game, BGMPlayer midi, SFXPlayer sfx) {
		this.game = game;
		this.midi = midi;
		this.sfx = sfx;
		up = false;
		down = false;
		enter = false;
		esc = false;
	}

	public void tick(Input input) {
		move(input);
		
		if (input.isKeyPressed(Input.KEY_ESC)) {
			if (esc)menu();
			esc = false;
		}else esc = true;

		if (bgm.length != 0 && !midi.isPlaying()) {
			midi.setSound(bgm[bgmIndex++ % bgm.length]);
			midi.play();
		}
		for (int i = 0; i < Tile.getTileIndexLength(); i++)
			Tile.getTileFromIndex(i).tick();
		for (Thing thing : getThings())
			thing.tick();

	}
	
	protected void menu() {
		game.changeLevel(new MenuLevel(this, "Paused", "Resume", "Options", "Save", "Load", "Exit"));
	}
	
	protected void move(Input input) {
		int speed = 2;

		if (input.isKeyPressed(Input.KEY_SHIFT)) speed = 4;

		if (input.isKeyPressed(Input.KEY_UP)) y = getY(y - speed);
		if (input.isKeyPressed(Input.KEY_DOWN)) y = getY(y + speed);
		if (input.isKeyPressed(Input.KEY_LEFT)) x = getX(x - speed);
		if (input.isKeyPressed(Input.KEY_RIGHT)) x = getX(x + speed);
	}

	public void draw(Screen screen) {
		screen.setOffset(x, y);
		for (int yPos = y >> 4; yPos <= (y + Screen.HEIGHT + 32) >> 4; yPos++)
			for (int xPos = x >> 4; xPos <= (x + Screen.WIDTH) >> 4; xPos++) {
				int i = getTilePos(xPos, yPos);
				int x2 = xPos << 4;
				int y2 = yPos << 4;

				int[] lights = getLights(xPos, yPos);

				getTile(i).draw(x2, y2, screen, lights);
				drawEdges(xPos, yPos, x2, y2, screen, lights);
			}
		for (Thing t : things)
			t.draw(screen);
	}

	protected int[] getLights(int x, int y) {
		int[] lights = new int[5];
		lights[0] = getLight(x, y);
		lights[1] = getLight(x, y - 1);
		lights[2] = getLight(x, y + 1);
		lights[3] = getLight(x - 1, y);
		lights[4] = getLight(x + 1, y);
		return lights;
	}

	protected void drawEdges(int x, int y, int x2, int y2, Screen screen, int... lights) {
		int loc = getTilePos(x, y);
		int[] increaseY = new int[] { 1, -1, 0, 0 };
		int[] increaseX = new int[] { 0, 0, 1, -1 };
		int h = getTile(loc).getHeight();

		for (int i = 0; i < 4; i++) {
			if (((tiles[loc] >> i) & 0x1) == 0x1) {
				int t = getTile(getTilePos(x + increaseX[i], y + increaseY[i])).getHeight();
				if (t > h) screen.drawSprite(x2, y2, Sprite.EDGES[t][i], lights);
			}
		}
	}

	public Tile getTile(int x, int y) {
		return getTile(x + y * width);
	}

	public Tile getTile(int i) {
		return Tile.getTile(tiles[i]);
	}

	public void changeTile(int x, int y, int tile, boolean random) {
		int i = getTilePos(x, y);
		tiles[i] &= ~(BIT_TILETYPE | BIT_TILEVAR);

		if (random) tiles[i] |= Tile.getRandomColor(tile) | BIT_RANDOM_T;
		else {
			tiles[i] &= ~(BIT_RANDOM_T);
			tiles[i] |= tile;
		}

		if (sfx != null) sfx.sound(127, 1);

		calculateEdges(x, y);
	}

	public void changeTile(int x, int y, int tile) {
		changeTile(x, y, tile, false);
	}

	public int getLight(int i) {
		int l = ((tiles[i] >> 29) & 0x7);
		return l;
	}

	public int getLight(int x, int y) {
		return getLight(getTilePos(x, y));
	}

	public void changeLight(int i, int light) {
		tiles[i] &= ~(BIT_LIGHT);
		tiles[i] |= light << 29;
	}

	public void changeLight(int x, int y, int light) {
		changeLight(x + y * width, light);
	}

	public int getX(int i) {
		int w = width << 4;
		if (i < w && i > 0) return i;
		int x = i % w;
		if (x < 0) return x + w;
		return x;
	}

	public int getY(int i) {
		int h = height << 4;
		if (i < h && i > 0) return i;
		int y = i % h;
		if (y < 0) return y + h;
		return y;
	}

	public int getPos(int x, int y) {
		return getX(x) + getY(y) * (width << 4);
	}

	public int getTileX(int i) {
		if (i < width && i > 0) return i;
		int x = i % width;
		if (x < 0) return x + width;
		return x;
	}

	public int getTileY(int i) {
		if (i < height && i > 0) return i;
		int y = i % height;
		if (y < 0) return y + height;
		return y;
	}

	public int getTilePos(int x, int y) {
		return getTileX(x) + getTileY(y) * width;
	}

	public void sendChange(Client client) {
		getPacket().writeData(client);
	}

	public void sendChangeTo(Server server, InetSocketAddress address) {
		getPacket().writeSingleData(server, address);;
	}

	private PacketLevelChange getPacket() {
		return new PacketLevelChange(width, height, tiles);
	}

	public void addPacket(Packet packet) {
		switch (packet.gePacketType()) {
			case LEVEL_CHANGE:
				width = ((PacketLevelChange) packet).getWidth();
				height = ((PacketLevelChange) packet).getHeight();
				tiles = ((PacketLevelChange) packet).getTiles();
				break;
			default:
				break;
		}
	}

	public void addThing(Thing Thing) {
		getThings().add(Thing);
	}

	protected synchronized List<Thing> getThings() {
		return things;
	}

	public void save(String name) {
		System.out.println("Saved!");
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		try {
			System.arraycopy(tiles, 0, pixels, 0, pixels.length);
			for (int i = 0; i < pixels.length; i++) {
				if ((pixels[i] & BIT_RANDOM_T) != 0) pixels[i] &= ~(BIT_TILEVAR);
			}
			ImageIO.write(image, "PNG", new File("save/" + name.toUpperCase() + ".PNG"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
