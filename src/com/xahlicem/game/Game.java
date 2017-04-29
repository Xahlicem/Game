package com.xahlicem.game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JOptionPane;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.graphics.Sprite;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.helpers.audio.BGMPlayer;
import com.xahlicem.game.helpers.audio.SFXPlayer;
import com.xahlicem.game.helpers.audio.Volume;
import com.xahlicem.game.helpers.net.Client;
import com.xahlicem.game.helpers.net.Server;
import com.xahlicem.game.helpers.net.packet.PacketLevelReq;
import com.xahlicem.game.helpers.net.packet.PacketLogin;
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
	private int ticks = 0;
	private Frame frame;
	private boolean running;
	private Input input;
	private BGMPlayer bgm;
	private SFXPlayer sfx;
	private Volume volume;
	private Screen screen;
	private Level level;

	private Client client;
	private Server server;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private int[] backPixels = new int[pixels.length];

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

		changeLevel(Level.TITLE);

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
				// draw = false;
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

		String name = JOptionPane.showInputDialog("Please enter name");
		String ip = "localhost";
		if (JOptionPane.showConfirmDialog(this, "Do you want to host?") == 0) {
			server = new Server(this);
			server.start();
		} // else ip = JOptionPane.showInputDialog("Please enter server IP");
		client = new Client(this, ip);
		client.start();

		new PacketLogin(name).writeData(client);

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
		input.tick();
		sfx.tick();
		level.tick();
		// if ((ticks % 600) == 0)client.sendData("*PING");
		// if ((ticks % 6000) == 0)client.requestLevel();
		int i = 2;

		if (input.isKeyPressed(Input.KEY_SHIFT)) i = 4;

		if (input.isKeyPressed(Input.KEY_UP)) y -= i;
		if (input.isKeyPressed(Input.KEY_DOWN)) y += i;
		if (input.isKeyPressed(Input.KEY_LEFT)) x -= i;
		if (input.isKeyPressed(Input.KEY_RIGHT)) x += i;

		int pointX = input.getPoint()[0];
		int pointY = input.getPoint()[1];

		boolean corner = (pointX < 74 && pointY < 74);
		if (corner) volume.set(volume.get() + .05 * input.getWheel());
		else if (input.getWheel() != 0) {
			if (level.lighted()) color = (Tile.list.size() * 2 + color + input.getWheel()) % Tile.list.size();
			else color = (Level.LIGHTS.length * 2 + color + input.getWheel()) % Level.LIGHTS.length;
		}

		pointX = (pointX / SCALE) + x >> 4;
		pointY = (pointY / SCALE) + y >> 4;
		xPos = (pointX & level.wMask);
		yPos = (pointY & level.hMask);

		if (edit && input.isKeyPressed(Input.KEY_PRESS)) {
			if ((pointX & level.wMask) != lastX || (pointY & level.hMask) != lastY) {
				if (corner) {
					level.toggleLight();
					color = 0;
				} else {
					if (level.lighted()) {
						level.changeTile(xPos, yPos, Tile.list.get(color).getColor(), input.isKeyPressed(Input.KEY_SHIFT));
					} else {
						level.changeLight(xPos, yPos, Level.LIGHTS[color]);
					}
				}

				level.getPacket().writeData(client);
				lastX = (pointX & level.wMask);
				lastY = (pointY & level.hMask);
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

		if (input.isKeyPressed(Input.KEY_ESC)) {
			level.save("save");
		}
		ticks++;
	}

	boolean click = false;
	boolean edit = true;
	int color = 0;
	int x = 0, y = 0;
	int xPos, yPos;
	int lastX = -1, lastY = -1;

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
			String s = String.valueOf(color);
			if (level.lighted()) {
				Tile.list.get(color).draw(x + 2, y + 2, screen);
				if (Tile.list.get(color).getClass().equals(RandomAnimatedTile.class)) s += "A";
			} else {
				level.getTile(xPos, yPos).draw(x + 2, y + 2, screen, Level.LIGHTS[color]);
			}
			for (int i = 0; i < s.length(); i++)
				screen.drawSprite(x + 3 + (i * 4), y + 12, Sprite.FONT[s.charAt(i)], 0xFFFFFFFF);
		}
		// if (Arrays.equals(pixels, backPixels)) return;
		// System.arraycopy(pixels, 0, backPixels, 0, pixels.length);

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
		if (server == null) new PacketLevelReq().writeData(client);
	}

	public Level getLevel() {
		return level;
	}
}
