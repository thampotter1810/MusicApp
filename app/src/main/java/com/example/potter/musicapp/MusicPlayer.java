package com.example.potter.musicapp;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class MusicPlayer implements MediaPlayer.OnCompletionListener {

    public static final int PLAYER_IDLE = -1;
    public static final int PLAYER_PLAY = 1;
    public static final int PLAYER_PAUSE = 2;
    private int state;
    //private onCompletionListener onCompletionListener;
    private  boolean isEnd;
    onComPletion onComPletion;

    private MediaPlayer player;

    public MusicPlayer() {

    }

    public int getState() {
        return state;
    }

    public void setup(String path){
        try {
            state = PLAYER_IDLE;
            player = new MediaPlayer();
            player.setDataSource(path);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.prepare();
            player.setOnCompletionListener(this);
        } catch (IOException e){

        }
    }

    public int getTimeTotal(){
        return player.getDuration() / 1000;
    }

    public void playmusic() {
        if (state == PLAYER_IDLE || state == PLAYER_PAUSE) {
            state = PLAYER_PLAY;
            player.start();
        }
    }

    public void stop() {
        if (state == PLAYER_PLAY || state == PLAYER_PAUSE) {
            state = PLAYER_IDLE;
            player.stop();
            player.release();
            player = null;
        }
    }

    public void pause() {
        if (state == PLAYER_PLAY) {
            player.pause();
            state = PLAYER_PAUSE;
        }
    }

    public int getTimeCurrent() {
        if (state != PLAYER_IDLE) {
            return player.getCurrentPosition() / 1000;
        } else return 0;
    }

    public void seek(int time){
        player.seekTo(time);
    }


    //khi kết thúc bài hát hàm này sẽ được gọi tự động

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        onComPletion.endOnMusic();
    }

    void setOnComPletion (onComPletion onComPletion){
        this.onComPletion = onComPletion;
    }

    //interface để activity biết khi nào bài hát dừng
    public interface onComPletion{
        void endOnMusic();
    }
}
