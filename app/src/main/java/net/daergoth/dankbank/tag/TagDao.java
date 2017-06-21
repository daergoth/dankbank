package net.daergoth.dankbank.tag;


import java.util.List;

public interface TagDao {

    List<Tag> getAllTags();

    void addTag(Tag t);
}
