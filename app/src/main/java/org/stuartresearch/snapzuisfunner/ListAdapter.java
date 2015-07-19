package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.stuartresearch.SnapzuAPI.Comment;
import org.stuartresearch.SnapzuAPI.Post;

/**
 * Created by jake on 7/7/15.
 */
public class ListAdapter extends ArrayAdapter<Comment> {
    private final LayoutInflater mLayoutInflater;
    private Comment[] objects;
    private int layoutResource;
    private Post post;

    public ListAdapter(Context context, int resource, Comment[] objects, Post post) {
        super(context, resource, objects);
        this.layoutResource = resource;
        this.objects = objects;
        mLayoutInflater = LayoutInflater.from(context);
        this.post = post;
    }

    static class ViewHolder {
        ImageView indent;
        ImageView postIndent;
        ImageView userIcon;
        TextView title;
        TextView paragraph;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title = (TextView) convertView.findViewById(R.id.comment_title);
        viewHolder.paragraph = (TextView) convertView.findViewById(R.id.comment_paragraph);
        viewHolder.indent = (ImageView) convertView.findViewById(R.id.comment_indent);
        viewHolder.postIndent = (ImageView) convertView.findViewById(R.id.post_padding);
        viewHolder.userIcon = (ImageView) convertView.findViewById(R.id.userIcon);

        if (position == 0) {
            String color = "#D32F2F";
            String vote = post.getScore();
            String date = post.getDate();
            String paragraph = post.getParagraph();
            String title = post.getTitle();
            String user = post.getUser();

            if (!vote.isEmpty() && Float.parseFloat(vote) > 0) {
                color = "#1976D2";
            }

            viewHolder.title.setText(Html.fromHtml(String.format(
                    "<h1>%s</h1><br><font color=\"%s\">%s</font> • <b>%s</b> • %s",
                    title, color, vote, user, date)));
            if (paragraph != null) {
                viewHolder.paragraph.setText(Html.fromHtml(paragraph));
            } else {
                viewHolder.paragraph.setText("");
            }

            viewHolder.userIcon.setVisibility(View.GONE);

            viewHolder.postIndent.setVisibility(View.VISIBLE);
            viewHolder.indent.getLayoutParams().width = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, convertView.getResources().getDisplayMetrics()) + 0.5f);
            viewHolder.indent.requestLayout();

            return convertView;
        }

        Comment object = objects[position - 1];
        String user = object.getUser();
        String vote = object.getVote();
        String date = object.getDate();
        String icon = object.getImageLink();
        Spanned paragraph = Html.fromHtml(object.getParagraph());
        String color = "#D32F2F";
        int indent = object.getIndent() * 6;



        if (!vote.isEmpty() && Float.parseFloat(vote) > 0) {
            color = "#1976D2";
        }

        viewHolder.title.setText(Html.fromHtml(String.format(
                "<font color=\"%s\">%s</font> • <b>%s</b> • %s"
                , color, vote, user, date)));
        viewHolder.paragraph.setText(paragraph.subSequence(0, paragraph.length() - 2));
        viewHolder.postIndent.setVisibility(View.GONE);
        viewHolder.indent.getLayoutParams().width = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) indent, convertView.getResources().getDisplayMetrics()) + 0.5f);
        viewHolder.indent.requestLayout();

        viewHolder.userIcon.setVisibility(View.VISIBLE);
        Picasso.with(convertView.getContext()).load(icon).into(viewHolder.userIcon);

        return convertView;
    }

    @Override
    public int getCount() {
        return objects.length + 1;
    }

}
