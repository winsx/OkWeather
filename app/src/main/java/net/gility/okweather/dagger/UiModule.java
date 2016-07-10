package net.gility.okweather.dagger;

import net.gility.okweather.ui.misc.ActivityHierarchyServer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Alimy
 */

@Module
public class UiModule {
    @Provides @Singleton
    ActivityHierarchyServer provideActivityHierarchyServer() {
        return ActivityHierarchyServer.NONE;
    }
}
