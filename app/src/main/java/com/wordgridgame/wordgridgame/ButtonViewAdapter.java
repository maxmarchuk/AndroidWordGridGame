package com.wordgridgame.wordgridgame;

import android.app.ActionBar;
import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by mmarchuk on 1/30/16.
 */
public class ButtonViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<ImageButton>  imageButtonList;

    public ButtonViewAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return imageButtonList.size();
    }

    public Object getItem(int position) {
        return imageButtonList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageButton for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageButton imageButton;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageButton = new ImageButton(mContext);
            imageButton.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageButton.setPadding(8, 8, 8, 8);
            imageButton.setBackgroundResource(R.drawable.bluebutton9patch);
            imageButton.setLayoutParams(new ViewGroup.LayoutParams(60, 60));

        } else {
            imageButton = (ImageButton) convertView;
        }

        return imageButton;
    }
}
