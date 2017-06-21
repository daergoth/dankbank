package net.daergoth.dankbank.meme;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.daergoth.dankbank.tag.Tag;
import net.daergoth.dankbank.tag.TagDao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemeDaoImpl implements MemeDao {
    private static final String FILENAME = "memes.json";

    private final TagDao tagDao;

    private final File saveFile;

    private final Gson gson;

    private List<Meme> cachedMemes;

    public MemeDaoImpl(TagDao tagDao, File saveDirectory, Gson gson) {
        this.tagDao = tagDao;

        this.saveFile = new File(saveDirectory, FILENAME);

        this.gson = gson;

        this.cachedMemes = loadMemes();
    }

    @Override
    public List<Meme> getAllMemes() {
        return Collections.unmodifiableList(cachedMemes);
    }

    @Override
    public void addMeme(Meme m) {
        if (m == null) {
            throw new NullPointerException("Meme cannot be null!");
        }

        if (!cachedMemes.contains(m)) {
            cachedMemes.add(m);

            saveChanges();
        }
    }

    @Override
    public List<Meme> getMemesByTag(final Tag t) {
        List<Meme> memesWithGivenTag = new ArrayList<>();

        for (Meme m : cachedMemes) {
            if(m.getTags().contains(t)) {
                memesWithGivenTag.add(m);
            }
        }

        return memesWithGivenTag;
    }

    private void saveChanges() {

    }



    private List<Meme> loadMemes() {
        if (!saveFile.exists()) {
            return new ArrayList<>();
        }

        try {
            final JsonReader jsonReader = new JsonReader(new FileReader(saveFile));

            final StoredMemes storedMemes = gson.fromJson(jsonReader, StoredMemes.class);

            return convertStoredMemes(storedMemes);
        } catch (FileNotFoundException e) {
            // this branch should not be reached at all

            return new ArrayList<>();
        }
    }

    private List<Meme> convertStoredMemes(StoredMemes storedMemes) {
        final List<Meme> result = new ArrayList<>();

        for (StoredMeme storedMeme : storedMemes.getMemes()) {
            final Meme meme = new Meme();

            meme.setUri(Uri.parse(storedMeme.getUri()));

            final List<Tag> tags = new ArrayList<>(storedMeme.getTagIds().size());

            for (Integer id : storedMeme.getTagIds()) {
                final Tag tag = tagDao.getTagById(id);

                if (tag != null) {
                    tags.add(tag);
                }
            }

            meme.setTags(tags);
        }

        return result;
    }


    private static class StoredMemes {
        private List<StoredMeme> memes;

        private List<StoredMeme> getMemes() {
            return memes;
        }

        private void setMemes(List<StoredMeme> memes) {
            this.memes = memes;
        }
    }

    private static class StoredMeme {
        private String uri;

        private List<Integer> tagIds;

        private String getUri() {
            return uri;
        }

        private void setUri(String uri) {
            this.uri = uri;
        }

        private List<Integer> getTagIds() {
            return tagIds;
        }

        private void setTagIds(List<Integer> tagIds) {
            this.tagIds = tagIds;
        }
    }
}
