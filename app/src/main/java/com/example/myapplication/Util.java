package com.example.myapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Util {

    //
    //
    //

    // toTitle()
    // pre: none
    // post: returns a copy of the given string with the first letter of each word capitalized
    public static String toTitle(String preTitle){
        String[] arrayTitle = preTitle.split(" ");
        String title = "";

        for (int i = 0; i < arrayTitle.length; i++){
            title += arrayTitle[i].substring(0, 1).toUpperCase() + arrayTitle[i].substring(1, arrayTitle[i].length()).toLowerCase();

            if (i != arrayTitle.length - 1)
                title += " ";
        }

        return title;
    }


    // =============================================================================================
    //
    // =============================================================================================


    // getImage()
    // pre: none
    // post:
    public static ImageView getImage(int id, Context context){
        ImageView image = new ImageView(context);
        image.setScaleType(ImageView.ScaleType.FIT_XY);

        // gets the file path to the needed eateries image
        AssetManager assetManager = context.getAssets();
        String file = "images/image"+id+".jpg";

        // sets the image objects image to the required eatery image
        try {
            InputStream is = assetManager.open(file);
            Drawable d = Drawable.createFromStream(is, null);

            image.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    // =============================================================================================
    // Shared Preferences
    // =============================================================================================


}
