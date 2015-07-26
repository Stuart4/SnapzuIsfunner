package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.stuartresearch.SnapzuAPI.Comment;
import org.stuartresearch.SnapzuAPI.Post;

/**
 * Created by jake on 7/24/15.
 */
public class TreeViewConfiguration {
    public static final String SETTING_FONT = "setting_font_size";
    public static final String SETTING_FONT_SMALL = "Small";
    public static final String SETTING_FONT_MEDIUM = "Medium";
    public static final String SETTING_FONT_LARGE = "Large";

    public static int[] colors;

    public static int getParagraphStyle(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        switch (prefs.getString(SETTING_FONT, SETTING_FONT_MEDIUM)) {
            case SETTING_FONT_SMALL:
                return R.style.TextCommentParagraphSmall;
            case SETTING_FONT_LARGE:
                return R.style.TextCommentParagraphLarge;
            case SETTING_FONT_MEDIUM:
            default:
                return R.style.TextCommentParagraphMedium;
        }
    }

    public static int getTitleStyle(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        switch (prefs.getString(SETTING_FONT, SETTING_FONT_MEDIUM)) {
            case SETTING_FONT_SMALL:
                return R.style.TextCommentTitleSmall;
            case SETTING_FONT_LARGE:
                return R.style.TextCommentTitleLarge;
            case SETTING_FONT_MEDIUM:
            default:
                return R.style.TextCommentTitleMedium;
        }
    }

    public static void setColors(Context context) {
        colors = context.getApplicationContext().getResources().getIntArray(R.array.comment_colors);
    }


    public static AndroidTreeView buildTreeView(Context context, Post post, Comment[] comments) {
        TreeViewConfiguration.setColors(context);
        TreeNode root = TreeNode.root();

        root.addChild(new TreeNode(post).setViewHolder(new TreeViewConfiguration.PostHolder(context)));

        if (comments.length == 0) {
            return new AndroidTreeView(context, root);
        }

        Comment currentComment = comments[0];
        TreeNode currentNode = new TreeNode(comments[0]).setViewHolder(new TreeViewConfiguration.CommentHolder(context, post));
        root.addChild(currentNode);

        for (int i = 1; i < comments.length; i++) {
            if (currentComment.getIndent() < comments[i].getIndent()) {
                currentNode.addChild(new TreeNode(comments[i]).setViewHolder(new TreeViewConfiguration.CommentHolder(context, post)));
            } else {
                for (int k = 0; k < currentComment.getIndent() - comments[i].getIndent(); k++)
                    currentNode = currentNode.getParent();
                currentNode.addChild(new TreeNode(comments[i]).setViewHolder(new TreeViewConfiguration.CommentHolder(context, post)));
            }
        }

        return new AndroidTreeView(context, root);
    }


    public static class PostHolder extends TreeNode.BaseNodeViewHolder<Post> {

        public PostHolder(Context context) {
            super(context);
        }

        @Override
        public View createNodeView(TreeNode treeNode, Post post) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.list_item, null, false);

            int paragraphStyle = TreeViewConfiguration.getParagraphStyle(context);
            int titleStyle = TreeViewConfiguration.getTitleStyle(context);

            TextView title = (TextView) view.findViewById(R.id.comment_title);
            TextView paragraph = (TextView) view.findViewById(R.id.comment_paragraph);
            ImageView indent = (ImageView) view.findViewById(R.id.comment_indent);
            ImageView postIndent = (ImageView) view.findViewById(R.id.post_padding);
            ImageView userIcon = (ImageView) view.findViewById(R.id.userIcon);
            ImageView commentColor = (ImageView) view.findViewById(R.id.comment_color);
            LinearLayout opBanner = (LinearLayout) view.findViewById(R.id.comment_title_banner);

            title.setTextAppearance(context, titleStyle);
            paragraph.setTextAppearance(context, paragraphStyle);

            String colorString = "#D32F2F";
            String voteString = post.getScore();
            String dateString = post.getDate();
            String paragraphString = post.getParagraph();
            String titleString = post.getTitle();
            String userString = post.getUser();

