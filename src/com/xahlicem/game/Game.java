package com.xahlicem.game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.xahlicem.game.helpers.Screen;

public class Game extends Canvas implements Runnable {
	private static final double TPS = 30D;
	private static final double NSPT = 1_000_000_000D / TPS;
	private static final long serialVersionUID = 3929185344600372879L;

	public static final int WIDTH = 300;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static final int SCALE = 3;
	public static final String TITLE = "Game";

	private Thread thread;
	private JFrame frame;
	private boolean running;

	private Screen screen;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

	public Game() {
		Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		setPreferredSize(size);

		screen = new Screen(WIDTH, HEIGHT);

		frame = new JFrame();
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.frame.setResizable(false);
		game.frame.setTitle(TITLE);
		game.frame.add(game);
		game.frame.pack();
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);

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
				System.out.println("TPS:" + tps + ", FPS:" + fps);
				frame.setTitle(TITLE + "   |   TPS:" + tps + ", FPS:" + fps);
				tps = 0;
				fps = 0;
			}
		}

	}

	private synchronized void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}

	private synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void tick() {
		x -= 2;
		y--;
	}

	int x = 0, y = 0;

	private void draw() {
		BufferStrategy strategy = getBufferStrategy();
		if (strategy == null) {
			createBufferStrategy(3);
			return;
		}

		//screen.clear();
		screen.draw(x, y);
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i];
		}

		Graphics g = strategy.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();

		strategy.show();
	}
}
