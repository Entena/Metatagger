package db;

import java.util.HashMap;

public class DBSong {

    private HashMap<String, String> metaData;
    private HashMap<String, String> songTableParams;
    
    private boolean dirty = false;
    
    private int songId;
    private String name;
    private String filepath;
    private String album;
    private String artist;
    private int lastPlayed;
    private int playCount;
    
    
    public DBSong(int songId) {
        this(songId, "", "", "", "", 0, 0);
    }
    
    public DBSong( int songId, String name, String filepath,
                   String album, String artist, int lastPlayed,
                   int playCount){

        this.songId = songId;
        this.name = name;
        this.filepath = filepath;
        this.album = album;
        this.artist = artist;
        this.lastPlayed = lastPlayed;
        this.playCount = playCount;
        
        metaData = new HashMap<String, String>();
        
        songTableParams = new HashMap<String, String>();
        songTableParams.put("songid", Integer.toString(songId));
        songTableParams.put("name", '\'' + name + '\'');
        songTableParams.put("filepath",  '\'' + filepath + '\'');
        songTableParams.put("album",  '\'' + album + '\'');
        songTableParams.put("artist",  '\'' + artist + '\'');
        songTableParams.put("lastplayed", Integer.toString(lastPlayed));
        songTableParams.put("playcount", Integer.toString(playCount));
        songTableParams.put("songid", Integer.toString(songId));
    }
    
    public boolean isDirty(){
        return dirty;
    }
    
    protected void markClean(){
        dirty = false;
    }
    
    public HashMap<String, String> getSongParams(){
        return songTableParams;
    }
    
    public int getSongId(){
        return songId;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        dirty = true;
        this.name = name;
        songTableParams.put("name", '\'' + name + '\'');
    }
    
    public String getFilepath(){
        return filepath;
    }
    
    public void setFilepath(String filepath){
        dirty = true;
        this.filepath = filepath;
        songTableParams.put("filepath", '\'' + filepath + '\'');
    }
    
    public String getAlbum(){
        return album;
    }
    
    public void setAlbum(String album){
        dirty = true;
        this.album = album;
        songTableParams.put("album", '\'' + album + '\'');
    }
    
    public String getArtist(){
        return artist;
    }
    
    public void setArtist(String artist){
        dirty = true;
        this.artist = artist;
        songTableParams.put("artist", '\'' + artist + '\'');
    }
    
    public int getLastPlayed(){
        return lastPlayed;
    }
    
    public void setLastPlayed(int lastPlayed){
        dirty = true;
        this.lastPlayed = lastPlayed;
        songTableParams.put("lastplayed", Integer.toString(lastPlayed));
    }
    
    public int getPlayCount(){
        return playCount;
    }
    
    public void setPlayCount(int playCount){
        dirty = true;
        this.playCount = playCount;
        songTableParams.put("playcount", Integer.toString(playCount));
    }

}
