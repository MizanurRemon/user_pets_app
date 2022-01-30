package com.example.databaseapp;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.databaseapp.RecyclerViewAdapter.RecyclerViewCursorAdapter;
import com.example.databaseapp.ui.PetProvider;
import com.example.databaseapp.ui.petAdapter2;
import com.example.databaseapp.ui.petContract.petEntry;

class RVAdapter extends RecyclerViewCursorAdapter<RVAdapter.ProductViewHolder>
{
    private static final String TAG = RVAdapter.class.getSimpleName();
    private Context mContext;
    public MyAdapterListener onClickListener;

    public interface MyAdapterListener {

        void editButtonViewOnClick(View v, int position , long id);
        void deleteButtonViewOnClick(View v, int position , long id);
    }



        public RVAdapter(Context context , Cursor cursor ) {
        super(context , cursor);


       // Cursor cursor = mContext.getContentResolver().query(productForLocationUri, null, null, null, sortOrder);
       // swapCursor(cursor);
    }

    public RVAdapter(Context context , Cursor cursor , RVAdapter.MyAdapterListener listener) {
        super(context , cursor);
        onClickListener = listener;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.pet_list3, parent, false);
        return new ProductViewHolder(view);
    }



    @Override
    protected void onBindViewHolder(ProductViewHolder holder, Cursor cursor)
    {
        int nameColumnIndex = cursor.getColumnIndex(petEntry.COLUMN_PET_NAME);
        int breedColumnIndex =cursor.getColumnIndex(petEntry.COLUMN_PET_BREED);
        int WeightColumnIndex = cursor.getColumnIndex(petEntry.COLUMN_PET_WEIGHT);
        int genderColumnIndex = cursor.getColumnIndex(petEntry.COLUMN_PET_GENDER);
        int priceColumnIndex = cursor.getColumnIndex(petEntry.COLUMN_PET_PRICE);



        String petName = cursor.getString(nameColumnIndex);
        String petBreed = cursor.getString(breedColumnIndex);
        int petWeight = cursor.getInt(WeightColumnIndex);
        int petGender = cursor.getInt(genderColumnIndex);
        int price = cursor.getInt(priceColumnIndex);

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

        holder.price.setText(price+"");
    }



    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView name, breed, weight, gender, price;
        Button edit, delete;


        ProductViewHolder(View v)
        {
            super(v);
            name = (TextView) v.findViewById(R.id.textView4);
            breed = (TextView) v.findViewById(R.id.textView5);
            weight = (TextView) v.findViewById(R.id.textView6);
            gender = (TextView) v.findViewById(R.id.textView8);
             price = (TextView) v.findViewById(R.id.petListPrice);

            edit = (Button) v.findViewById(R.id.petListEdit);
            delete = (Button) v.findViewById(R.id.petListDelete);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.editButtonViewOnClick(v, getBindingAdapterPosition() ,  RVAdapter.this.getItemId(getBindingAdapterPosition()));
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.deleteButtonViewOnClick(v, getBindingAdapterPosition(),  RVAdapter.this.getItemId(getBindingAdapterPosition()));
                }
            });


        }


        @Override
        public void onClick(View v) {
        }

    }









}