package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        ImageView intent;
        TextView title;
        TextView paragraph;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        Comment object = objects[position];
        String user = object.getUser();
        String vote = object.getVote();
        String date = object.getDate();
        String paragraph = object.getParagraph();
        String color = "#D32F2F";
        int indent = object.getIndent() * 4;


        if (convertView == null) {
            convertView = mLayoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (Float.parseFloat(vote) > 0) {
            color = "#1976D2";
        }

        viewHolder.title = (TextView) convertView.findViewById(R.id.comment_title);
        viewHolder.paragraph = (TextView) convertView.findViewById(R.id.comment_paragraph);
        viewHolder.intent = (ImageView) convertView.findViewById(R.id.comment_indent);


        viewHolder.title.setText(Html.fromHtml(String.format(
                "<font color=\"%s\">%s</font> • <b>%s</b> • %s"
                , color, vote, user, date)));
        viewHolder.paragraph.setText(paragraph);
        viewHolder.intent.getLayoutParams().width = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) indent, convertView.getResources().getDisplayMetrics()) + 0.5f);
        viewHolder.intent.requestLayout();

        return convertView;
    }

}
