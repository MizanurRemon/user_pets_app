package com.example.databaseapp;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.databaseapp.ui.petContract.petEntry;
import com.example.databaseapp.ui.petContract;



/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class PetDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = PetDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 2;


    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + petContract.petEntry.TABLE_NAME + " ("
                + petContract.petEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + petEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + petEntry.COLUMN_PET_BREED + " TEXT, "
                + petEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + petEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0,"
                + petEntry.COLUMN_PET_PRICE + "INTEGER NOT NULL);";
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + petEntry.TABLE_NAME);
            onCreate(db);
        }
    }


    }












