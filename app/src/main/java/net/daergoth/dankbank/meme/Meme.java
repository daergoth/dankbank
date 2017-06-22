package net.daergoth.dankbank.meme;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meme meme = (Meme) o;

        return uri.equals(meme.uri);

    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public String toString() {
        return "Meme{" +
                "uri=" +
                '}';
    }
}
