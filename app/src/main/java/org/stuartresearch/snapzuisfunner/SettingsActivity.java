package org.stuartresearch.snapzuisfunner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SettingsActivity extends AppCompatActivity {

    @Bind(R.id.settings_toolbar) Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.slide_left, 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, BillingProcessor.IBillingHandler {
        Preference licensesPreference;
        Preference donate;
        BillingProcessor billingProcessor;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Context context = getActivity();
            if (context != null) {
                billingProcessor = new BillingProcessor(context, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxEUlJ4wDNO5o2Zm5a73CNxgwZPZ8GbBrfBXpvY8RFUBO/YMFOzctx5P08xmSwXn/T/Q4on62hAPbx5DzHbOThnFx5V84ExESyiylgMusSYkCtO/tr6ULh4LHBTydu2TmUrM5o6nN4MtL+9M27YzYR3YfEhEnQc427Xm5QbO03xPQV4xYXGEMJSaU0EavOYQH9GQqYR4J0iYb+rkanKGG7RlNSF2VD5S2peAoqIZT5MJ22vaXFfewFCzVtn/l19RXimuWF2TatpDvMElDbf9oh3FF2ZzSr0F4grkPPkrf7zj92dwb4Dp/dnG7O/IjVQ042Mu3sRJX434wDdV9KnzXXQIDAQAB\n", this);
            }

            licensesPreference = findPreference("setting_license");
            donate = findPreference("setting_donate");

            donate.setEnabled(false);

            licensesPreference.setOnPreferenceClickListener(this);
            donate.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Activity activity = getActivity();
            switch(preference.getKey()) {
                case "setting_license":
                    Intent intent = new Intent(preference.getContext(), ActionActivity.class);
                    intent.putExtra("url", "file:///android_asset/licenses.html");
                    intent.putExtra("cookies", "");
                    startActivity(intent);
                    break;
                case "setting_donate":
                    if (activity != null) {
                        billingProcessor.purchase(activity, "donation_5");
                        billingProcessor.consumePurchase("donation_5");
                    }
            }
            return false;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (!billingProcessor.handleActivityResult(requestCode, resultCode, data))
                super.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public void onDestroy() {
            if (billingProcessor != null)
                billingProcessor.release();
            super.onDestroy();
        }

        @Override
        public void onProductPurchased(String s, TransactionDetails transactionDetails) {
            Context context = getActivity();
            if (context != null) {
                Toast.makeText(context, "Thanks Snapper!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPurchaseHistoryRestored() {

        }

        @Override
        public void onBillingError(int i, Throwable throwable) {
            Context context = getActivity();
            if (context != null) {
                Toast.makeText(context, "Billing Error :(", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onBillingInitialized() {
            if (donate != null) {
                donate.setEnabled(true);
            }
        }
    }


}
