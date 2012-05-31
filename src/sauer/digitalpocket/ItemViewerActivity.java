package sauer.digitalpocket;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Gallery;

public class ItemViewerActivity extends Activity {
  private static final String TAG = ItemViewerActivity.class.getName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.item_viewer);

    Uri uri = Uri.parse(getIntent().getExtras().getString("uri"));
    Gallery gallery = (Gallery) findViewById(R.id.gallery);
    gallery.setAdapter(new ImageAdapter(this, uri));
  }

  private void debug(String msg) {
    Log.d(TAG, msg);
    // Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }
}
