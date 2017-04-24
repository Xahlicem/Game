package com.xahlicem.game;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.helpers.Input;
import com.xahlicem.game.level.Level;

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
		
		addKeyListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
		
		level = new Level(8, 8);
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

		while (running) {
			now = System.nanoTime();
			delta += (double) (now - lastTime) / NSPT;
			lastTime = now;
			
			if (delta < 1)
				try {
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
				System.out.println("TPS:" + tps + ", FPS:" + fps);
				frame.setTitle(TITLE + "   |   TPS:" + tps + ", FPS:" + fps);
				tps = 0;
				fps = 0;
			}
		}

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
		level.tick();
		int i = 2;
		
		if (input.isKeyPressed(Input.KEY_SHIFT)) i = 4;
		
		if (input.isKeyPressed(Input.KEY_UP)) y -= i;
		if (input.isKeyPressed(Input.KEY_DOWN)) y += i;
		if (input.isKeyPressed(Input.KEY_LEFT)) x -= i;
		if (input.isKeyPressed(Input.KEY_RIGHT)) x += i;
		
		int pointX = input.getPoint()[0];
		int pointY = input.getPoint()[1];
		pointX = (pointX / SCALE) + x >> 4;
		pointY = (pointY / SCALE) + y >> 4;
		
		if (input.isKeyPressed(Input.KEY_PRESS))System.out.println((pointX&7) + ", " + (pointY&7));
	}

	int x = 0, y = 0;

	private void draw() {
		//BufferStrategy strategy = getBufferStrategy();
		//if (strategy == null) {
		//	createBufferStrategy(3);
		//	return;
		//}

		screen.clear();
		level.draw(x, y, screen);
		//screen.draw(x, y, pixels);
		if (Arrays.equals(pixels, backPixels)) return;
		backPixels = Arrays.copyOf(pixels, pixels.length);
		//for (int i = 0; i < pixels.length; i++) {
		//	pixels[i] = screen.pixels[i];
		//}

		//Graphics g = strategy.getDrawGraphics();
		Graphics g = getGraphics();
		//g.setColor(Color.BLACK);
		//g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		g.dispose();

		//strategy.show();
	}
}
