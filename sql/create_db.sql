CREATE TABLE Songs
(SONG_ID          INTEGER PRIMARY KEY   AUTOINCREMENT,
 NAME             CHARACTER(50)         NOT NULL,
 NAME_FLAG        BOOLEAN               DEFAULT 0,
 FILEPATH         TEXT                  NOT NULL,
 FILEPATH_FLAG    BOOLEAN               DEFAULT 0,
 ALBUM            CHARACTER(50),
 ALBUM_FLAG       BOOLEAN               DEFAULT 0,
 ARTIST           CHARACTER(50),
 ARTIST_FLAG      BOOLEAN               DEFAULT 0,
 LAST_PLAYED      INTEGER               DEFAULT 0,
 LAST_PLAYED_FLAG BOOLEAN               DEFAULT 0,
 PLAY_COUNT       INTEGER               DEFAULT 0,
 PLAY_COUNT_FLAG  BOOLEAN               DEFAULT 0,
 BPM              INTEGER               DEFAULT 0,
 BPM_FLAG         BOOLEAN               DEFAULT 0);
 
 CREATE TABLE MetaData
(META_ID               CHARACTER(20)               NOT NULL,
 FOREIGN KEY(SONG_ID)  REFERENCES Songs(SONG_ID)   NOT NULL,
 META_VALUE            TEXT                        NOT NULL,
 PRIMARY KEY (META_ID, SONG_ID));