package org.stuartresearch.snapzuisfunner;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SettingsActivity extends AppCompatActivity {

    @Bind(R.id.settings_toolbar) Toolbar toolbar;
    SlidrInterface slidrInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.slide_left, 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Sliding mechanism
        SlidrConfig config = new SlidrConfig.Builder().sensitivity(0.5f).build();
        slidrInterface = Slidr.attach(this, config);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        overridePendingTransition(0, R.anim.slide_right);
        return true;
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{
        Preference licensesPreference;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            licensesPreference = findPreference("setting_license");
            licensesPreference.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch(preference.getKey()) {
                case "setting_license":
                    Intent intent = new Intent(preference.getContext(), ActionActivity.class);
                    intent.putExtra("url", "file:///android_asset/licenses.html");
                    intent.putExtra("cookies", "");
                    startActivity(intent);
                    break;
            }
            return false;
        }
    }


}
