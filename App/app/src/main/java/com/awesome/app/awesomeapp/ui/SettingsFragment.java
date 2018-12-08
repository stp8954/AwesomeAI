package com.awesome.app.awesomeapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.awesome.app.awesomeapp.R;
import com.awesome.app.awesomeapp.util.EventRecognitionService;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences mPreferences;
    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.app_preferences);
        getActivity().setTitle("Settings");
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     *
     * <p>This callback will be run on your main thread.
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals("vibrate"))
        {
            boolean vibrate= mPreferences.getBoolean(key, true);
            HomeFragment.onVibratePreferenceChanged(vibrate);
        }else if(key.equals("flash"))
        {
            boolean vibrate= mPreferences.getBoolean(key, true);
            HomeFragment.onFlashPreferenceChanged(vibrate);
        }else if(key.equals("android_wear"))
        {
            boolean vibrate= mPreferences.getBoolean(key, true);
            HomeFragment.onFitbitPreferenceChanged(vibrate);
        }else if(key.equals("alertInterval"))
        {
            String interval = mPreferences.getString(key, "1");

            try {
                EventRecognitionService.onAlertIntervalPreferenceChanged(Integer.parseInt(interval));
            }catch (Exception e)
            {
                EventRecognitionService.onAlertIntervalPreferenceChanged(1);
            }

        }
    }
}