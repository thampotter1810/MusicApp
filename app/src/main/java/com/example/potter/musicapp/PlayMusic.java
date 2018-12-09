package com.example.potter.musicapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import static com.example.potter.musicapp.MusicPlayer.PLAYER_PLAY;

public class PlayMusic extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MusicPlayer.onComPletion {


    Database db;
    SQLiteDatabase database;
    ImageView imgPlay, imgNext, imgPrev, imgRepeat, imgShuffle;
    SeekBar sbProcess;
    TextView tvTitle, tvArtist, tvTimeprocess, tvTimeTotal;
    MusicPlayer musicPlayer;
    boolean isRunning;
    public static int UPDATE_TIME = 1;
    int position;
    int vitri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        db = new Database(this);
        database = db.getWritableDatabase();


        anhXa();
        getData();

        clickListener();

    }

    private void clickListener() {
        //lvSong.setOnItemClickListener(this);
        imgShuffle.setOnClickListener(this);
        imgRepeat.setOnClickListener(this);
        imgPlay.setOnClickListener(this);
        imgPrev.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        sbProcess.setOnSeekBarChangeListener(this);
        musicPlayer.setOnComPletion(this);
    }

    private void getData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bainhac");
        int vitri = bundle.getInt("vitri");
        this.vitri = vitri;
        musicPlayer = new MusicPlayer();

        Cursor cursor = database.rawQuery("select * from baihat",null);
        cursor.moveToPosition(vitri);
        playMusic(vitri,cursor);

        //Log.e("POSSSSS", String.valueOf(position));
    }

    private void anhXa() {
        imgPlay = findViewById(R.id.img_play);
        imgNext = findViewById(R.id.img_next);
        imgPrev = findViewById(R.id.img_prev);
        imgRepeat = findViewById(R.id.img_repeat);
        imgShuffle = findViewById(R.id.img_shuffle);

        sbProcess = findViewById(R.id.Sb_process1);
        tvTitle = findViewById(R.id.TvTitle);
        tvArtist = findViewById(R.id.TvTitle);
        tvTimeTotal = findViewById(R.id.Time_total);
        tvTimeprocess = findViewById(R.id.Time_process);
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_TIME){
                tvTimeprocess.setText(getTimeFormat(musicPlayer.getTimeCurrent()));
                sbProcess.setProgress(musicPlayer.getTimeCurrent());
            }
        }
    };



    public void playMusic(int i, Cursor cursor){

        database = db.getWritableDatabase();
        this.position = cursor.getPosition();
        Log.e("AYTUUS", String.valueOf(position));
        String path = cursor.getString(1);
        if (musicPlayer.getState() == PLAYER_PLAY){
            musicPlayer.stop();
        }
        Log.e("vi tri", String.valueOf(position));

        musicPlayer.setup(path);
        musicPlayer.playmusic();
        imgPlay.setImageResource(R.drawable.pause);
        //set tên bài hát, tên ca sĩ lên giao diện
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        tvTitle.setText(title);
        tvArtist.setText(artist);

        //setup thời gian
        isRunning = true;
        tvTimeTotal.setText(getTimeFormat(musicPlayer.getTimeTotal()));

        //setup lên seekbar
        sbProcess.setMax(musicPlayer.getTimeTotal());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning){
                    Message message = new Message();
                    message.what = UPDATE_TIME;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        //setup sự kiện lên seekbar
    }

    private String getTimeFormat(long time) {
        String tm = "";
        int h, s, m;
       /* String tmp = "";


        //chuyển thời gian sang đúng định dạng

        int s = (int) ((time % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        int m = (int) (time % (1000 * 60 * 60));
        int h = (int) (time / (1000 * 60 * 60));

        if (h > 0){
            tm = h + ":";
        }
        //thêm vào số 0 nếu có 1 chữ số;
        if (s < 10){
            s =
        }*/
        //giây
        s = (int) (time % 60);
        m = (int) ((time - s) / 60);
        if (m >= 60) {
            h = m / 60;
            m = m % 60;
            if (h > 0) {
                if (h < 10) {
                    tm += "0" + h + ":";
                } else
                    tm += h + ":";
            }
        }

        if (m < 10) {
            tm += "0" + m + ":";
        } else
            tm += m + ":";
        if(s < 10){
            tm += "0"+s;
        }else{
            tm += s;
        }
        return tm;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_next:
                nextMusic();
                Log.e("vi tri", String.valueOf(position));
                break;

            case R.id.img_prev:
                prevMusic();
                break;
            case R.id.img_play:
                pauseMusic();
                break;
            case R.id.img_shuffle:
                sufleMusic();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (musicPlayer.getTimeCurrent() != i && musicPlayer.getTimeCurrent() != 0){
            musicPlayer.seek(sbProcess.getProgress() * 1000);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void endOnMusic() {
        database = db.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from baihat",null);
        position++;
        if (position >= cursor.getCount()){
            position = 0;
        }
        cursor.moveToPosition(position);
        String path = cursor.getString(1);
        playMusic(position,cursor);
    }

    private void sufleMusic() {
        Random random = new Random();
        database = db.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from baihat",null);
        int x = random.nextInt(cursor.getCount());
        position = x;
        cursor.moveToPosition(position);
        String path = cursor.getString(1);
        playMusic(position,cursor);
    }

    private void pauseMusic() {
        if (musicPlayer.getState() == PLAYER_PLAY){
            musicPlayer.pause();
            imgPlay.setImageResource(R.drawable.play);
        }else {
            musicPlayer.playmusic();
            imgPlay.setImageResource(R.drawable.pause);
        }

    }

    public void nextMusic(){
        database = db.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from baihat",null);
        position++;
        if (position >= cursor.getCount()){

            position = 0;
        }
        cursor.moveToPosition(position);
        String path = cursor.getString(1);
        playMusic(cursor.getPosition(),cursor);
    }

    public void prevMusic(){
        database = db.getWritableDatabase();
        Cursor cursor = database.rawQuery("select * from baihat",null);

        position--;
        if (position < 0){
            position = cursor.getCount() - 1;
        }

        cursor.moveToPosition(position);
        String path = cursor.getString(1);
        playMusic(position,cursor);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlayMusic.this, MainActivity.class);

        Bundle bundle = new Bundle();
        // bundle.putString("lk",path);
        bundle.putInt("state",musicPlayer.getState());
        intent.putExtra("trangthai",bundle);
        startActivity(intent);
        super.onBackPressed();
    }
}
