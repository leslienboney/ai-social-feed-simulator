package com.example.a433assn4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class MediaAdapter extends BaseAdapter {

    private final Context context;
    private final List<MediaItem> items;

    public MediaAdapter(Context context, List<MediaItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.row_media, parent, false);
        }

        MediaItem item = items.get(pos);

        ImageView imgThumb = convertView.findViewById(R.id.imgThumb);
        TextView txtTags  = convertView.findViewById(R.id.txtMediaTags);
        TextView txtTime  = convertView.findViewById(R.id.txtMediaTime);

        txtTags.setText(item.tags);
        txtTime.setText(item.datetime);

        // Load thumbnail from file path
        Bitmap bmp = null;
        File f = new File(item.path);
        if (f.exists()) {
            Bitmap full = BitmapFactory.decodeFile(item.path);
            if (full != null) {
                bmp = Bitmap.createScaledBitmap(full, 120, 120, true);
                full.recycle();
            }
        }
        if (bmp != null) {
            imgThumb.setImageBitmap(bmp);
        } else {
            imgThumb.setImageDrawable(null);  // or set a placeholder drawable
        }

        // simple visual highlight for selected item
        if (item.isSelected) {
            convertView.setBackgroundColor(0xFFE0F7FA); // light cyan
        } else {
            convertView.setBackgroundColor(0x00000000); // transparent
        }

        return convertView;
    }
}