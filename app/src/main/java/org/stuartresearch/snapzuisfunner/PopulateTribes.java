package org.stuartresearch.snapzuisfunner;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.stuartresearch.SnapzuAPI.Soup;
import org.stuartresearch.SnapzuAPI.Tribe;

import java.io.IOException;

/**
 * Created by jake on 7/4/15.
 */
public class PopulateTribes extends AsyncTask<Void, Void, Tribe[]> {

    Profile profile;

    public PopulateTribes(@Nullable Profile profile) {
        this.profile = profile;
    }


    @Override
    protected Tribe[] doInBackground(Void... params) {
        Soup soup = new Soup();

        try {
            if (profile != null) {
                return soup.getTribes(MainActivity.address + profile.getName());
            } else {
                return soup.getTribes();
            }
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
