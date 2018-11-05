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

package com.smartcitylink.urgellet.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.smartcitylink.urgellet.helpers.Utils;
import com.smartcitylink.urgellet.manager.DataManager;
import com.smartcitylink.urgellet.models.Registrar;
import com.smartcitylink.urgellet.models.RegistrarOffline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SendRegistroService extends Service {

    private static final String TAG = "SEND-Service";

    Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        try{
            registrarREgistro();
        }catch (Exception e){
            Log.d(TAG, e.toString());
        }



        return START_NOT_STICKY;
    }

    private void registrarREgistro() {
        Log.d("appu", "ENtra registro online");
        if (!Utils.isConnected(this.context)){
            return;
        }
        RegistrarOffline registro = DataManager.getInstance().getOneRegistroOffline();
        if (registro != null){
            StartForground();

                Registrar registrar = new Registrar();
                registrar.setIdActuacion(registro.getIdActuacion());
                registrar.setAccuracy(registro.getAccuracy());
                registrar.setLongitud(registro.getLongitud());
                registrar.setLatitud(registro.getLatitud());
                if (registro.getIdElemento() != 0){
                    registrar.getIdElemento().add(registro.getIdElemento());
                }
                registrar.setModo(registro.getModo());
                registrar.getNfc().add(registro.getNfc());
                registrar.setTimestamp(registro.getTimestamp());
                if (registro.getImage() == null){
                    Log.d("appu", "ENVIA sense FOTO");

                    //TODO REGISTRAR

                }
                else{
                    //TODO REGISTRAR
                }

                DataManager.getInstance().deleteRegistroOffline(registro);
                //registrarREgistro();

        }

    }

    private File save(byte[] bytes, File file) throws IOException {
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(bytes);
        } finally {
            if (null != output) {
                output.close();
            }
        }

        return file;
    }

    @Override
    public void onDestroy() {


        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void StartForground() {
        Log.d("StartForground", "StartForground");

      /*  Intent notificationIntent = new Intent(getApplicationContext(), CheckActivity.class);

        //**add this line**
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        //**edit this line to put requestID as requestCode**
        PendingIntent contentIntent = PendingIntent.getActivity(this, 101,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new NotificationCompat.Builder(this)
                .setOngoing(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setContentTitle(getResources().getString(R.string.enviar_registre))
                .setContentText(getResources().getString(R.string.comproba))
                .build();
        startForeground(101,  notification);*/

    }

}