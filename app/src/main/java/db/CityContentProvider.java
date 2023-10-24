package db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

public class CityContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.edu.uiuc.cs427.app.provider";
    private static final String PATH_USERS = "users";
    private static final String PATH_CITIES = "cities";
    public static final Uri CONTENT_URI_USERS = Uri.parse("content://" + AUTHORITY + "/" + PATH_USERS);
    public static final Uri CONTENT_URI_CITIES = Uri.parse("content://" + AUTHORITY + "/" + PATH_CITIES);

    private static final int USERS = 1;
    private static final int CITIES = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, PATH_USERS, USERS);
        uriMatcher.addURI(AUTHORITY, PATH_CITIES, CITIES);
    }

    private dbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new dbHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = 0;
        Uri newUri = null;

        switch (uriMatcher.match(uri)) {
            case CITIES:
                id = dbHelper.getWritableDatabase().insert(dbHelper.TABLE_CITIES, null, values);
                if (id > 0) {
                    newUri = ContentUris.withAppendedId(CONTENT_URI_CITIES, id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                }
                break;
            case USERS:
                id = dbHelper.getWritableDatabase().insert(dbHelper.TABLE_USERS, null, values);
                if (id > 0) {
                    newUri = ContentUris.withAppendedId(CONTENT_URI_USERS, id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                }
                break;
            default:
                throw new SQLException("Failed to insert row into " + uri);
        }
        return newUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case CITIES:
                cursor = dbHelper.getReadableDatabase().query(dbHelper.TABLE_CITIES, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case USERS:
                cursor = dbHelper.getReadableDatabase().query(dbHelper.TABLE_USERS, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;

        switch (uriMatcher.match(uri)) {
            case CITIES:
                count = dbHelper.getWritableDatabase().update(dbHelper.TABLE_CITIES, values, selection, selectionArgs);
                break;
            case USERS:
                count = dbHelper.getWritableDatabase().update(dbHelper.TABLE_USERS, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;

        switch (uriMatcher.match(uri)) {
            case CITIES:
                count = dbHelper.getWritableDatabase().delete(dbHelper.TABLE_CITIES, selection, selectionArgs);
                break;
            case USERS:
                count = dbHelper.getWritableDatabase().delete(dbHelper.TABLE_USERS, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CITIES:
                return "vnd.android.cursor.dir/vnd.com.edu.uiuc.cs427.app.provider.cities";
            case USERS:
                return "vnd.android.cursor.dir/vnd.com.edu.uiuc.cs427.app.provider.users";
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
}


