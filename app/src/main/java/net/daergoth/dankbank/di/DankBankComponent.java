package net.daergoth.dankbank.di;

import net.daergoth.dankbank.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DankBankModule.class})
public interface DankBankComponent {


    void inject(MainActivity mainActivity);
}
