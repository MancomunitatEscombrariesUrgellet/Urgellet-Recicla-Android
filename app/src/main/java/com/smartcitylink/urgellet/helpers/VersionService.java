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
package com.smartcitylink.urgellet.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.UrgelletApplication;

import org.jsoup.Jsoup;

public class VersionService {

    public static final String TAG = VersionService.class.getName();

    private VersionChecker versionWS;

    private Activity activity;
    private String oldVersion;

    public VersionService(Activity a,String version) {
        this.activity = a;
        this.oldVersion = version;
    }

    public void checkVersion(String url){
        versionWS = new VersionChecker(activity,url,oldVersion);
        versionWS.execute();
    }


    private class VersionChecker extends AsyncTask<String, Void, Void> {

        private Activity activity;
        private String url;
        private String version;
        private String playVersion;


        private VersionChecker(Activity a,String url,String version) {
            this.activity = a;
            this.url = url;
            this.version = version;
            playVersion = null;
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground "+url);

            try {
                //String newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=com.whatsapp&hl=es")
                playVersion = Jsoup.connect(url)
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();

                Log.d(TAG,"play version "+playVersion);

            } catch (Exception e) {
                Log.e(TAG,e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");

            Log.i(TAG,"play version "+this.playVersion+" app version "+this.version);

            try {
                if( this.playVersion != null && versionCompare(this.playVersion, this.version) > 0){
                    newVersionPrompt(activity.getString(R.string.key_new_version),activity.getString(R.string.acceptar),false);
                }
            } catch (Exception e){

            }
        }

        /**
         * Compares two version strings.
         *
         * Use this instead of String.compareTo() for a non-lexicographical
         * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
         *
         * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
         *
         * @param str1 a string of ordinal numbers separated by decimal points.
         * @param str2 a string of ordinal numbers separated by decimal points.
         * @return The result is a negative integer if str1 is _numerically_ less than str2.
         *         The result is a positive integer if str1 is _numerically_ greater than str2.
         *         The result is zero if the strings are _numerically_ equal.
         */
        public int versionCompare(String str1, String str2) {
            String[] vals1 = str1.split("\\.");
            String[] vals2 = str2.split("\\.");
            int i = 0;
            // set index to first non-equal ordinal or length of shortest version string
            while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
                i++;
            }
            // compare first non-equal ordinal number
            if (i < vals1.length && i < vals2.length) {
                int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
                return Integer.signum(diff);
            }
            // the strings are equal or one string is a substring of the other
            // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
            return Integer.signum(vals1.length - vals2.length);
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }


        void newVersionPrompt(String message, String button, Boolean cancel){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            if(cancel){
                builder.setNegativeButton(
                        R.string.cancelar,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
            }

            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            final String appPackageName = UrgelletApplication.getInstance().getPackageName();
                            try {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    });

            AlertDialog alert = builder.create();
            alert.setCancelable(false);
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        }


    }
}
