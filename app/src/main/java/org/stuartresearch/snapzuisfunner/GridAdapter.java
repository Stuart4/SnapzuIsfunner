package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.etsy.android.grid.util.DynamicHeightImageView;

import org.stuartresearch.SnapzuAPI.Post;

/**
 * Created by jake on 7/4/15.
 */
public class GridAdapter extends ArrayAdapter<Post> {
    private final LayoutInflater mLayoutInflater;


    public GridAdapter(Context context, int resource, Post[] objects) {
        super(context, resource, objects);
        mLayoutInflater = LayoutInflater.from(context);
    }

    static class ViewHolder {
        DynamicHeightImageView imgView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.grid_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.imgView = (DynamicHeightImageView) convertView.findViewById(R.id.grid_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imgView.setHeightRatio(1.5);

        return convertView;
    }
}
