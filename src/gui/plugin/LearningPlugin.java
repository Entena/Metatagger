package gui.plugin;

import javax.swing.JPanel;

import db.DBSong;
import db.DatabaseModel;

/**
 * This interface defines all the API for all learning plugins. Any plugins
 * that are loaded will implement this interface.
 * @author pulsence
 *
 */
public interface LearningPlugin {
    
    /**
     * Gets the name of this plugin
     * @return
     */
    public String getName();
    
    public String getDescription();
    
    /**
     * This function is called once while the plugin is being loaded.
     * @param dbModel This provides a way for the plugin to interface with the
     * database.
     * @return True if everything was initialized successfully.
     */
    public boolean initialize(DatabaseModel dbModel);
    
    /**
     * This function is called once when the plugin is being shut down.
     */
    public void tearDown();
    
    /**
     * This function is called to get a panel that will be used to config this
     * plugin.
     * @return
     */
    public JPanel getConfigPanel();
    
    /**
     * The function will be given the status of the song that has just finished.
     * It is expected to then return the next song that should be played by the
     * player.
     * @param status
     * @return
     */
    public DBSong getNextSong(FinishedSongStatus status);
    
    /**
     * This function should be used to update the previous song otherwise we won't have a workable
     * song history. Plugins that don't use it won't care, so call it anyway.
     * @param song
     */
    public void setPrevSong(DBSong song);
}
