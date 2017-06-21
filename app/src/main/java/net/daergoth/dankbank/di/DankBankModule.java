package net.daergoth.dankbank.di;

import android.content.Context;

import net.daergoth.dankbank.image.MemeDao;
import net.daergoth.dankbank.image.MemeDaoImpl;
import net.daergoth.dankbank.tag.TagDao;
import net.daergoth.dankbank.tag.TagDaoImpl;

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
    public TagDao providesTagDao() {
        return new TagDaoImpl();
    }

    @Provides
    @Singleton
    public MemeDao providesMemeDao(TagDao tagDao) {
        return new MemeDaoImpl(tagDao);
    }

    @Provides
    @Singleton
    public Context providesContext() {
        return context;
    }
}
