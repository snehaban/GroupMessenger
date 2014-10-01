package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {

	SQLiteDatabase myDatabase;
	GMDBHelper sqlHelper;
	Context context;
	static final String DB_NAME = "groupMessengerDB";
	static final int DB_VERSION = 1;
	
	static final String TABLE_NAME = "gmTable";
	static final String KEY_FIELD = "key";
	static final String VALUE_FIELD = "value";
	static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " + 
										KEY_FIELD + " TEXT UNIQUE NOT NULL, " + 
										VALUE_FIELD + " TEXT NOT NULL );";

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
    	context = getContext();    	
    	sqlHelper = new GMDBHelper(context);
    	myDatabase = sqlHelper.getWritableDatabase();
    	if(myDatabase == null)
    		return false;
    	else
    		return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v("insert", values.toString());
    	long rowId = myDatabase.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    	if(rowId > 0) {    		
    		uri = ContentUris.withAppendedId(uri, rowId);
            context.getContentResolver().notifyChange(uri, null);
    		return uri;
    	}
    	return null;    	
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {            	
    	Cursor cursor = myDatabase.rawQuery("SELECT * from " + TABLE_NAME +
    							" WHERE " + KEY_FIELD + " = ?", new String[] {selection}); 
    	cursor.setNotificationUri(context.getContentResolver(), uri);
    	Log.v("query", selection);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }
    
    public class GMDBHelper extends SQLiteOpenHelper {
    	
		public GMDBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}    	
    }
}
