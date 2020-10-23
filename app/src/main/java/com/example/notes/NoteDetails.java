package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.sql.Timestamp;
import java.util.ArrayList;

public class NoteDetails extends AppCompatActivity {
    DisplayMetrics dm;

    NoteItem noteItem;

    // Pop up params
    CardView detailBgCard;
    EditText detailHeading;
    EditText detailBody;
    ImageView detailImage;
    ImageButton saveButton;
    ImageButton cancelButton;
    LinearLayout noteContainer;
    ConstraintLayout constraintCont;
    CardView toggleCols;
    ImageView settingsButton;
    LinearLayout toggleSettings;

    LinearLayout colorOptions;
    LinearLayout iconOptions;

    // Coloured dots
    ArrayList<CardView> coloredDots= new ArrayList<>();
    CardView chooseAlmondCol;
    CardView chooseChampagneCol;
    CardView choosePinkCol;
    CardView chooseBlueCol;
    CardView chooseOpalCol;
    CardView choosePurpleCol;
    View hightlightCol;

    // Icons
    ImageView iconToggle;
    ArrayList<ImageView> iconOptionsList = new ArrayList<>();
    ImageView chooseAndroid;
    ImageView chooseBookmark;
    ImageView chooseDone;
    ImageView chooseShoppingBasket;
    ImageView chooseAssignment;



    // Data params
    String icon;
    String heading;
    String body;
    String timestamp;
    int selectedColor;
    String selectedIcon;

    Intent intent;
    String state;
    int notePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        intent = getIntent();

        noteItem = intent.getParcelableExtra("Note Item");

        state = intent.getStringExtra("State");

        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        detailHeading = findViewById(R.id.detailHeading);
        detailBody = findViewById(R.id.detailBody);
        detailImage = findViewById(R.id.detailImage);
        detailBgCard = findViewById(R.id.noteDetailsBg);

        noteContainer = findViewById(R.id.noteContainer);
        constraintCont = findViewById(R.id.constrainedCont);


        settingsButton = findViewById(R.id.settingsButton);
        toggleSettings = findViewById(R.id.toggleSettings);
        toggleCols = findViewById(R.id.toggleColBtn);
        colorOptions = findViewById(R.id.colorOptions);
        iconOptions = findViewById(R.id.iconOptions);
        iconToggle = findViewById(R.id.iconToggle);

        // Color
        chooseAlmondCol = findViewById(R.id.chooseAlmondCol);
        chooseChampagneCol = findViewById(R.id.chooseChampagneCol);
        choosePinkCol = findViewById(R.id.choosePinkCol);;
        chooseBlueCol = findViewById(R.id.chooseBlueCol);;
        chooseOpalCol = findViewById(R.id.chooseOpalCol);;
        choosePurpleCol = findViewById(R.id.choosePurpleCol);;
        coloredDots.add(chooseAlmondCol);
        coloredDots.add(chooseChampagneCol);
        coloredDots.add(choosePinkCol);
        coloredDots.add(chooseBlueCol);
        coloredDots.add(chooseOpalCol);
        coloredDots.add(choosePurpleCol);

        // Highlight
        hightlightCol = new View(this);
        hightlightCol.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        hightlightCol.setBackground(getResources().getDrawable(R.drawable.togglecolors));
        hightlightCol.setVisibility(View.VISIBLE);

        // Icons
        chooseAndroid = findViewById(R.id.chooseAndroid);
        chooseBookmark = findViewById(R.id.chooseBookmark);
        chooseDone = findViewById(R.id.chooseDone);
        chooseShoppingBasket = findViewById(R.id.chooseShoppingBasket);
        chooseAssignment = findViewById(R.id.chooseAssignment);
        iconOptionsList.add(chooseAndroid);
        iconOptionsList.add(chooseBookmark);
        iconOptionsList.add(chooseDone);
        iconOptionsList.add(chooseShoppingBasket);
        iconOptionsList.add(chooseAssignment);



        if (state.equals("editing")){
            icon = noteItem.getIcon();
            heading = noteItem.getHeading();
            body = noteItem.getBody();

            selectedColor = noteItem.getColor();

            detailHeading.setText(heading);
            detailBody.setText(body);
            detailImage.setImageResource(getIconFromString(icon));
            detailBgCard.setCardBackgroundColor(selectedColor);

            iconToggle.setImageResource(getIconFromString(icon));
        }
        // Set it to default if not chosen
        else if (state.equals("creating")){
            selectedColor = detailBgCard.getCardBackgroundColor().getDefaultColor();
        }

