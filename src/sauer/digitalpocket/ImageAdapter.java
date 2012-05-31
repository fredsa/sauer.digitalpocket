package sauer.digitalpocket;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


public class ImageAdapter extends BaseAdapter {
  private final Context context;
  private final Uri uri;

  public ImageAdapter(Context context, Uri uri) {
    this.context = context;
    this.uri = uri;
  }

  public int getCount() {
    return 1;
  }

  public Object getItem(int position) {
    return position;
  }

  public long getItemId(int position) {
    return position;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    ImageView imageView = new ImageView(context);
    imageView.setImageURI(uri);
    return imageView;
  }
}