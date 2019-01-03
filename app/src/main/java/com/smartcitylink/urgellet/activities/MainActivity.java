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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.UrgelletApplication;
import com.smartcitylink.urgellet.events.CheckSettingsEvent;
import com.smartcitylink.urgellet.events.LocationChangedEvent;
import com.smartcitylink.urgellet.events.LogoutEvent;
import com.smartcitylink.urgellet.events.NoConnectionEvent;
import com.smartcitylink.urgellet.events.PDFDataSuccessEvent;
import com.smartcitylink.urgellet.events.RegistrarSuccessEvent;
import com.smartcitylink.urgellet.events.StartQRCameraEvent;
import com.smartcitylink.urgellet.events.UserDataSuccessEvent;
import com.smartcitylink.urgellet.fragments.CameraFragment;
import com.smartcitylink.urgellet.fragments.DescomptesFragment;
import com.smartcitylink.urgellet.fragments.IncidenciesFragment;
import com.smartcitylink.urgellet.fragments.NFCFragment;
import com.smartcitylink.urgellet.fragments.NotificacionsFragment;
import com.smartcitylink.urgellet.fragments.PerfilFragment;
import com.smartcitylink.urgellet.fragments.QRCompatFragment;
import com.smartcitylink.urgellet.fragments.QRFragment;
import com.smartcitylink.urgellet.fragments.ReadedSuccessFragment;
import com.smartcitylink.urgellet.fragments.SuccessMessageFragment;
import com.smartcitylink.urgellet.helpers.AppVersionManage;
import com.smartcitylink.urgellet.helpers.BottomNavigationViewHelper;
import com.smartcitylink.urgellet.helpers.Constants;
import com.smartcitylink.urgellet.helpers.OnRegistrarFragmentInteractionListener;
import com.smartcitylink.urgellet.helpers.Utils;
import com.smartcitylink.urgellet.manager.DataManager;
import com.smartcitylink.urgellet.models.Elemento;
import com.smartcitylink.urgellet.models.Missatge;
import com.smartcitylink.urgellet.models.Registrar;
import com.smartcitylink.urgellet.models.Singleton;
import com.smartcitylink.urgellet.models.responses.RegistrarResponse;
import com.smartcitylink.urgellet.models.responses.RegistrarResponsePhoto;
import com.smartcitylink.urgellet.models.responses.RegistrarResponseQuiz;
import com.smartcitylink.urgellet.services.ConnectivityReceiver;
import com.smartcitylink.urgellet.services.TrackLocationService;
import com.smartcitylink.urgellet.widgets.PerfilItemListDialogFragment;
import com.smartcitylink.urgellet.widgets.UrgelletBottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.location.GpsStatus.GPS_EVENT_STARTED;
import static android.location.GpsStatus.GPS_EVENT_STOPPED;

