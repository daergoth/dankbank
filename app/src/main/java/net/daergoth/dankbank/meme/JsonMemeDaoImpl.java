package net.daergoth.dankbank.meme;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.daergoth.dankbank.tag.Tag;
import net.daergoth.dankbank.tag.TagDao;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonMemeDaoImpl implements MemeDao {
    private static final String FILENAME = "memes.json";

    private final TagDao tagDao;

    private final File saveFile;

    private final Gson gson;

    private Map<String, Meme> cachedMemes;

    public JsonMemeDaoImpl(TagDao tagDao, File saveDirectory, Gson gson) {
        this.tagDao = tagDao;

        this.saveFile = new File(saveDirectory, FILENAME);

        this.gson = gson;

        this.cachedMemes = loadMemes();
    }

    @Override
    public List<Meme> getAllMemes() {
        return new ArrayList<>(cachedMemes.values());
    }

    @Override
    public Meme getMemeByUri(Uri uri) {
        return cachedMemes.get(uri.toString());
    }

    @Override
    public void saveMeme(Meme m) {
        if (m == null) {
            throw new NullPointerException("Meme cannot be null!");
        }

        cachedMemes.put(m.getUri().toString(), m);

        saveChanges();
    }

    @Override
    public void deleteMeme(Meme m) {
        if (m == null) {
            throw new NullPointerException("Meme cannot be null!");
        }

        String uniqueUriString = m.getUri().toString();

        if (cachedMemes.containsKey(uniqueUriString)) {
            cachedMemes.remove(uniqueUriString);

            saveChanges();
        }
    }

    @Override
    public List<Meme> getMemesByTag(final Tag t) {
        final List<Meme> memesWithGivenTag = new ArrayList<>();

        for (Meme m : cachedMemes.values()) {
            if (m.getTags().contains(t)) {
                memesWithGivenTag.add(m);
            }
        }

        return memesWithGivenTag;
    }

    private void saveChanges() {
        final StoredMemes storedMemes = convertToStoredMemes(this.cachedMemes.values());

        try {
            final JsonWriter jsonWriter = new JsonWriter(new FileWriter(saveFile));

            gson.toJson(storedMemes, StoredMemes.class, jsonWriter);

            jsonWriter.close();
        } catch (IOException e) {
            Log.e(JsonMemeDaoImpl.class.getName(), e.getMessage());
        }
    }

    private Map<String, Meme> loadMemes() {
        if (!saveFile.exists()) {
            return new HashMap<>();
        }

        try {
            final JsonReader jsonReader = new JsonReader(new FileReader(saveFile));

            final StoredMemes storedMemes = gson.fromJson(jsonReader, StoredMemes.class);

            return convertFromStoredMemes(storedMemes);
        } catch (IOException e) {
            // this branch should not be reached at all

            return new HashMap<>();
        }
    }

    private StoredMemes convertToStoredMemes(Collection<Meme> memes) {
        final StoredMemes storedMemes = new StoredMemes();

        final List<StoredMeme> storedMemeList = new ArrayList<>();

        for (Meme meme : memes) {
            final StoredMeme storedMeme = new StoredMeme();

            storedMeme.setUri(meme.getUri().getPath());

            final List<Integer> tagIds = new ArrayList<>();

            for (Tag tag : meme.getTags()) {
                tagIds.add(tag.getId());
            }

            storedMeme.setTagIds(tagIds);

            storedMemeList.add(storedMeme);
        }

        storedMemes.setMemes(storedMemeList);

        return storedMemes;
    }

    private Map<String, Meme> convertFromStoredMemes(StoredMemes storedMemes) {
        final Map<String, Meme> result = new HashMap<>();

        for (StoredMeme storedMeme : storedMemes.getMemes()) {
            final Meme meme = new Meme();

            meme.setUri(Uri.parse(storedMeme.getUri()));

            final Set<Tag> tags = new HashSet<>(storedMeme.getTagIds().size());

            for (Integer id : storedMeme.getTagIds()) {
                final Tag tag = tagDao.getTagById(id);

                if (tag != null) {
                    tags.add(tag);
                }
            }

            meme.setTags(tags);

            result.put(meme.getUri().toString(), meme);
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
