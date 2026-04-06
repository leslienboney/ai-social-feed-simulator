package com.example.a433assn4;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SketchTaggerActivity extends AppCompatActivity {

    private SketchView sketchView;   // your custom drawing view
    private Button btnSaveSketch, btnBackSketch;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch_tagger);

        sketchView = findViewById(R.id.sketchView);
        btnSaveSketch = findViewById(R.id.btnSaveSketch);
        btnBackSketch = findViewById(R.id.btnBackSketch);

        db = new DBHelper(this);

        // ★ Back button
        btnBackSketch.setOnClickListener(v -> finish());

        // ★ Auto-tag + save sketch
        btnSaveSketch.setOnClickListener(v -> saveSketch());
    }

    private void saveSketch() {

        // 1. Export the drawn sketch to a Bitmap
        Bitmap bmp = sketchView.exportBitmap();
        if (bmp == null) {
            Toast.makeText(this, "Sketch empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Feed to ML Kit for tagging
        InputImage image = InputImage.fromBitmap(bmp, 0);

        ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                .process(image)
                .addOnSuccessListener(labels -> handleTagsAndSave(bmp, labels))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Auto-tagging failed", Toast.LENGTH_SHORT).show()
                );
    }

    private void handleTagsAndSave(Bitmap bmp, List<ImageLabel> labels) {

        // Convert labels to comma-separated string
        StringBuilder tagBuilder = new StringBuilder();
        for (ImageLabel label : labels) {
            if (label.getConfidence() > 0.5) {
                tagBuilder.append(label.getText()).append(",");
            }
        }
        String tags = tagBuilder.toString();
        if (tags.endsWith(",")) {
            tags = tags.substring(0, tags.length() - 1);
        }
        if (tags.isEmpty()) {
            tags = "untagged";
        }

        // 3. Save bitmap to file
        File sketchFile = new File(getFilesDir(),
                "sketch_" + System.currentTimeMillis() + ".png");

        try {
            FileOutputStream out = new FileOutputStream(sketchFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save sketch", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. Insert into DB
        String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date());

        db.insertSketch(sketchFile.getAbsolutePath(), tags, datetime);

        // ★ Show the tags to the user and stay on this screen
        Toast.makeText(this, "Sketch saved with tags: " + tags,
                Toast.LENGTH_LONG).show();

        // Optional: clear the canvas so you can draw a new one
        sketchView.clear();

        // ★ NO finish(); here – you stay in SketchTagger.
        // Use the Back button we added when you're done.
    }
}