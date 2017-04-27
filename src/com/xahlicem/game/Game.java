package com.xahlicem.game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.helpers.audio.BGMPlayer;
import com.xahlicem.game.helpers.audio.SFXPlayer;
import com.xahlicem.game.helpers.audio.Volume;
import com.xahlicem.game.level.Level;
import com.xahlicem.game.level.tile.RandomAnimatedTile;
import com.xahlicem.game.level.tile.Tile;

public class Game extends Canvas implements Runnable {
	private static final double TPS = 60D;
	private static final double NSPT = 1_000_000_000D / TPS;
	private static final long serialVersionUID = 3929185344600372879L;

	public static final int WIDTH = 300;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static final int SCALE = 4;
	public static final String TITLE = "Game";

	private Thread thread;
	private Frame frame;
	private boolean running;
	private Input input;
	private BGMPlayer bgm;
	private SFXPlayer sfx;
	private Volume volume;
	private Screen screen;
	private Level level;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private int[] backPixels;

	public Game() {
		Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		setPreferredSize(size);
		setFocusable(true);
		requestFocus();

		screen = new Screen(WIDTH, HEIGHT, pixels);
		frame = new Frame(this);
		input = new Input();

		bgm = new BGMPlayer();
		sfx = new SFXPlayer();
		volume = new Volume();

		addKeyListener(input);
		addMouseListener(input);
		addMouseWheelListener(input);
		addMouseMotionListener(input);

		changeLevel(Level.TITLE);
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.frame.init();

		game.start();
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime(), now = 0;
		long timer = System.currentTimeMillis();
		double delta = 0;
		int fps = 0, tps = 0;
		boolean draw = false;
		volume.set(0.05);

		while (running) {
			now = System.nanoTime();
			delta += (double) (now - lastTime) / NSPT;
			lastTime = now;

			if (delta < 1) try {
				Thread.sleep(5);
			} catch (Exception e) {
				e.printStackTrace();
			}

			while (delta >= 1) {
				tick();
				tps++;
				draw = true;
				delta--;
			}

			if (draw) {
				draw();
				fps++;
				draw = false;
			}

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle(TITLE + "   |   TPS:" + tps + ", FPS:" + fps);
				tps = 0;
				fps = 0;
			}
		}
		sfx.close();
		bgm.close();

	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void tick() {
		sfx.tick();
		level.tick();
		int i = 2;

		if (input.isKeyPressed(Input.KEY_SHIFT)) i = 4;

		if (input.isKeyPressed(Input.KEY_UP)) y -= i;
		if (input.isKeyPressed(Input.KEY_DOWN)) y += i;
		if (input.isKeyPressed(Input.KEY_LEFT)) x -= i;
		if (input.isKeyPressed(Input.KEY_RIGHT)) x += i;

		int pointX = input.getPoint()[0];
		int pointY = input.getPoint()[1];
		
		if (pointX < 32 && pointY < 32) {
			if (input.isKeyPressed(Input.KEY_UP)) volume.set(volume.get() + .05);
			if (input.isKeyPressed(Input.KEY_DOWN)) volume.set(volume.get() - .05);
		}

		pointX = (pointX / SCALE) + x >> 4;
		pointY = (pointY / SCALE) + y >> 4;

		input.wheelPos = (Tile.list.size() * 2 + input.wheelPos) % Tile.list.size();

		tile = Tile.list.get(input.wheelPos).getColor();

		if (edit && input.isKeyPressed(Input.KEY_PRESS)) {
			if ((pointX & level.wMask) != lastX || (pointY & level.hMask) != lastY) {
				level.changeTile((pointX & level.wMask), (pointY & level.hMask), tile);
				sfx.sound(127, 1);
				lastX = (pointX & level.wMask);
				lastY = (pointY & level.hMask);
			}
		}

		if (!click && input.isKeyPressed(Input.KEY_PRESS)) {
			level.changeTile((pointX & level.wMask), (pointY & level.hMask), tile);
			sfx.sound(127, 1);
			click = true;
		}
		if (click && !input.isKeyPressed(Input.KEY_PRESS)) {
			click = false;
			lastX = -1;
			lastY = -1;
		}
	}

	boolean click = false;
	boolean edit = true;
	int tile = 0;
	int x = 0, y = 0;
	int lastX, lastY;

	private void draw() {
		BufferStrategy strategy = getBufferStrategy();
		if (strategy == null) {
			createBufferStrategy(3);
			return;
		}

		// screen.clear();
		level.draw(x, y, screen);
		if (edit) {
			screen.drawSprite(x, y, Sprite.CONTAINER, 0xFFFFFFFF);
			Tile.getTile(tile).draw(x + 2, y + 2, screen);
			String s = String.valueOf(input.wheelPos);
			if (Tile.getTile(tile).getClass().equals(RandomAnimatedTile.class)) s += "A";
			for (int i = 0; i < s.length(); i++)
				screen.drawSprite(x + 3 + (i * 4), y + 12, Sprite.FONT[s.charAt(i)], 0xFFFFFFFF);
		}
		if (Arrays.equals(pixels, backPixels)) return;
		backPixels = Arrays.copyOf(pixels, pixels.length);

		Graphics g = strategy.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		g.dispose();

		strategy.show();
	}

	private void changeLevel(Level level) {
		this.level = level;
		level.init(bgm, sfx);
	}
}