            if (!voteString.isEmpty() && Float.parseFloat(voteString) > 0) {
                colorString = "#1976D2";
            }

            title.setText(Html.fromHtml(String.format(
                    "<h1>%s</h1><br><font color=\"%s\">%s</font> • <b>%s</b> • %s",
                    titleString, colorString, voteString, userString, dateString)));
            title.setTextColor(context.getResources().getColor(R.color.primary_dark));
            if (paragraph != null) {
                paragraph.setText(Html.fromHtml(paragraphString));
            } else {
                paragraph.setText("");
            }

            opBanner.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

            userIcon.setVisibility(View.GONE);
            commentColor.setVisibility(View.GONE);

            postIndent.setVisibility(View.VISIBLE);
            indent.getLayoutParams().width = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f, view.getResources().getDisplayMetrics()) + 0.5f);
            indent.requestLayout();

            return view;
        }
    }

    public static class CommentHolder extends TreeNode.BaseNodeViewHolder<Comment> {
        Post post;
        public CommentHolder(Context context, Post post) {
            super(context);
            this.post = post;
        }

        @Override
        public View createNodeView(TreeNode treeNode, Comment comment) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.list_item, null, false);

            int paragraphStyle = TreeViewConfiguration.getParagraphStyle(context);
            int titleStyle = TreeViewConfiguration.getTitleStyle(context);

            TextView title = (TextView) view.findViewById(R.id.comment_title);
            TextView paragraph = (TextView) view.findViewById(R.id.comment_paragraph);
            ImageView indent = (ImageView) view.findViewById(R.id.comment_indent);
            ImageView postIndent = (ImageView) view.findViewById(R.id.post_padding);
            ImageView userIcon = (ImageView) view.findViewById(R.id.userIcon);
            ImageView commentColor = (ImageView) view.findViewById(R.id.comment_color);
            LinearLayout opBanner = (LinearLayout) view.findViewById(R.id.comment_title_banner);

            title.setTextAppearance(context, titleStyle);
            paragraph.setTextAppearance(context, paragraphStyle);

            String userString = comment.getUser();
            String voteString = comment.getVote();
            String dateString = comment.getDate();
            String iconString = comment.getImageLink();
            Spanned paragraphSpanned = Html.fromHtml(comment.getParagraph());
            String voteColorString = "#D32F2F";
            int indentValue = comment.getIndent();
            int commentColorValue = context.getResources().getColor(android.R.color.transparent);

            if (indentValue != 0) {
                commentColorValue = TreeViewConfiguration.colors[comment.getIndent() % TreeViewConfiguration.colors.length];
                commentColor.setVisibility(View.VISIBLE);
            } else {
                commentColor.setVisibility(View.GONE);
            }

            if (!voteString.isEmpty() && Float.parseFloat(voteString) > 0) {
                voteColorString = "#1976D2";
            }

            if (post.getUser().equals(comment.getUser())) {
                title.setText(Html.fromHtml(String.format(
                        "<font color=\"%s\" bgcolor=\"F056FA\"><mark>%s</mark></font> • <b>%s</b> • %s"
                        , voteColorString, voteString, userString, dateString)));
                opBanner.setBackground(context.getResources().getDrawable(R.drawable.op_banner));
            } else {
                title.setText(Html.fromHtml(String.format(
                        "<font color=\"%s\" bgcolor=\"F056FA\"><mark>%s</mark></font> • <b>%s</b> • %s"
                        , voteColorString, voteString, userString, dateString)));
                opBanner.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }


            paragraph.setText(paragraphSpanned.subSequence(0, paragraphSpanned.length() - 2));
            commentColor.setBackgroundColor(commentColorValue);

            postIndent.setVisibility(View.GONE);
            indent.getLayoutParams().width = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) indentValue * 6, context.getResources().getDisplayMetrics()) + 0.5f);
            userIcon.setVisibility(View.VISIBLE);

            if (!iconString.isEmpty()) {
                Glide.with(context).load(iconString).fitCenter().into(userIcon);
            }

            return view;
        }
    }


}
