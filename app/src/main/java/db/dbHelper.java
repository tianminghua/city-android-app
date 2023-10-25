package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
}

