package com.example.databaseapp;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.example.databaseapp.ui.petContract;
import com.example.databaseapp.ui.petContract.petEntry;
public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c) {
        super(context,c,0);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.pet_list, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.list_item1);
        TextView summaryTextView = (TextView) view.findViewById(R.id.list_item2);


       // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(petEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(petEntry.COLUMN_PET_BREED);

        // Extract properties from cursor
        String petName = cursor.getString(nameColumnIndex);
        String petBreed = cursor.getString(breedColumnIndex);

        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(petBreed)) {
            petBreed = context.getString(R.string.unknown_breed);
        }


        nameTextView.setText(petName);
        summaryTextView.setText(petBreed);



    }





}

