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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.databaseapp.ui.petAdapter;
import com.example.databaseapp.ui.petAdapter2;
import com.example.databaseapp.ui.petContract.petEntry;
import com.example.databaseapp.ui.petContract;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.databaseapp.RecyclerViewAdapter.RecyclerViewCursorAdapter;
import com.example.databaseapp.RecyclerViewAdapter.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {



     private static final int PET_LOADER = 0;
     PetDbHelper2 mDbHelper2 ;
     private RecyclerView mRecyclerView;
     private RVAdapter rvAdapter;
     private Cursor cursor;
    private Button btnLogOut;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;




    @Override
    protected void onStart() {
        super.onStart();
        Log.e("TAG", "ONStart");
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, login.class));
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.e("TAG", "ONResume");
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        mDbHelper2 = new PetDbHelper2(this);
        View emptyView = findViewById(R.id.empty_view2);
        mAuth = FirebaseAuth.getInstance();



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
                showDeleteConfirmationDialog(id);
            }
        });


        mRecyclerView.setAdapter( rvAdapter);

        RVEmptyObserver observer = new RVEmptyObserver(mRecyclerView, emptyView);
        rvAdapter.registerAdapterDataObserver(observer);

        LoaderManager.getInstance(this).initLoader(PET_LOADER, null, this);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_catalog, menu);
        return super.onCreateOptionsMenu(menu);
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

            case R.id.action_logout:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, login.class));
                finish();
                return true;
        }


        return super.onOptionsItemSelected(item);

    }








    private void insertDb(){

       // SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(petEntry.COLUMN_PET_NAME , "Todo");
        values.put(petEntry.COLUMN_PET_BREED , "Terrier");
        values.put(petEntry.COLUMN_PET_GENDER , petEntry.GENDER_MALE);
        values.put(petEntry.COLUMN_PET_WEIGHT , 7);
        values.put(petEntry.COLUMN_PET_PRICE, 50);


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
                petEntry.COLUMN_PET_WEIGHT,
                petEntry.COLUMN_PET_PRICE};



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


