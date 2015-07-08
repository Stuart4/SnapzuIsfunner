package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.squareup.picasso.Picasso;

import org.stuartresearch.SnapzuAPI.Post;
import org.stuartresearch.SnapzuAPI.Tribe;

import java.util.ArrayList;

/**
 * Created by jake on 7/4/15.
 */
public class GridAdapter extends ArrayAdapter<Post> {
    private final LayoutInflater mLayoutInflater;
    private ArrayList<Post> objects;
    private int layoutResource;


    public GridAdapter(Context context, int resource, ArrayList<Post> objects) {
        super(context, resource, objects);
        mLayoutInflater = LayoutInflater.from(context);
        layoutResource = resource;
        this.objects = objects;
    }

    static class ViewHolder {
        DynamicHeightImageView imgView;
        TextView extra;
        TextView title;
        TextView paragraph;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        Post object = objects.get(position);
        String score = object.getScore();
        String user = object.getUser();
        String comments = object.getComments();
        String title = object.getTitle();
        String paragraph = object.getParagraph();
        String imageUrl = object.getItemImage();
        String tribe = tribesToString(object.getTribes());
        String date = object.getDate();
        String type = object.getType();
        String color = "#D32F2F";


        if (convertView == null) {
            convertView = mLayoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imgView = (DynamicHeightImageView) convertView.findViewById(R.id.grid_image);
        viewHolder.extra = (TextView) convertView.findViewById(R.id.grid_extra);
        viewHolder.title = (TextView) convertView.findViewById(R.id.grid_title);
        viewHolder.paragraph = (TextView) convertView.findViewById(R.id.grid_paragraph);

        if (Float.parseFloat(score) > 0) {
            color = "#1976D2";
        }

        viewHolder.extra.setText(Html.fromHtml(String.format(
                "<font color=\"%s\">%s</font> • <b>%s</b> • %s • %s • %s • %s"
                , color, score, user, type, comments, date, tribe)));
        viewHolder.title.setText(title);
        viewHolder.paragraph.setText(paragraph);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.with(convertView.getContext()).load(object.getItemImage()).into(viewHolder.imgView);
            viewHolder.imgView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imgView.setVisibility(View.GONE);
        }



        viewHolder.imgView.setHeightRatio(0.7);

        return convertView;
    }

    private String tribesToString(Tribe[] tribes) {
        if (tribes.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(tribes[0].getName());

        for (int i = 1; i < tribes.length && i < 3; i++) {
            sb.append(" " + tribes[i].getName());
        }

        if (tribes.length > 3) {
            sb.append('…');
        }

        return sb.toString();
    }
}
