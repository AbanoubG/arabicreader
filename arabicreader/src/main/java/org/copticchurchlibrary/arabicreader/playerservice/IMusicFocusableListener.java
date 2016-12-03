package org.copticchurchlibrary.arabicreader.playerservice;

public interface IMusicFocusableListener {
	public void onGainedAudioFocus();
	public void onLostAudioFocus(boolean canDuck);
}
