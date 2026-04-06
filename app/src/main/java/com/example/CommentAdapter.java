package com.example.a433assn4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CommentAdapter extends BaseAdapter {

    private final Context context;
    private final List<CommentItem> items;

    public CommentAdapter(Context context, List<CommentItem> items) {
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
                    .inflate(R.layout.row_comment, parent, false);
        }

        CommentItem item = items.get(pos);

        TextView friend = convertView.findViewById(R.id.txtCommentFriend);
        TextView text = convertView.findViewById(R.id.txtCommentText);
        TextView time = convertView.findViewById(R.id.txtCommentTime);

        friend.setText(item.friend);
        text.setText(item.text);
        time.setText(item.time);

        return convertView;
    }
}