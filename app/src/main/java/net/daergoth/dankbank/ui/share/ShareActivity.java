package net.daergoth.dankbank.ui.share;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import net.daergoth.dankbank.DankBankApplication;
import net.daergoth.dankbank.R;
import net.daergoth.dankbank.meme.Meme;
import net.daergoth.dankbank.meme.MemeDao;
import net.daergoth.dankbank.tag.Tag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import javax.inject.Inject;

/**
 * @author Attila Bagossy
 * @author Petya Lakatos
 */
public class ShareActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String ALBUM_NAME = "DankMemes";

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 24231;

    @Inject
    MemeDao memeDao;

    private ImageView imageShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((DankBankApplication) getApplication()).getDankBankComponent().inject(this);

        setContentView(R.layout.share_layout);

        imageShow = (ImageView) findViewById(R.id.imageShow);

        final Intent intent = getIntent();

        final Uri imageUri = getImageUri(intent);


        if (imageUri != null) {
            Log.d(ShareActivity.class.getName(), "Shared image URI: " + imageUri.toString());

            imageShow.setImageURI(imageUri);

            saveImage(imageUri, getFileExtension(intent));
        }
    }

    private void saveImage(Uri imageUri, String extension) {
        if (!isExternalStorageWritable()) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

        try {
            final InputStream is = getContentResolver().openInputStream(imageUri);

            final File albumDir = getAlbumStorageDir(getApplicationContext());

            final File outputFile = generateFile(albumDir, extension);

            OutputStream os = new FileOutputStream(outputFile);

            byte[] buffer = new byte[10240];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }

            is.close();
            os.close();

            final Meme meme = new Meme();

            meme.setTags(new ArrayList<Tag>());

            meme.setUri(Uri.parse(outputFile.getPath()));

            memeDao.addMeme(meme);

            Log.i(ShareActivity.class.getName(), "Saved meme at " + outputFile.getPath());
        } catch (IOException e) {
            Log.e(ShareActivity.class.getName(), e.getMessage());
        }
    }

    private File generateFile(File parent, String extension) {
        File result;

        do {
            final String uuid = UUID.randomUUID().toString();

            result = new File(parent, uuid + "." + extension);
        } while (result.exists());

        return result;
    }

    private Uri getImageUri(Intent intent) {
        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if (intent.getType().startsWith("image/")) {
                return (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            }
        }

        return null;
    }

    private String getFileExtension(Intent intent) {
        final String type = intent.getType();

        return type.split("\\/")[1];
    }

    private boolean isExternalStorageWritable() {
        final String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public File getAlbumStorageDir(Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ALBUM_NAME);

        if (!file.mkdirs()) {
            Log.e(ShareActivity.class.getName(), "Directory not created");

            Log.d(ShareActivity.class.getName(), String.valueOf(file.exists()));
        }

        return file;
    }
}
