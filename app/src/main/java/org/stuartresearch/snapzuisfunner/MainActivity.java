package org.stuartresearch.snapzuisfunner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.etsy.android.grid.StaggeredGridView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;
import com.squareup.picasso.Picasso;

import org.stuartresearch.SnapzuAPI.Post;
import org.stuartresearch.SnapzuAPI.Tribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import icepick.Icepick;
import icepick.Icicle;


public class MainActivity extends AppCompatActivity implements Drawer.OnDrawerItemClickListener,
        AccountHeader.OnAccountHeaderListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String address = "http://snapzu.com/";
    public static final String PREF_NAME = "preferences";
    public static final String PREF_PROFILE_ID = "profile_id";

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.grid_view) StaggeredGridView gridView;
    @Bind(R.id.pull_to_refresh) SwipeRefreshLayout refresh;

    public static Bus bus = new Bus(ThreadEnforcer.MAIN);

    Drawer drawer;
    AccountHeader accountHeader;
    GridAdapter mAdapter;
    @Icicle Tribe[] tribes;
    List<Profile> profiles;

    //ATTN: HAS TO BE STATIC - Don't ask me why.
    static ArrayList<Post> posts = new ArrayList<>(50);

    @Icicle String sorting = "/trending";
    @Icicle Tribe tribe = new Tribe("all", "http://snapzu.com/list");
    Post post;
    @Icicle int page = 1;
    @Icicle int drawerSelection = 5;
    @Icicle Profile profile;


    EndlessScrollListener endlessScrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bus.register(this);


        setSupportActionBar(toolbar);

        refresh.setOnRefreshListener(this);

        // Set titles
        updateTitle();

        // Load avatars with Picasso in header
        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable drawable) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(R.drawable.profile).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

            @Override
            public Drawable placeholder(Context context) {
                return null;
            }
        });

        // Build account header_back
        accountHeader = generateAccounterHeader();
        drawer = generateDrawer();

        //Make hamburger appear and function
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);


        //get profile
        if (profile == null) {
            profile = getSavedProfile();
        }

        // Fill tribes list
        if (tribes != null) {
            showTribes(tribes);
        }
        downloadTribes();

        // fill grid with posts
        if (posts.isEmpty()) {
            downloadPosts();
        }

        //Gridview business
        mAdapter = new GridAdapter(this, R.layout.grid_item, posts);
        gridView.setAdapter(mAdapter);




        refresh.setEnabled(false);

        if (savedInstanceState == null) {
            // Receive updates from other components

            // bug 77712
            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });

            // Fill posts
        } else {
            // or else will not loading on orientation change
            endlessScrollListener = new EndlessScrollListener();
            gridView.setOnScrollListener(endlessScrollListener);

        }

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
            showSortingDialog();
            return true;
        } else if (id == R.id.action_compose) {
            Toast.makeText(this, "Compose not implemented.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
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
                drawerSelection = i;
                tribeSelected(tribes[i - 5]);
                break;

        }
        return false;
    }

    // ACCOUNT SELECTED
    @Override
    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
        switch (iProfile.getIdentifier()) {
            case -1:
                // ADD ACCOUNT
                Intent i = new Intent(this, Login.class);
                startActivity(i);
                break;
            case -2:
                // MANAGE ACCOUNTS
                Toast.makeText(this, "Manage Accounts is not implemented", Toast.LENGTH_SHORT).show();
                break;
            default:
                // ACCOUNT SELECTED
                profile = profiles.get(iProfile.getIdentifier());
                Toast.makeText(this, String.format("Logged in as %s", profile.getName()), Toast.LENGTH_SHORT).show();
                downloadTribes();
                break;
        }


        drawer.closeDrawer();

        //false if you have not consumed the event and it should close the drawer
        return false;
    }


    // POST IS SELECTED
    @OnItemClick(R.id.grid_view)
    public void grid_selected(int position) {
        this.post = posts.get(position);
        Intent i = new Intent(this, PostActivity.class);
        i.putExtra("url", this.post.getLink());
        startActivity(i);
    }

    private void downloadPosts() {
        if (tribe.getName().equals("all")) {
            if (page == 1) {
                new PopulatePosts(tribe, sorting, "").execute();
                page++;
            } else {
                new PopulatePosts(tribe, "", Integer.toString(page++)).execute();
            }
        } else {
            new PopulatePosts(tribe, sorting, Integer.toString(page++)).execute();
        }
    }

    private void downloadTribes() {
        drawer.removeAllItems();
        new PopulateTribes(profile).execute();
    }

    private void tribeSelected(Tribe tribe) {
        refresh.setRefreshing(true);
        this.tribe = tribe;
        this.sorting = "/trending";
        if (endlessScrollListener != null)
            endlessScrollListener.setLoading(true);
        hideCards();
        downloadPosts();

        updateTitle();
    }

    public void hideCards() {
        posts.clear();
        mAdapter.notifyDataSetInvalidated();
        page = 1;
        gridView.setVisibility(View.GONE);
    }


    // PULLED TO REFRESH
    @Override
    public void onRefresh() {
        tribeSelected(tribe);
    }

    // SENT FROM PopulatePosts
    @Subscribe
    public void onPostsReady(PopulatePosts.PostsPackage postsPackage) {
        showPosts(postsPackage.posts);
    }

    public void showPosts (Post[] posts) {
        for (int i = 0; i < posts.length; i++) {
            this.posts.add(posts[i]);
        }

        mAdapter.notifyDataSetChanged();
        gridView.setVisibility(View.VISIBLE);

        refresh.setRefreshing(false);
        endlessScrollListener = new EndlessScrollListener();
        gridView.setOnScrollListener(endlessScrollListener);
    }

    // SENT FROM PopulatePosts
    @Subscribe
    public void onPostsError(PopulatePosts.PostsError postsError) {
        Toast.makeText(this, "Network errors not implemented", Toast.LENGTH_SHORT).show();
        refresh.setRefreshing(false);
    }

    // SENT FROM PopulateTribes
    @Subscribe
    public void onTribesReady(PopulateTribes.TribesPackage tribesPackage) {
        showTribes(tribesPackage.tribes);
    }

    public void showTribes(Tribe[] tribes) {
        this.tribes = tribes;



        drawer.removeAllItems();

        drawer.addItems(
                new PrimaryDrawerItem().withName("Profile").withIcon(R.drawable.ic_account_box_black_18dp).withCheckable(false),
                new PrimaryDrawerItem().withName("Messages").withIcon(R.drawable.ic_message_black_18dp).withCheckable(false),
                new PrimaryDrawerItem().withName("Open User").withIcon(R.drawable.ic_group_black_18dp).withCheckable(false),
                new PrimaryDrawerItem().withName("Open Tribe").withIcon(R.drawable.ic_filter_tilt_shift_black_18dp).withCheckable(false),
                new DividerDrawerItem());

        for (int i = 0; i < tribes.length; i++) {
            drawer.addItem(new SecondaryDrawerItem().withName(this.tribes[i].getName()));
        }
    }

    // SENT FROM PopulateTribes
    @Subscribe
    public void onTribesError(PopulateTribes.TribesError tribesError) {
        Toast.makeText(this, "Network errors not implemented", Toast.LENGTH_SHORT).show();
    }

    public void updateTitle() {
        getSupportActionBar().setTitle(tribe.getName().toUpperCase());
        getSupportActionBar().setSubtitle(this.sorting.substring(1));
    }

    public static class SinglePostPackage {
        public final Post post;

        public SinglePostPackage(Post post) {
            this.post = post;
        }
    }

    @Produce
    public SinglePostPackage produceSinglePostPackage() {
        return new SinglePostPackage(this.post);
    }

    @Subscribe
    public void onLoadMoreRequest(EndlessScrollListener.LoadMorePackage loadMorePackage) {
        refresh.setRefreshing(true);
        downloadPosts();
    }

    public void showSortingDialog() {
        if (tribe.getName().equals("all")) {
            new MaterialDialog.Builder(this).title("Sorting").items(R.array.all_sorting).itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                    if (i == 0) {
                        sorting = "/trending";
                    } else if (i == 1) {
                        sorting = "/new";
                    }
                    updateTitle();
                    refresh.setRefreshing(true);
                    endlessScrollListener.setLoading(true);
                    hideCards();
                    downloadPosts();
                }
            }).show();
        } else {
            new MaterialDialog.Builder(this).title("Sorting").items(R.array.sorting).itemsCallback(new MaterialDialog.ListCallback() {
                @Override
                public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                    if (i == 0) {
                        sorting = "/trending";
                    } else if (i == 1) {
                        sorting = "/newest";
                    } else {
                        sorting = "/topscores";
                    }
                    updateTitle();
                    refresh.setRefreshing(true);
                    endlessScrollListener.setLoading(true);
                    hideCards();
                    downloadPosts();
                }
            }).show();
        }
    }

    public AccountHeader generateAccounterHeader() {
        AccountHeaderBuilder headerBuilder = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabled(true)
                .withSelectionListEnabledForSingleProfile(true)
                .withOnAccountHeaderListener(this);

        try {
            profiles = Profile.listAll(Profile.class);
        } catch (Exception e) {
            profiles = new ArrayList<>(0);
        }

        for (int i = 0; i < profiles.size(); i++) {
            headerBuilder.addProfiles(profiles.get(i).toProfileDrawerItem().withIdentifier(i));
        }

        headerBuilder.addProfiles(
                new ProfileSettingDrawerItem().withName("Add Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add).actionBarSize().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(-1),
                new ProfileSettingDrawerItem().withName("Manage Accounts").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(-2)
        );


        return headerBuilder.build();
    }

    public Drawer generateDrawer() {
        return new DrawerBuilder().withActivity(this).withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
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
    }

    @Subscribe
    public void onProfile(AddPictureToProfile.ProfilePicturePackage profilePicturePackage) {
    }

    @Subscribe
    public void onProfileError(AddPictureToProfile.ProfilePictureError profilePictureError) {

    }


    @Subscribe
    public void onPicturedAdded(AddPictureToProfile.ProfilePicturePackage profilePicturePackage) {
        profile = profilePicturePackage.profile;
        profile.save();
        saveProfile();
        accountHeader.addProfile(profile.toProfileDrawerItem(), accountHeader.getProfiles().size() - 2);
        downloadTribes();
    }

    public void saveProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(PREF_PROFILE_ID, profile.getId());
        editor.commit();
    }

    public Profile getSavedProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return Profile.findById(Profile.class, sharedPreferences.getLong(PREF_PROFILE_ID, -1));
    }

}
