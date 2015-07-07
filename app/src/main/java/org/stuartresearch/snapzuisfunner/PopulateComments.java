package org.stuartresearch.snapzuisfunner;

import android.os.AsyncTask;

import org.stuartresearch.SnapzuAPI.Soup;
import org.stuartresearch.SnapzuAPI.Comment;

import java.io.IOException;

/**
 * Created by jake on 7/7/15.
 */
public class PopulateComments extends AsyncTask<Void, Void, Comment[]> {

    String link;

    public PopulateComments(String link) {

        this.link = link;
    }

    @Override
    protected Comment[] doInBackground(Void... params) {
        Soup soup = new Soup();

        try {
            return soup.getComments(link);
        } catch (IOException dropIt) {}

        return null;
    }

    @Override
    protected void onPostExecute(Comment[] comments) {
        if (comments == null) {
            // Network Error
            MainActivity.bus.post(new CommentsError(CommentsError.NETWORK_ERROR));
        } else {
            // Send to main activity
            MainActivity.bus.post(new CommentsPackage(comments));
        }
    }

    public static class CommentsPackage {
        public final Comment[] comments;

        public CommentsPackage(Comment[] comments) {
            this.comments = comments;
        }
    }

    public static class CommentsError {
        public static final int NETWORK_ERROR = 0;
        public final int error;

        public CommentsError(int error) {
            this.error = error;
        }
    }
}
