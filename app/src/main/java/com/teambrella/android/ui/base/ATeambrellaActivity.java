package com.teambrella.android.ui.base;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.teambrella.android.dagger.Dependencies;
import com.teambrella.android.image.TeambrellaImageLoader;
import com.teambrella.android.ui.TeambrellaUser;
import com.teambrella.android.util.log.Log;
import com.teambrella.android.wallet.TeambrellaWalletRequestFragment;

import java.util.LinkedList;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Base Teambrella Activity
 */
public abstract class ATeambrellaActivity extends TeambrellaDataHostActivity {

    public static final String EXTRA_BACK_PRESSED_INTENT = "extra_back_pressed_intent";

    private static final String LOG_TAG = ATeambrellaActivity.class.getSimpleName();

    private static final String WALLET_DATA_FRAGMENT_TAG = "wallet_data_fragment";

    @Named(Dependencies.TEAMBRELLA_USER)
    TeambrellaUser mUser;

    @Inject
    @Named(Dependencies.IMAGE_LOADER)
    TeambrellaImageLoader mImageLoader;

    protected final TeambrellaUser getUser() {
        return mUser;
    }

    private LinkedList<TeambrellaActivityLifecycle> mLifecycleCallbacks = new LinkedList<>();


    public void registerLifecycleCallback(TeambrellaActivityLifecycle lifecycle) {
        mLifecycleCallbacks.add(lifecycle);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent().inject(this);
        for (TeambrellaActivityLifecycle lifecycle : mLifecycleCallbacks) {
            lifecycle.onCreate(this, savedInstanceState);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentByTag(WALLET_DATA_FRAGMENT_TAG) == null) {
            fragmentManager.beginTransaction().add(new TeambrellaWalletRequestFragment()
                    , WALLET_DATA_FRAGMENT_TAG).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (TeambrellaActivityLifecycle lifecycle : mLifecycleCallbacks) {
            lifecycle.onStart();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        TeambrellaWalletRequestFragment fragment = (TeambrellaWalletRequestFragment)
                fragmentManager.findFragmentByTag(WALLET_DATA_FRAGMENT_TAG);
        if (fragment != null) {
            fragment.sync();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        for (TeambrellaActivityLifecycle lifecycle : mLifecycleCallbacks) {
            lifecycle.onResume();
        }
    }

    protected TeambrellaImageLoader getImageLoader() {
        return mImageLoader;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final Intent intent = getIntent();
        final PendingIntent backPressedIntent = intent != null ? intent.getParcelableExtra(EXTRA_BACK_PRESSED_INTENT) : null;
        if (backPressedIntent != null) {
            try {
                backPressedIntent.send();
            } catch (PendingIntent.CanceledException e) {
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (TeambrellaActivityLifecycle lifecycle : mLifecycleCallbacks) {
            lifecycle.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (TeambrellaActivityLifecycle lifecycle : mLifecycleCallbacks) {
            lifecycle.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (TeambrellaActivityLifecycle lifecycle : mLifecycleCallbacks) {
            lifecycle.onDestroy(this);
        }
    }
}
