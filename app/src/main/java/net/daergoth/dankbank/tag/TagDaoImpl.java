package net.daergoth.dankbank.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TagDaoImpl implements TagDao {

    private List<Tag> cachedTags;

    public TagDaoImpl() {
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
