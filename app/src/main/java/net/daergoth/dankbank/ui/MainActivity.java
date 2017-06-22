package net.daergoth.dankbank.ui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import net.daergoth.dankbank.DankBankApplication;
import net.daergoth.dankbank.R;
import net.daergoth.dankbank.meme.MemeDao;
import net.daergoth.dankbank.tag.Tag;
import net.daergoth.dankbank.tag.TagDao;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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


        // RecyclerView setup
        Log.d(MainActivity.class.getName(), memeDao.getAllMemes().get(0).toString());
        mainRecyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);

        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(this);
        mainRecyclerView.setLayoutManager(recyclerViewLayoutManager);

        recyclerViewAdapter = new MemeAdapter(memeDao.getAllMemes());
        mainRecyclerView.setAdapter(recyclerViewAdapter);
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

}
