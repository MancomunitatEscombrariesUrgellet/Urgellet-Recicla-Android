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
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.UrgelletApplication;
import com.smartcitylink.urgellet.services.ConnectionStateMonitor;
import com.smartcitylink.urgellet.services.SendRegistroService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {


    public static int getScreenWidth(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        return size.x;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static boolean hasNFC(Context context) {

        //Comprobem que en tingui
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        return adapter != null;
    }

    public static boolean isNFCEnabled(Context context) {
        //Comprobem que estigui activat
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        return adapter != null && adapter.isEnabled();
    }

    public static String getStringElementFromUrl(String text) {
        String[] url = text.split("/");
        return url[url.length-1];
    }

    public static String getFraccionName(Context context, int fraccion) {
        String fracc = "";
        switch (fraccion){
            case Constants.FRACCIO_ENVASOS:
                fracc = UrgelletApplication.getInstance().getApplicationContext().getResources().getString(R.string.gracies_envasos);
                break;
            case Constants.FRACCIO_ORGANICA:
                fracc = UrgelletApplication.getInstance().getApplicationContext().getResources().getString(R.string.gracies_organica);
                break;
            case Constants.FRACCIO_PAPER:
                fracc = UrgelletApplication.getInstance().getApplicationContext().getResources().getString(R.string.gracies_paper);
                break;
            case Constants.FRACCIO_RESTA:
                fracc = UrgelletApplication.getInstance().getApplicationContext().getResources().getString(R.string.gracies_resta);
                break;
            case Constants.FRACCIO_VIDRE:
                fracc = UrgelletApplication.getInstance().getApplicationContext().getResources().getString(R.string.gracies_vidre);
                break;
            default:
                fracc = UrgelletApplication.getInstance().getApplicationContext().getResources().getString(R.string.gracies_generic);
                break;
        }

        return fracc;
    }

    public static String getFraccionShortName(int fraccion) {
        String fracc = UrgelletApplication.getInstance().getApplicationContext().getResources().getString(R.string.deixalla);
        switch (fraccion){
            case Constants.FRACCIO_ENVASOS:
                fracc = UrgelletApplication.getInstance().getApplicationContext().getResources().getString(R.string.envasos);
                break;
            case Constants.FRACCIO_ORGANICA:
                fracc = UrgelletApplication.getInstance().getApplicationContext().getResources().getString(R.string.organica);
                break;
            case Constants.FRACCIO_PAPER:
                fracc = UrgelletApplication.getInstance().getApplicationContext().getResources().getString(R.string.paper);
                break;
            case Constants.FRACCIO_RESTA:
                fracc = UrgelletApplication.getInstance().getApplicationContext().getResources().getString(R.string.resta);
                break;
            case Constants.FRACCIO_VIDRE:
                fracc = UrgelletApplication.getInstance().getApplicationContext().getResources().getString(R.string.vidre);
                break;
        }

        return fracc;
    }


    public static int getFraccionIcon(Context context, int fraccion) {
        int fraccDrawable = R.drawable.resta;
        switch (fraccion){
            case Constants.FRACCIO_ENVASOS:
                fraccDrawable = R.drawable.envasos;
                break;
            case Constants.FRACCIO_ORGANICA:
                fraccDrawable = R.drawable.organica;
                break;
            case Constants.FRACCIO_PAPER:
                fraccDrawable = R.drawable.paper;
                break;
            case Constants.FRACCIO_RESTA:
                fraccDrawable = R.drawable.resta;
                break;
            case Constants.FRACCIO_VIDRE:
                fraccDrawable = R.drawable.vidre;
                break;
            default:
                //fraccDrawable = R.drawable.resta;
                fraccDrawable = R.drawable.success_icon;
                break;
        }

        return fraccDrawable;
    }


    public static Date getDateFromString(String dateString){
        SimpleDateFormat dateformat = new SimpleDateFormat("d/m/yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = dateformat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return date;
    }

    public static void vibrate(Context context) {

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(300);
    }

    public static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }


    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected() || netInfo != null && netInfo.isConnected() && !checkConnectionVPN(ctx);
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) UrgelletApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting()  && !checkConnectionVPN(context);
    }

    public static boolean checkConnectionVPN(Context contex) {
        ConnectivityManager cm = (ConnectivityManager) contex.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks;
        boolean isVPNActive = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            networks = cm.getAllNetworks();
            Log.d("VPNNNN", "Network count: " + networks.length);
            for(int i = 0; i < networks.length; i++) {
                NetworkCapabilities caps = cm.getNetworkCapabilities(networks[i]);
                if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)){
                    isVPNActive = true;
                    break;
                }

                Log.d("VPNNNN", "Network " + i + ": " + networks[i].toString());
                Log.d("VPNNNN", "VPN transport is: " + caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
                Log.d("VPNNNN", "NOT_VPN capability is: " + caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN));
            }
        }

        return isVPNActive;
    }


    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Starts connectionService
     */
    public static void startConnectionService(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ConnectionStateMonitor conn = new ConnectionStateMonitor(context);
            conn.enable(context);
        }

    }

    /**
     * Starts backgroundService
     */
    public static void startBackgroundService(Context context) {
        Intent i = new Intent(context, SendRegistroService.class);
        context.startService(i);
    }

    public static String getModoString(int modo) {
        String modoString = "";
       switch (modo){
           case Constants.REGISTRAR_TIPUS_QR:
               modoString = "QR";
               break;
           case Constants.REGISTRAR_TIPUS_NFC:
               modoString = "NFC";
               break;
       }

        return modoString;
    }

    public static int getFraccionColor(int fraccion) {
        int color = R.color.grayMedium;
        switch (fraccion){
            case Constants.FRACCIO_ENVASOS:
                color = R.color.envasos;
                break;
            case Constants.FRACCIO_VIDRE:
                color = R.color.vidre;
                break;
            case Constants.FRACCIO_PAPER:
                color = R.color.paper;
                break;
            case Constants.FRACCIO_ORGANICA:
                color = R.color.organica;
                break;
            case Constants.FRACCIO_RESTA:
                color = R.color.resta;
                break;

        }
        return color;
    }

    public static int getFraccionName(int fraccion) {
        int name = R.color.grayMedium;
        switch (fraccion){
            case Constants.FRACCIO_ENVASOS:
                name = R.string.envasos;
                break;
            case Constants.FRACCIO_VIDRE:
                name = R.string.vidre;
                break;
            case Constants.FRACCIO_PAPER:
                name = R.string.paper;
                break;
            case Constants.FRACCIO_ORGANICA:
                name = R.string.organica;
                break;
            case Constants.FRACCIO_RESTA:
                name = R.string.resta;
                break;

        }
        return name;
    }

    public static int getFraccionIconSimpleSuccess( int fraccion) {

        int fraccDrawable = R.drawable.resta;
        switch (fraccion){
            case Constants.FRACCIO_ENVASOS:
                fraccDrawable = R.drawable.envasos_success_simple;
                break;
            case Constants.FRACCIO_ORGANICA:
                fraccDrawable = R.drawable.organica_success_simple;
                break;
            case Constants.FRACCIO_PAPER:
                fraccDrawable = R.drawable.paper_success_simple;
                break;
            case Constants.FRACCIO_RESTA:
                fraccDrawable = R.drawable.resta_success_simple;
                break;
            case Constants.FRACCIO_VIDRE:
                fraccDrawable = R.drawable.vidre_success_simple;
                break;
            default:
                //fraccDrawable = R.drawable.resta_success_simple;
                fraccDrawable = R.drawable.success_icon;
                break;
        }

        return fraccDrawable;
    }
}
