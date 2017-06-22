package net.daergoth.dankbank.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

//        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(this);
//        mainRecyclerView.setLayoutManager(recyclerViewLayoutManager);

        recyclerViewAdapter = new MemeAdapter(memeDao.getAllMemes());
        mainRecyclerView.setAdapter(recyclerViewAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(MainActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                memeDao.deleteMeme(memeDao.getAllMemes().get(viewHolder.getAdapterPosition()));
                recyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mainRecyclerView);
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

}
