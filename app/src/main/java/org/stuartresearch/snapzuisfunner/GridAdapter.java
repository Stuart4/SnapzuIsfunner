package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.squareup.picasso.Picasso;

import org.stuartresearch.SnapzuAPI.Post;

import java.util.ArrayList;

/**
 * Created by jake on 7/4/15.
 */
public class GridAdapter extends ArrayAdapter<Post> {
    private final LayoutInflater mLayoutInflater;
    ArrayList<Post> objects;


    public GridAdapter(Context context, int resource, ArrayList<Post> objects) {
        super(context, resource, objects);
        mLayoutInflater = LayoutInflater.from(context);
        this.objects = objects;
    }

    static class ViewHolder {
        DynamicHeightImageView imgView;
        TextView user;
        TextView score;
        TextView title;
        TextView paragraph;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        Post object = objects.get(position);

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.grid_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.imgView = (DynamicHeightImageView) convertView.findViewById(R.id.grid_image);
            viewHolder.user = (TextView) convertView.findViewById(R.id.grid_user);
            viewHolder.score = (TextView) convertView.findViewById(R.id.grid_score);
            viewHolder.title = (TextView) convertView.findViewById(R.id.grid_title);
            viewHolder.paragraph = (TextView) convertView.findViewById(R.id.grid_paragraph);

            int score = object.getScore();

            viewHolder.user.setText(object.getUser());
            viewHolder.score.setText(Integer.toString(score));
            viewHolder.title.setText(object.getTitle());
            viewHolder.paragraph.setText(object.getParagraph());

            Picasso.with(convertView.getContext()).load(object.getItemImage()).into(viewHolder.imgView);

            if (score > 0) {
                viewHolder.score.setTextColor(R.color.md_blue_400);
            } else {
                viewHolder.score.setTextColor(R.color.md_red_400);
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

            int score = object.getScore();

            viewHolder.user.setText(object.getUser());
            viewHolder.score.setText(Integer.toString(score));
            viewHolder.title.setText(object.getTitle());
            viewHolder.paragraph.setText(object.getParagraph());

            Picasso.with(convertView.getContext()).load(object.getItemImage()).into(viewHolder.imgView);

            if (score > 0) {
                viewHolder.score.setTextColor(R.color.md_blue_400);
            } else {
                viewHolder.score.setTextColor(R.color.md_red_400);
            }
        }

        viewHolder.imgView.setHeightRatio(0.7);

        return convertView;
    }
}
