package com.xahlicem.game;

import javax.swing.JFrame;

public class Frame extends JFrame {
	private static final long serialVersionUID = -5902218571111718013L;
	
	private Game game;
	
	public Frame(Game game) {
		this.game = game;
	}

	public void init() {
		setResizable(false);
		setTitle(Game.TITLE);
		add(game);
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	@Override
	public void dispose() {
		game.stop();
		super.dispose();
	}
}
