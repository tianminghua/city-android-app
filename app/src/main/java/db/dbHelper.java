package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    public static final String COLUMN_CITY_KEY = "city_key";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";

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
                COLUMN_CITY_KEY + " TEXT NOT NULL, " +
                COLUMN_LONGITUDE + " REAL NOT NULL, " +
                COLUMN_LATITUDE + " REAL NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_CITY_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "));";
        db.execSQL(createCityTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("ALTER TABLE " + TABLE_CITIES + " ADD COLUMN " + COLUMN_CITY_KEY + " TEXT");
        db.execSQL("ALTER TABLE " + TABLE_CITIES + " ADD COLUMN " + COLUMN_LONGITUDE + " REAL");
        db.execSQL("ALTER TABLE " + TABLE_CITIES + " ADD COLUMN " + COLUMN_LATITUDE + " REAL");
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
    public boolean addCityForUser(long userId, String cityName, String cityKey, double longitude, double latitude) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY_NAME, cityName);
        values.put(COLUMN_CITY_USER_ID, userId);
        values.put(COLUMN_CITY_KEY, cityKey);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_LATITUDE, latitude);
        long result = db.insert(TABLE_CITIES, null, values);
        return result != -1;
    }


    // delete city
    public void deleteCityForUser(long userId, String cityName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_CITY_USER_ID + " = ? AND " + COLUMN_CITY_NAME + " = ?";
        String[] whereArgs = new String[] { String.valueOf(userId), cityName };
        db.delete(TABLE_CITIES, whereClause, whereArgs);
    }

    public void updateUserCityKey(long userId, String cityKey) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, cityKey);
        String whereClause = COLUMN_USER_ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(userId) };
        db.update(TABLE_USERS, values, whereClause, whereArgs);
    }

    // get city location key
    public String getLocationKeyForCity(long userId, String cityName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_CITY_KEY};
        String selection = COLUMN_CITY_USER_ID + " = ? AND " + COLUMN_CITY_NAME + " = ?";
        String[] selectionArgs = {String.valueOf(userId), cityName};
        Cursor cursor = db.query(TABLE_CITIES, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_CITY_KEY);
            if (columnIndex != -1) {
                String cityKey = cursor.getString(columnIndex);
                cursor.close();
                Log.d("DBHelper", "Found city key: " + cityKey + " for user: " + userId + " and city: " + cityName);
                return cityKey;
            } else {
                Log.d("DBHelper", "Column index for city key not found");
            }
        } else {
            Log.d("DBHelper", "No entry found for user: " + userId + " and city: " + cityName);
        }
        cursor.close();
        return null;
    }

    // get city lon and lat
    public double[] getCityLocation(long userId, String cityName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_LONGITUDE, COLUMN_LATITUDE};
        String selection = COLUMN_CITY_USER_ID + " = ? AND " + COLUMN_CITY_NAME + " = ?";
        String[] selectionArgs = {String.valueOf(userId), cityName};
        Cursor cursor = db.query(TABLE_CITIES, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int longitudeIndex = cursor.getColumnIndex(COLUMN_LONGITUDE);
            int latitudeIndex = cursor.getColumnIndex(COLUMN_LATITUDE);

            if (longitudeIndex != -1 && latitudeIndex != -1) {
                double longitude = cursor.getDouble(longitudeIndex);
                double latitude = cursor.getDouble(latitudeIndex);
                cursor.close();
                return new double[]{longitude, latitude};
            } else {
                Log.d("DBHelper", "Column index for longitude or latitude not found");
            }
        } else {
            Log.d("DBHelper", "No entry found for user: " + userId + " and city: " + cityName);
        }

        cursor.close();
        return null;
    }


}

