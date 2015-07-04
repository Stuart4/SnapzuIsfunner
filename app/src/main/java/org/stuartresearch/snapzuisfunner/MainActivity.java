package org.stuartresearch.snapzuisfunner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    Drawer drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        drawer = new DrawerBuilder().withActivity(this).withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Profile").withIcon(R.drawable.ic_account_box_black_18dp),
                        new PrimaryDrawerItem().withName("Messages").withIcon(R.drawable.ic_message_black_18dp),
                        new PrimaryDrawerItem().withName("User").withIcon(R.drawable.ic_group_black_18dp),
                        new PrimaryDrawerItem().withName("Tribe").withIcon(R.drawable.ic_filter_tilt_shift_black_18dp),
                        new DividerDrawerItem()
                )
                .addStickyDrawerItems(
                        new PrimaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_settings_black_18dp)
                ).build();

        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        new PopulateTribes(drawer).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
