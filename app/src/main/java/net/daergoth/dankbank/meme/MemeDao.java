package net.daergoth.dankbank.meme;

import net.daergoth.dankbank.tag.Tag;

import java.util.List;

public interface MemeDao {

    List<Meme> getAllMemes();

    void addMeme(Meme m);

    void deleteMeme(Meme m);

    List<Meme> getMemesByTag(Tag t);

}
