package db;

import java.util.Collection;
import java.util.HashMap;

public class DBSong {

    private HashMap<String, DBMetaData> metaData;
    private HashMap<String, String> songTableParams;
    
    private boolean dirty = false;
    
    private int songId;
    private String name;
    private String filepath;
    private String album;
    private String artist;
    private int lastPlayed;
    private int playCount;
    private int bpm;
    
    
    public DBSong(int songId) {
        this(songId, "", "", "", "", 0, 0, 0);
    }
    
    public DBSong( int songId, String name, String filepath,
                   String album, String artist, int lastPlayed,
                   int playCount, int bpm){

        this.songId = songId;
        this.name = name;
        this.filepath = filepath;
        this.album = album;
        this.artist = artist;
        this.lastPlayed = lastPlayed;
        this.playCount = playCount;
        this.bpm = bpm;
        
        metaData = new HashMap<String, DBMetaData>();
        
        songTableParams = new HashMap<String, String>();
        songTableParams.put("songid", Integer.toString(songId));
        songTableParams.put("name", name);
        songTableParams.put("filepath",  filepath);
        songTableParams.put("album",  album);
        songTableParams.put("artist",  artist);
        songTableParams.put("lastplayed", Integer.toString(lastPlayed));
        songTableParams.put("playcount", Integer.toString(playCount));
        songTableParams.put("songid", Integer.toString(songId));
        songTableParams.put("bpm", Integer.toString(bpm));
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
        songTableParams.put("name", name);
    }
    
    public String getFilepath(){
        return filepath;
    }
    
    public void setFilepath(String filepath){
        dirty = true;
        this.filepath = filepath;
        songTableParams.put("filepath", filepath);
    }
    
    public String getAlbum(){
        return album;
    }
    
    public void setAlbum(String album){
        dirty = true;
        this.album = album;
        songTableParams.put("album", album);
    }
    
    public String getArtist(){
        return artist;
    }
    
    public void setArtist(String artist){
        dirty = true;
        this.artist = artist;
        songTableParams.put("artist", artist);
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
    
    public int getBPM(){
        return bpm;
    }
    
    public void setBPM(int bpm){
        dirty = true;
        this.bpm = bpm;
        songTableParams.put("bpm", Integer.toString(bpm));
    }
    
    /**
     * Sets a meta data field for this song. If the song does not have the field
     * it is created, else the field is updated.
     * @param key
     * @param value
     */
    public void setMetaDataField(String key, String value){
        dirty = true;
        if(metaData.containsKey(key)){
            metaData.get(key).setValue(value);
        } else {
            metaData.put(key, new DBMetaData(key, value, songId));
        }
    }
    
    /**
     * Gets the value of the meta data field. If the field is not specified an
     * empty string is returned.
     * @param key
     * @return
     */
    public String getMetaDataField(String key){
        DBMetaData value = metaData.get(key);
        if(value == null)
            return "";
        return value.getValue();
    }
    
    public boolean containsMetaDataField(String key){
        return metaData.containsKey(key);
    }
    
    protected Collection<DBMetaData> getDBMetaData(){
        return metaData.values();
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append('\n');
        sb.append("Filepath: ").append(filepath).append('\n');
        sb.append("Artist: ").append(artist).append('\n');
        sb.append("Play Count: ").append(playCount).append('\n');
        sb.append("BPM: ").append(bpm);
        return sb.toString();
    }
    
    protected class DBMetaData{
        private boolean dirty;
        private boolean newField;
        private String key;
        private String value;
        private HashMap<String, String> metaDataParams;
        
        public DBMetaData(String key, String value, int songId){
            this(true, key, value, songId);
        }
        
        public DBMetaData( boolean newField, String key, String value,
                           int songId){
            this.newField = newField;
            dirty = false;
            this.key = key;
            this.value = value;
            
            metaDataParams = new HashMap<String, String>();
            metaDataParams.put("songid", Integer.toString(songId));
            metaDataParams.put("key", key);
            metaDataParams.put("value",  value);
        }
        
        public void markClean(){
            dirty = false;
        }
        
        public boolean isDirty(){
            return dirty;
        }
        
        public boolean isNewField(){
            return newField;
        }
        
        public void markNotNew(){
            newField = false;
            dirty = false;
        }
        
        public String getValue(){
            return value;
        }
        
        public void setValue(String value){
            dirty = true;
            this.value = value;
            metaDataParams.put("value",  value);
        }
        
        public String getKey(){
            return key;
        }
        
        public HashMap<String, String> getMetaDataParams(){
            return metaDataParams;
        }
    }

}
