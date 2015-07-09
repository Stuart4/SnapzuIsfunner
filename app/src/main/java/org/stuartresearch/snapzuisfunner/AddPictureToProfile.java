package org.stuartresearch.snapzuisfunner;

/**
 * Created by jake on 7/9/15.
 */

import android.os.AsyncTask;

import org.stuartresearch.SnapzuAPI.Soup;

import java.io.IOException;

public class AddPictureToProfile extends AsyncTask<Void, Void, Profile> {

    public static final String address = "http://snapzu.com/";
    Profile user;

    public AddPictureToProfile(Profile user) {
        this.user = user;
    }


    @Override
    protected Profile doInBackground(Void... aVoid) {
        Soup soup = new Soup();

        try {
            user.setImageUrl(soup.getPicture(address + "/" + user.getName()));
            return user;
        } catch (IOException dropIt) {}

        return null;
    }

    @Override
    protected void onPostExecute(Profile profile) {
        if (profile == null) {
            // Network Error
            MainActivity.bus.post(new ProfilePictureError(ProfilePictureError.NETWORK_ERROR));
        } else {
            MainActivity.bus.post(new ProfilePicturePackage(profile));
        }
    }

    public static class ProfilePicturePackage {
        public Profile profile;

        public ProfilePicturePackage(Profile profile) {
            this.profile = profile;
        }
    }

    public static class ProfilePictureError {
        public static final int NETWORK_ERROR = 0;
        public final int error;

        public ProfilePictureError(int error) {
            this.error = error;
        }
    }
}
