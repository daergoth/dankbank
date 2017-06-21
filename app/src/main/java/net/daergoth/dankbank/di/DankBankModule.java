package net.daergoth.dankbank.di;

import net.daergoth.dankbank.image.MemeDao;
import net.daergoth.dankbank.image.MemeDaoImpl;
import net.daergoth.dankbank.tag.TagDao;
import net.daergoth.dankbank.tag.TagDaoImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DankBankModule {
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
}
