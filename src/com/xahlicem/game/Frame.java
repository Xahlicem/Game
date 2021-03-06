package com.xahlicem.game;

import javax.swing.JFrame;

import com.xahlicem.game.graphics.Screen;
import com.xahlicem.game.helpers.Input;

public class Frame extends JFrame {
	private static final long serialVersionUID = -5902218571111718013L;

	private Screen screen;
	private Input input;

	public Frame(Screen screen, Input input) {
		this.screen = screen;
		this.input = input;
	}

	public void init() {
		setResizable(false);
		setTitle(Game.TITLE);
		add(screen);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);

		screen.addKeyListener(input);
		screen.addMouseListener(input);
		screen.addMouseWheelListener(input);
		screen.addMouseMotionListener(input);
	}
}
