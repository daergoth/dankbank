package net.daergoth.dankbank.image;

import android.net.Uri;

import net.daergoth.dankbank.tag.Tag;

import java.util.List;

public class Meme {

    private Uri uri;

    private List<Tag> tags;

    public Uri getUri() {
        return uri;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
