package net.daergoth.dankbank.tag;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TagDaoImpl implements TagDao {

    private final File saveDirectory;

    private List<Tag> cachedTags;

    public TagDaoImpl(File saveDirectory) {
        this.saveDirectory = saveDirectory;

        this.cachedTags = new ArrayList<>();
    }

    @Override
    public List<Tag> getAllTags() {
        return Collections.unmodifiableList(cachedTags);
    }

    @Override
    public void addTag(Tag t) {
        if (t == null) {
            throw new NullPointerException("Tag cannot be null!");
        }

        if (!cachedTags.contains(t)) {
            cachedTags.add(t);

            saveChanges();
        }
    }

    private void saveChanges() {

    }
}
