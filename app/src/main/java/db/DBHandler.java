package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import model.MoveData;
import model.TinyUser;

public class DBHandler extends SQLiteOpenHelper {
    private Context context;
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "db_one_system";
    private static final String ATTENDANCES = "attendances";

    /*ATTENDANCES COLUMNS NAME*/
    private static final String ID="id"; //Primary key,auto increment
    private static final String DATE = "date";
    private static final String IN_LAT = "in_lat";
    private static final String IN_LNG = "in_lng";
    private static final String IN_AREA = "in_area";

    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_ATTENDANCES_TAB = "CREATE TABLE "
                + DBHandler.ATTENDANCES
                + "("
                + DBHandler.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DBHandler.DATE + " TEXT, "
                + DBHandler.IN_LAT +" TEXT, "
                + DBHandler.IN_LNG + " TEXT, "
                + DBHandler.IN_AREA +" TEXT "
                + ")";

            db.execSQL(CREATE_ATTENDANCES_TAB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* Drop older table if existed */
        db.execSQL("DROP TABLE IF EXISTS "+ATTENDANCES);
        /*Create Table again */
        onCreate(db);

    }

    public DBHandler(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context =context;
    }


    public void addAttendance(MoveData location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHandler.DATE, location.getVisitDateTime());
        values.put(DBHandler.IN_LAT, location.getLatitude());
        values.put(DBHandler.IN_LNG, location.getLongitude());
        values.put(DBHandler.IN_AREA, location.getAddress());
        db.insert(DBHandler.ATTENDANCES, null, values);
        db.close();
    }

    public List<MoveData> getAllAttendance() {
        List<MoveData> groupList = new ArrayList<MoveData>();
        String selectQuery = "SELECT  * FROM " + DBHandler.ATTENDANCES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MoveData location = new MoveData();
                location.setVisitDateTime(cursor.getString(cursor.getColumnIndex(DBHandler.DATE)));
                location.setLatitude(cursor.getString(cursor.getColumnIndex(DBHandler.IN_LAT)));
                location.setLongitude(cursor.getString(cursor.getColumnIndex(DBHandler.IN_LNG)));
                location.setAddress(cursor.getString(cursor.getColumnIndex(DBHandler.IN_AREA)));
                // Adding group to list
                groupList.add(location);
            } while (cursor.moveToNext());
        }
        // return note list
        return groupList;
    }


    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_NAME,null,null);
        //db.execSQL("delete * from"+ TABLE_NAME);
        //db.execSQL("delete from " + DBHandler.ATTENDANCES);
        db.delete(DBHandler.ATTENDANCES,null,null);
        db.close();
    }



}