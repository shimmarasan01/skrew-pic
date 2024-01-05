package com.example.myapplication;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
public class DBHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "test.db";
    private static final String TABLE_Users = "history";
    private static final String e = "info";
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ACC = "accuracy";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_DATE = "date";
    private static final String DB_PATH = "/data/user/0/com.example.myapplication/databases/";
    private static Context mContext;
    public DBHandler(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }
    private boolean checkDatabase(){
        try{
            final String mPath = DB_PATH + DB_NAME;
            final File file = new File(mPath);
            return file.exists();
        }catch (SQLiteException e){
            e.printStackTrace();
            return false;
        }
    }
    private void copyDatabase() throws IOException {
        try {
            InputStream minputStream = mContext.getAssets().open(DB_NAME);
            String outFileName = DB_PATH + DB_NAME;
            OutputStream moutputStream = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while((length = minputStream.read(buffer))>0){
                moutputStream.write(buffer, 0, length);
            }
            moutputStream.flush();
            moutputStream.close();
            minputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void createDatabase() throws IOException{
        boolean mDatabaseExist = checkDatabase();
        if(!mDatabaseExist){
            this.getReadableDatabase();
            this.close();
            try{
                copyDatabase();
            }catch(IOException mIOException){
                mIOException.printStackTrace();
                throw new Error("Error copying Database");
            }finally{
                this.close();
            }
        }
    }
    public void loadHandler(){
        try{
            createDatabase();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db){
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }
    void insertHistory(String t, String a, String i, String d){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_TYPE, t);
        cValues.put(KEY_ACC, a);
        cValues.put(KEY_IMAGE, i);
        cValues.put(KEY_DATE, d);
        db.insert(TABLE_Users, null, cValues);
        db.close();
    }
    public ArrayList<HashMap<String, String>> getAllHistory(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        String query = "SELECT id, type, accuracy, image, date FROM " + TABLE_Users + " ORDER BY id DESC;";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("id", cursor.getString(cursor.getColumnIndex(KEY_ID)));
            user.put("type", cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
            user.put("accuracy", cursor.getString(cursor.getColumnIndex(KEY_ACC)));
            user.put("date", cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            list.add(user);
        }
        return list;
    }
    public ArrayList<HashMap<String, String>> getAllImagesFromHistory(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        String query = "SELECT id, image FROM history ORDER BY id DESC";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("image", cursor.getString(cursor.getColumnIndex("image")));
            list.add(user);
        }
        return list;
    }
    public ArrayList<HashMap<String, String>> getHistoryByID(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        String query = "SELECT id, type, accuracy, image, date FROM " + TABLE_Users + " WHERE id=" + id;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("id", cursor.getString(cursor.getColumnIndex(KEY_ID)));
            user.put("type", cursor.getString(cursor.getColumnIndex(KEY_TYPE)));
            user.put("accuracy", cursor.getString(cursor.getColumnIndex(KEY_ACC)));
            user.put("image", cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));
            user.put("date", cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            list.add(user);
        }
        return list;
    }
    public ArrayList<HashMap<String, String>> getInfoByID(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        String query = "SELECT id, type, desc, uses, image FROM " + e + " WHERE id=" + id;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("id", cursor.getString(cursor.getColumnIndex("id")));
            user.put("type", cursor.getString(cursor.getColumnIndex("type")));
            user.put("desc", cursor.getString(cursor.getColumnIndex("desc")));
            user.put("uses", cursor.getString(cursor.getColumnIndex("uses")));
            user.put("image", cursor.getString(cursor.getColumnIndex("image")));
            list.add(user);
        }
        return list;
    }
    public void deleteHistory(int userid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Users, KEY_ID + " = ?", new String[]{String.valueOf(userid)});
        db.close();
    }
}