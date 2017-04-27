package com.xahlicem.game.helpers.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

public class Volume {
	Mixer mixer;
	Port lineIn;
	FloatControl volCtrl;

	public Volume() {
		try {
			for (Mixer.Info m : AudioSystem.getMixerInfo()) {
				if (m.getName().contains("Speakers")) mixer = AudioSystem.getMixer(m);
			}
			lineIn = (Port) mixer.getLine(Port.Info.SPEAKER);

			lineIn.open();
			// Assuming getControl call succeeds,
			// we now have our LINE_IN VOLUME control.
			volCtrl = (FloatControl) lineIn.getControl(FloatControl.Type.VOLUME);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void set(double value) {
		if (value > 1) value = 1;
		volCtrl.setValue((float) value);

	}

	public float get() {
		return volCtrl.getValue();
	}

}
