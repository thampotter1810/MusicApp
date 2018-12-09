package com.example.potter.musicapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "dsbaihat";

    public static final String TABLE_NAME = "baihat";
    public static final String ID = "id";
    public static final String SONG_PATH = "duongdan";
    public static final String SONG_NAME = "tenbai";
    public static final String SONG_ALBUM = "album";
    public static final String SONG_ARTIST = "casi";
    public ArrayList<String> paths;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
//        SQLiteDatabase database = this.getWritableDatabase();

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String create = "create table "+TABLE_NAME+" ("+ID+" integer primary key autoincrement, "+SONG_PATH+" text, "+SONG_NAME+" text, "+SONG_ALBUM+" text, "+ SONG_ARTIST +" text)";
        sqLiteDatabase.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //tìm nhạc trong CSDL
    public ArrayList<Song> findData(){
        ArrayList<Song> list = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            list.add(new Song(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
            cursor.moveToNext();
        }
        return list;
    }

    public void addSong(){
        SQLiteDatabase database = this.getWritableDatabase();

        boolean rs = true;

        ContentValues values = new ContentValues();
        //đọc danh sách nhạc từ thẻ nhớ. cụ thể là trong thư mục Zing MP3. mặc định là Download
        //lấy hết đường dẫn vào trong CSDL
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
        Log.e("Path", path);
        File file = new File(path);

        Log.d("err", path);

        //lấy tất cả các file trong thư mục.

        File[] files = file.listFiles();

        paths = new ArrayList<>();

        for (int i = 0; i < files.length; i++){
            //đọc tất cả các file trong thư mục và thêm vào CSDL
            String name = files[i].getName();

            if (name.endsWith(".mp3")){
                paths.add(files[i].getAbsolutePath());

            }
        }

        for (int i = 0; i < paths.size();i++){
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();

            mmr.setDataSource(paths.get(i));
            Log.e("name",files[i].getAbsolutePath());
            String songName = files[i].getName();
            String pathFile = files[i].getPath();


            Log.e("ádddddddđsđa",songName);
            String songAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String songArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            //thêm vào cơ sở dữ liệu
            //dùng contenvalues để lưu các giá trị vào trong bảng.
            Cursor cs = database.rawQuery("Select * from "+TABLE_NAME,null);
            cs.moveToFirst();

            if (cs.getCount() == 0){
                values.put(SONG_PATH,pathFile);
                values.put(SONG_NAME,songName);
                values.put(SONG_ALBUM,songAlbum);
                values.put(SONG_ARTIST,songArtist);

                database.insert(TABLE_NAME,null,values);

            }else {
                int a= 0;
                //Log.e("BBBBBBB", String.valueOf(pathFile.equals(cs.getString(1))));
                while (cs.isAfterLast() == false){
                    if (pathFile.equals(cs.getString(1))){
                        a++;
                       // Log.e("BBBBBBB", String.valueOf(a));
                    }
                    cs.moveToNext();
                }

                if (a == 0){
                    values.put(SONG_PATH,pathFile);
                    values.put(SONG_NAME,songName);
                    values.put(SONG_ALBUM,songAlbum);
                    values.put(SONG_ARTIST,songArtist);

                    database.insert(TABLE_NAME,null,values);
                    Log.e("da them", String.valueOf(cs.getCount()));
                }
            }


            /*int a = 1;
            while (cs.isAfterLast()){
                if (pathFile == cs.getString(1)) {
                    a = 1;
                    break;
                }else{
                    a = 0;
                }
                cs.moveToNext();
            }
            Log.e("AAAAAAAAAAAAAA",a+"");
            if (a == 0){
                values.put(SONG_PATH,pathFile);
                values.put(SONG_NAME,songName);
                values.put(SONG_ALBUM,songAlbum);
                values.put(SONG_ARTIST,songArtist);

                database.insert(TABLE_NAME,null,values);
                Log.e("da them", String.valueOf(cs.getCount()));
            }*/

        }


    }

}
