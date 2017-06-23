package net.daergoth.dankbank.di;

import net.daergoth.dankbank.ui.main.MainActivity;
import net.daergoth.dankbank.ui.share.ShareActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DankBankModule.class})
public interface DankBankComponent {
    void inject(MainActivity mainActivity);

    void inject(ShareActivity shareActivity);
}
