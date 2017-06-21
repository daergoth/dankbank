package net.daergoth.dankbank.tag;

import java.util.Collection;

public interface TagDao {
    Collection<Tag> getAllTags();

    void addTag(Tag t);

    Tag getTagById(int id);
}
