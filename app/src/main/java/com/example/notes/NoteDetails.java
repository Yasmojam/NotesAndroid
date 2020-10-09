package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

public class NoteDetails extends AppCompatActivity {
    DisplayMetrics dm;

    // Pop up params
    CardView detailBgCard;
    EditText detailHeading;
    EditText detailBody;
    ImageView detailImage;

    // Data params
    int imageResource;
    String heading;
    String body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        Intent intent = getIntent();
        NoteItem noteItem = intent.getParcelableExtra("Note Item");

        imageResource = noteItem.getImageResource();
        heading = noteItem.getHeading();
        body = noteItem.getBody();
        int color = Color.parseColor(noteItem.getColor());

        detailHeading = findViewById(R.id.detailHeading);
        detailHeading.setText(heading);

        detailBody = findViewById(R.id.detailBody);
        detailBody.setText(body);

        detailImage = findViewById(R.id.detailImage);
        detailImage.setImageResource(imageResource);

        detailBgCard = findViewById(R.id.noteDetailsBg);
        detailBgCard.setCardBackgroundColor(color);

        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.9), (int) (height*0.7));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
    }
}