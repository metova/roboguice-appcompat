package com.metova.roboguice.appcompat;

import java.util.HashMap;
import java.util.Map;

import roboguice.RoboGuice;
import roboguice.activity.event.*;
import roboguice.context.event.*;
import roboguice.event.EventManager;
import roboguice.inject.ContentViewListener;
import roboguice.inject.RoboInjector;
import roboguice.util.RoboContext;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.inject.Inject;
import com.google.inject.Key;

public class RoboActionBarActivity extends AppCompatActivity implements RoboContext {

    protected EventManager eventManager;
    protected HashMap<Key<?>, Object> scopedObjects = new HashMap<Key<?>, Object>();

    @Inject
    ContentViewListener ignored; // BUG find a better place to put this

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final RoboInjector injector = RoboGuice.getInjector(this);
        eventManager = injector.getInstance(EventManager.class);
        injector.injectMembersWithoutViews(this);
        super.onCreate(savedInstanceState);
        eventManager.fire(new OnCreateEvent<>(this, savedInstanceState));
    }

    @Override
    protected void onRestart() {

        super.onRestart();
        eventManager.fire(new OnRestartEvent(this));
    }

    @Override
    protected void onStart() {

        super.onStart();
        eventManager.fire(new OnStartEvent<>(this));
    }

    @Override
    protected void onResume() {

        super.onResume();
        eventManager.fire(new OnResumeEvent(this));
    }

    @Override
    protected void onPause() {

        super.onPause();
        eventManager.fire(new OnPauseEvent(this));
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        eventManager.fire(new OnNewIntentEvent(this));
    }

    @Override
    protected void onStop() {

        try {
            eventManager.fire(new OnStopEvent(this));
        } finally {
            super.onStop();
        }
    }

    @Override
    protected void onDestroy() {

        try {
            eventManager.fire(new OnDestroyEvent<>(this));
        } finally {
            try {
                RoboGuice.destroyInjector(this);
            } finally {
                super.onDestroy();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        final Configuration currentConfig = getResources().getConfiguration();
        super.onConfigurationChanged(newConfig);
        eventManager.fire(new OnConfigurationChangedEvent<>(this, currentConfig, newConfig));
    }

    @Override
    public void onSupportContentChanged() {

        super.onSupportContentChanged();
        RoboGuice.getInjector(this).injectViewMembers(this);
        eventManager.fire(new OnContentChangedEvent(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        eventManager.fire(new OnActivityResultEvent(this, requestCode, resultCode, data));
    }

    @Override
    public Map<Key<?>, Object> getScopedObjectMap() {

        return scopedObjects;
    }
}