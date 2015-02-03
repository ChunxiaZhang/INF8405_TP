package com.memorygame.example.zoe.tp1_memorygame;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Zoe on 15-01-26.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public List<Integer> resourceValues = new ArrayList<Integer>();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {

        return resourceValues.size();
    }

    @Override
    public Object getItem(int position) {
        return resourceValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //Create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85,85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,8,8,8);
        }
        else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(this.getImageValues().get(position));
        return imageView;
    }

    public List<Integer> getImageValues() {
        Field[] drawableFields = R.drawable.class.getFields();

        for(Field field: drawableFields) {
            if(field.getName().indexOf("g_") != -1) {
                try {
                    resourceValues.add(field.getInt(R.drawable.class));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        resourceValues.addAll(resourceValues);
        Collections.shuffle(resourceValues);
        return resourceValues;
    }

}
