package com.example.a433assn4;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class CommentBoardActivity extends AppCompatActivity {

    private EditText searchBox;
    private CheckBox includeSketches;
    private ListView mediaList, commentList;
    private TextView selectedMsg;
    private Button btnFindMedia, btnGenerateComments, btnBack;

    private DBHelper db;
    private final List<MediaItem> mediaItems = new ArrayList<>();
    private MediaAdapter mediaAdapter;

    private final List<CommentItem> comments = new ArrayList<>();
    private CommentAdapter commentAdapter;

    // Friend personas
    private final String[] friendNames = {
            "Alex (Supportive)",
            "Jordan (Sarcastic)",
            "Casey (Photographer)",
            "Taylor (Funny)",
            "Morgan (Analytical)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_board);

        // Find views
        searchBox           = findViewById(R.id.searchBox);
        includeSketches     = findViewById(R.id.includeSketches);
        mediaList           = findViewById(R.id.mediaList);
        commentList         = findViewById(R.id.commentList);
        selectedMsg         = findViewById(R.id.selectedMsg);
        btnFindMedia        = findViewById(R.id.btnFindMedia);
        btnGenerateComments = findViewById(R.id.btnGenerateComments);
        btnBack             = findViewById(R.id.btnBack);

        db = new DBHelper(this);

        mediaAdapter = new MediaAdapter(this, mediaItems);
        mediaList.setAdapter(mediaAdapter);

        commentAdapter = new CommentAdapter(this, comments);
        commentList.setAdapter(commentAdapter);

        // When a media row is tapped: mark it selected
        mediaList.setOnItemClickListener((parent, view, position, id) -> {
            for (MediaItem m : mediaItems) {
                m.isSelected = false;
            }
            MediaItem sel = mediaItems.get(position);
            sel.isSelected = true;
            mediaAdapter.notifyDataSetChanged();
            updateSelectedMessage();
        });

        btnFindMedia.setOnClickListener(v -> loadMedia());
        includeSketches.setOnCheckedChangeListener((b, checked) -> loadMedia());
        btnGenerateComments.setOnClickListener(v -> generateComments());
        btnBack.setOnClickListener(v -> finish());

        loadMedia();
    }

    private void loadMedia() {
        mediaItems.clear();

        String input = searchBox.getText().toString().trim();
        String[] tags = input.isEmpty() ? new String[]{} : input.split(",");

        // Photos
        Cursor pCursor = db.searchPhotos(tags);
        while (pCursor.moveToNext()) {
            String path = pCursor.getString(pCursor.getColumnIndexOrThrow("path"));
            String t    = pCursor.getString(pCursor.getColumnIndexOrThrow("tags"));
            String d    = pCursor.getString(pCursor.getColumnIndexOrThrow("datetime"));
            mediaItems.add(new MediaItem(path, t, d));
        }
        pCursor.close();

        // Sketches
        if (includeSketches.isChecked()) {
            Cursor sCursor = db.searchSketches(tags);
            while (sCursor.moveToNext()) {
                String path = sCursor.getString(sCursor.getColumnIndexOrThrow("path"));
                String t    = sCursor.getString(sCursor.getColumnIndexOrThrow("tags"));
                String d    = sCursor.getString(sCursor.getColumnIndexOrThrow("datetime"));
                mediaItems.add(new MediaItem(path, t, d));
            }
            sCursor.close();
        }

        mediaAdapter.notifyDataSetChanged();
        updateSelectedMessage();
    }

    private MediaItem getSelectedMedia() {
        for (MediaItem m : mediaItems) {
            if (m.isSelected) return m;
        }
        return null;
    }

    private void updateSelectedMessage() {
        MediaItem sel = getSelectedMedia();
        if (sel == null) {
            selectedMsg.setText("You selected: (none)");
        } else {
            selectedMsg.setText("You selected: " + sel.tags);
        }
    }

    private void generateComments() {
        MediaItem sel = getSelectedMedia();
        if (sel == null) {
            Toast.makeText(this, "Select a photo/sketch first", Toast.LENGTH_SHORT).show();
            return;
        }

        comments.clear();
        commentAdapter.notifyDataSetChanged();

        int count = 7 + new Random().nextInt(4);  // 7–10 comments
        StringBuilder history = new StringBuilder();

        new Thread(() -> {
            for (int i = 0; i < count; i++) {

                String friend = friendNames[new Random().nextInt(friendNames.length)];

                String prompt = "A friend named " + friend +
                        " is reacting to a post with tags: " + sel.tags +
                        ". Stay in persona. Keep the comment under 20 words. " +
                        "Previous comments: " + history;

                String text = GeminiApi.generateText(prompt);
                if (text == null || text.trim().isEmpty()) {
                    text = "Gemini returned empty response";
                }

                String time = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
                ).format(new Date());

                CommentItem item = new CommentItem(friend, text, time);
                history.append(" ").append(text);

                runOnUiThread(() -> {
                    comments.add(item);
                    commentAdapter.notifyDataSetChanged();
                });
            }
        }).start();
    }
}