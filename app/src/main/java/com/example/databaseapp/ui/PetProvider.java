package com.example.databaseapp.ui;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.databaseapp.PetDbHelper;
import com.example.databaseapp.PetDbHelper2;
import com.example.databaseapp.ui.petContract.petEntry;



public class PetProvider extends ContentProvider {

    private PetDbHelper2 mDbHelper2 ;
    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PET_ID = 101;


    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {

           sUriMatcher.addURI(petEntry.CONTENT_AUTHORITY , petEntry.PATH_PETS , PETS);
           sUriMatcher.addURI(petEntry.CONTENT_AUTHORITY , petEntry.PATH_PETS+"/#" , PET_ID);

    }



    @Override
    public boolean onCreate() {
        mDbHelper2 = new PetDbHelper2(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder){

        mDbHelper2 = new PetDbHelper2(getContext());
        SQLiteDatabase database = mDbHelper2.getReadableDatabase();

        Cursor cursor ;

        int match = sUriMatcher.match(uri);
        switch (match){

            case PETS:
                cursor = database.query(petEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            case PET_ID:
                selection = petEntry._ID + "+?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(petEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);


        }


        cursor.setNotificationUri(getContext().getContentResolver() , uri);
         return cursor;
     //   return  null;
    }



    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + petEntry.TABLE_NAME + " ("
                + petEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + petEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + petEntry.COLUMN_PET_BREED + " TEXT, "
                + petEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + petEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0,  "
                + petEntry.COLUMN_PET_PRICE + " INTEGER NOT NULL);";

        Log.e(".PetProvider" , SQL_CREATE_PETS_TABLE);
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(petEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        Integer weight = values.getAsInteger(petEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Please enter valid the weight"); }


        Integer gender = values.getAsInteger(petEntry.COLUMN_PET_GENDER);
        if (gender == null || !petEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }


        // Get writeable database
        SQLiteDatabase database = mDbHelper2.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(petEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri , null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:

                selection = petEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Sanity checks

        if (values.containsKey(petEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(petEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }


        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(petEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(petEntry.COLUMN_PET_GENDER);
            if (gender == null || !petEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(petEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(petEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if (values.size() == 0) {
            return 0;
        }


        SQLiteDatabase database = mDbHelper2.getWritableDatabase();

        int rowsUpdated = database.update(petEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
        //return database.update(petEntry.TABLE_NAME, values, selection, selectionArgs);
    }




    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
       // PetDbHelper mDbHelper = new PetDbHelper(getContext());
        SQLiteDatabase database = mDbHelper2.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:

               // return database.delete(petEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted = database.delete(petEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PET_ID:

                selection = petEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(petEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }



    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return petEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return petEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }



}