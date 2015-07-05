package org.stuartresearch.snapzuisfunner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.stuartresearch.SnapzuAPI.Post;
import org.stuartresearch.SnapzuAPI.Tribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;


public class MainActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener,
        AccountHeader.OnAccountHeaderListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.grid_view) StaggeredGridView gridView;


    static Drawer drawer;
    static GridAdapter mAdapter;
    static Tribe[] tribes;
    static ArrayList<Post> posts = new ArrayList<>(50);
    static String sorting = "/trending";
    static Tribe tribe = new Tribe("Frontpage", "/list");
    static int page = 1;
    int drawerSelection = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Build account header
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("SubZeroJake").withEmail("jake@spacejake.com").withIcon(getResources().getDrawable(R.drawable.profile)),
                        new ProfileDrawerItem().withName("vexix11").withEmail("the_jake@sbcglobal.net")
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                }).withOnAccountHeaderListener(this)
                .build();

        // Build drawer
        drawer = new DrawerBuilder().withActivity(this).withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withSelectedItem(drawerSelection)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Profile").withIcon(R.drawable.ic_account_box_black_18dp).withCheckable(false),
                        new PrimaryDrawerItem().withName("Messages").withIcon(R.drawable.ic_message_black_18dp).withCheckable(false),
                        new PrimaryDrawerItem().withName("Open User").withIcon(R.drawable.ic_group_black_18dp).withCheckable(false),
                        new PrimaryDrawerItem().withName("Open Tribe").withIcon(R.drawable.ic_filter_tilt_shift_black_18dp).withCheckable(false),
                        new DividerDrawerItem()
                )
                .addStickyDrawerItems(
                        new PrimaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_settings_black_18dp).withCheckable(false)
                ).withOnDrawerItemClickListener(this)
                .build();

        //Make hamburger appear and function
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        // Fill tribes list
        downloadTribes();

        //Gridview business
        mAdapter = new GridAdapter(this, R.layout.grid_item, posts);
        gridView.setAdapter(mAdapter);

        // Fill posts
        downloadPosts();


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
        if (id == R.id.action_sort) {
            Toast.makeText(this, "Sort not implemented.", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_compose) {
            Toast.makeText(this, "Compose not implemented.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // CLOSE DRAWER ON BACK
    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    // DRAWER CLICKED
    @Override
    public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {

        switch(i) {
            // Profile
            case 0:
                Toast.makeText(this, "Profile is not implemented", Toast.LENGTH_SHORT).show();
                break;
            // Messages
            case 1:
                Toast.makeText(this, "Messages is not implemented", Toast.LENGTH_SHORT).show();
                break;
            // Open User
            case 2:
                Toast.makeText(this, "Open User is not implemented", Toast.LENGTH_SHORT).show();
                break;
            // Open Tribe
            case 3:
                Toast.makeText(this, "Open Tribe not implemented", Toast.LENGTH_SHORT).show();
                break;
            // Settings
            case -1:
                Toast.makeText(this, "Settings is not implemented", Toast.LENGTH_SHORT).show();
                break;
            // Tribe Selected
            default:
                tribeSelected(tribes[i - 5]);
                break;

        }
        return false;
    }

    // ACCOUNT SELECTED
    @Override
    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
        Toast.makeText(this, String.format("Almost logged in as %s", iProfile.getName()), Toast.LENGTH_SHORT).show();
        return false;
    }

    public static void setTribes(Tribe[] tribes) {
        MainActivity.tribes = tribes;
    }

    public static ArrayList<Post> getPosts() {
        return posts;
    }

    @OnItemClick(R.id.grid_view)
    public void grid_selected(int position) {
        Toast.makeText(this, String.format("Post selection (%s) is not implemented", posts.get(position)), Toast.LENGTH_SHORT).show();
    }

    private void downloadPosts() {
        if (tribe.getName().equals("Frontpage")) {
            new PopulatePosts(mAdapter, gridView, tribe, "", page++).execute();
        } else {
            new PopulatePosts(mAdapter, gridView, tribe, sorting, page++).execute();
        }
    }

    private void downloadTribes() {
        new PopulateTribes(drawer).execute();
    }

    private void tribeSelected(Tribe tribe) {
        MainActivity.tribe = tribe;
        posts = new ArrayList<>(50);
        gridView.setVisibility(View.GONE);
        page = 0;
        downloadPosts();
    }
}
