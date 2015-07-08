package org.stuartresearch.snapzuisfunner;

import android.os.AsyncTask;

import org.stuartresearch.SnapzuAPI.Post;
import org.stuartresearch.SnapzuAPI.Soup;
import org.stuartresearch.SnapzuAPI.Tribe;

import java.io.IOException;

/**
 * Created by jake on 7/4/15.
 */
public class PopulatePosts extends AsyncTask<Void, Void, Post[]> {

    String tribe;
    String sorting;
    String page;

    public PopulatePosts(Tribe tribe, String sorting, String page) {

        this.tribe = tribe.getLink();
        this.sorting = sorting;
        this.page = page;
    }

    @Override
    protected Post[] doInBackground(Void... params) {
        Soup soup = new Soup();

        try {
            return soup.getPosts(tribe, sorting, page);
        } catch (IOException dropIt) {}

        return null;
    }

    @Override
    protected void onPostExecute(Post[] posts) {
        if (posts == null) {
            // Network Error
            MainActivity.bus.post(new PostsError(PostsError.NETWORK_ERROR));
        } else {
            // Send to main activity
            MainActivity.bus.post(new PostsPackage(posts));
        }
    }

    public static class PostsPackage {
        public final Post[] posts;

        public PostsPackage(Post[] posts) {
            this.posts = posts;
        }
    }

    public static class PostsError {
        public static final int NETWORK_ERROR = 0;
        public final int error;

        public PostsError(int error) {
            this.error = error;
        }
    }
}
