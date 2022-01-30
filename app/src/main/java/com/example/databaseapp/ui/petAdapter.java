package com.example.databaseapp.ui;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.databaseapp.EditorActivity;
import com.example.databaseapp.MainActivity;
import com.example.databaseapp.PetDbHelper;
import com.example.databaseapp.R;
import com.example.databaseapp.pet;

import org.jetbrains.annotations.Contract;
import com.example.databaseapp.ui.petContract.petEntry;
import com.example.databaseapp.ui.petContract;
import java.util.ArrayList;

public class petAdapter extends RecyclerView.Adapter<petAdapter.ViewHolder> {


    private final Context context;
    private Cursor mCursor;



    public petAdapter(Context context) {
        this.context = context;


    }


    @NonNull
    @Override
    public petAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.pet_list3, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull petAdapter.ViewHolder holder, int position) {

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = mCursor.getColumnIndex(petEntry.COLUMN_PET_NAME);
        int breedColumnIndex = mCursor.getColumnIndex(petEntry.COLUMN_PET_BREED);
        int WeightColumnIndex = mCursor.getColumnIndex(petEntry.COLUMN_PET_WEIGHT);
        int genderColumnIndex = mCursor.getColumnIndex(petEntry.COLUMN_PET_GENDER);

        mCursor.moveToPosition(position);

        String petName = mCursor.getString(nameColumnIndex);
        String petBreed = mCursor.getString(breedColumnIndex);
        int petWeight = mCursor.getInt(WeightColumnIndex);
        int petGender = mCursor.getInt(genderColumnIndex);

        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(petBreed)) {
            petBreed = context.getString(R.string.unknown_breed);
        }


        holder.name.setText(petName);
        holder.breed.setText(petBreed);
        holder.weight.setText(petWeight + "");
        switch (petGender) {
            case (0):
                holder.gender.setText("Unknown" + "");
                break;

            case (1):
                holder.gender.setText("Male" + "");
                break;

            case (2):
                holder.gender.setText("Female" + "");
                break;

        }


        //Add functionality to edit button
    /*   holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getBindingAdapterPosition();
                long id = holder.getItemId();
                Intent intent = new Intent(context, EditorActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(petEntry.CONTENT_URI, holder.getBindingAdapterPosition());
                intent.setData(currentPetUri);


                context.startActivity(intent);

                CharSequence text = "Edit button clicked";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });


        //Add functionality to edit button
        holder.delete.setOnClickListener(new View.OnClickListener() {

            Uri mCurrentPetUri = ContentUris.withAppendedId(petEntry.CONTENT_URI, position);
            PetDbHelper mDbHelper = new PetDbHelper(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            PetProvider petProvider = new PetProvider();
            final String uri = ""+mCurrentPetUri;
            int rowsDeleted;

            @Override
            public void onClick(View v) {
                    if (mCurrentPetUri != null) {
                        // Call the ContentResolver to delete the pet at the given content URI.
                        // Pass in null for the selection and selection args because the mCurrentPetUri
                        // content URI already identifies the pet that we want.
                       // int rowsDeleted = db.delete(petEntry.TABLE_NAME , petEntry._ID + "=?" ,  new String[] { String.valueOf(holder.getBindingAdapterPosition())});
                        String selection = petEntry._ID + "=?";
                        String[] selectionArgs = new String[] { String.valueOf(ContentUris.parseId(mCurrentPetUri)) };
                        rowsDeleted = db.delete(petEntry.TABLE_NAME, selection, selectionArgs);


                        // Show a toast message depending on whether or not the delete was successful.
                        if (rowsDeleted == 0) {
                            // If no rows were deleted, then there was an error with the delete.
                            Toast.makeText(context, "Deletion Failed",
                                    Toast.LENGTH_SHORT).show();
                            Log.v("CatalogActivity",    petEntry.TABLE_NAME + petEntry._ID + " = " + position);
                        } else {
                            // Otherwise, the delete was successful and we can display a toast.

                            Toast.makeText(context, "Delete Successfull",
                                    Toast.LENGTH_SHORT).show();
                            Log.v("CatalogActivity", rowsDeleted +" petEntry.CONTENT_URI "+ holder.getBindingAdapterPosition());
                        }
                    }

            }
        }); */


    }


    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }


    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }



    //public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        //pet list 2 inflate
        TextView name, breed, weight, gender, price;
        Button edit, delete;


        public ViewHolder(View v) {
            super(v);
            //v.setOnClickListener(this);

            name = (TextView) v.findViewById(R.id.textView4);
            breed = (TextView) v.findViewById(R.id.textView5);
            weight = (TextView) v.findViewById(R.id.textView6);
            gender = (TextView) v.findViewById(R.id.textView8);
            //  price = (TextView) v.findViewById(R.id.petListPrice);

            edit = (Button) v.findViewById(R.id.petListEdit);
            delete = (Button) v.findViewById(R.id.petListDelete);

    //        edit.setOnClickListener(this);
            delete.setOnClickListener(this);


        }

    /* public void onClick(View view) {
             Log.d("ClickFromViewHolder", "Clicked");

            long id = getItemId();
            Intent intent = new Intent(context , EditorActivity.class);
            Uri currentPetUri = ContentUris.withAppendedId(petEntry.CONTENT_URI , getBindingAdapterPosition());
            intent.setData(currentPetUri);
            // intent.setData(currentPetUri);
            context.startActivity(intent);


        }
    } */


    @Override
    public void onClick(View view) {
        Log.d("ClickFromViewHolder", "Clicked");

        PetDbHelper mDbHelper = new PetDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        PetProvider petProvider = new PetProvider();
        int rowsDeleted;

        int id = getBindingAdapterPosition();
        view.setId(id);

        Uri currentPetUri = ContentUris.withAppendedId(petEntry.CONTENT_URI , id);
        String selection = petEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(ContentUris.parseId(currentPetUri)) };
        rowsDeleted = db.delete(petEntry.TABLE_NAME, selection, selectionArgs);
        Log.v("CatalogActivity", rowsDeleted +" petEntry.CONTENT_URI "+ id);

    }
}





   // int getId(int position){
 //       return  data.get(position).ID;
 //   }


    }





   /* public class ViewHolder extends RecyclerView.ViewHolder   {

        //pet list 2 inflate
        TextView name , breed , weight , gender , price;
        Button edit , delete;



        public ViewHolder(View v) {
            super(v);
            //v.setOnClickListener(this);

            name = (TextView) v.findViewById(R.id.textView4);
            breed = (TextView) v.findViewById(R.id.textView5);
            weight = (TextView) v.findViewById(R.id.textView6);
            gender = (TextView) v.findViewById(R.id.textView8);
            //  price = (TextView) v.findViewById(R.id.petListPrice);

            edit = (Button) v.findViewById(R.id.petListEdit);
            delete = (Button) v.findViewById(R.id.petListDelete);

          // edit.setOnClickListener(this);


        }




    } */





