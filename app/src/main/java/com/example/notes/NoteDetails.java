package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class NoteDetails extends AppCompatActivity {
    DisplayMetrics dm;

    // Pop up params
    CardView detailBgCard;
    EditText detailHeading;
    EditText detailBody;
    ImageView detailImage;
    ImageButton saveButton;
    ImageButton cancelButton;

    // Data params
    int imageResource;
    String heading;
    String body;

    Intent intent;
    String state;
    int notePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        intent = getIntent();

        NoteItem noteItem = intent.getParcelableExtra("Note Item");

        state = intent.getStringExtra("State");

        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        detailHeading = findViewById(R.id.detailHeading);
        detailBody = findViewById(R.id.detailBody);
        detailImage = findViewById(R.id.detailImage);
        detailBgCard = findViewById(R.id.noteDetailsBg);

        if (state.equals("editing")){
            imageResource = noteItem.getImageResource();
            heading = noteItem.getHeading();
            body = noteItem.getBody();
            int color = Color.parseColor(noteItem.getColor());

            detailHeading.setText(heading);
            detailBody.setText(body);
            detailImage.setImageResource(imageResource);
            detailBgCard.setCardBackgroundColor(color);
        }



        setUpPopUp();
        setOnClickListeners();
    }

    private void setOnClickListeners(){
        // On saving create a result and pass it to previous activity.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                if (state.equals("editing")) {
                    // Assign note position
                    notePos = intent.getIntExtra("Note position", -1);
                    String headingText = detailHeading.getText().toString();
                    String bodyText = detailBody.getText().toString();

                    Intent intent = new Intent();
                    intent.putExtra("Heading text", headingText);
                    intent.putExtra("Body text", bodyText);
                    intent.putExtra("Note position", notePos);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else if (state.equals("creating")){
                    Log.i("Note", "saved");

                    String headingText = detailHeading.getText().toString();
                    String bodyText = detailBody.getText().toString();

                    Intent intent = new Intent();
                    intent.putExtra("Heading text", headingText);
                    intent.putExtra("Body text", bodyText);
                    intent.putExtra("Note position", notePos);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    private void setUpPopUp(){
        dm = new DisplayMetrics();

        // For popUp effect
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



    @Override
    protected void onStop() {
        super.onStop();
        CardView view = findViewById(R.id.noteDetailsBg);
        hideKeyboard(view);
    }

    /*Hides keyboard*/
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}