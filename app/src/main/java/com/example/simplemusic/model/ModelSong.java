package com.example.simplemusic.model;

public class ModelSong {
    String title,album,artist;

    public ModelSong(String title, String artist) {
        this.title=title;
        this.artist=artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
