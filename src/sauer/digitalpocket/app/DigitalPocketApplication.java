package sauer.digitalpocket.app;

import java.util.ArrayList;

import sauer.digitalpocket.MainActivity;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DigitalPocketApplication extends Application {
  private static final String TAG = DigitalPocketApplication.class.getName();

  /**
   * Hold the name of the world writable file currently being shared with camera app, so we can
   * clean it up if something goes wrong.
   */
  public static final String CURRENT_FILENAME = "current-filename";

  private SQLiteDatabase sql;

  private SharedPreferences prefs;

  @Override
  public void onCreate() {
    super.onCreate();
    debug("================onCreate()==============");
    prefs = getSharedPreferences("digitalpocket", MODE_PRIVATE);
    sql = new MySQLiteOpenHelper(this).getWritableDatabase();
  }

  public void addItem(String filename) {
    if (filename == null) {
      throw new RuntimeException("Filename == null");
    }
    sql.execSQL("insert into items (filename) values (?)", new String[] {filename});
  }

  public ArrayList<String> getItems() {
    //     sql.execSQL("delete from items");
    ArrayList<String> list = new ArrayList<String>();
    Cursor query = sql.query("items", new String[] {"filename"}, null, null, "filename", null,
        "filename DESC");
    while (query.moveToNext()) {
      String listName = query.getString(0);
      list.add(listName);
    }
    return list;
  }

  private void debug(String msg) {
    Log.d(TAG, msg);
    // Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }

  public String getCurrentFilename(MainActivity mainActivity) {
    return prefs.getString(CURRENT_FILENAME, null);
  }

  public void setCurrentFilename(String filename) {
    if (filename == null) {
      prefs.edit().remove(CURRENT_FILENAME).apply();
    } else {
      prefs.edit().putString(CURRENT_FILENAME, filename).apply();
    }
  }

}
