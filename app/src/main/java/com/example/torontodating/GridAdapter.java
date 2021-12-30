package com.example.torontodating;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.torontodating.authentication.Model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends BaseAdapter {
    List<User> itemsList = new ArrayList<User>();

    Context context;

    LayoutInflater inflater;

    public GridAdapter(List<User> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       if (inflater == null)
           inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

       if (convertView == null){

           convertView = inflater.inflate(R.layout.grid_item,null);

       }

        ImageView imageView = convertView.findViewById(R.id.grid_image);
        TextView textView = convertView.findViewById(R.id.item_name);
        TextView agetv = convertView.findViewById(R.id.item_age);

        Picasso.with(context).
                load(itemsList.get(position).imageURL).into(imageView);
        textView.setText(itemsList.get(position).name);
        agetv.setText(itemsList.get(position).age);
        return convertView;
    }
}
