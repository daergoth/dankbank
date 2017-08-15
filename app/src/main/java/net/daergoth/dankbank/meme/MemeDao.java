package net.daergoth.dankbank.meme;

import android.net.Uri;

import net.daergoth.dankbank.tag.Tag;

import java.util.List;

public interface MemeDao {

    List<Meme> getAllMemes();

    Meme getMemeByUri(Uri uri);

    void saveMeme(Meme m);

    void deleteMeme(Meme m);

    List<Meme> getMemesByTag(Tag t);

}
