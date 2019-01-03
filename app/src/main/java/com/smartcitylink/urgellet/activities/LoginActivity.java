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
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.events.LoginSuccessEvent;
import com.smartcitylink.urgellet.events.NoConnectionEvent;
import com.smartcitylink.urgellet.events.SendCodiToServerEvent;
import com.smartcitylink.urgellet.helpers.Utils;
import com.smartcitylink.urgellet.models.Singleton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    private RelativeLayout loader;
    private boolean waitingCodi = false;

    @BindView(R.id.phoneEditText)
    EditText phoneEditText;
    @BindView(R.id.enviarButton)
    Button enviarButton;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.changeModoText)
    Button changeModoButton;
    @BindView(R.id.benvingut)
    TextView benvingut;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        loader = (RelativeLayout) findViewById(R.id.loader);
        loader.setVisibility(View.GONE);

        checkDensity();


    }

    private void checkDensity() {
        float density = getResources().getDisplayMetrics().density;

    }

    private boolean checkSMSPermission() {


        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;

    }

    private void showPermissionsRationale() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.permisos_sms))
                .setNeutralButton(getResources().getString(R.string.acceptar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        grantPermissions();

                    }
                })
                .show();
    }

    private void grantPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_SMS},333);
    }

    /**
     * Click Login
     */
    @OnClick(R.id.enviarButton)
   public void enviarButtonClick(){
        if (Utils.isOnline(this)){
            if (isValid()){
                if (checkSMSPermission()){
                    doLogin();
                }
                else{
                    showPermissionsRationale();
                }
            }
        }else{
            showNeutralAlert(getResources().getString(R.string.no_internet), this);
        }



   }

    /**
     * Change phone screen to
     * code screen and viceversa
     */
    @OnClick(R.id.changeModoText)
    public void changeModo(){
        Singleton.getInstance().setPhone(phoneEditText.getText().toString());
        goCheckCodiActivity();
    }

    /**
     * Click Login from keyboard Action
     * @param actionId int
     * @return boolean
     */
    @OnEditorAction(R.id.phoneEditText)
    public boolean onClickAction(int actionId){
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            doLogin();
            return true;
        }

        return false;
    }

    /**
     * Calls the API to do the Login
     */
    private void doLogin() {


        if (isValid()){
            loader.setVisibility(View.VISIBLE);
            if (!waitingCodi) {
                //TODO: do login sending phone number to server and trigger LoginSuccessEvent with EventBus library to continue
            }
            else {
                //TODO: send sms code on phoneEditText to server and receive LoginResponse Object and trigger saveLoginInfo method on DataManager.java class
                //TODO: sync from server User class and call saveUserInfo method on DataManager.java class and save user data on Singleton
                //TODO: sync from server Salari class and save data on Realm and call saveUserData method on DataManager.java
                //TODO: sync from server SalariDeixalleria class and call saveUserDataDeixalleria method on DataManager.java class
                //TODO: sync from server NotificationsResponse class and call saveNotificacions method on DataManager.java class
                //TODO: sync from server ActuacionesResponse class and call saveActuaciones method on DataManager.java class
                //TODO: sync from server ElementoResponse class and save data on Realm and call saveElementos method on DataManager.java class
            }
        }
        else{
            Snackbar.make(this.findViewById(R.id.activity_login),getResources().getString(R.string.field_required) , Snackbar.LENGTH_SHORT).show();
        }

    }

    /**
     * Checks phoneEditText is not empty
     * @return boolean
     */
    private boolean isValid() {
        return !phoneEditText.getText().toString().isEmpty();
    }

    /**
     * Goes to Main Activity
     */
    private void onSuccessCode() {
        showSuccessAlert();
    }

    private void showSuccessAlert() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.felicitats))
                .setNeutralButton(getResources().getString(R.string.comenca), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        goToMainActivity();
                    }
                })
                .show();
    }


    /**
     * Resets de EditText Code
     */
    private void resetCode() {

        loader.setVisibility(View.GONE);
        phoneEditText.setText("");

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Handles API Success or Failure Response
     * @param event LoginSuccessEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccessEvent(LoginSuccessEvent event){
        if (!event.success){
            resetCode();
            showNeutralAlert(event.message, this);
        }
        else{
            if (waitingCodi){
                onSuccessCode();
            }
            else{
                changeModo();
            }
        }
    }

    /**
     * Handles API Success or Failure Response
     * @param event LoginSuccessEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendCodiToServerEvent(SendCodiToServerEvent event){
        phoneEditText.setText(event.codi);
        doLogin();
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 333: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    doLogin();

                } else {

                    doLogin();
                }
                return;
            }
        }
    }

    /**
     * Listens on Location Changed Event
     * @param event LocationChangedEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoConnectionEventEvent(NoConnectionEvent event) {
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content));
        showNeutralAlert(getResources().getString(R.string.no_internet), this);
        loader.setVisibility(View.GONE);
    }
}
