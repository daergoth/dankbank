package net.daergoth.dankbank.image;

import net.daergoth.dankbank.tag.Tag;

import java.util.List;

public interface MemeDao {

    List<Meme> getAllMemes();

    void addMeme(Meme m);

    List<Meme> getMemesByTag(Tag t);

}
