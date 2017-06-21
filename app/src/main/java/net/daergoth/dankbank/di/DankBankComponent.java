package net.daergoth.dankbank.di;

import net.daergoth.dankbank.ui.MainActivity;
import net.daergoth.dankbank.ui.ShareActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DankBankModule.class})
public interface DankBankComponent {
    void inject(MainActivity mainActivity);

    void inject(ShareActivity shareActivity);
}
