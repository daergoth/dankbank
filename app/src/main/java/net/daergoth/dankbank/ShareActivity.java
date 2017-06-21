package net.daergoth.dankbank;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

/**
 * @author Attila Bagossy
 * @author Petya Lakatos
 */
public class ShareActivity extends AppCompatActivity {

    private ImageView imageShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share_layout);

        imageShow = (ImageView) findViewById(R.id.imageShow);

        final Intent intent = getIntent();
        final String action = intent.getAction();
        final String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleImage(intent);
            }
        }
     }

    private void handleImage(Intent intent) {
        final Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            imageShow.setImageURI(imageUri);
        }
    }


}
