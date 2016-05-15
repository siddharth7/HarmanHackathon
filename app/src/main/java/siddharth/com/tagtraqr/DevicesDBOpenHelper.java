package siddharth.com.tagtraqr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DevicesDBOpenHelper extends SQLiteOpenHelper {

    private static final String LOGTAG = "DEVICES";
    private static final String DATABASE_NAME = "devices.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_DEVICES = "devices";
    public static final String COLUMN_ID = "devicesId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_MAC = "mac";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_LATI = "latitude";
    public static final String COLUMN_LONGI = "longitude";



    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_DEVICES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_MAC + " TEXT, " +
                    COLUMN_LATI + " TEXT, " +
                    COLUMN_LONGI + " TEXT, " +
                    COLUMN_IMAGE + " BLOB );";

    public DevicesDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        Log.i(LOGTAG, "Table has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICES);
        onCreate(db);
    }
    public List<String> getAllNames(){
        List<String> names = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DEVICES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                names.add(cursor.getString(1));

            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();
        // returning names
        return names;
    }
    public List<Double> getAllLatitudes(){
        List<Double> Lati = new ArrayList<Double>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DEVICES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Lati.add(cursor.getDouble(3));

            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();
        // returning names
        return Lati;
    }
    public List<Double> getAllLongitudes(){
        List<Double> Longi = new ArrayList<Double>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DEVICES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Longi.add(cursor.getDouble(4));

            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();
        // returning names
        return Longi;
    }
}
