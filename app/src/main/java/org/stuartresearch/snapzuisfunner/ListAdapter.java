package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.stuartresearch.SnapzuAPI.Comment;
import org.stuartresearch.SnapzuAPI.Post;

/**
 * Created by jake on 7/7/15.
 */
public class ListAdapter extends ArrayAdapter<Comment> {

    public static final String SETTING_FONT = "setting_font_size";
    public static final String SETTING_FONT_SMALL = "Small";
    public static final String SETTING_FONT_MEDIUM = "Medium";
    public static final String SETTING_FONT_LARGE = "Large";


    private int titleStyle;
    private int paragraphStyle;

    private final LayoutInflater mLayoutInflater;
    private Comment[] objects;
    private int layoutResource;
    private Post post;

    private int[] colors;

    public ListAdapter(Context context, int resource, Comment[] objects, Post post) {
        super(context, resource, objects);
        this.layoutResource = resource;
        this.objects = objects;
        mLayoutInflater = LayoutInflater.from(context);
        this.post = post;
        colors = context.getApplicationContext().getResources().getIntArray(R.array.comment_colors);
    }

    static class ViewHolder {
        ImageView indent;
        ImageView postIndent;
        ImageView userIcon;
        ImageView commentColor;
        TextView title;
        TextView paragraph;
        LinearLayout opBanner;
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

        Context context = convertView.getContext();

        getStyles(context);

        viewHolder.title = (TextView) convertView.findViewById(R.id.comment_title);
        viewHolder.paragraph = (TextView) convertView.findViewById(R.id.comment_paragraph);
        viewHolder.indent = (ImageView) convertView.findViewById(R.id.comment_indent);
        viewHolder.postIndent = (ImageView) convertView.findViewById(R.id.post_padding);
        viewHolder.userIcon = (ImageView) convertView.findViewById(R.id.userIcon);
        viewHolder.commentColor = (ImageView) convertView.findViewById(R.id.comment_color);
        viewHolder.opBanner = (LinearLayout) convertView.findViewById(R.id.comment_title_banner);

        viewHolder.title.setTextAppearance(context, titleStyle);
        viewHolder.paragraph.setTextAppearance(context, paragraphStyle);

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
            viewHolder.title.setTextColor(context.getResources().getColor(R.color.primary_dark));
            if (paragraph != null) {
                viewHolder.paragraph.setText(Html.fromHtml(paragraph));
            } else {
                viewHolder.paragraph.setText("");
            }

            viewHolder.opBanner.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

            viewHolder.userIcon.setVisibility(View.GONE);
            viewHolder.commentColor.setVisibility(View.GONE);

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
        String voteColor = "#D32F2F";
        int indent = object.getIndent();
        int commentColor = context.getResources().getColor(android.R.color.transparent);

        if (indent != 0) {
            commentColor = colors[object.getIndent() % colors.length];
            viewHolder.commentColor.setVisibility(View.VISIBLE);
        } else {
            viewHolder.commentColor.setVisibility(View.GONE);
        }

        if (!vote.isEmpty() && Float.parseFloat(vote) > 0) {
            voteColor = "#1976D2";
        }

        if (post.getUser().equals(object.getUser())) {
            viewHolder.title.setText(Html.fromHtml(String.format(
                    "<font color=\"%s\" bgcolor=\"F056FA\"><mark>%s</mark></font> • <b>%s</b> • %s"
                    , voteColor, vote, user, date)));
            viewHolder.opBanner.setBackground(context.getResources().getDrawable(R.drawable.op_banner));
        } else {
            viewHolder.title.setText(Html.fromHtml(String.format(
                    "<font color=\"%s\" bgcolor=\"F056FA\"><mark>%s</mark></font> • <b>%s</b> • %s"
                    , voteColor, vote, user, date)));
            viewHolder.opBanner.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }


        viewHolder.paragraph.setText(paragraph.subSequence(0, paragraph.length() - 2));
        viewHolder.commentColor.setBackgroundColor(commentColor);

        viewHolder.postIndent.setVisibility(View.GONE);
        viewHolder.indent.getLayoutParams().width = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) indent * 6, convertView.getResources().getDisplayMetrics()) + 0.5f);
        viewHolder.indent.requestLayout();

        viewHolder.userIcon.setVisibility(View.VISIBLE);

        if (!icon.isEmpty()) {
            Glide.with(convertView.getContext()).load(icon).fitCenter().into(viewHolder.userIcon);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return objects.length + 1;
    }

    private void getStyles(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        switch (prefs.getString(SETTING_FONT, SETTING_FONT_MEDIUM)) {
            case SETTING_FONT_SMALL:
                titleStyle =  R.style.TextCommentTitleSmall;
                paragraphStyle =  R.style.TextCommentParagraphSmall;
                break;
            case SETTING_FONT_LARGE:
                titleStyle = R.style.TextCommentTitleLarge;
                paragraphStyle =  R.style.TextCommentParagraphLarge;
                break;
            case SETTING_FONT_MEDIUM:
            default:
                titleStyle = R.style.TextCommentTitleMedium;
                paragraphStyle =  R.style.TextCommentParagraphMedium;
                break;
        }
    }
}
