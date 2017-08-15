package net.daergoth.dankbank.di;

import android.content.Context;

import com.google.gson.Gson;

import net.daergoth.dankbank.meme.MemeDao;
import net.daergoth.dankbank.meme.JsonMemeDaoImpl;
import net.daergoth.dankbank.tag.TagDao;
import net.daergoth.dankbank.tag.JsonTagDaoImpl;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DankBankModule {
    private final Context context;

    public DankBankModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    @Named("Internal Directory")
    public File getInternalDirectory(Context context) {
        return context.getFilesDir();
    }

    @Provides
    @Singleton
    public TagDao providesTagDao(@Named("Internal Directory") File saveDirectory, Gson gson) {
        return new JsonTagDaoImpl(saveDirectory, gson);
    }

    @Provides
    @Singleton
    public MemeDao providesMemeDao(TagDao tagDao, @Named("Internal Directory") File saveDirectory, Gson gson) {
        return new JsonMemeDaoImpl(tagDao, saveDirectory, gson);
    }

    @Provides
    @Singleton
    public Context providesContext() {
        return context;
    }

    @Provides
    @Singleton
    public Gson providesGson() {
        return new Gson();
    }
}
