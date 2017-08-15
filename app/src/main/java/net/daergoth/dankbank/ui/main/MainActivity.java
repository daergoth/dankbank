package net.daergoth.dankbank.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import net.daergoth.dankbank.DankBankApplication;
import net.daergoth.dankbank.R;
import net.daergoth.dankbank.meme.Meme;
import net.daergoth.dankbank.meme.MemeDao;
import net.daergoth.dankbank.tag.Tag;
import net.daergoth.dankbank.tag.TagDao;
import net.daergoth.dankbank.ui.meme.MemeActivity;
import net.daergoth.dankbank.ui.util.ClickListener;
import net.daergoth.dankbank.ui.util.RecyclerTouchListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 124;
    public static final String STATE_SELECTED_TAG_ID = "selectedTagId";

    @Inject
    TagDao tagDao;

    @Inject
    MemeDao memeDao;

    @BindView(R.id.mainRecyclerView)
    RecyclerView mainRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private MemeAdapter recyclerViewAdapter;

    private Integer selectedTagId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((DankBankApplication) getApplication()).getDankBankComponent().inject(this);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            selectedTagId = savedInstanceState.getInt(STATE_SELECTED_TAG_ID);
        } else {
            selectedTagId = -1;
        }

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }


        // RecyclerView setup
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mainRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mainRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Open meme on bigger window
                Intent bigMemeIntent = new Intent(MainActivity.this, MemeActivity.class);
                bigMemeIntent.putExtra("memeUri", recyclerViewAdapter.getMemeList().get(position).getUri());
                startActivity(bigMemeIntent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recyclerViewAdapter = new MemeAdapter(memeDao.getAllMemes());
        mainRecyclerView.setAdapter(recyclerViewAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final Meme toBeDeleted = memeDao.getAllMemes().get(viewHolder.getAdapterPosition());

                memeDao.deleteMeme(toBeDeleted);
                recyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                Snackbar undoSnackbar = Snackbar.make(findViewById(R.id.main_coordinator_layout),
                        "Meme deleted", Snackbar.LENGTH_LONG);
                undoSnackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        memeDao.saveMeme(toBeDeleted);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                });
                undoSnackbar.show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mainRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (selectedTagId != -1) {
            Tag t = tagDao.getTagById(selectedTagId);
            recyclerViewAdapter = new MemeAdapter(memeDao.getMemesByTag(t));
        } else {
            recyclerViewAdapter = new MemeAdapter(memeDao.getAllMemes());
        }
        mainRecyclerView.setAdapter(recyclerViewAdapter);
        mainRecyclerView.invalidate();

        navigationView.setNavigationItemSelectedListener(this);
        Menu navMenu = navigationView.getMenu();
        navMenu.clear();

        SubMenu tagSubMenu = navMenu.addSubMenu("Tags");
        tagSubMenu.add(Menu.NONE, -1, Menu.NONE, "All");
        for (Tag t : tagDao.getAllTags()) {
            tagSubMenu.add(Menu.NONE, t.getId(), Menu.NONE, t.getTagName());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectedTagId = item.getItemId();

        if (selectedTagId == -1) {
            recyclerViewAdapter = new MemeAdapter(memeDao.getAllMemes());
            Toast.makeText(this, "All tags", Toast.LENGTH_SHORT).show();
        } else {
            Tag t = tagDao.getTagById(selectedTagId);
            recyclerViewAdapter = new MemeAdapter(memeDao.getMemesByTag(t));
            Toast.makeText(this, t.getTagName() + " tag", Toast.LENGTH_SHORT).show();
        }

        mainRecyclerView.setAdapter(recyclerViewAdapter);
        mainRecyclerView.invalidate();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return;
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(STATE_SELECTED_TAG_ID, selectedTagId);

        super.onSaveInstanceState(outState, outPersistentState);
    }
}
