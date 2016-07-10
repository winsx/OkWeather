package net.gility.okweather.utils;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class RxDrawer {

    private static final float OFFSET_THRESHOLD = 0.01f;

    public static Observable<Void> close(DrawerLayout drawer) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                drawer.closeDrawer(GravityCompat.START);
                DrawerLayout.DrawerListener listener = new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        if (slideOffset < OFFSET_THRESHOLD) {
                            subscriber.onNext(null);
                            subscriber.onCompleted();
                        }
                    }
                };
                drawer.addDrawerListener(listener);
                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        drawer.removeDrawerListener(listener);
                    }
                });
            }
        });
    }
}
