package net.daergoth.dankbank.ui.meme;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import net.daergoth.dankbank.DankBankApplication;
import net.daergoth.dankbank.R;
import net.daergoth.dankbank.meme.Meme;
import net.daergoth.dankbank.meme.MemeDao;
import net.daergoth.dankbank.tag.Tag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MemeActivity extends AppCompatActivity {

    @Inject
    MemeDao memeDao;

    @BindView(R.id.big_meme_display)
    ImageView bigMemeDisplayImageView;

    @BindView(R.id.listViewMemeTags)
    ListView tagListView;

    private ShareActionProvider mShareActionProvider;

    private Meme meme;

    private String mimeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);

        ((DankBankApplication) getApplication()).getDankBankComponent().inject(this);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Uri memeUri = (Uri) getIntent().getExtras().get("memeUri");
        meme = memeDao.getMemeByUri(memeUri);

        bigMemeDisplayImageView.setImageURI(memeUri);

        String[] pathSplit = memeUri.getPath().split("\\.");
        mimeType = "image/" + pathSplit[pathSplit.length - 1];

        Log.d(MemeActivity.class.getName(), "Meme MIME type: " + mimeType);

        List<String> tagNames = new ArrayList<>();
        for (Tag t : meme.getTags()) {
            tagNames.add(t.getTagName());
        }

        tagListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tagNames));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meme_action_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.meme_share:
                ShareCompat.IntentBuilder
                        .from(this)
                        .setChooserTitle("Share your meme")
                        .setStream(Uri.fromFile(new File(meme.getUri().getPath())))
                        .setType(mimeType)
                        .startChooser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
