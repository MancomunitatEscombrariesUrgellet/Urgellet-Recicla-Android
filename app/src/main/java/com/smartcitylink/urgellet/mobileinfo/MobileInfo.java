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
package com.smartcitylink.urgellet.mobileinfo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.smartcitylink.urgellet.BuildConfig;
import com.smartcitylink.urgellet.mobileinfo.model.ApHeaderModel;

import java.util.Calendar;

public class MobileInfo {

    public static String getApp() {

        ApHeaderModel appHeaderModel = new ApHeaderModel();
        appHeaderModel.setVersionCode(BuildConfig.VERSION_CODE);
        appHeaderModel.setVersionName(BuildConfig.VERSION_NAME);
        appHeaderModel.setSdkVersion(android.os.Build.VERSION.SDK_INT);
        appHeaderModel.setManufacturer(Build.MANUFACTURER);
        appHeaderModel.setModel(Build.MODEL);
        appHeaderModel.setAppName(BuildConfig.APPLICATION_ID);
        appHeaderModel.setOs("Android");
        Gson gson = new Gson();
        return gson.toJson(appHeaderModel);
    }

    public static void setAlarm(Context context) {

        Log.d("AlarmReceiver",  "SETTING ALARM" );

        // Set the alarm here.
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND,0);

        Intent newIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, newIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    static void sendMobiin(final Context context) {
        Log.d("AlarmReceiver",  "sendMobiin" );

        //TODO SEND DATA

    }
}
