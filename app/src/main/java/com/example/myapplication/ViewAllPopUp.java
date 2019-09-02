package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;

import static com.example.myapplication.DatabaseHelper.selectEateriesByTag;

public class ViewAllPopUp extends Activity {

    LinearLayout viewAllLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_all_pop_up);

        // sets the pop up windows layout
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getWindow().setLayout((int) (dm.widthPixels * .92), (int) (dm.heightPixels * .9));

        viewAllLinearLayout = findViewById(R.id.viewAllLinearLayout);

        String title = getIntent().getStringExtra("title");
        String tag   = getIntent().getStringExtra("tag");

        // =====================================================================================
        // Relative Layout -- Title
        // =====================================================================================
        // creates a new Relative Layout
        RelativeLayout titleRelativeLayout = new RelativeLayout(getApplicationContext());

        // sets the card views relative layouts layout parameter
        RelativeLayout.LayoutParams titleRelativeLayoutParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleRelativeLayout.setLayoutParams(titleRelativeLayoutParam);

        // sets the horizontal linear layouts tag
        titleRelativeLayout.setTag(tag);

        // adds the horizontal linear layout to the horizontal scroll view
        viewAllLinearLayout.addView(titleRelativeLayout);

        // =========================================================================================
        // Title
        // =========================================================================================

        // creates a new text view
        TextView viewAllTitle = new TextView(getApplicationContext());

        // sets the layout parameters for the title
        RelativeLayout.LayoutParams titleParam = new RelativeLayout.LayoutParams(
                (getScreenWidth() * 2) / 3,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        titleParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        titleParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        titleParam.setMargins(15, 0, 0, 0);
        viewAllTitle.setLayoutParams(titleParam);

        // sets the text views text, size, and color
        viewAllTitle.setText(title);
        viewAllTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
        viewAllTitle.setTextColor(getResources().getColor(R.color.textColor));

        // sets the text views tag to match the title
        viewAllTitle.setTag(tag);

        // adds specified title to the page
        titleRelativeLayout.addView(viewAllTitle);

        // =========================================================================================
        // Close Pop Up
        // =========================================================================================
        // creates a image view
        final ImageView closePopUp = new ImageView(getApplicationContext());

        // sets the image views layout parameters, and pushes image to the bottom right
        RelativeLayout.LayoutParams closePopUpParam = new RelativeLayout.LayoutParams(
                getScreenWidth() / 8,
                getScreenWidth() / 8);
        closePopUpParam.setMargins(0, 5, 5, 0);
        closePopUpParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closePopUpParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closePopUp.setLayoutParams(closePopUpParam);

        // sets the image view to an x
        closePopUp.setImageResource(R.drawable.close_pop_up_foreground);

        // sets the image views tag
        closePopUp.setTag(tag);

        // sets the image views onClick
        closePopUp.setOnClickListener(new View.OnClickListener() {

               // onClick()
               // pre: none
               // post: updates the main menus ui
               @Override
               public void onClick(View v) {
                    finish();
               }

           }
        );

        // adds the text view to the horizontal linear layout
        titleRelativeLayout.addView(closePopUp);

        // =========================================================================================
        // Line
        // =========================================================================================

        //View line = new View(getApplicationContext());
        ImageView line = new ImageView(new ContextThemeWrapper(this, R.style.Divider), null, 0);

        viewAllLinearLayout.addView(line);

        // =========================================================================================
        // Scroll View
        // =========================================================================================

        // creates a new scroll view to hold the specified eateries
        ScrollView viewAllScrollView = new ScrollView(getApplicationContext());

        // sets the scroll views layout parameters
        LinearLayout.LayoutParams viewAllScrollViewParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT );
        viewAllScrollViewParam.setMargins(0,0, 0, 10); // fixes clipping issue at the bottom
        viewAllScrollView.setLayoutParams(viewAllScrollViewParam);

        // sets the scroll views tag
        viewAllScrollView.setTag(tag);

        // disables gray figures that appear when you over scroll
        viewAllScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // removes the scroll bar on the right side of the scroll view
        viewAllScrollView.setVerticalScrollBarEnabled(false);

        // adds the scroll view to the main page
        viewAllLinearLayout.addView(viewAllScrollView);

        // =========================================================================================
        // Linear Layout
        // =========================================================================================

        // creates a new scroll view to hold the specified eateries
        LinearLayout viewAllLinearLayout = new LinearLayout(getApplicationContext());

        LinearLayout.LayoutParams viewAllLinearLayoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT );
        viewAllLinearLayout.setLayoutParams(viewAllLinearLayoutParam);

        // sets the linear layouts orientation
        viewAllLinearLayout.setOrientation(LinearLayout.VERTICAL);

        // sets the scroll views tag
        viewAllLinearLayout.setTag(tag);

        // adds the linear layout to the scroll view
        viewAllScrollView.addView(viewAllLinearLayout);

        // =========================================================================================
        // Obtains Selected Eateries
        // =========================================================================================
        // gets the id and name of every eatery that correspond
        ArrayList<ArrayList<String>> eateries = new ArrayList<ArrayList <String>>();

        if (tag.contains("/")){
            String[] tags = tag.split("/");

            for (int i = 0; i < tags.length; i++){
                ArrayList<ArrayList <String>> temp = selectEateriesByTag(tags[i]);

                for (int j = 0; j < temp.size(); j++){
                    if (!eateries.contains(temp.get(j))){
                        eateries.add(temp.get(j));
                    }
                }
            }
        } else {
            eateries = selectEateriesByTag(tag);
        }

        // =========================================================================================
        // Card View -- View All
        // =========================================================================================

        // creates a Card View for each specified eatery
        for (int i = 0; i < eateries.size(); i++){
            // creates a new Card View
            CardView card = new CardView(getApplicationContext());

            // sets the card views layout parameter
            LinearLayout.LayoutParams cardParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT );

            // sets the margins for the card
            if (i != 0 && i != eateries.size() - 1)
                cardParam.setMargins(5, 45, 5, 45);
            else
                cardParam.setMargins(5, 0, 5, 0);

            card.setLayoutParams(cardParam);

            // sets the card views background color to white
            card.setCardBackgroundColor(Color.WHITE);

            // sets the cards radius
            card.setRadius(50);

            // sets teh cards elevation
            card.setCardElevation(2.1f);
            card.setMaxCardElevation(3f);

            // sets the cards padding
            card.setUseCompatPadding(true);

            // sets corner overlap to true
            card.setPreventCornerOverlap(true);

            // sets the tag to the eateries id
            int id = Integer.parseInt(eateries.get(i).get(0));
            card.setTag(tag);

            // allows the card view to be clickable
            card.setClickable(true);

            // adds the card to the linear layout
            viewAllLinearLayout.addView(card);

            // =====================================================================================
            // Linear Layout -- Card View
            // =====================================================================================

            // creates a new Relative Layout
            LinearLayout cardLinearLayout = new LinearLayout(getApplicationContext());

            // sets the card views relative layouts layout parameter
            LinearLayout.LayoutParams cardLinearLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            cardLinearLayout.setLayoutParams(cardLinearLayoutParam);

            // sets the cards orientation to vertical
            cardLinearLayout.setOrientation(LinearLayout.VERTICAL);

            // sets the cards one view to this linear layout
            card.addView(cardLinearLayout);

            // =========================================================================================
            // Card View -- Image View
            // =========================================================================================

            // creates a new Card View
            CardView cardImageView = new CardView(getApplicationContext());

            // sets the card views layout parameter
            LinearLayout.LayoutParams cardImageViewParam = new LinearLayout.LayoutParams(
                    1000,
                    500 );
            cardImageView.setLayoutParams(cardImageViewParam);

            // sets the cards radius
            cardImageView.setRadius(50);

            // adds the card to the linear layout
            cardLinearLayout.addView(cardImageView);

            // =====================================================================================
            // Eatery Image
            // =====================================================================================

            // gets the image that corresponds to the eateries id
            ImageView image = Util.getImage(id, this);

            // sets the eateries image layout parameters and relative constraints
            LinearLayout.LayoutParams eateryImageParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT );
            image.setLayoutParams(eateryImageParam);

            // adds the eateries image to the relative layout
            cardImageView.addView(image);

            // =====================================================================================
            // Eatery Text
            // =====================================================================================

            // initializes a new TextView to display the eateries name in the CardView
            TextView eateryName = new TextView(getApplicationContext());

            // sets the eateries title layout parameters and relative constraints
            LinearLayout.LayoutParams eateryNameParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    125);
            eateryName.setLayoutParams(eateryNameParam);

            // sets the text views text, size and color
            eateryName.setText(eateries.get(i).get(1));
            eateryName.setTextSize(TypedValue.TYPE_STRING, 12);
            eateryName.setTextColor(getResources().getColor(R.color.textColor));

            // sets the text views texts alignment and padding
            eateryName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            eateryName.setPadding(20, 0, 0, 15);

            // sets the card views background color to white
            eateryName.setBackgroundColor(Color.WHITE);

            // adds the text to the linear layout
            cardLinearLayout.addView(eateryName);
        }
    }

    // getScreenWidth()
    // pre: none
    // post:
    public int getScreenWidth(){
        // gets the size of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}