        // Attach transitions to the note container and the constraint layouts
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING);
        noteContainer.setLayoutTransition(layoutTransition);
        constraintCont.setLayoutTransition(layoutTransition);


        setUpPopUp();
        setOnClickListeners();
        generateColorPicker();
    }

    private void setOnClickListeners(){
        // On saving create a result and pass it to previous activity.
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                timestamp = new Timestamp(System.currentTimeMillis()).toString();
                if (state.equals("editing")) {
                    // Assign note position
                    notePos = intent.getIntExtra("Note position", -1);
                    String headingText = detailHeading.getText().toString();
                    String bodyText = detailBody.getText().toString();

                    Intent intent = new Intent();
                    intent.putExtra("Heading text", headingText);
                    intent.putExtra("Body text", bodyText);
                    // Must convert to hexstring for format to class
                    intent.putExtra("Selected color", selectedColor);
                    intent.putExtra("Timestamp", timestamp);
                    intent.putExtra("Note position", notePos);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else if (state.equals("creating")){


                    String headingText = detailHeading.getText().toString();
                    String bodyText = detailBody.getText().toString();

                    Intent intent = new Intent();
                    intent.putExtra("Heading text", headingText);
                    intent.putExtra("Body text", bodyText);
                    // Must convert to hexstring for format to class
                    intent.putExtra("Selected color", selectedColor);
                    intent.putExtra("Timestamp", timestamp);
                    setResult(RESULT_OK, intent);

                    Log.i("heading", headingText);
                    Log.i("body", bodyText);
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

        toggleCols.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (colorOptions.isShown()){
                    colorOptions.setVisibility(View.GONE);
                }
                else{
                    colorOptions.setVisibility(View.VISIBLE);

                }
            }
        });

        iconToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iconOptions.isShown()){
                    iconOptions.setVisibility(View.GONE);
                }
                else{
                    iconOptions.setVisibility(View.VISIBLE);
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleSettings.isShown()){
                    toggleSettings.setVisibility(View.GONE);
                    colorOptions.setVisibility(View.GONE);
                    iconOptions.setVisibility(View.GONE);
                }
                else{
                    toggleSettings.setVisibility(View.VISIBLE);
                }
            }
        });

        // click listeners for select colours
        for (final CardView cardView : coloredDots){
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove the highlight from previous selected colour
                    getSelectedColOption(selectedColor).removeView(hightlightCol);
                    // Reassign selected colour to the cardView colour and add highlight
                    selectedColor = cardView.getCardBackgroundColor().getDefaultColor();
                    cardView.addView(hightlightCol);
                    // Change toggle colour
                    toggleCols.setCardBackgroundColor(selectedColor);
                    // Change background colour of note activity.
                    detailBgCard.setCardBackgroundColor(selectedColor);


                }
            });
        }

//        for (ImageView imageView : iconOptionsList){
//            if (imageView.getImageResource())
//        }
    }

    private void setUpPopUp(){
        dm = new DisplayMetrics();

        // For popUp effect
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.9), (int) (height*0.85));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
    }


    /**
     * Hides Keyboard when activity is exited.
     */
    @Override
    protected void onStop() {
        super.onStop();
        CardView view = findViewById(R.id.noteDetailsBg);
        hideKeyboard(view);
    }


    /**
     * Method which returns an icon int from the associated string name.
     */
    public int getIconFromString(String icon) {
        switch (icon) {
            case ("android"):
                return R.drawable.ic_android;
            case ("assignment"):
                return R.drawable.ic_assignment;
            case ("bookmark"):
                return R.drawable.ic_bookmark;
            default:
                return R.drawable.ic_error;
        }
    }

    /*Hides keyboard*/
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    /**
     *  Function which highlights the option which is already chosen to the user
     *  and assigns a colour to the settings menu icon.
     */
    private void generateColorPicker(){
        if (state.equals("editing")) {
//            int noteColor = Color.parseColor(noteItem.getColor());
            getSelectedColOption(selectedColor).addView(hightlightCol);
            // Also set toggle button
            toggleCols.setCardBackgroundColor(selectedColor);
        }
        else if (state.equals("creating")) {
            chooseOpalCol.addView(hightlightCol);

            // Also set toggle button
            toggleCols.setCardBackgroundColor(selectedColor);
        }
        Log.i("selected col", String.valueOf(selectedColor));
    }

    public CardView getSelectedColOption(int selectedColor) {
        for (CardView cardView : coloredDots) {
            if (cardView.getCardBackgroundColor().getDefaultColor() == selectedColor) {
                return cardView;
            }
        }
        // If the for loop returns nothing just return default.
        return chooseOpalCol;
    }
}