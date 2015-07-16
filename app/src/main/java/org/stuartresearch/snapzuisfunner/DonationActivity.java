package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DonationActivity extends AppCompatActivity {

    @Bind(R.id.donation_toolbar)
    Toolbar toolbar;
    SlidrInterface slidrInterface;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        ButterKnife.bind(this);
        overridePendingTransition(R.anim.slide_left, 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Donations");

        // Sliding mechanism
        SlidrConfig config = new SlidrConfig.Builder().sensitivity(0.5f).build();
        slidrInterface = Slidr.attach(this, config);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_right);
    }

    public static class DonationFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, BillingProcessor.IBillingHandler{
        BillingProcessor bp;

        Preference donation1;
        Preference donation5;
        Preference donation10;
        Preference donation50;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.donations);

            Context context = getActivity();
            if (context != null) {
                bp = new BillingProcessor(context, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxEUlJ4wDNO5o2Zm5a73CNxgwZPZ8GbBrfBXpvY8RFUBO/YMFOzctx5P08xmSwXn/T/Q4on62hAPbx5DzHbOThnFx5V84ExESyiylgMusSYkCtO/tr6ULh4LHBTydu2TmUrM5o6nN4MtL+9M27YzYR3YfEhEnQc427Xm5QbO03xPQV4xYXGEMJSaU0EavOYQH9GQqYR4J0iYb+rkanKGG7RlNSF2VD5S2peAoqIZT5MJ22vaXFfewFCzVtn/l19RXimuWF2TatpDvMElDbf9oh3FF2ZzSr0F4grkPPkrf7zj92dwb4Dp/dnG7O/IjVQ042Mu3sRJX434wDdV9KnzXXQIDAQAB\n", this);
            }

            donation1 = findPreference("donation_1");
            donation5 = findPreference("donation_5");
            donation10 = findPreference("donation_10");
            donation50 = findPreference("donation_50");

            donation1.setOnPreferenceClickListener(this);
            donation5.setOnPreferenceClickListener(this);
            donation10.setOnPreferenceClickListener(this);
            donation50.setOnPreferenceClickListener(this);

            donation1.setEnabled(false);
            donation5.setEnabled(false);
            donation10.setEnabled(false);
            donation50.setEnabled(false);

        }

        @Override
        public void onDestroy() {
            if (bp != null)
                bp.release();

            super.onDestroy();;
        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (!bp.handleActivityResult(requestCode, resultCode, data))
                super.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch(preference.getKey()) {
                case "donation_1":
                    bp.consumePurchase("YOUR PRODUCT ID FROM GOOGLE PLAY CONSOLE HERE");
                    break;
                case "donation_5":
                    bp.consumePurchase("YOUR PRODUCT ID FROM GOOGLE PLAY CONSOLE HERE");
                    break;
                case "donation_10":
                    bp.consumePurchase("YOUR PRODUCT ID FROM GOOGLE PLAY CONSOLE HERE");
                    break;
                case "donation_50":
                    bp.consumePurchase("YOUR PRODUCT ID FROM GOOGLE PLAY CONSOLE HERE");
                    break;
            }
            return true;
        }

        @Override
        public void onProductPurchased(String s, TransactionDetails transactionDetails) {
            Context context = getActivity();
            if (context != null) {
                Toast.makeText(context, "Thank You", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPurchaseHistoryRestored() {

        }

        @Override
        public void onBillingError(int i, Throwable throwable) {
            Context context = getActivity();
            if (context != null) {
                Toast.makeText(context, "Thank You", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onBillingInitialized() {
            donation1.setEnabled(true);
            donation5.setEnabled(true);
            donation10.setEnabled(true);
            donation50.setEnabled(true);
        }
    }


}
