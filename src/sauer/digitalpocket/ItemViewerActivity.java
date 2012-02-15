package sauer.digitalpocket;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class ItemViewerActivity extends Activity {
  private static final String TAG = ItemViewerActivity.class.getName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.item_viewer);

    Uri uri = Uri.parse(getIntent().getExtras().getString("uri"));

    doit(uri);
    doit(uri);
  }

  private void doit(Uri uri) {
    ViewFlipper mainView = (ViewFlipper) findViewById(R.id.view_flipper);

    ImageView imageView = new ImageView(this);
    imageView.setImageURI(uri);
    mainView.addView(imageView);
  }

  private void debug(String msg) {
    Log.d(TAG, msg);
    // Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }

}
