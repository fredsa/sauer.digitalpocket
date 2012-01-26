package sauer.digitalpocket;

import java.io.File;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
  private static final String TEMP_FILE_PATH_PREF = "temp_file_path";
  private static final int TAKE_PICTURE = 42;
  private static final String TAG = MainActivity.class.getName();
  private Uri imageUri;
  private SharedPreferences prefs;
  private File tempFile;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    prefs = getApplicationContext().getSharedPreferences("stuff", MODE_PRIVATE);

    Button newItemButton = (Button) findViewById(R.id.net_item_button);
    newItemButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        tempFile = makeTempFile();
        debug(tempFile.getAbsolutePath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        imageUri = Uri.fromFile(tempFile);
        debug(imageUri.toString());
        startActivityForResult(intent, TAKE_PICTURE);
      }

    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    cleanupTempfile();
  }

  private File makeTempFile() {
    cleanupTempfile();
    Time time = new Time();
    time.setToNow();
    String filename = MainActivity.class.getPackage().getName() + " temp "
        + time.format("%Y%m%d %H%M%S") + ".jpg";
    File file = new File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename);
    prefs.edit().putString(TEMP_FILE_PATH_PREF, file.getAbsolutePath()).apply();
    return file;
  }

  private void cleanupTempfile() {
    String path = prefs.getString(TEMP_FILE_PATH_PREF, null);
    if (path != null) {
      boolean deleted = new File(path).delete();
      debug("Delete success?" + deleted);
      if (deleted) {
        prefs.edit().remove(TEMP_FILE_PATH_PREF).apply();
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
          } catch (Exception e) {
            String msg = "Failed to load: " + e;
            debug(msg);
            Log.e(TAG, "oops", e);
          }
        }
    }
  }

  private void debug(String msg) {
    Log.d(TAG, msg);
    //    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }
}