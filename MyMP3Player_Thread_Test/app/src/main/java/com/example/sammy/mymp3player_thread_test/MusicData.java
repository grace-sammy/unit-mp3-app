package com.example.sammy.mymp3player_thread_test;

public class MusicData {

    int num;
    String albumImg;
    String singer;
    String musicTitle;

    public MusicData(int num, String albumImg, String singer, String musicTitle) {
        this.num = num;
        this.albumImg = albumImg;
        this.singer = singer;
        this.musicTitle = musicTitle;
    }

    public MusicData(String albumImg, String singer, String musicTitle) {
        this.albumImg = albumImg;
        this.singer = singer;
        this.musicTitle = musicTitle;

    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getAlbumImg() {
        return albumImg;
    }

    public void setAlbumImg(String albumImg) {
        this.albumImg = albumImg;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

}
