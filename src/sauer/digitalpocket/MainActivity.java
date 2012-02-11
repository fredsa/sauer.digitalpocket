package sauer.digitalpocket;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
  /**
   * Hold the name of the world writeable file currently being shared with camera app, so we can
   * clean it up if something goes wrong.
   */
  private static final String CURRENT_FILENAME = "current-filename";
  private static final int TAKE_PICTURE = 42;
  private static final String TAG = MainActivity.class.getName();
  private Uri imageUri;
  private SharedPreferences prefs;
  private WakeLock wakeLock;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    debug("================onCreate()==============");
    setContentView(R.layout.main);

    prefs = getApplicationContext().getSharedPreferences("stuff", MODE_PRIVATE);

    Button newItemButton = (Button) findViewById(R.id.net_item_button);
    newItemButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = createWorldWriteableFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PICTURE);
      }

    });

    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
    wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
  }

  @Override
  protected void onPause() {
    super.onPause();
    wakeLock.release();
  }

  @Override
  protected void onResume() {
    super.onResume();
    wakeLock.acquire();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    cleanupWorldWriteableFile();
  }

  private Uri createWorldWriteableFile() {
    cleanupWorldWriteableFile();
    Time time = new Time();
    time.setToNow();
    String filename = MainActivity.class.getPackage().getName() + "-"
        + time.format("%Y%m%d-%H%M%S") + ".jpg";
    // Empty world writeable file, so camera app can write to it
    try {
      openFileOutput(filename, MODE_WORLD_WRITEABLE).close();
    } catch (Exception e) {
      throw new RuntimeException("Unable to create filename '" + filename + "'", e);
    }

    File file = getFileStreamPath(filename);
    debug("file=" + file.getAbsolutePath());
    prefs.edit().putString(CURRENT_FILENAME, filename).apply();

    return Uri.fromFile(file);
  }

  private void cleanupWorldWriteableFile() {
    String filename = prefs.getString(CURRENT_FILENAME, null);
    if (filename != null) {
      boolean deleted = deleteFile(filename);
      debug("Delete success?" + deleted);
      if (deleted) {
        prefs.edit().remove(CURRENT_FILENAME).apply();
      }
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case TAKE_PICTURE:
        if (resultCode == Activity.RESULT_OK) {
          ContentResolver cr = getContentResolver();
          try {
            Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);

            LinearLayout mainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
            ImageView imageView = new ImageView(getApplicationContext());
            mainLinearLayout.addView(imageView);

            imageView.setImageBitmap(bitmap);

            makeCurrentFilePrivate();
          } catch (Exception e) {
            throw new RuntimeException("failed to set bitmap", e);
          }
        }
    }
  }

  private void makeCurrentFilePrivate() throws IOException {
    String filename = prefs.getString(CURRENT_FILENAME, null);
    openFileOutput(filename, MODE_APPEND).close();
    prefs.edit().remove(CURRENT_FILENAME).apply();
  }

  private void debug(String msg) {
    Log.d(TAG, msg);
    // Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }
}