UPDATE Songs
SET NAME = '*name*', NAME_FLAG = *nameflag*,
    FILEPATH = '*filepath*', FILEPATH_FLAG = *filepathflag*,
    ALBUM = '*album*', ALBUM_FLAG = *albumflag*,
    ARTIST = '*artist*', ARTIST_FLAG = *artistflag*,
    LAST_PLAYED = *lastplayed*, LAST_PLAYED_FLAG = *lastplayedflag*,
    PLAY_COUNT = *playcount*, PLAY_COUNT_FLAG = *playcountflag*,
    BPM = *bpm*, BPM_FLAG = *bpmflag*
WHERE SONG_ID = *songid*;