package com.example.firebasedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ItemViewHolder> {
    LayoutInflater layoutInflater;
    Context mContext;
    ArrayList<String> listNames;
    ArrayList<Bitmap> listImages;

    public UserAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        listImages = new ArrayList<>();
        listNames = new ArrayList<>();
        mContext = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(this.getClass().getSimpleName(), "size of list : " + String.valueOf(listNames.size()));
        View itemView = layoutInflater.inflate(R.layout.list_item, parent, false);
        ItemViewHolder noteViewHolder = new ItemViewHolder(itemView);
        return noteViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Bitmap bitmap = listImages.get(position);
        String name = listNames.get(position);
        holder.setData(name, bitmap, position);

    }

    @Override
    public int getItemCount() {
        if(listNames==null) {
            return 0;
        }
        else{
            return listNames.size();
        }
    }

    public void setListNamesBitmaps(ArrayList<String> names,ArrayList<Bitmap> bitmaps) {
        listNames=names;
        listImages=bitmaps;
        Log.d("ADAPTER", "setLists");
        notifyDataSetChanged();
    }

    public void addItem(String name, Bitmap bitmap){
        if(!listNames.contains(name)){
            listNames.add(name);
            listImages.add(bitmap);
        }
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imageView;
        int mPosition;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemText);
            imageView = itemView.findViewById(R.id.itemImage);
        }

        public void setData(String name, Bitmap bitmap, int position) {
            Log.d(this.getClass().getSimpleName(), "setting viewHolder number "+String.valueOf(position));
            textView.setText(name);
            if(bitmap!=null){
                imageView.setImageBitmap(bitmap);
            }
            mPosition=position;
        }
    }
}