public class MainActivity extends BaseActivity implements OnRegistrarFragmentInteractionListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "MainActivity";

    private AppVersionManage mAppVersionManage = null;

    public int REGISTRAR_TIPUS = Constants.REGISTRAR_TIPUS_QR;
    private Location currentLocation;
    private int idActuacion = -1;
    private String REGISTRO_TYPE = Constants.REG_TYPE_DEIXALLA;
    private Boolean hasDoneNfc = false;

    private NfcAdapter mNfcAdapter;

    private String lastNFC = "";
    private long lastNFCTime;

    private boolean hasNFC = false;
    private boolean isRequesting = false;
    private long time;

    Activity activity;

    @BindView(R.id.bottom_navigation)
    UrgelletBottomNavigationView bottomNavigationView;
    @BindView(R.id.activity_main)
    View parentLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private TrackLocationService locationService;
    private boolean mBounded;
    private boolean successMessageFragmentVisible;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAppVersionManage = new AppVersionManage(this);

        initRequestGeo();
        activity = this;

        initDefaultReader();

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView, this);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        tabClick(item.getItemId());
                        item.setChecked(true);
                        return false;
                    }
                });

        if (!hasDoneNfc) {
            initFirstFragment();
            hasDoneNfc = true;
        }

        Utils.startConnectionService(this);

        DataManager.fillMissatges(this);

    }



    /**
     * Inicialitzem la manera de llegir
     * codis preferida de l'usuari
     */
    private void initDefaultReader() {

        if (Utils.hasNFC(this)) {
            hasNFC = true;
            initNfc();
            REGISTRAR_TIPUS = checkDefaultReader();
        } else {
            hasNFC = false;
            toggleDefaultReaderVisibility(false);
            REGISTRAR_TIPUS = Constants.REGISTRAR_TIPUS_QR;
        }

        if (REGISTRAR_TIPUS == -1) {
            if (hasNFC) {
                REGISTRAR_TIPUS = Constants.REGISTRAR_TIPUS_NFC;
            } else {
                REGISTRAR_TIPUS = Constants.REGISTRAR_TIPUS_QR;
            }
        }

        DataManager.getInstance().setDefaultReaderType(REGISTRAR_TIPUS);

        setupDefaultMenu();
    }

    private int checkDefaultReader() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());
        return sharedPref.getInt(Constants.SHARED_TYPE, -1);
    }


    /**
     * Canviem l'estat del botó
     * i la manera de llegir els codis
     * @param v
     */
    public void toggleDefaultReader(View v) {
        initRequestGeo();
        int oldTipus = REGISTRAR_TIPUS;

        if (REGISTRAR_TIPUS == Constants.REGISTRAR_TIPUS_NFC) {

            REGISTRAR_TIPUS = Constants.REGISTRAR_TIPUS_QR;
        } else {
            fab.setImageResource(R.drawable.qr_icon);
            REGISTRAR_TIPUS = Constants.REGISTRAR_TIPUS_NFC;
        }

        if (REGISTRO_TYPE.equals(Constants.REG_TYPE_DEIXALLA)) {
            tabClick(R.id.action_inicio);
        } else {
            tabClick(R.id.action_incidencies);
        }
        int newTipus = REGISTRAR_TIPUS;

        DataManager.getInstance().setDefaultReaderType(REGISTRAR_TIPUS);
        setupDefaultMenu();
    }

    /**
     * Intentem agafar la posicio del device
     */
    private boolean initRequestGeo() {

        if (locationService != null){
            Log.d("TrackLocationService", "locationService --> NOT NULL");
            locationService.settingsrequest();
        }

        if (geoPermissionsGranted(Constants.PERMISSIONS_FROM_INIT_GEO)) {
           // startService(new Intent(this, TrackLocationService.class));
            bindService(new Intent(this, TrackLocationService.class), mConnection, BIND_AUTO_CREATE);
            return true;
        }
        return false;
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            locationService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            TrackLocationService.LocalBinder mLocalBinder = (TrackLocationService.LocalBinder)service;
            locationService = mLocalBinder.getServerInstance();
        }
    };

    /**
     * Inicialitzem NFC
     */
    private void initNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            Log.d(TAG, "This device doesn't support NFC.");
        }

        if (!mNfcAdapter.isEnabled()) {
            Log.d(TAG, "NFC is disabled.");

        } else {
            Log.d(TAG, "NFC is enabled.");
        }
        try {
            handleIntent(getIntent());
        } catch (Exception e) {
            Log.e(TAG, "Nfc Error");
        }

    }

    /**
     * Listener click de la navegació
     * @param item int
     */
    private void tabClick(int item) {
        Class cl = null;
        Bundle data = null;
        hasDoneNfc = false;
        initRequestGeo();
        if (item == R.id.action_inicio || item == R.id.action_incidencies) {
            toggleDefaultReaderVisibility(true);
        } else {
            toggleDefaultReaderVisibility(false);
        }

        switch (item) {
            case R.id.action_inicio:

                //Tipus de Registre
                REGISTRO_TYPE = Constants.REG_TYPE_DEIXALLA;

                //Extres per al Fragment
                data = new Bundle();
                data.putString(Constants.REG_TYPE, Constants.REG_TYPE_DEIXALLA);
                setIdActuacion(DataManager.getInstance().getIdActuacion());

                //Inicialitzem Fragment
                if (REGISTRAR_TIPUS == Constants.REGISTRAR_TIPUS_NFC) {
                    lastNFC = "";
                    cl = NFCFragment.class;
                } else if (REGISTRAR_TIPUS == Constants.REGISTRAR_TIPUS_QR) {
                    if (checkPermissionsCamera(Constants.PERMISSIONS_FROM_TAB_CLICK_INICIO)) {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                            cl = QRCompatFragment.class;
                        } else {

                           cl = QRFragment.class;
                        }

                    }
                }
                break;
            case R.id.action_notifications:
                if (checkPermissionsStorage(Constants.PERMISSIONS_STORAGE)) {
                    cl = NotificacionsFragment.class;
                    bottomNavigationView.deleteBadge();
                }
                break;
            case R.id.action_salari:
                if (checkPermissionsStorage(Constants.PERMISSIONS_STORAGE)) {
                    cl = DescomptesFragment.class;
                }
                break;
            case R.id.action_incidencies:
                //Tipus de Registre
                REGISTRO_TYPE = Constants.REG_TYPE_INCIDENCIES;
                //Extres per al Fragment
                data = new Bundle();
                data.putString(Constants.REG_TYPE, Constants.REG_TYPE_INCIDENCIES);
                //Inicialitzem Fragment
                if (REGISTRAR_TIPUS == Constants.REGISTRAR_TIPUS_NFC) {
                    lastNFC = "";
                    cl = NFCFragment.class;
                } else if (REGISTRAR_TIPUS == Constants.REGISTRAR_TIPUS_QR) {
                    if (checkPermissionsCamera(Constants.PERMISSIONS_FROM_TAB_CLICK_INCIDENCIA)) {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                           cl = QRCompatFragment.class;
                        } else {
                            cl = QRFragment.class;
                        }
                    }
                }

                break;
            case R.id.action_menu:
                    cl = PerfilFragment.class;
            break;
        }

        if (cl != null) {
            goFragment(cl, data);

        }
    }


    /**
     * Shows and hide Fab Button
     * @param visible boolean
     */
    private void toggleDefaultReaderVisibility(boolean visible) {

        if (visible) {
            if (!hasNFC) {
                return;
            }
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

    }

    /**
     * Encenem pantalla incial
     */
    private void initFirstFragment() {
        tabClick(R.id.action_inicio);

    }


    /**
     * Configurem l'icono inicial segons el
     * tipus de dispositiu
     */
    private void setupDefaultMenu() {
        //Canvia icono inicial segons TIPUS_REGISTRE
        MenuItem itemRegistre = bottomNavigationView.getMenu().findItem(R.id.action_inicio);

        switch (REGISTRAR_TIPUS) {
            case Constants.REGISTRAR_TIPUS_NFC:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemRegistre.setIcon(getResources().getDrawable(R.drawable.nfc_icon, null));
                } else {
                    itemRegistre.setIcon(getResources().getDrawable(R.drawable.nfc_icon));
                }
                fab.setImageResource(R.drawable.qr_icon);
                break;
            case Constants.REGISTRAR_TIPUS_QR:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    itemRegistre.setIcon(getResources().getDrawable(R.drawable.qr_icon, null));
                } else {
                    itemRegistre.setIcon(getResources().getDrawable(R.drawable.qr_icon));
                }
                fab.setImageResource(R.drawable.nfc_icon);
                break;
        }

    }

    /**
     * Mètode per canviar de Fragment
     * @param fragmentClass Fragment de destí
     * @param data Informació extra
     */
    public void goFragment(Class fragmentClass, Bundle data) {
        Fragment fragment = null;

        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                if (data != null) {
                    fragment.setArguments(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContent, fragment);
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    /**
     * Comprobem permisos de GRO
     * @param permissions_type int
     * @return boolean
     */
    private boolean geoPermissionsGranted(int permissions_type) {

        boolean permsGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            permsGranted = true;
        }

        if (!permsGranted) {
            if (!isRequesting){
                isRequesting = true;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        permissions_type);
            }

            return false;
        }

        return true;
    }

    /**
     * Comprobem permissos Camera
     * @param permissions int
     * @return boolean
     */
    private boolean checkPermissionsCamera(int permissions) {

        boolean permsGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            permsGranted = true;
        }

        if (!permsGranted) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, permissions);
            return false;
        } else {
            return true;
        }

    }

    /**
     * Comprobem permissos Storage
     * @param permissions int
     * @return boolean
     */
    private boolean checkPermissionsStorage(int permissions) {

        boolean permsGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            permsGranted = true;
        }

        if (!permsGranted) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, permissions);
            return false;
        } else {
            return true;
        }

    }

    /**
     * Resultat Permisos demanats
     * @param requestCode int
     * @param permissions String[]
     * @param grantResults int[]
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isRequesting = false;
        switch (requestCode) {
            case Constants.PERMISSIONS_FROM_TAB_CLICK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //tabClick(R.id.action_registrar);
                } else {
                    // showNeutralAlert(getResources().getString(R.string.no_permissions_granted));
                }
                break;
            case Constants.PERMISSIONS_FROM_TAB_CLICK_INCIDENCIA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tabClick(R.id.action_incidencies);
                } else {
                    showNeutralAlert(getResources().getString(R.string.no_permissions_granted));
                }
                break;
            case Constants.PERMISSIONS_FROM_TAB_CLICK_INICIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateNotificationsBadge();
                    tabClick(R.id.action_inicio);
                } else {
                    showNeutralAlert(getResources().getString(R.string.no_permissions_granted));
                }
                break;

            case Constants.PERMISSIONS_GEO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //   tabClick(R.id.action_registrar);
                } else {
                    showNeutralAlert(getResources().getString(R.string.no_permissions_granted));
                }
            case Constants.PERMISSIONS_FROM_SINGLE_LOCATION:

                break;

            case Constants.PERMISSIONS_FROM_INIT_GEO:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            initRequestGeo();
                        }
                    }

                } else {
                    showNeutralAlert(getResources().getString(R.string.no_permissions_granted));
                }
                break;
            default:
                break;
        }
    }

    /**
     * Update Notifications Badge
     */
    private void updateNotificationsBadge() {
        if (bottomNavigationView != null) {
            bottomNavigationView.updateBadge();
        }
    }

    /**
     * Mostra alerta botó neutral
     * @param message String
     */
    public void showNeutralAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.acceptar),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Mostrem Fragment de Success
     * @param nfc String
     * @param modo int
     */
    @Override
    public void showSuccessReadedDialog(String nfc, int modo) {

        showDialogFragmentSuccessWithMessage(nfc, true, modo, -1);
        crearRegistre(Integer.parseInt(nfc), modo);



    }

    /**
     * Mostrem Fragment de Success amb missatge
     * @param message String
     * @param placeholder boolean
     * @param modo
     */
    private void showDialogFragmentSuccessWithMessage(String message, boolean placeholder, int modo, int fraccio) {

        Missatge miss;

        try{
            miss = DataManager.getInstance().getSuccessMissatgeToShow(Integer.parseInt(message));
        }catch(NumberFormatException ex){ // handle your exception
            miss = null;
        }

        //Ordindari Success Screen
        Class cl = ReadedSuccessFragment.class;

        //Message Success Screen
        if (miss != null){
            cl = SuccessMessageFragment.class;
            successMessageFragmentVisible = true;
        }
        else{
            successMessageFragmentVisible = false;

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = null;
        try {
            fragment = (Fragment) cl.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Bundle data = new Bundle();

        Elemento elem;

        try{
            elem = DataManager.getInstance().getElementoByNFC(Integer.parseInt(message));
        }catch(NumberFormatException ex){ // handle your exception
            elem = null;
        }

        int fr = -1;
        if(elem != null){
            fr = elem.getFraccion();
        }
        Log.d("incidencFR", "FR " + String.valueOf(fr));
        Log.d("incidencFR", "fraccio " + String.valueOf(fraccio));

        //Message Success Screen
        if (miss != null) {
            data.putString("message", miss.getMissatge());
            data.putString("fraccioText", DataManager.getInstance().getElementoFraccionShortByNFC(Integer.parseInt(message)));
            data.putString("link", miss.getLink());
            data.putString("image", miss.getImatge());
            data.putInt("icon", Utils.getFraccionIcon(this,fr));
            if (fr == -1){
                data.putInt("icon", Utils.getFraccionIcon(this,fraccio));
            }
        }
        else{
            //Ordinari Success Screen
            String theMessage = message;
            if (placeholder) {
                theMessage = DataManager.getInstance().getElementoFraccionByNFC(Integer.parseInt(message));
            }
            data.putString("message", theMessage);
            data.putBoolean("placeholder", placeholder);
            data.putInt("image", Utils.getFraccionIconSimpleSuccess(fr));
            if (fr == -1){
                data.putInt("image", Utils.getFraccionIcon(this,fraccio));
            }

        }

        if (fragment != null) {
            fragment.setArguments(data);
            fragmentTransaction.add(R.id.flContent, fragment);
            fragmentTransaction.addToBackStack("ReaderSuccess");
            fragmentTransaction.commitAllowingStateLoss();
        }

        if (modo == Constants.REGISTRAR_TIPUS_QR) {
            startCountDownForBack();
            Log.d(TAG, "TIPUS QR");
        } else {
            Log.d(TAG, "TIPUS NFC");
        }
    }

    /**
     * Sets idActuacion for the Register Action
     * @param idAct int
     */
    @Override
    public void setIdActuacion(int idAct) {
        if (idAct != DataManager.getInstance().getIdActuacion()) {
            REGISTRO_TYPE = Constants.REG_TYPE_INCIDENCIES;
        } else {
            REGISTRO_TYPE = Constants.REG_TYPE_DEIXALLA;
        }
        idActuacion = idAct;

        Log.d(TAG, String.valueOf(idActuacion));
    }

    /**
     * Goes to CameraOverlay for Incidencies with image
     * @param nfc String
     * @param modo int
     */
    @Override
    public void crearRegistreAmbFoto(String nfc, int modo) {
        Bundle data = new Bundle();
        data.putString("nfc", nfc);
        data.putInt("modo", modo);
        goFragment(CameraFragment.class, data);
        fab.setVisibility(View.GONE);
    }

    /**
     * Sends Register with image to API or backgroundService
     * @param nfc String
     * @param modo int
     * @param file File
     */
    @Override
    public void enviarRegistreAmbFoto(String nfc, int modo, File file) {

        Registrar registrar = createRegistrarToSend(Integer.parseInt(nfc), modo);

        if (checkConnection(this)) {
            //TODO send to server Registrar Object and File
        } else {
            DataManager.getInstance().saveRegistroOffline(registrar, file);
            Utils.startBackgroundService(this);
        }
    }

    /**
     * Creates Registrar Object to send
     * @param nfc int
     * @param modo int
     * @return Registrar
     */
    private Registrar createRegistrarToSend(int nfc, int modo) {
        Registrar registrar = new Registrar();
        registrar.getNfc().add(nfc);
        Elemento elemento = DataManager.getInstance().getElementoByNFC(nfc);
        if (elemento != null) {
            registrar.getIdElemento().add(elemento.getId());
        }
        registrar.setModo(modo);
        int timestamp = (int) (System.currentTimeMillis() / 1000L);
        registrar.setTimestamp(timestamp);
        if (currentLocation != null) {
            registrar.setAccuracy(currentLocation.getAccuracy());
            registrar.setLatitud(currentLocation.getLatitude());
            registrar.setLongitud(currentLocation.getLongitude());
        }

        registrar.setIdActuacion(idActuacion);

        return registrar;
    }

    /**
     * Goes to Incidencies Fragment
     * @param nfc String
     * @param modo int
     */
    @Override
    public void goIncidenciesFragment(String nfc, int modo) {
        Bundle bundle = new Bundle();
        bundle.putString("nfc", nfc);
        bundle.putInt("modo", Constants.REGISTRAR_TIPUS_NFC);
        goFragment(IncidenciesFragment.class, bundle);
    }

    /**
     * Creates a Registrar Object
     * Sends it to the API or BackgroundService
     * @param nfc String
     * @param modo int
     */
    @Override
    public void crearRegistre(int nfc, int modo) {
        Registrar registrar = createRegistrarToSend(nfc, modo);

        if (checkConnection(this)) {
            //TODO send to server Registrar Object
        } else {
            DataManager.getInstance().saveRegistroOffline(registrar, null);
            Utils.startBackgroundService(this);
        }

    }

    /**
     * Goes back to start page of section
     * @param item int
     */
    @Override
    public void tornaInici(int item) {
        tabClick(item);

        try {
            Menu menuNav = bottomNavigationView.getMenu();
            MenuItem logoutItem = menuNav.findItem(item);
            logoutItem.setChecked(true);
        } catch (Exception e) {
            Log.e(TAG, "Item Check Error a Torna Inici");
        }

    }


    /**
     * Checks connection status
     * Show SnackBar is it's not connected
     * @return boolean
     */
    private boolean checkConnection(Context context) {
        boolean isConnected = Utils.isConnected(context);
        showSnackConnection(isConnected);
        return isConnected;
    }


    /**
     * CountDown before GoBack
     */
    private void startCountDownForBack() {
           time = 2000;

        if(successMessageFragmentVisible){
            time = 8000;
        }

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    new CountDownTimer(time, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            try {
                                onBackPressed();
                            } catch (Exception e) {
                                EventBus.getDefault().post(new StartQRCameraEvent(true));
                            }

                        }
                    }.start();
                }
            });


    }

    /**
     * Goes back to first Tab
     * @param v View
     */
    public void tornaInici(View v) {
        tabClick(R.id.action_inicio);
    }

    protected void onResume() {

        super.onResume();

        if (mAppVersionManage.isMyVersionOld()) {
            mAppVersionManage.updateLinkVersionDialog();
        }

        Utils.startBackgroundService(this);

        UrgelletApplication.getInstance().setConnectivityListener(this);
        if (mNfcAdapter != null) {
            setupForegroundDispatch(this, mNfcAdapter);
        }

        if (locationService != null){
            Log.d("ONRESUME", "NOT NULL");
            locationService.settingsrequest();
        }

        locationEnabledListener();

    }

    private void locationEnabledListener() {
        Log.d("PS_EVENT", "locationEnabledListener");
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("PS_EVENT", "NO PERMS");

            return;
        }
        lm.addGpsStatusListener(new android.location.GpsStatus.Listener() {
            public void onGpsStatusChanged(int event) {
                switch (event) {
                    case GPS_EVENT_STARTED:
                        Log.d("PS_EVENT", "GPS_EVENT_STARTED");
                        break;
                    case GPS_EVENT_STOPPED:
                        Log.d("PS_EVENT", "GPS_EVENT_STOPPED");
                        initRequestGeo();
                        break;
                }
            }
        });
    }

    /**
     * Shows a Toast with a message
     * @param string string
     */
    private void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "MAIN ACT onPause");

        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        if (mNfcAdapter != null) {
            stopForegroundDispatch(this, mNfcAdapter);
        }
        //EventBus.getDefault().unregister(this);
        super.onPause();

    }

    /**
     * Handles NFC new Intent
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "NEW INTENT");
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (locationService != null){
            locationService.settingsrequest();
        }

        String action = intent.getAction();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) || (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) || (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))) {
            String url = intent.getDataString();
            if (url == null){
                return;
            }
            if (url.isEmpty()){
                return;
            }
            String[] separated = url.split("/");

            if (separated.length < 2){
                return;
            }
            initRequestGeo();
               try{
                    Singleton.getInstance().setNfc(separated[separated.length - 1]);
                    Log.d(TAG, "SET MODO NFC");
                    String nfc = separated[separated.length - 1];

                    if (nfc.equals(lastNFC) && Math.abs(lastNFCTime-System.currentTimeMillis()) < Constants.NFC_TIMER_MILLISECONDS ){
                        Log.e(TAG, "NFC already registered less than " + String.valueOf(Constants.NFC_TIMER_MILLISECONDS) + " milliseconds ago");
                        return;
                    }
                    lastNFC = nfc;
                    lastNFCTime = System.currentTimeMillis();
                    if (REGISTRO_TYPE.equals(Constants.REG_TYPE_INCIDENCIES)) {
                        goIncidenciesFragment(nfc, Constants.REGISTRAR_TIPUS_NFC);

                    } else {
                        hasDoneNfc = true;
                        if (idActuacion == -1){
                            idActuacion = DataManager.getInstance().getIdActuacion();
                        }
                        showSuccessReadedDialog(nfc, Constants.REGISTRAR_TIPUS_NFC);
                    }
               }
               catch (Exception e){
                  Log.e(TAG, "NFC url Reader Error");
               }

        }
    }


    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filters[0].addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);


        adapter.enableForegroundDispatch(activity, pendingIntent, null, null);
    }

    /**
     * @param activity The corresponding {@link MainActivity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    /**
     * Handles Registrar Quiz Response
     * @param response RegistrarResponseQuiz
     */
    @Override
    public void registeredSuccessQuiz(RegistrarResponseQuiz response) {
        Log.d(TAG, "Go Quiz");
    }

    /**
     * Handles Registrar Photo Response
     * @param response RegistrarResponsePhoto
     */
    @Override
    public void registeredSuccessPhoto(RegistrarResponsePhoto response) {
        Log.d(TAG, "Go Photo");

    }

    /**
     * Handles Registrar Message Response
     * @param message String
     */
    @Override
    public void registeredSuccessMessage(String message, int fraccio) {
        showDialogFragmentSuccessWithMessage(message, false, -1, fraccio);

        Log.d(TAG, "Show message");
    }

    /**
     * Handles Registrar Success Response
     * @param response RegistrarResponse
     */
    @Override
    public void registeredSuccess(RegistrarResponse response) {
        Log.d(TAG, "Success");
    }



    /**
     * Listens to RegistrarSuccessEvent EVent
     * @param event RegistrarSuccessEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void oRegistrarEventEvent(RegistrarSuccessEvent event) {
        if (!event.success) {
            initFirstFragment();
            showNeutralAlert(getResources().getString(R.string.error_retry));
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
     * Listens to CheckSettingsEvent
     * @param event CheckSettingsEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckSettingsEventt(CheckSettingsEvent event) {
        if (event.status != null) {
            try {
                event.status.startResolutionForResult(MainActivity.this, Constants.REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Listens on UserData saved Event
     * @param event UserDataSuccessEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void oUserDataSuccessEvent(UserDataSuccessEvent event) {
        if (event.success) {
            updateNotificationsBadge();
        }
    }


    /**
     * Listens on Location Changed Event
     * @param event LocationChangedEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void oLocationChangedEvent(LocationChangedEvent event) {
        if (event.location != null){
            currentLocation = event.location;
            Singleton.getInstance().setLocation(currentLocation);
            // showToast(currentLocation.toString());
            Log.d("TrackLocationService", "MAIN ACTIVITY location Changed");
        }


    }

    /**
     * Listens on Location Changed Event
     * @param event LocationChangedEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void oLocationChangedEvent(NoConnectionEvent event) {
        showSnackConnection(false);
    }


    /**
     * Listens to CheckSettingsEvent
     * @param event PDFDataSuccessEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPDFDataSuccessEvent(PDFDataSuccessEvent event) {
        if (event.success) {

            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+ File.separator+Constants.RECIBO_NAME;

            Uri uri = null;
            Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
            pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(getApplicationContext(), "com.smartcitylink.urgellet.provider",new File(path) );
                pdfOpenintent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }else{
                uri = Uri.fromFile(new File(path));
            }

            pdfOpenintent.setDataAndType(uri, "application/pdf");

            try {
                startActivity(pdfOpenintent);
            }
            catch (ActivityNotFoundException e) {

            }

        }
    }



    /**
     * Listens on Network connection changes
     * @param isConnected boolean
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnackConnection(isConnected);
    }

    /**
     * Shows SnackBar if there's no connection
     * @param isConnected
     */
    private void showSnackConnection(boolean isConnected) {
        if (!isConnected) {
            String message;
            int color;
            message = getResources().getString(R.string.no_internet);
            color = Color.RED;

            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.flContent), message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case Constants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        locationEnabledListener();
                        initRequestGeo();
                        break;
                    case Activity.RESULT_CANCELED:
                       initRequestGeo();
                        break;
                }
                break;
        }
    }


    public void showContractModal() {
        PerfilItemListDialogFragment addPhotoBottomDialogFragment =
                PerfilItemListDialogFragment.newInstance(11);
        addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                "perfil_contract_modal");
    }
}
