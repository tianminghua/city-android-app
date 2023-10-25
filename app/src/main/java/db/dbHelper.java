package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class dbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CityApp.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_CITIES = "cities";
    public static final String COLUMN_CITY_ID = "_id";
    public static final String COLUMN_CITY_NAME = "city_name";
    public static final String COLUMN_CITY_USER_ID = "user_id";

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL);";
        db.execSQL(createUserTable);

        String createCityTable = "CREATE TABLE " + TABLE_CITIES + " (" +
                COLUMN_CITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                COLUMN_CITY_USER_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_CITY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "));";
        db.execSQL(createCityTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    //check username
    public long ensureUserExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_USER_ID);
            if (columnIndex != -1) {
                return cursor.getLong(columnIndex);
            } else {
                return -1;
            }
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_PASSWORD, "123");
            return db.insert(TABLE_USERS, null, values);
        }
    }

    // Get user's citylist by username
    public List<String> getCityListForUser(long userId) {
        List<String> cityList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_CITY_NAME};
        String selection = COLUMN_CITY_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_CITIES, columns, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_CITY_NAME);
            if (columnIndex != -1) {
                String cityName = cursor.getString(columnIndex);
                cityList.add(cityName);
            } else {
                return cityList; //
            }
        }
        cursor.close();
        return cityList;
    }

    // add city
    public void addCityForUser(long userId, String cityName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY_NAME, cityName);
        values.put(COLUMN_CITY_USER_ID, userId);
        db.insert(TABLE_CITIES, null, values);
    }

}

