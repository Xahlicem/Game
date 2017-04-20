package com.xahlicem.game.helpers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

public class Input implements KeyListener, MouseInputListener {
	public static final int KEY_UP = 	0b000001;
	public static final int KEY_DOWN = 	0b000010;
	public static final int KEY_LEFT = 	0b000100;
	public static final int KEY_RIGHT = 0b001000;
	public static final int KEY_SHIFT = 0b010000;
	public static final int KEY_PRESS = 0b100000;

	private int keys = 0;
	private int[] point = new int[2];

	public boolean isKeyPressed(int keyCode) {
		return (keys & keyCode) == keyCode;
	}

	public int[] getPoint() {
		return point;
	}

	private void tick(int key, boolean pressed) {
		int bit = 0;
		switch (key) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				bit = KEY_UP;
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				bit = KEY_DOWN;
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				bit = KEY_LEFT;
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				bit = KEY_RIGHT;
				break;
			case KeyEvent.VK_SHIFT:
				bit = KEY_SHIFT;
				break;
			default:
				return;
		}
		if (pressed) keys |= bit;
		else keys &= ~bit;

		System.out.println(keys + " " + pressed);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		tick(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		tick(e.getKeyCode(), false);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		keys |= KEY_PRESS;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		keys &= ~KEY_PRESS;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		point[0] = e.getX();
		point[1] = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		point[0] = e.getX();
		point[1] = e.getY();
	}

}
