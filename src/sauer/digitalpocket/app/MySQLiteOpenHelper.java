package sauer.digitalpocket.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class MySQLiteOpenHelper extends SQLiteOpenHelper {

  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_NAME = "digitalpocket";
  private static final String TAG = MySQLiteOpenHelper.class.getName();

  MySQLiteOpenHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    debug("SQL ctor version=" + DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    debug("SQL onCreate()");
    db.execSQL("CREATE TABLE items (filename TEXT);");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    debug("SQL onUpgrade() " + oldVersion + " -> " + newVersion);
  }

  private void debug(String msg) {
    Log.d(TAG, msg);
    // Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }

}