package net.daergoth.dankbank.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 124;

    @Inject
    TagDao tagDao;

    @Inject
    MemeDao memeDao;

    private RecyclerView.Adapter recyclerViewAdapter;

    private RecyclerView mainRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((DankBankApplication) getApplication()).getDankBankComponent().inject(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu navMenu = navigationView.getMenu();

        SubMenu tagSubMenu = navMenu.addSubMenu("Tags");
        tagSubMenu.add(Menu.NONE, -1, Menu.NONE, "All");
        for (Tag t : tagDao.getAllTags()) {
            tagSubMenu.add(Menu.NONE, t.getId(), Menu.NONE, t.getTagName());
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }


        // RecyclerView setup
        mainRecyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        mainRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mainRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mainRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Open meme on bigger window
                Intent bigMemeIntent = new Intent(MainActivity.this, MemeActivity.class);
                bigMemeIntent.putExtra("memeUri", memeDao.getAllMemes().get(position).getUri());
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
                        memeDao.addMeme(toBeDeleted);
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

        recyclerViewAdapter = new MemeAdapter(memeDao.getAllMemes());
        mainRecyclerView.setAdapter(recyclerViewAdapter);
        mainRecyclerView.invalidate();

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (item.getItemId() == -1) {
            recyclerViewAdapter = new MemeAdapter(memeDao.getAllMemes());
            Toast.makeText(this, "All tags", Toast.LENGTH_SHORT).show();
        } else {
            Tag t = tagDao.getTagById(item.getItemId());
            recyclerViewAdapter = new MemeAdapter(memeDao.getMemesByTag(t));
            Toast.makeText(this, t.getTagName() + " tag", Toast.LENGTH_SHORT).show();
        }

        mainRecyclerView.invalidate();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
