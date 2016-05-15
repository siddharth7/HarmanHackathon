package siddharth.com.tagtraqr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohan Tiwari on 08-10-2015.
 */
public class DeviceDBDataSource {
    SQLiteOpenHelper dbhelper;
    SQLiteDatabase database;
    private static final String LOGTAG = "DEVICES";

    private static final String[] allColumns = {
            siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_ID,
            siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_NAME,
            siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_MAC,
            siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_IMAGE,
            siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_LATI,
            siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_LONGI
    };

    public DeviceDBDataSource(Context context){

        dbhelper = new siddharth.com.tagtraqr.DevicesDBOpenHelper(context);
    }
    public void open() {
        Log.i(LOGTAG,"DataBase Open");
        database = dbhelper.getWritableDatabase();
    }
    public void close() {
        Log.i(LOGTAG,"DataBase Closed");
        dbhelper.close();
    }

    public siddharth.com.tagtraqr.Devices create(Devices device){
        ContentValues values = new ContentValues();
        values.put(siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_NAME,device.getName());
        values.put(siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_MAC, device.getMac());
        values.put(siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_IMAGE,device.getImage());
        values.put(siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_LATI,device.getLati());
        values.put(siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_LONGI, device.getLongi());

        long insertid = database.insert(siddharth.com.tagtraqr.DevicesDBOpenHelper.TABLE_DEVICES,null,values);

        device.setId(insertid);

        return device;
    }

    public List<siddharth.com.tagtraqr.Devices> findAll(){
        List<Devices> tags = new ArrayList<siddharth.com.tagtraqr.Devices>();

        Cursor cursor = database.query(siddharth.com.tagtraqr.DevicesDBOpenHelper.TABLE_DEVICES,allColumns,null,null,null,null,null);

        Log.i(LOGTAG," Returned " + cursor.getCount() + " rows");
        if(cursor.getCount() > 0)
        {
            while (cursor.moveToNext()){
                siddharth.com.tagtraqr.Devices device = new Devices();
                device.setId(cursor.getLong(cursor.getColumnIndex(siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_ID)));
                device.setName(cursor.getString(cursor.getColumnIndex(siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_NAME)));
                device.setMac(cursor.getString(cursor.getColumnIndex(siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_MAC)));
                device.setLati(cursor.getDouble(cursor.getColumnIndex(siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_LATI)));
                device.setLongi(cursor.getDouble(cursor.getColumnIndex(siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_LONGI)));
                device.setImage(cursor.getBlob(cursor.getColumnIndex(siddharth.com.tagtraqr.DevicesDBOpenHelper.COLUMN_IMAGE)));
                tags.add(device);
            }
        }
        return tags;
    }

}
