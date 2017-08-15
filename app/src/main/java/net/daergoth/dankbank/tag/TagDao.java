package net.daergoth.dankbank.tag;

import java.util.Collection;

public interface TagDao {
    Collection<Tag> getAllTags();

    void saveMeme(Tag t);

    Tag getTagById(int id);

    Tag getTagByName(String name);
}
