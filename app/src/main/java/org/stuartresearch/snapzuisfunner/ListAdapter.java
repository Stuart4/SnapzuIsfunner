package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.stuartresearch.SnapzuAPI.Comment;

/**
 * Created by jake on 7/7/15.
 */
public class ListAdapter extends ArrayAdapter<Comment> {
    private final LayoutInflater mLayoutInflater;
    private Comment[] objects;
    private int layoutResource;

    public ListAdapter(Context context, int resource, Comment[] objects) {
        super(context, resource, objects);
        this.layoutResource = resource;
        this.objects = objects;
        mLayoutInflater = LayoutInflater.from(context);
    }

    static class ViewHolder {
        TextView title;
        TextView paragraph;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        Comment object = objects[position];
        String title = object.getUser();
        String paragraph = object.getParagraph();


        if (convertView == null) {
            convertView = mLayoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.title = (TextView) convertView.findViewById(R.id.comment_title);
        viewHolder.paragraph = (TextView) convertView.findViewById(R.id.comment_paragraph);



        viewHolder.title.setText(title);
        viewHolder.paragraph.setText(paragraph);


        return convertView;
    }

}
