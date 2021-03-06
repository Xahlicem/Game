package com.xahlicem.game.helpers.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

public class Volume {
	Mixer mixer;
	Port line;
	FloatControl volCtrl;

	public Volume() {}

	private void init() {
		for (Mixer.Info m : AudioSystem.getMixerInfo()) {
			if (m.getName().contains("Port Speakers") || m.getName().contains("Port Headphones")) {
				mixer = AudioSystem.getMixer(m);
				break;
			}
		}
		setLine();
	}

	private void setLine() {
		boolean speaker = false;
		try {
			line = (Port) mixer.getLine(Port.Info.SPEAKER);
			speaker = true;
		} catch (Exception tryHeadphones) {}
		if (!speaker) try {
			line = (Port) mixer.getLine(Port.Info.HEADPHONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void set(double value) {
		try {
			init();
			line.open();
			// Assuming getControl call succeeds,
			// we now have our LINE_IN VOLUME control.
			volCtrl = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		if (value > 1.0) value = 1;
		if (value < 0.0) value = 0;
		volCtrl.setValue((float) value);
	}

	public float get() {
		return volCtrl.getValue();
	}

}
