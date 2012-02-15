package sauer.digitalpocket;

import java.io.File;
import java.util.ArrayList;

import sauer.digitalpocket.app.DigitalPocketApplication;
import android.app.Activity;
import android.content.Intent;
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
  private static final int TAKE_PICTURE = 42;
  private static final String TAG = MainActivity.class.getName();
  private Uri imageUri;
  private WakeLock wakeLock;
  private DigitalPocketApplication app;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    debug("================onCreate()==============");
    setContentView(R.layout.main);

    app = (DigitalPocketApplication) getApplication();

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

    addImages();
  }

  private void addImages() {
    ArrayList<String> items = app.getItems();
    for (String item : items) {
      File file = getFileStreamPath(item);
      Uri uri = Uri.fromFile(file);
      debug("uri=" + uri);
      addImage(uri);
    }
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
    // Empty world writable file, so camera app can write to it
    try {
      openFileOutput(filename, MODE_WORLD_WRITEABLE).close();
    } catch (Exception e) {
      throw new RuntimeException("Unable to create filename '" + filename + "'", e);
    }

    File file = getFileStreamPath(filename);
    debug("file=" + file.getAbsolutePath());
    app.setCurrentFilename(filename);

    return Uri.fromFile(file);
  }

  private void cleanupWorldWriteableFile() {
    String filename = app.getCurrentFilename(this);
    if (filename != null) {
      boolean deleted = deleteFile(filename);
      debug("Delete success?" + deleted);
      if (deleted) {
        app.setCurrentFilename(null);
      }
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case TAKE_PICTURE:
        if (resultCode == Activity.RESULT_OK) {
          makeCurrentFilePrivate();
          addImage(imageUri);
        }
    }
  }

  private void addImage(final Uri uri) {
    final LinearLayout mainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
    ImageView imageView = new ImageView(getApplicationContext());
    imageView.setImageURI(uri);
    mainLinearLayout.addView(imageView);
    imageView.getLayoutParams().height = 200;

    imageView.setOnClickListener(new OnClickListener() {
      private Intent intent;

      @Override
      public void onClick(View view) {
        mainLinearLayout.removeView(view);
        intent = new Intent(MainActivity.this, ItemViewerActivity.class);
        intent.putExtra("uri", uri.toString());
        startActivity(intent);
      }
    });
  }

  private void makeCurrentFilePrivate() {
    String filename = app.getCurrentFilename(this);
    app.addItem(filename);
    // make file private
    try {
      openFileOutput(filename, MODE_APPEND).close();
    } catch (Exception e) {
      throw new RuntimeException("Failed to make filename '" + filename + "' private", e);
    }
    app.setCurrentFilename(null);
  }

  private void debug(String msg) {
    Log.d(TAG, msg);
    // Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }
}