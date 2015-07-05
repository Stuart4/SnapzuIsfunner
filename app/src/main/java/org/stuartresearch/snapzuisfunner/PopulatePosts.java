package org.stuartresearch.snapzuisfunner;

import android.os.AsyncTask;
import android.view.View;

import com.etsy.android.grid.StaggeredGridView;

import org.stuartresearch.SnapzuAPI.Post;
import org.stuartresearch.SnapzuAPI.Soup;
import org.stuartresearch.SnapzuAPI.Tribe;

import java.io.IOException;

/**
 * Created by jake on 7/4/15.
 */
public class PopulatePosts extends AsyncTask<Void, Void, Post[]> {

    GridAdapter mAdapter;
    StaggeredGridView gridView;
    String tribe;
    String sorting;
    int page;

    public PopulatePosts(GridAdapter mAdapter, StaggeredGridView gridView, Tribe tribe, String sorting, int page) {
        this.mAdapter = mAdapter;
        this.gridView = gridView;
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
            System.out.println("NETWORKING ERROR!!!!");
            return;
        }

        for (int i = 0; i < posts.length; i++) {
            MainActivity.getPosts().add(posts[i]);
        }

        mAdapter.notifyDataSetChanged();
        gridView.setVisibility(View.VISIBLE);

    }
}
