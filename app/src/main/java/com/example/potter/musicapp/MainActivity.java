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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import static com.example.potter.musicapp.MusicPlayer.PLAYER_PLAY;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, MusicPlayer.onComPletion {

    ListView lvSong;
    SongAdapter adapter;
    ArrayList<Song> list;
    Database db;
    SQLiteDatabase database;
    ImageView imgPlay, imgPause, imgNext, imgPrev, imgRepeat, imgShuffle;
    SeekBar sbProcess;
    TextView tvTitle, tvArtist, tvTimeprocess, tvTimeTotal;
    MusicPlayer musicPlayer;
    boolean isRunning;
    public static int UPDATE_TIME = 1;
    int position;
    int trangThai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new Database(this);
        db.addSong();

        try {
            Intent getIntent = getIntent();
            Bundle myBundle = getIntent.getBundleExtra("trangthai");
            this.trangThai = myBundle.getInt("state");
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        Log.e("TRANGTHAI", String.valueOf(trangThai));

        anhXa();

        //database = db.getWritableDatabase();
       /* Cursor cs = database.rawQuery("select * from baihat",null);
        cs.moveToFirst();
        Log.d("Count",String.valueOf(cs.getCount()));
        Toast.makeText(MainActivity.this,cs.getString(2),Toast.LENGTH_SHORT).show();
        Log.d("Count",String.valueOf(cs.getCount()));*/

        addData();

        clickListener();
    }



    private void anhXa() {
        lvSong = findViewById(R.id.LvSong);
        list = new ArrayList<>();
        adapter = new SongAdapter(MainActivity.this,list);
        lvSong.setAdapter(adapter);

        imgNext = findViewById(R.id.iv_next);
        imgPlay = findViewById(R.id.iv_play);
        imgPrev = findViewById(R.id.iv_previous);
        imgRepeat = findViewById(R.id.iv_repeat);
        imgShuffle = findViewById(R.id.iv_shuffle);

        tvArtist = findViewById(R.id.tv_artist);
        tvTitle = findViewById(R.id.tv_title);
        tvTimeprocess = findViewById(R.id.time_process);
        tvTimeTotal = findViewById(R.id.time_total);
        sbProcess = findViewById(R.id.sb_process);

        musicPlayer = new MusicPlayer();

    }


    private void addData(){
        database = db.getWritableDatabase();

        list.clear();
        Cursor cs = database.rawQuery("select * from baihat",null);

        for (int i = 0; i < cs.getCount(); i++){
            cs.moveToPosition(i);
            String name = cs.getString(2);
            String atist = cs.getString(4);
            String album = cs.getString(3);
            String path = cs.getString(1);

            list.add(new Song(name,path,album,atist));
        }

        adapter.notifyDataSetChanged();

    }

    private void clickListener() {
        lvSong.setOnItemClickListener(this);
        imgShuffle.setOnClickListener(this);
        imgRepeat.setOnClickListener(this);
        imgPlay.setOnClickListener(this);
        imgPrev.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        sbProcess.setOnSeekBarChangeListener(this);
        musicPlayer.setOnComPletion(this);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        this.position = i;

        //String path = list.get(i).getSongPath();
        Intent intent = new Intent(MainActivity.this, PlayMusic.class);

        Bundle bundle = new Bundle();
       // bundle.putString("lk",path);
        bundle.putInt("vitri",i);
        intent.putExtra("bainhac",bundle);

        if (trangThai == PLAYER_PLAY){
            musicPlayer.stop();
            stopService(intent);
        }

        startActivity(intent);

        //playMusic(i);

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

    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void endOnMusic() {

    }
}
