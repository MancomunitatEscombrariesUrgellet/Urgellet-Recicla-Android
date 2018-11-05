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
package com.smartcitylink.urgellet.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.UrgelletApplication;
import com.smartcitylink.urgellet.events.ChangedContratoEvent;
import com.smartcitylink.urgellet.events.CheckCodiSuccessEvent;
import com.smartcitylink.urgellet.events.IncidenciesSuccessEvent;
import com.smartcitylink.urgellet.events.LoginSuccessEvent;
import com.smartcitylink.urgellet.events.LogoutEvent;
import com.smartcitylink.urgellet.events.NotificationsSuccessEvent;
import com.smartcitylink.urgellet.events.UserDataSuccessEvent;
import com.smartcitylink.urgellet.helpers.Constants;
import com.smartcitylink.urgellet.helpers.MissatgesSeeder;
import com.smartcitylink.urgellet.helpers.Utils;
import com.smartcitylink.urgellet.models.Actuacio;
import com.smartcitylink.urgellet.models.Contrato;
import com.smartcitylink.urgellet.models.Descuento;
import com.smartcitylink.urgellet.models.Elemento;
import com.smartcitylink.urgellet.models.Missatge;
import com.smartcitylink.urgellet.models.Notificacio;
import com.smartcitylink.urgellet.models.Registrar;
import com.smartcitylink.urgellet.models.RegistrarOffline;
import com.smartcitylink.urgellet.models.Salari;
import com.smartcitylink.urgellet.models.SalariDeixalleria;
import com.smartcitylink.urgellet.models.Singleton;
import com.smartcitylink.urgellet.models.User;
import com.smartcitylink.urgellet.models.responses.ActuacionesResponse;
import com.smartcitylink.urgellet.models.responses.ElementoResponse;
import com.smartcitylink.urgellet.models.responses.LoginResponse;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class DataManager {
    private static final String TAG = "DATA-MANAGER";
    private static  DataManager instance;
    private Context context;
    private static Realm realm;


    private DataManager() {
        this.context = UrgelletApplication.getInstance().getApplicationContext();

        realm = Realm.getDefaultInstance();

    }

    /**
     * Initialize DataManager Instance
     * @return multiple
     */
    public static synchronized DataManager getInstance() {
        if(instance == null){
            instance = new DataManager();
        }

        realm = Realm.getDefaultInstance();

        return instance;
    }


    /**
     * Delete All Database Entries
     */
    private void cleanDatabase() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
        EventBus.getDefault().post(new LogoutEvent(true));

    }


    /**
     * Save Login Info
     * @param body LoginResponse
     */
    public void saveLoginInfo(LoginResponse body) {

       // saveOnPrivate(body);

        String authToken = "Bearer " +body.getToken();

        Log.d("bearTT", authToken);

        Singleton.getInstance().setToken(authToken);
        //1. Save Login Info
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Constants.SHARED_LOGGED, true);
        editor.putString(Constants.SHARED_TOKEN, authToken);

       editor.putString(Constants.SHARED_REFRESH_TOKEN, body.getRefresh_token());
        boolean editorSuccess  = editor.commit();

