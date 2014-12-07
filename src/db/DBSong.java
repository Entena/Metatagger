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
    
    private boolean nameFlag;
    private boolean filepathFlag;
    private boolean albumFlag;
    private boolean artistFlag;
    private boolean lastPlayedFlag;
    private boolean playCountFlag;
    private boolean bpmFlag;
    
    
    public DBSong(int songId) {
        this( songId, "", false, "", false, "", false, "", false,
              0, false, 0, false, 0, false);
    }
    
    public DBSong( int songId, String name, boolean nameFlag,
                   String filepath, boolean filepathFlag,
                   String album, boolean albumFlag,
                   String artist, boolean artistFlag,
                   int lastPlayed, boolean lastPlayedFlag,
                   int playCount, boolean playCountFlag,
                   int bpm, boolean bpmFlag){
        
        this.songId = songId;
        this.name = name;
        this.filepath = filepath;
        this.album = album;
        this.artist = artist;
        this.lastPlayed = lastPlayed;
        this.playCount = playCount;
        this.bpm = bpm;
        

        this.nameFlag = nameFlag;
        this.filepathFlag = filepathFlag;
        this.albumFlag = albumFlag;
        this.artistFlag = artistFlag;
        this.lastPlayedFlag = lastPlayedFlag;
        this.playCountFlag = playCountFlag;
        this.bpmFlag = bpmFlag;
        
        metaData = new HashMap<String, DBMetaData>();
        
        songTableParams = new HashMap<String, String>();
        songTableParams.put("songid", Integer.toString(songId));
        
        songTableParams.put("name", name);
        songTableParams.put("nameflag", DatabaseHelper.booleanToString(nameFlag));
        
        songTableParams.put("filepath",  filepath);
        songTableParams.put("filepathflag", DatabaseHelper.booleanToString(filepathFlag));
        
        songTableParams.put("album",  album);
        songTableParams.put("albumflag", DatabaseHelper.booleanToString(albumFlag));
        
        songTableParams.put("artist",  artist);
        songTableParams.put("artistflag", DatabaseHelper.booleanToString(artistFlag));
        
        songTableParams.put("lastplayed", Integer.toString(lastPlayed));
        songTableParams.put("lastplayedflag", DatabaseHelper.booleanToString(lastPlayedFlag));
        
        songTableParams.put("playcount", Integer.toString(playCount));
        songTableParams.put("playcountflag", DatabaseHelper.booleanToString(playCountFlag));
        
        songTableParams.put("bpm", Integer.toString(bpm));
        songTableParams.put("bpmflag", DatabaseHelper.booleanToString(bpmFlag));
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
    
    public boolean isNameValid(){
        return nameFlag;
    }
    
    public void setName(String name){
        dirty = true;
        this.name = name;
        songTableParams.put("name", name);
        
        nameFlag = true;
        songTableParams.put("nameflag", DatabaseHelper.booleanToString(nameFlag));
    }
    
    public String getFilepath(){
        return filepath;
    }
    
    public boolean isFilepathValid(){
        return filepathFlag;
    }
    
    public void setFilepath(String filepath){
        dirty = true;
        this.filepath = filepath;
        songTableParams.put("filepath", filepath);
        
        filepathFlag = true;
        songTableParams.put("filepathflag", DatabaseHelper.booleanToString(filepathFlag));
    }
    
    public String getAlbum(){
        return album;
    }
    
    public boolean isAlbumValid(){
        return albumFlag;
    }
    
    public void setAlbum(String album){
        dirty = true;
        this.album = album;
        songTableParams.put("album", album);
        
        albumFlag = true;
        songTableParams.put("albumflag", DatabaseHelper.booleanToString(albumFlag));
    }
    
    public String getArtist(){
        return artist;
    }
    
    public boolean isArtistValid(){
        return artistFlag;
    }
    
    public void setArtist(String artist){
        dirty = true;
        this.artist = artist;
        songTableParams.put("artist", artist);
        
        artistFlag = true;
        songTableParams.put("artistflag", DatabaseHelper.booleanToString(artistFlag));
    }
    
    public int getLastPlayed(){
        return lastPlayed;
    }
    
    public boolean isLastPlayedValid(){
        return lastPlayedFlag;
    }
    
    public void setLastPlayed(int lastPlayed){
        dirty = true;
        this.lastPlayed = lastPlayed;
        songTableParams.put("lastplayed", Integer.toString(lastPlayed));
        
        lastPlayedFlag = true;
        songTableParams.put("lastplayedflag", DatabaseHelper.booleanToString(lastPlayedFlag));
    }
    
    public int getPlayCount(){
        return playCount;
    }
    
    public boolean isPlayCountValid(){
        return playCountFlag;
    }
    
    public void setPlayCount(int playCount){
        dirty = true;
        this.playCount = playCount;
        songTableParams.put("playcount", Integer.toString(playCount));
        
        playCountFlag = true;
        songTableParams.put("playcountflag", DatabaseHelper.booleanToString(playCountFlag));
    }
    
    public int getBPM(){
        return bpm;
    }
    
    public boolean isBPMValid(){
        return bpmFlag;
    }
    
    public void setBPM(int bpm){
        dirty = true;
        this.bpm = bpm;
        songTableParams.put("bpm", Integer.toString(bpm));
        
        bpmFlag = true;
        songTableParams.put("bpmflag", DatabaseHelper.booleanToString(bpmFlag));
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

    public boolean equals(Object o){
    	if(this.songId == ((DBSong)o).getSongId()){
    		return true;
    	} else {
    		return false;
    	}
    }
}
