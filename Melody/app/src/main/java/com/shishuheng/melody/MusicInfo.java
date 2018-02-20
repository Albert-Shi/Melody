package com.shishuheng.melody;

/**
 * Created by 史书恒 on 2016/9/9.
 */

public class MusicInfo {
    protected String songName;
    protected String artist;
    protected String albumName;
    protected String url;
    protected String picUrl;
    protected String songId;
    protected String artistId;
    protected String lyricUrl;
    protected String lyricText;
    protected String lyricText_trans;
    public MusicInfo(String songName, String artist, String albumName, String url) {
        this.songName = songName;
        this.artist = artist;
        this.albumName = albumName;
        this.url = url;
    }

    public MusicInfo(){};

    public String getSongName() {
        return songName;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getUrl() {
        return url;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getSongId() {
        return songId;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getLyricUrl() {
        return lyricUrl;
    }

    public String getLyricText() {
        return lyricText;
    }

    public String getLyricText_trans() {
        return lyricText_trans;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public void setLyricUrl(String lyricUrl) {
        this.lyricUrl = lyricUrl;
    }

    public void setLyricText(String lyricText) {
        this.lyricText = lyricText;
    }

    public void setLyricText_trans(String lyricText_trans) {
        this.lyricText_trans = lyricText_trans;
    }
}
