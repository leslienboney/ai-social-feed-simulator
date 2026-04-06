package com.example.a433assn4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnPhotoTagger;
    private Button btnSketchTagger;
    private Button btnCommentBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: make sure these IDs match the ones in activity_main.xml
        btnPhotoTagger   = findViewById(R.id.btnPhotoTagger);
        btnSketchTagger  = findViewById(R.id.btnSketchTagger);
        btnCommentBoard  = findViewById(R.id.btnCommentBoard);

        // Open photo tagger
        btnPhotoTagger.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, PhotoTaggerActivity.class);
            startActivity(i);
        });

        // Open sketch tagger
        btnSketchTagger.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, SketchTaggerActivity.class);
            startActivity(i);
        });

        // Open comment board
        btnCommentBoard.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, CommentBoardActivity.class);
            startActivity(i);
        });
    }
}