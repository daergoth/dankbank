package net.daergoth.dankbank.ui.meme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import net.daergoth.dankbank.DankBankApplication;
import net.daergoth.dankbank.R;
import net.daergoth.dankbank.meme.Meme;
import net.daergoth.dankbank.meme.MemeDao;
import net.daergoth.dankbank.tag.Tag;
import net.daergoth.dankbank.tag.TagDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MemeActivity extends AppCompatActivity {

    @Inject
    MemeDao memeDao;

    @Inject
    TagDao tagDao;

    @BindView(R.id.meme_display)
    ImageView bigMemeDisplayImageView;

    @BindView(R.id.listViewMemeTags)
    ListView tagListView;

    @BindView(R.id.autoTextViewNewTag)
    AutoCompleteTextView autoTextNewTag;

    private Meme meme;

    private String mimeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);

        ((DankBankApplication) getApplication()).getDankBankComponent().inject(this);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Uri memeUri;
        Bundle intentBundle = getIntent().getExtras();
        if (intentBundle != null) {
            memeUri = (Uri) intentBundle.get("memeUri");
        } else {
            memeUri = Uri.parse(savedInstanceState.getString("memeUri"));
        }

        meme = memeDao.getMemeByUri(memeUri);

        bigMemeDisplayImageView.setImageURI(memeUri);

        String[] pathSplit = memeUri.getPath().split("\\.");
        mimeType = "image/" + pathSplit[pathSplit.length - 1];

        Log.d(MemeActivity.class.getName(), "Meme MIME type: " + mimeType);

        List<String> tagNames = new ArrayList<>();
        for (Tag t : tagDao.getAllTags()) {
            tagNames.add(t.getTagName());
        }
        autoTextNewTag.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tagNames));

        updateTagList();
    }

    @OnClick(R.id.meme_display)
    void memeDisplayOnClick() {
        Intent immerseMemeIntent = new Intent(MemeActivity.this, ImmerseMemeActivity.class);
        immerseMemeIntent.putExtra("memeUri", meme.getUri());
        startActivity(immerseMemeIntent);
    }

    @OnClick(R.id.buttonAddTag)
    void addNewTagToMemeOnClick() {
        String newTagName = autoTextNewTag.getText().toString();

        if (!newTagName.isEmpty()) {
            Tag tag = tagDao.getTagByName(newTagName);

            if (tag == null) {
                tag = new Tag();
                tag.setTagName(newTagName);
                tagDao.saveMeme(tag);
            }

            meme.getTags().add(tag);
            memeDao.saveMeme(meme);

            updateTagList();

            autoTextNewTag.setText("");
            autoTextNewTag.clearFocus();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("memeUri", meme.getUri().toString());

        super.onSaveInstanceState(outState);
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
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void removeTagOnClick(View v) {
        TextView tagNameTextView = ButterKnife.findById(v.getRootView(), R.id.textViewTagItemName);

        meme.getTags().remove(tagDao.getTagByName(tagNameTextView.getText().toString()));

        updateTagList();
    }

    private void updateTagList() {
        List<String> addedTagNames = new ArrayList<>();
        for (Tag t : meme.getTags()) {
            addedTagNames.add(t.getTagName());
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, R.layout.tag_list_item, R.id.textViewTagItemName, addedTagNames);
        tagListView.setAdapter(arrayAdapter);
    }
}
