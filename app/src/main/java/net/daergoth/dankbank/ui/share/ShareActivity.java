package net.daergoth.dankbank.ui.share;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.daergoth.dankbank.DankBankApplication;
import net.daergoth.dankbank.R;
import net.daergoth.dankbank.meme.Meme;
import net.daergoth.dankbank.meme.MemeDao;
import net.daergoth.dankbank.tag.Tag;
import net.daergoth.dankbank.tag.TagDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Attila Bagossy
 * @author Petya Lakatos
 */
public class ShareActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String ALBUM_NAME = "DankMemes";

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 24231;

    @Inject
    MemeDao memeDao;

    @Inject
    TagDao tagDao;

    @BindView(R.id.memeImage)
    ImageView imageShow;

    @BindView(R.id.autoTextViewTag)
    AutoCompleteTextView tagAutoTextView;

    @BindView(R.id.listViewAddedTags)
    ListView tagsListView;

    @BindView(R.id.saveMemeFloatingActionButton)
    FloatingActionButton saveMemeButton;

    private Uri imageUri;

    private List<String> tagItemList;

    private ListAdapter listAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((DankBankApplication) getApplication()).getDankBankComponent().inject(this);

        setContentView(R.layout.share_layout);

        ButterKnife.bind(this);

        imageUri = getImageUri(getIntent());

        tagItemList = new ArrayList<>();
        updateAddedTagsList();

        List<String> tagNames = new ArrayList<>();
        for (Tag t : tagDao.getAllTags()) {
            tagNames.add(t.getTagName());
        }
        tagAutoTextView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tagNames));

        if (imageUri != null) {
            Log.d(ShareActivity.class.getName(), "Shared image URI: " + imageUri.toString());

            imageShow.setImageURI(imageUri);
        } else {
            saveMemeButton.setEnabled(false);
        }
    }

    @OnClick(R.id.buttonAddTag)
    void addTagOnClick() {
        String tagName = tagAutoTextView.getText().toString();

        tagItemList.add(tagName);

        updateAddedTagsList();

        tagAutoTextView.setText("");
        tagAutoTextView.clearFocus();
    }

    void removeTagOnClick(View v) {
        TextView tagNameTextView = ButterKnife.findById(v.getRootView(), R.id.textViewTagItemName);

        tagItemList.remove(tagNameTextView.getText().toString());

        updateAddedTagsList();
    }

    @OnClick(R.id.saveMemeFloatingActionButton)
    void saveMemeOnClick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Can't save meme without permission", Toast.LENGTH_SHORT).show();

                return;
            }
        }

        saveImage(imageUri, getFileExtension(getIntent()));
        finish();
    }

    private void updateAddedTagsList() {
        listAdapter = new ArrayAdapter<>(this, R.layout.share_tag_list_item, R.id.textViewTagItemName, tagItemList);
        tagsListView.setAdapter(listAdapter);
    }

    private void saveImage(Uri imageUri, String extension) {
        if (!isExternalStorageWritable()) {
            return;
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

            List<Tag> attachedTagList = new ArrayList<>();
            for (String tagName : tagItemList) {
                Tag tag = tagDao.getTagByName(tagName);

                if (tag == null) {
                    tag = new Tag();
                    tag.setTagName(tagName);
                    tagDao.addTag(tag);
                }

                attachedTagList.add(tag);
            }
            meme.setTags(attachedTagList);

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

    private File getAlbumStorageDir(Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ALBUM_NAME);

        if (!file.mkdirs()) {
            Log.e(ShareActivity.class.getName(), "Directory not created");

            Log.d(ShareActivity.class.getName(), String.valueOf(file.exists()));
        }

        return file;
    }
}
