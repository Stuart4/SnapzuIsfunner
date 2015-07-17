package org.stuartresearch.snapzuisfunner;

import android.os.AsyncTask;

import org.stuartresearch.SnapzuAPI.Soup;

import java.io.IOException;

/**
 * Created by jake on 7/16/15.
 */
public class PopulateMessagesAndLevel extends AsyncTask<Void, Void, String[]> {

    String cookies;

    public PopulateMessagesAndLevel(String cookies) {
        this.cookies = cookies;
    }


    @Override
    protected String[] doInBackground(Void... params) {
        Soup soup = new Soup();
        String[] data = new String[2];

        try {
            data[0] = soup.getLevel(cookies);
            data[1] = soup.getMessages(cookies);
            return data;
        } catch (IOException dropIt) {}

        return null;
    }

    @Override
    protected void onPostExecute(String[] data) {
        if (data == null) {
            // Network Error
            MainActivity.bus.post(new MessagesAndLevelError(MessagesAndLevelError.NETWORK_ERROR));
        } else {
            MainActivity.bus.post(new MessagesAndLevelPackage(data));
        }
    }

    public static class MessagesAndLevelPackage {
        public final String[] data;

        public MessagesAndLevelPackage(String[] data) {
            this.data = data;
        }
    }

    public static class MessagesAndLevelError {
        public static final int NETWORK_ERROR = 0;
        public final int error;

        public MessagesAndLevelError(int error) {
            this.error = error;
        }
    }
}