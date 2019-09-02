package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class DatabaseHelper {
    private SQLiteOpenHelper openHelper;
    private static SQLiteDatabase database;
    private static DatabaseHelper instance;

    // =============================================================================================
    //                                        Helper Methods
    // =============================================================================================

    // DatabaseHelper()
    // pre: none
    // post: opens connection to the eateryDB database
    private DatabaseHelper(Context context) {
        this.openHelper = new DatabaseConnection(context);
    }

    // open()
    // pre: none
    // post: sets the database to writable
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    // close()
    // pre: the database must be opened
    // post: closes the database
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    // getInstance()
    // pre: none
    // post: returns an open connection to the database
    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    // =============================================================================================
    //                                        Eateries Table
    // =============================================================================================

    // selectEateriesByTag()
    // pre: none
    // post: returns a list of all eateries ids' and names that compare to the tag
    public static ArrayList<ArrayList<String>> selectEateriesByTag(String tag){
        ArrayList<ArrayList<String>> eateries = new ArrayList<ArrayList<String>>();

        String filters = "SELECT eatery_id, name FROM Eateries " +
                "WHERE name LIKE '%" + tag + "%' " +
                "OR category LIKE '%" + tag + "%' " +
                "OR location LIKE '%" + tag + "%' ;";

        // cursor executes sql and holds returned valued
        Cursor cr = database.rawQuery(filters, null);

        // stores each eateries name and id into the ArrayList to be returned
        while(cr.moveToNext()) {
            ArrayList<String> returnedEatery = new ArrayList<String>();

            returnedEatery.add(cr.getString(0));
            returnedEatery.add(cr.getString(1));

            eateries.add(returnedEatery);
        }

        return eateries;
    }

    // =============================================================================================
    //                                           Foods Table
    // =============================================================================================



    // =============================================================================================
    //                                           Hours Table
    // =============================================================================================

    private class DatabaseConnection extends SQLiteAssetHelper {
        private static final String DATABASE_NAME = "eateryDB";
        private static final int DATABASE_VERSION = 1;

        // DatabaseConnection()
        // pre: none
        // pre: opens connection to the eateryDB database
        public DatabaseConnection(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
    }
}
