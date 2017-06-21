package net.daergoth.dankbank.di;

import android.content.Context;

import com.google.gson.Gson;

import net.daergoth.dankbank.meme.MemeDao;
import net.daergoth.dankbank.meme.MemeDaoImpl;
import net.daergoth.dankbank.tag.TagDao;
import net.daergoth.dankbank.tag.TagDaoImpl;

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
    public TagDao providesTagDao(@Named("Internal Directory") File saveDirectory) {
        return new TagDaoImpl(saveDirectory);
    }

    @Provides
    @Singleton
    public MemeDao providesMemeDao(TagDao tagDao, @Named("Internal Directory") File saveDirectory) {
        return new MemeDaoImpl(tagDao, saveDirectory);
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
