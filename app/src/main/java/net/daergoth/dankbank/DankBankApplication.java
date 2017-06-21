package net.daergoth.dankbank;

import android.app.Application;

import net.daergoth.dankbank.di.DaggerDankBankComponent;
import net.daergoth.dankbank.di.DankBankComponent;
import net.daergoth.dankbank.di.DankBankModule;

public class DankBankApplication extends Application {

    private DankBankComponent dankBankComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        dankBankComponent = DaggerDankBankComponent.builder()
                .dankBankModule(new DankBankModule(this))
                .build();
    }

    public DankBankComponent getDankBankComponent() {
        return dankBankComponent;
    }
}
