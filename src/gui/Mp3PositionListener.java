package gui;

/**
 * Listens for changes
 */

public interface Mp3PositionListener {
	
	/*
	 * called when the position of the song has changed
	 * 
	 * @param pos position of track 0.0 - 1.0
	 */
	void updateSeektime(double pos);
	
	/*
	 * called when the song has completed playing (not called 
	 * when stopped or paused)
	 */
	void songFinished();
}
