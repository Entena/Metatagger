UPDATE Songs
SET NAME = *name*, FILEPATH = *filepath*, ALBUM = *album*, ARTIST = *artist*,
    LAST_PLAYED = *lastplayed*, PLAY_COUNT = *playcount*, BPM = *bpm*
WHERE SONG_ID = *songid*;