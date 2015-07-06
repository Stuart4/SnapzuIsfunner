package org.stuartresearch.snapzuisfunner;

import android.os.AsyncTask;

import com.mikepenz.materialdrawer.Drawer;

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
    protected Tribe[] doInBackground(Void... params) {
        Soup soup = new Soup();

        try {
            return soup.getTribes();
        } catch (IOException dropIt) {}

        return null;
    }

    @Override
    protected void onPostExecute(Tribe[] tribes) {
        if (tribes == null) {
            // Network Error
            MainActivity.bus.post(new TribesError(TribesError.NETWORK_ERROR));
        } else {
            MainActivity.bus.post(new TribesPackage(tribes));
        }
    }

    public static class TribesPackage {
        public final Tribe[] tribes;

        public TribesPackage(Tribe[] tribes) {
            this.tribes = tribes;
        }
    }

    public static class TribesError {
        public static final int NETWORK_ERROR = 0;
        public final int error;

        public TribesError(int error) {
            this.error = error;
        }
    }
}
