/*
Aplicació mòbil que permet implementar sistemes de pagament per generació de residus enfocats a bonificar els usuaris en base al seu bon comportament. Mitjançant la lectura de codis QR adherits als contenidors, el ciutadà pot informar proactivament al seu ajuntament de les seves actuacions de reciclatge i rebre bonificacions en base a actuacions positives.
Copyright (C) 2018  Urgellet Recicla

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

Contact email meu@urgellet.cat

The GNU General Public License does not permit incorporating your program
into proprietary programs.  If your program is a subroutine library, you
may consider it more useful to permit linking proprietary applications with
the library.  If this is what you want to do, use the GNU Lesser General
Public License instead of this License.  But first, please read
<https://www.gnu.org/licenses/why-not-lgpl.html>.
*/

package com.smartcitylink.urgellet.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.UrgelletApplication;
import com.smartcitylink.urgellet.events.FinishSyncEvent;
import com.smartcitylink.urgellet.events.LogoutEvent;
import com.smartcitylink.urgellet.events.NoConnectionEvent;
import com.smartcitylink.urgellet.helpers.Constants;
import com.smartcitylink.urgellet.helpers.Utils;
import com.smartcitylink.urgellet.manager.DataManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckActivity extends BaseActivity {

    /**
     * Listens to FinishSyncEvent Event
     * @param event FinishSyncEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishSyncEvent(FinishSyncEvent event) {
        if (event.success) {
            goToMainActivity();
        }
    }

    /**
     * Listens to Logout EVent
     * @param event LogoutEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void oLogoutEventEvent(LogoutEvent event) {
        if (!event.success) {
            goToLoginActivity();
        } else {
            goToLoginActivity();
        }
    }


    /**
     * Listens on NoConnectionEvent
     * @param event NoConnectionEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoConnectionEventEvent(NoConnectionEvent event) {
        goToMainActivity();
    }

    private int milliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_activity);

        EventBus.getDefault().register(this);

        if(Utils.isOnline(getApplicationContext())){
            checkUserStatus(true);
        } else {
            delay(2);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * Shows the Splash Screen two seconds.
     *
     * @param seconds [description] Seconds before check user status
     */
    public void delay(int seconds){
        milliseconds = seconds * 1000;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkUserStatus(false);
                    }
                }, milliseconds);
            }
        });
    }

    /**
     * Checks if the user has already log in the application
     * @param isOnline true si es online para sincronizar
     */
    private void checkUserStatus(boolean isOnline) {

        if (!checkUserIsLoggedIn()) {
            DataManager.getInstance().doLogout();
            goToLoginActivity();
        }
        else {
            if(isOnline) {
                
                //TODO: sync from server User class and call saveUserInfo method on DataManager.java class and save user data on Singleton
                //TODO: sync from server Salari class and save data on Realm and call saveUserData method on DataManager.java
                //TODO: sync from server SalariDeixalleria class and call saveUserDataDeixalleria method on DataManager.java class
                //TODO: sync from server NotificationsResponse class and call saveNotificacions method on DataManager.java class
                //TODO: sync from server ActuacionesResponse class and call saveActuaciones method on DataManager.java class
                //TODO: sync from server ElementoResponse class and save data on Realm and call saveElementos method on DataManager.java class
                
                goToMainActivity();
            }
            else goToMainActivity();
        }
    }

    /**
     * Checks if User is Logged in
     * @return Boolean
     */
    private boolean checkUserIsLoggedIn() {
            loadPreferences();
            loadPreferencesThis();
            loadPreferencesPrivate();


            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());

            Boolean isLoggedIn = sharedPref.getBoolean(Constants.SHARED_LOGGED, false);
            String authToken = sharedPref.getString(Constants.SHARED_TOKEN, null);

            if(!isLoggedIn || authToken == null){
                SharedPreferences sharedPref2 = getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE);

                isLoggedIn = sharedPref2.getBoolean(Constants.SHARED_LOGGED, false);
                authToken = sharedPref2.getString(Constants.SHARED_TOKEN, null);

                Log.d("sharedprefsLog", "vals private " + isLoggedIn);
                Log.d("sharedprefsLog", "vals private" + authToken);

                if (isLoggedIn || authToken != null){
                    Log.d("sharedprefsLog", "vals if");
                    saveOnAppContext(isLoggedIn, authToken);
                }

            }

        Log.d("sharedprefsLog", "vals " + isLoggedIn);
        Log.d("sharedprefsLog", "vals " + authToken);

            return !(!isLoggedIn || authToken == null);

    }

    private void saveOnAppContext(Boolean isLoggedIn, String authToken) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Constants.SHARED_LOGGED, isLoggedIn);
        editor.putString(Constants.SHARED_TOKEN, authToken);

        editor.apply();
    }

    @SuppressWarnings("unchecked")
    public void loadPreferencesPrivate() {
        Map<String, ?> prefs = getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE).getAll();
        Log.d("sharedprefsLog", "_____________________START LOG PRIVATE_____________________");

        for (String key : prefs.keySet()) {
            Object pref = prefs.get(key);
            String printVal = "";
            if (pref instanceof Boolean) {
                printVal =  key + " : " + (Boolean) pref;
            }
            if (pref instanceof Float) {
                printVal =  key + " : " + (Float) pref;
            }
            if (pref instanceof Integer) {
                printVal =  key + " : " + (Integer) pref;
            }
            if (pref instanceof Long) {
                printVal =  key + " : " + (Long) pref;
            }
            if (pref instanceof String) {
                printVal =  key + " : " + (String) pref;
            }
            if (pref instanceof Set<?>) {
                printVal =  key + " : " + (Set<String>) pref;
            }
            Log.d("sharedprefsLog", "PRIVATE " + printVal);
        }
    }


    @SuppressWarnings("unchecked")
    public void loadPreferences() {
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext()).getAll();
        Log.d("sharedprefsLog", "_____________________START LOG_____________________");

        for (String key : prefs.keySet()) {
            Object pref = prefs.get(key);
            String printVal = "";
            if (pref instanceof Boolean) {
                printVal =  key + " : " + (Boolean) pref;
            }
            if (pref instanceof Float) {
                printVal =  key + " : " + (Float) pref;
            }
            if (pref instanceof Integer) {
                printVal =  key + " : " + (Integer) pref;
            }
            if (pref instanceof Long) {
                printVal =  key + " : " + (Long) pref;
            }
            if (pref instanceof String) {
                printVal =  key + " : " + (String) pref;
            }
            if (pref instanceof Set<?>) {
                printVal =  key + " : " + (Set<String>) pref;
            }
            Log.d("sharedprefsLog", printVal);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadPreferencesThis() {
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(
                this).getAll();
        Log.d("sharedprefsLog", "_____________________START LOG THIS_____________________");

        for (String key : prefs.keySet()) {
            Object pref = prefs.get(key);
            String printVal = "";
            if (pref instanceof Boolean) {
                printVal =  key + " : " + (Boolean) pref;
            }
            if (pref instanceof Float) {
                printVal =  key + " : " + (Float) pref;
            }
            if (pref instanceof Integer) {
                printVal =  key + " : " + (Integer) pref;
            }
            if (pref instanceof Long) {
                printVal =  key + " : " + (Long) pref;
            }
            if (pref instanceof String) {
                printVal =  key + " : " + (String) pref;
            }
            if (pref instanceof Set<?>) {
                printVal =  key + " : " + (Set<String>) pref;
            }
            Log.d("sharedprefsLog", "THIS " + printVal);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
