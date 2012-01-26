package sauer.digitalpocket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
  private static final int TAKE_PICTURE = 42;
  private static final String TAG = MainActivity.class.getName();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    Button newItemButton = (Button) findViewById(R.id.net_item_button);
    newItemButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE);
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case TAKE_PICTURE:
        if (resultCode == Activity.RESULT_OK) {
          try {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            LinearLayout mainLinearLayout = (LinearLayout) findViewById(R.id.main_linear_layout);
            ImageView imageView = new ImageView(getApplicationContext());
            mainLinearLayout.addView(imageView);

            imageView.setImageBitmap(bitmap);
          } catch (Exception e) {
            Toast.makeText(this, "Failed to load: " + e, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "oops", e);
          }
        }
    }
  }
}