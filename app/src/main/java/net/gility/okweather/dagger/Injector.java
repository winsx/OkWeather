package net.gility.okweather.dagger;

import android.content.Context;

/**
 * @author Alimy
 */

public final class Injector {
    private static final String INJECTOR_SERVICE = "net.gility.okweather.dagger.injector";

    @SuppressWarnings({"ResourceType", "WrongConstant"}) // Explicitly doing a custom service.
    public static AppComponent obtain(Context context) {
        return (AppComponent) context.getSystemService(INJECTOR_SERVICE);
    }

    public static boolean matchesService(String name) {
        return INJECTOR_SERVICE.equals(name);
    }

    private Injector() {
        throw new AssertionError("No instances.");
    }
}