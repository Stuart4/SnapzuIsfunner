package org.stuartresearch.snapzuisfunner;

import android.os.AsyncTask;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import org.stuartresearch.SnapzuAPI.Soup;
import org.stuartresearch.SnapzuAPI.Tribe;

import java.io.IOException;

/**
 * Created by jake on 7/4/15.
 */
public class PopulateTribes extends AsyncTask<Void, Void, Tribe[]> {

    Drawer drawer;

    public PopulateTribes(Drawer drawer) {
        this.drawer = drawer;
    }

    @Override
    protected void onPostExecute(Tribe[] tribes) {
        for (int i = 5; i < drawer.getDrawerItems().size(); i++) {
            drawer.removeItem(i);
        }
        for (int i = 0; i < tribes.length; i++) {
            drawer.addItem(new SecondaryDrawerItem().withName(tribes[i].getName()));
        }
    }

    @Override
    protected Tribe[] doInBackground(Void... params) {
        Soup soup = new Soup();

        try {
            return soup.getTribes();
        } catch (IOException dropIt) {}

        return null;
    }
}
