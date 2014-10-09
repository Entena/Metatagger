package gui;

/**
 * Listens for changes
 */

public interface Mp3PositionListener {
	
	/*
	 * called when the position of the song has changed
	 */
	void updateSeektime(long pos);
	
	/*
	 * called when the song has completed playing (not called 
	 * when stopped or paused)
	 */
	void songFinished();
}