//        ApiManager.getInstance(context).getUserInfo();

        EventBus.getDefault().post(new LoginSuccessEvent("", true));
        EventBus.getDefault().post(new CheckCodiSuccessEvent(true));

    }

    private void saveOnPrivate(LoginResponse body) {
        String authToken = "Bearer " +body.getToken();

        Singleton.getInstance().setToken(authToken);
        //1. Save Login Info
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Constants.SHARED_LOGGED, true);
        editor.putString(Constants.SHARED_TOKEN, authToken);
        // editor.putString(Constants.SHARED_REFRESH_TOKEN, body.getRefreshToken());
        boolean editorSuccess  = editor.commit();

        //GET INFO

        EventBus.getDefault().post(new LoginSuccessEvent("", true));
    }

    /**
     * Check if User is logged in
     * @return boolean
     */
    public boolean checkUserIsLoggedIn() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());

        Boolean isLoggedIn = sharedPref.getBoolean(Constants.SHARED_LOGGED, false);
        String authToken = sharedPref.getString(Constants.SHARED_TOKEN, null);

        return !(!isLoggedIn || authToken == null);

    }

    /**
     * Check user Default Reader
     * @return boolean
     */
    public int checkDefaultReader() {
        if (context == null){
            context = UrgelletApplication.getInstance().getApplicationContext();
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());
        return sharedPref.getInt(Constants.SHARED_TYPE, -1);
    }

    /**
     * Save Default Reader
     * @param readerType int
     */
    public void setDefaultReaderType(int readerType) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.SHARED_TYPE, readerType);
        editor.commit();

    }


    /**
     * Get User Auth Token
     * @return UserAuthToken
     */
    public String getUserAuth() {

        String authToken = Singleton.getInstance().getToken();

        if (authToken == null){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());
            authToken = sharedPref.getString(Constants.SHARED_TOKEN, null);
            Singleton.getInstance().setToken(authToken);
        }

        return authToken;
    }

    /**
     * Get User Auth Token
     * @return UserAuthToken
     */
    public String getUserAuthRefresh() {

       SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());
       return sharedPref.getString(Constants.SHARED_REFRESH_TOKEN, null);
    }

    public String getAuthTokenHeader() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());
        String authToken = sharedPref.getString(Constants.SHARED_REFRESH_TOKEN, null);

        if(authToken != null){
            return "Bearer " +authToken;
        }
        return null;
    }


    /**
     * Handle Logout methods
     */
    public void doLogout() {
        cleanDatabase();
    }


    /**
     * Get User id Planificado
     * @return int
     */
    public int getUserIdPlanificado() {
        int idPlanificado = -1;
        realm.beginTransaction();

        User user = realm.where(User.class).findFirst();

        if (user != null){
            idPlanificado = user.getIdPlanificado();
        }

        realm.commitTransaction();

        if (idPlanificado == -1){
            idPlanificado = Singleton.getInstance().getIdPlanificado();
        }

        return idPlanificado;
    }


    /**
     * Save Elementos
     * @param body ElementoResponse
     */
    public void saveElementos(final ElementoResponse body) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(body.getElementos());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Success Saving Elementos");

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error Saving Elementos");
            }
        });
    }


    /**
     * Save user Salari and Notificacions
     * @param body Salari
     */
    public void saveUserData(final Salari body) {

        final ArrayList<Notificacio> notificacions = constructNotificacionsModel(body.getDescuentos());

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
               /* for (Notificacio not : notificacions){
                    Notificacio nn = bgRealm.where(Notificacio.class).equalTo("id", Utils.getDateFromString(not.getData()).getTime()).findFirst();
                    if (nn == null){
                        bgRealm.copyToRealmOrUpdate(not);
                    }

                }*/
                bgRealm.copyToRealmOrUpdate(body);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Success Saving Salari");
                EventBus.getDefault().post(new UserDataSuccessEvent("", true));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error Saving Salari");
            }
        });

    }

    /**
     * Save Notificacions
     * @param mensajes
     */
    public void saveNotificacions(final ArrayList<Notificacio> mensajes) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                long count = bgRealm.where(Notificacio.class).equalTo("readed", true).count();
                bgRealm.delete(Notificacio.class);

                int i = 1;
                for (Notificacio not : mensajes){
                        not.setId(i);
                        if (i <= count){
                            not.setReaded(true);
                        }
                        bgRealm.copyToRealmOrUpdate(not);
                    i++;
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Success Saving Notificacions");
                EventBus.getDefault().post(new NotificationsSuccessEvent("", true));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error Saving Notificacions");
            }
        });
    }

    /**
     * Construct Notificacio Model from Salari response
     * @param descuentos RealmList<Descuento>
     * @return ArrayList<Notificacio>
     */
    private ArrayList<Notificacio> constructNotificacionsModel(RealmList<Descuento> descuentos) {
        ArrayList<Notificacio> notificacions = new ArrayList<>();
        for (Descuento desc : descuentos) {
            Notificacio not = new Notificacio();
            not.setId(Utils.getDateFromString(desc.getSemana()).getTime());
            not.setText(desc.getSalari());
            not.setData(desc.getSemana());
            not.setTipo(0);
            notificacions.add(not);
        }

        return notificacions;
    }


    /**
     * Get User Salari Model
     * @return
     */
    public Salari getSalari() {
        realm.beginTransaction();

        Salari salari = realm.where(Salari.class).findFirst();

        realm.commitTransaction();
        return salari;
    }

    /**
     * Get All User Notifications
     * @return ArrayList<Notificacio>
     */
    public ArrayList<Notificacio> getNotificacions() {

        realm.beginTransaction();

        RealmResults<Notificacio> notificacions = realm.where(Notificacio.class).findAll();
        List<Notificacio> unmanagedNotificacions = realm.copyFromRealm(notificacions);

        realm.commitTransaction();

        return new ArrayList<>(unmanagedNotificacions);
    }

    /**
     * Set Notificacio Readed true
     */
    public void setReadedNotifications() {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmResults<Notificacio> notificacions = bgRealm.where(Notificacio.class).findAll();
                for (Notificacio notificacio : notificacions){
                    notificacio.setReaded(true);
                    bgRealm.copyToRealmOrUpdate(notificacio);
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Success Updating Notifications");

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error Updating Notifications");
            }
        });
    }


    /**
     * Save User Info
     * @param body User
     * @param changeContrato
     */
    public void saveUserInfo(final User body, final boolean changeContrato) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(body);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Success Saving User");
                if (!changeContrato){
                    setDefaultContrato(-1);
                }

                //EventBus.getDefault().post(new NotificationsUpdatedEvent(true));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error Saving User");
            }
        });
    }

    /**
     * Set Default Contrato
     * @param id int
     */
    public void setDefaultContrato(int id) {
        if (id == -1){
            try{
                id = getUser().getContratos().get(0).getIdVivienda();
            }catch (Exception e){
                Log.e("setDefaultContrato", "No contratos found");
            }
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(Constants.SHARED_CONTRATO, id);
        Log.e("setDefaultContrato", "ID CONTRATO SELECCIONADO -->" + id);

        editor.commit();

        EventBus.getDefault().post(new ChangedContratoEvent());

        refreshContrato();

    }

    private void refreshContrato() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.delete(Salari.class);
                bgRealm.delete(SalariDeixalleria.class);
                bgRealm.delete(Notificacio.class);
                bgRealm.delete(Missatge.class);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "refreshContrato SUCCESS");
                //TODO GET INFO
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "refreshContrato ERROR");

            }
        });

    }

    public Contrato getDefaultContrato(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());
        int contratoID = sharedPref.getInt(Constants.SHARED_CONTRATO, -1);
        realm.beginTransaction();

        Contrato contrato = realm.where(Contrato.class).equalTo("idVivienda", contratoID).findFirst();
        Contrato unmanagedContrato = null;
        if (contrato != null){
            unmanagedContrato = realm.copyFromRealm(contrato);
        }
        realm.commitTransaction();

        return unmanagedContrato;
    }


    /**
     * Get Elemento by NFC
     * @param nfc int
     * @return Eemento
     */
    public Elemento getElementoByNFC(int nfc) {

        realm.beginTransaction();

        Elemento elemento = realm.where(Elemento.class).equalTo("nfc", nfc).findFirst();

        realm.commitTransaction();

        return elemento;
    }

    /**
     * Get Logged User
     * @return User
     */
    public User getUser() {

        realm.beginTransaction();

        User user = realm.where(User.class).findFirst();

        realm.commitTransaction();
        return user;
    }

    /**
     * Get nearest Elemento by Coordinates
     * @param coordinates LatLng
     * @return Elemento
     */
    public Elemento getElementoByCoordinates(LatLng coordinates) {
        Location userLocation = new Location("User");

        userLocation.setLatitude(coordinates.latitude);
        userLocation.setLongitude(coordinates.longitude);

        realm.beginTransaction();

        RealmResults<Elemento> elementos = realm.where(Elemento.class).findAll();
        List<Elemento> unmanagedElementos = realm.copyFromRealm(elementos);

        realm.commitTransaction();


        if (elementos == null) {
            return null;
        }

        for (Elemento element : unmanagedElementos){

            Location locationElement = new Location("Element");
            locationElement.setLatitude(Float.parseFloat(element.getLatitud()));
            locationElement.setLongitude(Float.parseFloat(element.getLongitud()));
            int distance = (int) userLocation.distanceTo(locationElement);
            element.setDistance(distance);
        }

        Collections.sort(unmanagedElementos, new Comparator<Elemento>(){
            public int compare(Elemento emp1, Elemento emp2) {
                // ## Ascending order
                 return Integer.valueOf(emp1.getDistance()).compareTo(emp2.getDistance()); // To compare integer values
            }
        });

        return unmanagedElementos.get(0);
    }

    /**
     * Get Elemento by id
     * @param id int
     * @return Elemento
     */
    public Elemento getELementoByID(int id) {
        realm.beginTransaction();

        Elemento elemento = realm.where(Elemento.class).equalTo("id", id).findFirst();

        realm.commitTransaction();

        return elemento;
    }

    /**
     * Get Fraccion Success message by Elemento NFC
     * @param elementoId int
     * @return String
     */
    public String getElementoFraccionByNFC(int elementoId) {

        String fraccion = "";

        realm.beginTransaction();

        Elemento elemento = realm.where(Elemento.class).equalTo("nfc", elementoId).findFirst();
        Elemento elementoUnmanaged = null;
        if (elemento != null){
            elementoUnmanaged = realm.copyFromRealm(elemento);
        }

        realm.commitTransaction();

        if (elementoUnmanaged != null){
            fraccion = Utils.getFraccionName(context, elementoUnmanaged.getFraccion());
        }
        else{
            fraccion = context.getResources().getString(R.string.gracies_generic);
        }

        return fraccion;
    }

    public String getElementoFraccionShortByNFC(int elementoId) {

        String fraccion = "";

        realm.beginTransaction();

        Elemento elemento = realm.where(Elemento.class).equalTo("nfc", elementoId).findFirst();
        Elemento elementoUnmanaged = null;
        if (elemento != null){
            elementoUnmanaged = realm.copyFromRealm(elemento);
        }

        realm.commitTransaction();

        if (elementoUnmanaged != null){
            fraccion = Utils.getFraccionShortName(elementoUnmanaged.getFraccion());
        }
        else{
            fraccion = context.getResources().getString(R.string.deixalla);
        }

        return fraccion;
    }

    /**
     * Get Actuaciones
     * @return Collection<Actuaciones>
     */
    public Collection<? extends Actuacio> getActuaciones() {

        realm.beginTransaction();

        RealmResults<Actuacio> elementos = realm.where(Actuacio.class).findAll();
        List<Actuacio> unmanagedElementos = realm.copyFromRealm(elementos);

        realm.commitTransaction();
        return unmanagedElementos;
    }

    /**
     * Save Actuaciones
     * @param body ActuacionesResponse
     */
    public void saveActuaciones(final ActuacionesResponse body) {
         realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(body.getActuaciones());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Success Saving Notifications");
                EventBus.getDefault().post(new IncidenciesSuccessEvent("",true));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error Saving Notifications");
            }
        });
    }

    /**
     * Get User Id Actuacion
     * @return int
     */
    public int getIdActuacion() {
        int idActuacio = -1;
        realm.beginTransaction();

        User user = realm.where(User.class).findFirst();

        if (user != null){
            idActuacio = user.getRegistradoAct();
        }

        realm.commitTransaction();


        return idActuacio;
    }

    /**
     * Save Registro Offline
     * @param registrar Registrar
     */
    public void saveRegistroOffline(final Registrar registrar, File file) {
        byte[] image = null;
        if (file != null){
            image = Utils.readBytesFromFile(file.getPath());
        }
        int idElemento = 0;
        if (registrar.getIdElemento().size() != 0){
            idElemento = registrar.getIdElemento().get(0);
        }
        int nfc = 0;
        if (registrar.getNfc().size() != 0){
            nfc = registrar.getNfc().get(0);
        }
        final RegistrarOffline reg = new RegistrarOffline(registrar.getTimestamp(),
                                                    registrar.getModo(),
                                                    idElemento,
                                                    nfc,
                                                    registrar.getAccuracy(),
                                                    registrar.getIdActuacion(),
                                                    registrar.getLatitud(),
                                                    registrar.getLongitud(),
                                                    image);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(reg);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Success Saving Registrar OFFline");
                EventBus.getDefault().post(new IncidenciesSuccessEvent("",true));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error Registrar OFFline");
            }
        });
    }

    /**
     * Get first available offline registro
     * @return RegistrarOffline
     */
    public RegistrarOffline getOneRegistroOffline() {
        RegistrarOffline unmanagedRegistro = null;

        Realm realm2 = Realm.getDefaultInstance();
        realm2.beginTransaction();
        RealmResults<RegistrarOffline> registroCount = realm2.where(RegistrarOffline.class).findAll();
        if (registroCount != null){
            Log.d("OFFReg", "SIZE -->" +  String.valueOf(registroCount.size()));
        }

        RegistrarOffline registro = realm2.where(RegistrarOffline.class).equalTo("processing", false).findFirst();
        if (registro != null){
            Log.d("OFFReg", "registro TO PROCESS -->" +  String.valueOf(registro.getNfc()));

            registro.setProcessing(true);
            registro.setStartProcessingTimestamp((int) (System.currentTimeMillis() / 1000L));
            unmanagedRegistro = realm2.copyFromRealm(registro);
            realm2.commitTransaction();

        }
        else{

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            long yest = cal.getTimeInMillis();

            registro = realm2.where(RegistrarOffline.class).equalTo("processing", true).lessThan("startProcessingTimestamp", yest).findFirst();
            if (registro != null){
                registro.setStartProcessingTimestamp((int) (System.currentTimeMillis() / 1000L));
                unmanagedRegistro = realm2.copyFromRealm(registro);
            }
            realm2.commitTransaction();
        }


        return unmanagedRegistro;
    }

    /**
     * Delete Registro Offline
     * @param reg RegistrarOffline
     */
    public void deleteRegistroOffline(final RegistrarOffline reg) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.where(RegistrarOffline.class).equalTo("timestamp",reg.getTimestamp()).findFirst().deleteFromRealm();

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

                Log.d(TAG, "Success deleteRegistroOffline");

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error RdeleteRegistroOffline");
            }
        });




    }

    /**
     * Get number of new Notificacions
     * @return int
     */
    public int getNewNotifications() {
        realm.beginTransaction();

        RealmResults<Notificacio> notificacions = realm.where(Notificacio.class).equalTo("readed", false).findAll();
        List<Notificacio> unmanagedNotificacions = realm.copyFromRealm(notificacions);

        realm.commitTransaction();

        ArrayList<Notificacio> noti =  new ArrayList<>(unmanagedNotificacions);


        return noti.size();
    }


    public static void fillMissatges(final Context context) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(MissatgesSeeder.init(context));

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

                Log.d(TAG, "Success fillMissatges");

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error fillMissatges");
            }
        });
    }

    public Missatge getSuccessMissatgeToShow(int nfc) {

        if (!isTimeForSuccessMessage()){
            return null;
        }

        realm.beginTransaction();

        Elemento elemento = realm.where(Elemento.class).equalTo("nfc", nfc).findFirst();
        Elemento elementoUnmanaged = null;
        if (elemento != null){
            elementoUnmanaged = realm.copyFromRealm(elemento);
        }

        realm.commitTransaction();

        int fraccio = -1;
        if (elementoUnmanaged != null){
            fraccio = elementoUnmanaged.getFraccion();
        }

        Integer[] frArrStrings = new Integer[3];
        frArrStrings[0] = Constants.FRACCIO_DEIXALLERIA;
        frArrStrings[1] = Constants.FRACCIO_GENERAL;
        frArrStrings[2] = fraccio;

        //GET missatges
        realm.beginTransaction();
        RealmResults<Missatge> missatges = realm.where(Missatge.class).in("fraccio", frArrStrings).equalTo("shown", false).findAll();
        if (missatges == null){
            missatges = realm.where(Missatge.class).in("fraccio", frArrStrings).findAll();
            for (Missatge miss : missatges) {
                miss.setShown(false);
            }
        }

        Random r = new Random(System.nanoTime());
        int firstRandomNumber = r.nextInt(missatges.size());
        Missatge missatge = missatges.get(firstRandomNumber);
        missatge.setShown(true);

        Missatge missatgeUnmanaged = null;
        if (missatge != null){
            missatgeUnmanaged = realm.copyFromRealm(missatge);
        }
        realm.commitTransaction();
        return missatgeUnmanaged;
    }

    private boolean isTimeForSuccessMessage() {

       SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(UrgelletApplication.getInstance().getApplicationContext());
        int times = sharedPref.getInt(Constants.SHARED_TIMES, 0);
        Log.d("isTimeForSuccessMessage", "Times : " + String.valueOf(times));
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean theReturn = false;
        if (times >= 3){
            editor.putInt(Constants.SHARED_TIMES, 0);
            theReturn = true;
        }
        else{
            editor.putInt(Constants.SHARED_TIMES, times+1);
        }

        editor.commit();

        return theReturn;
    }

    public List<Contrato> getContratos() {
        User user = getUser();
        if (user == null){
            return new ArrayList<>();
        }
        RealmList<Contrato> ll =  getUser().getContratos();
        realm.beginTransaction();
        List<Contrato> contratosUnmanaged = new ArrayList<>();
        if (ll != null){
            contratosUnmanaged = realm.copyFromRealm(ll);
        }
        realm.commitTransaction();
        return contratosUnmanaged;
    }

    public SalariDeixalleria getSalariDeixalleria() {
        realm.beginTransaction();

        SalariDeixalleria salari = realm.where(SalariDeixalleria.class).findFirst();

        realm.commitTransaction();
        return salari;

    }

    public Integer getIdVivienda() {

        Contrato contrato = getDefaultContrato();

        Integer idVivienda = -1;

        if (contrato != null){
            idVivienda = contrato.getIdVivienda();
        }
        return idVivienda;
    }

    public void saveUserDataDeixalleria(final SalariDeixalleria body) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(body);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Success Saving SalariDeixalleria");
                EventBus.getDefault().post(new UserDataSuccessEvent("", true));
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "Error Saving SalariDeixalleria");
            }
        });
    }
}
