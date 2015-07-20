package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

    public static final String SETTING_FONT = "setting_font_size";
    public static final String SETTING_FONT_SMALL = "Small";
    public static final String SETTING_FONT_MEDIUM = "Medium";
    public static final String SETTING_FONT_LARGE = "Large";


    private int titleStyle;
    private int paragraphStyle;
    private int extraStyle;

    private final LayoutInflater mLayoutInflater;
    private ArrayList<Post> objects;
    private int layoutResource;
    private String tribe;

    public GridAdapter(Context context, int resource, ArrayList<Post> objects) {
        super(context, resource, objects);
        mLayoutInflater = LayoutInflater.from(context);
        layoutResource = resource;
        this.objects = objects;
    }


    public void setTribe(String tribe) {
        this.tribe = tribe;
    }

    public void removeTribe() {
        this.tribe = null;
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


        Context context = convertView.getContext();

        getStyles(context);

        viewHolder.imgView = (DynamicHeightImageView) convertView.findViewById(R.id.grid_image);
        viewHolder.extra = (TextView) convertView.findViewById(R.id.grid_extra);
        viewHolder.title = (TextView) convertView.findViewById(R.id.grid_title);
        viewHolder.paragraph = (TextView) convertView.findViewById(R.id.grid_paragraph);

        viewHolder.title.setTextAppearance(context, titleStyle);
        viewHolder.paragraph.setTextAppearance(context, paragraphStyle);
        viewHolder.extra.setTextAppearance(context, extraStyle);


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
        if (this.tribe != null) {
            return tribe;
        }
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

    private void getStyles(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        switch (prefs.getString(SETTING_FONT, SETTING_FONT_MEDIUM)) {
            case SETTING_FONT_SMALL:
                titleStyle =  R.style.TextCardTitleSmall;
                paragraphStyle =  R.style.TextCardParagraphSmall;
                extraStyle = R.style.TextCardExtraSmall;
                break;
            case SETTING_FONT_LARGE:
                titleStyle = R.style.TextCardTitleLarge;
                paragraphStyle =  R.style.TextCardParagraphLarge;
                extraStyle = R.style.TextCardExtraLarge;
                break;
            case SETTING_FONT_MEDIUM:
            default:
                titleStyle = R.style.TextCardTitleMedium;
                paragraphStyle =  R.style.TextCardParagraphMedium;
                extraStyle = R.style.TextCardExtraMedium;
                break;
        }
    }

}
