package com.example.potter.musicapp;

public class Song {

    private String songName, songPath, songAlbum, songArtits;

    public Song(String songName, String songPath, String songAlbum, String songArtits) {
        this.songName = songName;
        this.songPath = songPath;
        this.songAlbum = songAlbum;
        this.songArtits = songArtits;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getSongAlbum() {
        return songAlbum;
    }

    public void setSongAlbum(String songAlbum) {
        this.songAlbum = songAlbum;
    }

    public String getSongArtits() {
        return songArtits;
    }

    public void setSongArtits(String songArtits) {
        this.songArtits = songArtits;
    }
}
