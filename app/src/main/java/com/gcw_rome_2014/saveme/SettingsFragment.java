package com.gcw_rome_2014.saveme;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Alessio on 05/01/2015.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}