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

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ItemViewHolder> {
    LayoutInflater layoutInflater;
    Context mContext;
    ArrayList<String> listNames;
    ArrayList<String> listPrices;
    ArrayList<Bitmap> listImages;

    public ProductsAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        listImages = new ArrayList<>();
        listNames = new ArrayList<>();
        listPrices = new ArrayList<>();
        mContext = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(this.getClass().getSimpleName(), "size of list : " + String.valueOf(listNames.size()));
        View itemView = layoutInflater.inflate(R.layout.product_list_item, parent, false);
        ItemViewHolder noteViewHolder = new ItemViewHolder(itemView);
        return noteViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Bitmap bitmap = listImages.get(position);
        String name = listNames.get(position);
        String price = listPrices.get(position);
        holder.setData(name, price, bitmap, position);

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

    public void addItem(String name, String price, Bitmap bitmap){
        if(!listNames.contains(name)){
            listNames.add(name);
            listPrices.add(price);
            listImages.add(bitmap);
        }
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        TextView textViewPrice;
        ImageView imageView;
        int mPosition;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.listProdName);
            textViewPrice = itemView.findViewById(R.id.listProdPrice);
            imageView = itemView.findViewById(R.id.itemImage);
        }

        public void setData(String name, String price, Bitmap bitmap, int position) {
            Log.d(this.getClass().getSimpleName(), "setting viewHolder number "+String.valueOf(position));
            textView.setText(name);
            textViewPrice.setText(price);
            if(bitmap!=null){
                imageView.setImageBitmap(bitmap);
            }
            mPosition=position;
        }
    }
}
