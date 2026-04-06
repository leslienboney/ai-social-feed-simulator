package com.example.a433assn4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoTaggerActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private EditText editTags;
    private Button btnTakePhoto, btnSave, btnClear, btnFind;

    private DBHelper db;

    private Uri currentImageUri;
    private File currentImageFile;

    // Launch camera and get photo
    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    imagePreview.setImageURI(currentImageUri);
                    runAutoTagging();
                }
            }
    );

    // Request Camera Permission
    ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_tagger);
        Button btnBackPhoto = findViewById(R.id.btnBackPhoto);
        btnBackPhoto.setOnClickListener(v -> finish());

        imagePreview = findViewById(R.id.imagePreview);
        editTags = findViewById(R.id.editTags);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSave = findViewById(R.id.btnSave);
        btnClear = findViewById(R.id.btnClear);
        btnFind = findViewById(R.id.btnFind);

        db = new DBHelper(this);

        btnTakePhoto.setOnClickListener(v -> checkPermissionAndOpenCamera());

        btnSave.setOnClickListener(v -> saveToDatabase());
        btnClear.setOnClickListener(v -> clearScreen());
        btnFind.setOnClickListener(v -> openCommentBoard());
    }

    private void checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            currentImageFile = createTempImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
            return;
        }

        currentImageUri = FileProvider.getUriForFile(
                this,
                "com.example.a433assn4.fileprovider",
                currentImageFile
        );

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
        cameraLauncher.launch(intent);
    }

    private File createTempImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir("images");
        return File.createTempFile("IMG_" + timestamp + "_", ".jpg", storageDir);
    }

    private void runAutoTagging() {
        try {
            InputImage image = InputImage.fromFilePath(this, currentImageUri);

            ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                    .process(image)
                    .addOnSuccessListener(labels -> {
                        StringBuilder sb = new StringBuilder();
                        for (ImageLabel label : labels) {
                            sb.append(label.getText()).append(", ");
                        }
                        if (sb.length() > 2) sb.setLength(sb.length() - 2);
                        editTags.setText(sb.toString());
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Tagging failed", Toast.LENGTH_SHORT).show()
                    );

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToDatabase() {
        if (currentImageUri == null) {
            Toast.makeText(this, "Take a photo first", Toast.LENGTH_SHORT).show();
            return;
        }

        String tags = editTags.getText().toString().trim();
        if (tags.isEmpty()) tags = "untagged";

        String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        db.insertPhoto(currentImageFile.getAbsolutePath(), tags, datetime);

        Toast.makeText(this, "Photo saved", Toast.LENGTH_SHORT).show();
    }

    private void clearScreen() {
        imagePreview.setImageDrawable(null);
        editTags.setText("");
        currentImageUri = null;
        currentImageFile = null;
    }

    private void openCommentBoard() {
        Intent intent = new Intent(this, CommentBoardActivity.class);
        startActivity(intent);
    }
}