package net.daergoth.dankbank.ui.meme;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import net.daergoth.dankbank.R;

import java.io.File;

public class MemeActivity extends AppCompatActivity {

    private ImageView bigMemeDisplayImageView;

    private ShareActionProvider mShareActionProvider;

    private Uri memeUri;

    private String mimeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);

        bigMemeDisplayImageView = (ImageView) findViewById(R.id.big_meme_display);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        memeUri = (Uri) getIntent().getExtras().get("memeUri");
        bigMemeDisplayImageView.setImageURI(memeUri);

        String[] pathSplit = memeUri.getPath().split("\\.");
        mimeType = "image/" + pathSplit[pathSplit.length - 1];

        Log.d(MemeActivity.class.getName(), "Meme MIME type: " + mimeType);
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
                        .setStream(Uri.fromFile(new File(memeUri.getPath())))
                        .setType(mimeType)
                        .startChooser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
