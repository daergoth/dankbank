package net.daergoth.dankbank.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import net.daergoth.dankbank.R;

public class MemeActivity extends AppCompatActivity {

    private ImageView bigMemeDisplayImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme);

        bigMemeDisplayImageView = (ImageView) findViewById(R.id.big_meme_display);

        Uri memeUri = (Uri) getIntent().getExtras().get("memeUri");
        bigMemeDisplayImageView.setImageURI(memeUri);
    }
}
