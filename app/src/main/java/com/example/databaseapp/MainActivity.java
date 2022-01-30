package com.example.databaseapp;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentUris;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import com.example.databaseapp.ui.petAdapter;
import com.example.databaseapp.ui.petAdapter2;
import com.example.databaseapp.ui.petContract.petEntry;
import com.example.databaseapp.ui.petContract;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.databaseapp.RecyclerViewAdapter.RecyclerViewCursorAdapter;
import com.example.databaseapp.RecyclerViewAdapter.*;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {



     private static final int PET_LOADER = 0;
     PetDbHelper mDbHelper ;
     private RecyclerView mRecyclerView;
     private RVAdapter rvAdapter;
     private Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main2);

        mDbHelper = new PetDbHelper(this);


        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        // Adding RecyclerView

        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rvAdapter = new RVAdapter(this, cursor, new RVAdapter.MyAdapterListener() {
            @Override
            public void editButtonViewOnClick(View v, int position, long id) {
                Intent intent = new Intent(MainActivity.this , EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(petEntry.CONTENT_URI , id);
                intent.setData(currentPetUri);
                startActivity(intent);
                Log.v("CatalogActivity", petEntry.TABLE_NAME + petEntry._ID + " = " + id);
            }

            @Override
            public void deleteButtonViewOnClick(View v, int position, long id) {
              //  Uri currentPetUri = ContentUris.withAppendedId(petEntry.CONTENT_URI , id);
              //  int rowsDeleted = getContentResolver().delete(currentPetUri, null, null);
              //  Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
                showDeleteConfirmationDialog(id);
               // deletePet(id);

            }
        });


        mRecyclerView.setAdapter( rvAdapter);


        LoaderManager.getInstance(this).initLoader(PET_LOADER, null, this);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_insert_dummy_data:
                insertDb();
                return true;


            case R.id.action_delete_all_entries:
                showDeleteAllConfirmationDialog();

                return true;
        }
        return super.onOptionsItemSelected(item);

    }








    private void insertDb(){

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(petEntry.COLUMN_PET_NAME , "Todo");
        values.put(petEntry.COLUMN_PET_BREED , "Terrier");
        values.put(petEntry.COLUMN_PET_GENDER , petEntry.GENDER_MALE);
        values.put(petEntry.COLUMN_PET_WEIGHT , 7);


        Log.i("MainActivity", "Dummy Data has been stored successfully ");

        Uri newUri = getContentResolver().insert(petEntry.CONTENT_URI, values);

    }







    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {
                petEntry._ID,
                petEntry.COLUMN_PET_NAME,
                petEntry.COLUMN_PET_BREED,
                petEntry.COLUMN_PET_GENDER,
                petEntry.COLUMN_PET_WEIGHT };



        return new CursorLoader(this ,
                petEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

    }


        @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
            rvAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        rvAdapter.swapCursor(null);


    }



    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(petEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }



    private void showDeleteConfirmationDialog(long my_id) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet(my_id);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void showDeleteAllConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete all pets");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllPets();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void deletePet(long id) {
        Uri currentPetUri = ContentUris.withAppendedId(petEntry.CONTENT_URI , id);
        int rowsDeleted = getContentResolver().delete(currentPetUri, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

}



//Find and set empty view on the ListView, so that it only shows when the list has 0 items.
//View emptyView = findViewById(R.id.empty_view);
// mRecyclerView.setEmptyView(emptyView);

// mCursorAdapter = new PetCursorAdapter(this, null);

//  petListView.setAdapter( mCursorAdapter);
