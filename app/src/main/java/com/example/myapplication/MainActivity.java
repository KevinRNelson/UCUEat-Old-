package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import android.os.Parcel;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;

import android.util.TypedValue;
import java.util.ArrayList;

import static com.example.myapplication.DatabaseHelper.selectEateriesByTag;

public class MainActivity extends AppCompatActivity {

    public static DatabaseHelper db;
    private FloatingSearchView searchBar;
    private LinearLayout eateryLinearLayout;
    private DynamicScrollView eateryScrollView;
    private String mainMenuStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.nav_eateries);

        // allows for continuous updating while searching
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        // opens connection to the database
        db = DatabaseHelper.getInstance(this);
        db.open();


        // sets up a connection to the Floating SearchView
        searchBar = findViewById(R.id.eateries_search_view); // inititate a search view
        setSearchBar();

        // sets connection to the scroll view's linear layout on the main page
        eateryLinearLayout = findViewById(R.id.mainLinearLayout);

        // sets connection to the scroll view on the main page
        eateryScrollView = findViewById(R.id.mainScrollView);

        // removes scroll bar in main scroll view
        ScrollView eateryScrollView = findViewById(R.id.mainScrollView);
        eateryScrollView.setVerticalScrollBarEnabled(false);

        // removes all scroll bars from each horizontal scroll
        HorizontalScrollView eateryOptionsScrollView = findViewById(R.id.options_horizontal_scroll_view);
        eateryOptionsScrollView.setHorizontalScrollBarEnabled(false);

        // displays the eateryOptions
        // setEaterySearchOptions();

        // sets the default main page layout
        setDefaultView();
    }

    // =============================================================================================
    // Modifiers
    // =============================================================================================


    // setScreenStyle()
    // pre: none
    // post: changes the screen style to the specified string
    public void setMainMenuStyle(String style){
        mainMenuStyle = style;

        if (!style.equals("vertical")){
            //eateryScrollView.setScrolling(true);
        } else {
            //eateryScrollView.setScrolling(false);
        }
    }


    // setSearchBar()
    // pre: none
    // post: handles the programmatically needed functions of the search bar
    public void setSearchBar(){
        // brings the search bar and its suggestions as the main focus of this activity
        searchBar.bringToFront();

        // sets the suggestion text
        searchBar.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if (!oldQuery.equals(newQuery)){
                    final ArrayList<ArrayList<String>> suggestedEateries = db.selectEateriesByTag(newQuery);
                    ArrayList<SearchSuggestion> suggestions = new ArrayList<SearchSuggestion>();

                    for (int i = 0; i < suggestedEateries.size(); i++) {
                        final int index = i;
                        SearchSuggestion suggestion = new SearchSuggestion() {
                            String body = suggestedEateries.get(index).get(1);

                            @Override
                            public String getBody() {
                                return this.body;
                            }

                            @Override
                            public int describeContents() {
                                return 0;
                            }

                            @Override
                            public void writeToParcel(Parcel parcel, int i) {
                            }
                        };

                        suggestions.add(suggestion);
                    }

                    //pass them on to the search view
                    searchBar.swapSuggestions(suggestions);
                }
            }
        });

        searchBar.setOnLeftMenuClickListener(
                new FloatingSearchView.OnLeftMenuClickListener() {
                    @Override
                    public void onMenuOpened() {
                    }

                    @Override
                    public void onMenuClosed() {
                    }
                } );

        searchBar.setOnSearchListener(
                new FloatingSearchView.OnSearchListener() {

                    @Override
                    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                        Log.d("search", searchSuggestion.getBody());
                    }

                    @Override
                    public void onSearchAction(String currentQuery) {
                        ArrayList<ArrayList <String>> searchEateries = MainActivity.db.selectEateriesByTag(currentQuery);
                        if (searchEateries.size() > 0) {
                            if (mainMenuStyle.equals("default"))
                                clearViews();

                            addToView(Util.toTitle(currentQuery), currentQuery);
                        }
                    }
                }
        );

        searchBar.setOnHomeActionClickListener(
                new FloatingSearchView.OnHomeActionClickListener() {
                    @Override
                    public void onHomeClicked() {
                    }
                });

        searchBar.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
            }

        });
    }


    // =============================================================================================
    // OnClicks
    // =============================================================================================

    // eaterySearchOptions()
    // pre: none
    // post: adds or removes the specified eateries from the main menu
    public void eaterySearchOptions(View view){
        // obtains the correct chip
        Chip chip = findViewById(view.getId());

        // checks whether we need to add or remove the corresponding view from the layout
        if (chip.getChipBackgroundColor().getDefaultColor() == getResources().getColor(R.color.white)){
            // set the chip to gray
            chip.setChipBackgroundColorResource(R.color.textColor);

            // checks whether we remove the view or not
            if (!mainMenuStyle.equals("horizontal")){
                // change the view to the default view
                resetOptions(chip.getTag().toString());

                // clear the view
                clearViews();
            }

            // adds the corresponding view to the layout
            addToView(chip.getText().toString(), chip.getTag().toString());
        } else if (chip.getChipBackgroundColor().getDefaultColor() == getResources().getColor(R.color.textColor)){
            // set the chip to white
            chip.setChipBackgroundColorResource(R.color.white);

            // remove the corresponding view from the layout
            removeFromView(chip.getTag().toString());

            // checks whether we should set the view to the default view or not
            if (getLayoutCount() == 0){
                // change the view to the default view
                setDefaultView();
            }
        }
    }

    // =============================================================================================
    // View Accessors
    // =============================================================================================

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


    // getLayoutCount()
    // pre: none
    // post: returns the number of eatery horizontal scroll views
    public int getLayoutCount(){
        LinearLayout ll = findViewById(R.id.mainLinearLayout);

        return (ll.getChildCount() / 2);
    }

    // =============================================================================================
    // View Modifiers
    // =============================================================================================

    // setEaterySearchOptions()
    // pre: none
    // post: creates 10 different buttons to help direct user searches
    public void setEaterySearchOptions(){
        // disables the scroll bar on the horizontal scroll view
        HorizontalScrollView eateryOptionsScrollView = findViewById(R.id.options_horizontal_scroll_view);
        eateryOptionsScrollView.setHorizontalScrollBarEnabled(false);

        // text for each chip button
        String[] eateryOptions = {
                "Porter/Kresge",
                "College 9/10",
                "Crown Merill",
                "Rachel Carson/Oakes",
                "Cowell Stevenson",
                "Science Hill",
                "Quarry",
                "Dining Halls",
                "Cafes",
                "Food trucks"
        };

        // connects to the horizontal linear layout that holds the chips
        LinearLayout hll = findViewById(R.id.options_horizontal_linear_layout);

        for (int i = 0; i < eateryOptions.length; i++){
            // creates a new chip
            Chip eateryOption = new Chip(this);

            // sets the chips id
            //eateryOption.setId("chips" + i);

            LinearLayout.LayoutParams chipParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT );
            chipParam.setMargins(8, 8, 0, 0);

            // sets the chips text
            eateryOption.setText(eateryOptions[i]);

            // sets the chips tag
            eateryOption.setTag(eateryOptions[i]);

            //eateryOption.setOnClickListener();

            // sets the chips background color and outline
            eateryOption.setChipBackgroundColorResource(R.color.white);
            eateryOption.setChipStrokeColorResource(R.color.black);
            eateryOption.setChipStrokeWidth(1);

            // adds the chip to the horizontal linear layout
            hll.addView(eateryOption);
        }
    }


    // setDefaultView()
    // pre: none
    // post: sets the pages view to be categorized by eatery
    public void setDefaultView(){
        addToView(getString( R.string.food_trucks), "foodtruck");
        addToView(getString(       R.string.cafes), "cafe");
        addToView(getString(R.string.dining_halls), "dininghall");

        setMainMenuStyle("default");
    }


    // addToView()
    // pre: none
    // post: adds the specified eateries as a horizontal scroll group under the tile given
    public void addToView(String title, String tag){
        // adds all eateries that correspond to the tile to the horizontal scroll view
        addEateries(tag);

        // adds the broadened title of all the selected eateries
        addTitle(title, tag);

        setMainMenuStyle("horizontal");
    }


    // addTitle()
    // pre: none
    // post: adds a title above the corresponding horizontal scroll view
    public void addTitle(final String title, final String tag){
        // =====================================================================================
        // Relative Layout -- Title
        // =====================================================================================
        // creates a new Relative Layout
        RelativeLayout titleRelativeLayout = new RelativeLayout(getApplicationContext());

        // sets the card views relative layouts layout parameter
        RelativeLayout.LayoutParams titleRelativeLayoutParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        titleRelativeLayout.setLayoutParams(titleRelativeLayoutParam);

        // sets the horizontal linear layouts tag
        titleRelativeLayout.setTag(tag);

        // adds the horizontal linear layout to the horizontal scroll view
        eateryLinearLayout.addView(titleRelativeLayout, 0);

        // =========================================================================================
        // Title
        // =========================================================================================
        // creates a new text view
        TextView eateriesTitle = new TextView(getApplicationContext());

        // sets the layout parameters for the title
        RelativeLayout.LayoutParams titleParam = new RelativeLayout.LayoutParams(
                (getScreenWidth() * 2) / 3,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        titleParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        eateriesTitle.setLayoutParams(titleParam);

        // sets the text views text, size, and color
        eateriesTitle.setText(title);
        eateriesTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
        eateriesTitle.setTextColor(getResources().getColor(R.color.textColor));
        //eateriesTitle.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        // sets the text views tag to match the title
        eateriesTitle.setTag(tag);

        // adds specified title to the page
        titleRelativeLayout.addView(eateriesTitle);

        // =========================================================================================
        // View All
        // =========================================================================================
        // creates a new text view to view all
        final TextView viewAll = new TextView(getApplicationContext());

        // sets the text views layout parameters, and pushes text to the bottom right
        RelativeLayout.LayoutParams viewAllParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT );
        viewAllParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        viewAllParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        viewAll.setLayoutParams(viewAllParam);

        // sets the text views text, size, text color, and background color
        viewAll.setText(R.string.view_all);
        viewAll.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        viewAll.setTextColor(getResources().getColor(R.color.textColor));

        // sets the text views tag
        viewAll.setTag(tag);

        // sets the text views onClick
        viewAll.setOnClickListener(new View.OnClickListener() {

            // onClick()
            // pre: none
            // post: activates the view all pop up window for the specified eateries
            @Override
            public void onClick(View v) {
                // adds scroll view of specified eateries
                Intent viewAllPopUp = new Intent(MainActivity.this, ViewAllPopUp.class);
                viewAllPopUp.putExtra("title", title);
                viewAllPopUp.putExtra(  "tag",   tag);

                startActivity(viewAllPopUp);
            }

        });

        // adds the text view to the horizontal linear layout
        //hll.addView(viewAll);
        titleRelativeLayout.addView(viewAll);

        // =========================================================================================
        // Remove Horizontal View
        // =========================================================================================
        // creates a image view
        final ImageView closePopUp = new ImageView(getApplicationContext());

        // sets the image views layout parameters, and pushes image to the bottom right
        RelativeLayout.LayoutParams closePopUpParam = new RelativeLayout.LayoutParams(
                getScreenWidth() / 10,
                getScreenWidth() / 10);
        closePopUpParam.setMargins(0, 0, 0, 0);
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
            // post: activates the view all pop up window for the specified eateries
            @Override
            public void onClick(View v) {
                removeFromView(v.getTag().toString());

                if (v.getTag().toString().equals(findViewById(R.id.chip0).getTag().toString())){
                    ((Chip) findViewById(R.id.chip0)).setChipBackgroundColorResource(R.color.white);
                } else if (v.getTag().toString().equals(findViewById(R.id.chip1).getTag().toString())){
                    ((Chip) findViewById(R.id.chip1)).setChipBackgroundColorResource(R.color.white);
                } else if (v.getTag().toString().equals(findViewById(R.id.chip2).getTag().toString())){
                    ((Chip) findViewById(R.id.chip2)).setChipBackgroundColorResource(R.color.white);
                } else if (v.getTag().toString().equals(findViewById(R.id.chip3).getTag().toString())){
                    ((Chip) findViewById(R.id.chip3)).setChipBackgroundColorResource(R.color.white);
                } else if (v.getTag().toString().equals(findViewById(R.id.chip4).getTag().toString())){
                    ((Chip) findViewById(R.id.chip4)).setChipBackgroundColorResource(R.color.white);
                } else if (v.getTag().toString().equals(findViewById(R.id.chip5).getTag().toString())){
                    ((Chip) findViewById(R.id.chip5)).setChipBackgroundColorResource(R.color.white);
                } else if (v.getTag().toString().equals(findViewById(R.id.chip6).getTag().toString())){
                    ((Chip) findViewById(R.id.chip6)).setChipBackgroundColorResource(R.color.white);
                } else if (v.getTag().toString().equals(findViewById(R.id.chip7).getTag().toString())){
                    ((Chip) findViewById(R.id.chip7)).setChipBackgroundColorResource(R.color.white);
                } else if (v.getTag().toString().equals(findViewById(R.id.chip8).getTag().toString())){
                    ((Chip) findViewById(R.id.chip8)).setChipBackgroundColorResource(R.color.white);
                } else if (v.getTag().toString().equals(findViewById(R.id.chip9).getTag().toString())){
                    ((Chip) findViewById(R.id.chip9)).setChipBackgroundColorResource(R.color.white);
                }
            }
        });

        // adds the text view to the horizontal linear layout
        titleRelativeLayout.addView(closePopUp);
    }


    // addEateries()
    // pre: none
    // post: adds the given eateries into a horizontal scroll view
    public void addEateries(String tag){
        // =========================================================================================
        // Obtains Selected Eateries
        // =========================================================================================

        // gets the id and name of every eatery that correspond
        ArrayList<ArrayList <String>> eateries = new ArrayList<ArrayList <String>>();

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
        // Horizontal Scroll View
        // =========================================================================================

        // creates new horizontal scroll view
        HorizontalScrollView hsv = new HorizontalScrollView(getApplicationContext());
        HorizontalScrollView.LayoutParams horizontalScrollView = new HorizontalScrollView.LayoutParams(
                HorizontalScrollView.LayoutParams.WRAP_CONTENT,
                HorizontalScrollView.LayoutParams.WRAP_CONTENT );
        hsv.setLayoutParams(horizontalScrollView);

        // disables the scroll bar
        hsv.setHorizontalScrollBarEnabled(false);

        // sets the horizontal scroll views tag
        hsv.setTag(tag);

        // removes gray tint when you scroll too far to the left or right
        hsv.setOverScrollMode(hsv.OVER_SCROLL_NEVER);

        // adds the horizontal scroll view to the linear layout
        eateryLinearLayout.addView(hsv, 0);

        // =========================================================================================
        // Linear Layout
        // =========================================================================================

        // creates new horizontal linear layout
        LinearLayout eateryLinearLayout = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams horizontalLinearLayoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT ,
                LinearLayout.LayoutParams.MATCH_PARENT );
        eateryLinearLayout.setLayoutParams(horizontalLinearLayoutParam);
        eateryLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        // fixes issue where the last card view would be cut off
        eateryLinearLayout.setBaselineAligned(false);

        // adds the horizontal linear layout to the horizontal scroll view
        hsv.addView(eateryLinearLayout);

        // =========================================================================================
        // Card View
        // =========================================================================================

        // creates a Card View for each specified eatery
        for (int i = 0; i < eateries.size(); i++){
            // creates a new Card View
            CardView card = new CardView(getApplicationContext());

            // sets the card views layout parameter
            LinearLayout.LayoutParams cardParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT );

            // sets the margins for the card
            if (i != 0 && i != eateries.size() - 1)
                cardParam.setMargins(45, 0, 45, 0);
            else
                cardParam.setMargins(0, 0, 0, 0);
            card.setLayoutParams(cardParam);

            // sets the card views background color to white
            card.setCardBackgroundColor(Color.WHITE);

            // sets the cards radius
            card.setRadius(50);

            // sets the cards elevation
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

            // =====================================================================================
            // Linear Layout -- Card View
            // =====================================================================================

            // creates a new Relative Layout
            LinearLayout cardLinearLayout = new LinearLayout(getApplicationContext());

            // sets the card views relative layouts layout parameter
            LinearLayout.LayoutParams cardLinearLayoutParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            cardLinearLayout.setLayoutParams(cardLinearLayoutParam);

            cardLinearLayout.setOrientation(LinearLayout.VERTICAL);

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

            // sets the card views background color to white
            cardImageView.setCardBackgroundColor(Color.WHITE);

            // sets the cards radius
            cardImageView.setRadius(50);

            // adds the card view to its parent card view
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
            // Constraint Layout
            // =====================================================================================

            // =====================================================================================
            // Eatery Text
            // =====================================================================================

            // initializes a new TextView to display the eateries name in the CardView
            TextView eateryName = new TextView(getApplicationContext());

            // sets the eateries title layout parameters and relative constraints
            LinearLayout.LayoutParams eateryNameParam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    125 );
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

            // adds the text view to the relative layout
            cardLinearLayout.addView(eateryName);

            // adds the complete relative layout to the card view
            card.addView(cardLinearLayout);

            // adds the complete card to the horizontal linear layout
            eateryLinearLayout.addView(card);
        }
    }


    // viewAll()
    // pre: none
    // post: changes main menus view to a vertical scroll list of the specified eateries
    public void viewAll(String title, String tag){
        // =========================================================================================
        // Title
        // =========================================================================================

        // creates a new text view
        TextView viewAllTitle = new TextView(getApplicationContext());

        // sets the layout parameters for the title
        LinearLayout.LayoutParams titleParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT );
        viewAllTitle.setLayoutParams(titleParam);

        // sets the text views text, size, and color
        viewAllTitle.setText(title);
        viewAllTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
        viewAllTitle.setTextColor(getResources().getColor(R.color.textColor));

        // sets the text views tag to match the title
        viewAllTitle.setTag(tag);

        // adds specified title to the page
        eateryLinearLayout.addView(viewAllTitle);

        // =========================================================================================
        // Scroll View
        // =========================================================================================

        // creates a new scroll view to hold the specified eateries
        DynamicScrollView viewAllScrollView = new DynamicScrollView(getApplicationContext());

        LinearLayout.LayoutParams viewAllScrollViewParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT );
        viewAllScrollView.setLayoutParams(viewAllScrollViewParam);

        // sets the scroll views tag
        viewAllScrollView.setTag(tag);

        // allows the scroll view to scroll
        viewAllScrollView.setScrolling(true);

        // adds the scroll view to the main page
        eateryLinearLayout.addView(viewAllScrollView);

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
        ArrayList<ArrayList <String>> eateries = new ArrayList<ArrayList <String>>();

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
                cardParam.setMargins(0, 45, 0, 45);
            else
                cardParam.setMargins(0, 0, 0, 0);

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


    // removeFromView()
    // pre: none
    // post: removes all specified eateries with the desired tag
    public void removeFromView(String tag){
        while (eateryLinearLayout.findViewWithTag(tag) != null){
            eateryLinearLayout.removeView(eateryLinearLayout.findViewWithTag(tag));
        }

        if (getLayoutCount() == 0){
            setDefaultView();
        }
    }


    // clearsView()
    // pre: none
    // post: removes all views from the scroll view
    public void clearViews(){
        // clears all views from the scroll view -- reset
        eateryLinearLayout.removeAllViews();
    }


    // resetOptions()
    // pre: none
    // post: reset all options to white
    public void resetOptions(String tag){
        Chip chip0 = findViewById(R.id.chip0);
        if (!chip0.getTag().toString().equals(tag)){
            chip0.setChipBackgroundColorResource(R.color.white);
        }

        Chip chip1 = findViewById(R.id.chip1);
        if (!chip1.getTag().toString().equals(tag)){
            chip1.setChipBackgroundColorResource(R.color.white);
        }

        Chip chip2 = findViewById(R.id.chip2);
        if (!chip2.getTag().toString().equals(tag)){
            chip2.setChipBackgroundColorResource(R.color.white);
        }

        Chip chip3 = findViewById(R.id.chip3);
        if (!chip3.getTag().toString().equals(tag)){
            chip3.setChipBackgroundColorResource(R.color.white);
        }

        Chip chip4 = findViewById(R.id.chip4);
        if (!chip4.getTag().toString().equals(tag)){
            chip4.setChipBackgroundColorResource(R.color.white);
        }

        Chip chip5 = findViewById(R.id.chip5);
        if (!chip5.getTag().toString().equals(tag)){
            chip5.setChipBackgroundColorResource(R.color.white);
        }

        Chip chip6 = findViewById(R.id.chip6);
        if (!chip6.getTag().toString().equals(tag)){
            chip6.setChipBackgroundColorResource(R.color.white);
        }

        Chip chip7 = findViewById(R.id.chip7);
        if (!chip7.getTag().toString().equals(tag)){
            chip7.setChipBackgroundColorResource(R.color.white);
        }

        Chip chip8 = findViewById(R.id.chip8);
        if (!chip8.getTag().toString().equals(tag)){
            chip8.setChipBackgroundColorResource(R.color.white);
        }

        Chip chip9 = findViewById(R.id.chip9);
        if (!chip9.getTag().toString().equals(tag)){
            chip9.setChipBackgroundColorResource(R.color.white);
        }
    }

    // =============================================================================================
    // Navigation Bar
    // =============================================================================================

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        // onNavigationItemSelected()
        // pre: none
        // post: takes the user to the correct activity
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_eateries:
                    return true;
                case R.id.nav_map:
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                    return true;
                case R.id.nav_search:
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
                    return true;
                case R.id.nav_favorites:
                    startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                    return true;
                case R.id.nav_account:
                    startActivity(new Intent(MainActivity.this, AccountActivity.class));
                    return true;
            }
            return false;
        }
    };
}
