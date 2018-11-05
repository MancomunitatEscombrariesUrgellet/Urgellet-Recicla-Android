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
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.events.CheckCodiSuccessEvent;
import com.smartcitylink.urgellet.events.LoginSuccessEvent;
import com.smartcitylink.urgellet.events.NoConnectionEvent;
import com.smartcitylink.urgellet.events.SendCodiToServerEvent;
import com.smartcitylink.urgellet.helpers.Constants;
import com.smartcitylink.urgellet.helpers.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckCodiActivity extends BaseActivity {

    private static final String TAG = "CheckCodiActivity";

    private RelativeLayout loader;
    private boolean waitingCodi = false;

    @BindView(R.id.codiEditText)
    EditText codiEditText;
    @BindView(R.id.enviarButton)
    Button enviarButton;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.changeModoText)
    Button changeModoButton;
    @BindView(R.id.benvingut)
    TextView benvingut;
    @BindView(R.id.countDown)
    TextView countDown;

    @BindView(R.id.codiEditTextLayout)
    LinearLayout codiEditTextLayout;

    @BindView(R.id.waitingLoader)
    ProgressBar waitingLoader;

    @BindView(R.id.reEnviarButton)
    Button reEnviarButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_codi);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        loader = (RelativeLayout) findViewById(R.id.loader);
        loader.setVisibility(View.GONE);

        enableAutomaticCodeParsing(checkSMSPermission());

        startCountDown();

    }

    private void enableAutomaticCodeParsing(boolean enable) {
        if (enable){
            codiEditTextLayout.setVisibility(View.GONE);
            textView.setText(R.string.waiting_code);
            waitingLoader.setVisibility(View.VISIBLE);
            hideKeyBoard();
        }
        else{
            codiEditTextLayout.setVisibility(View.VISIBLE);
            codiEditText.requestFocus();
            textView.setText(R.string.posa_codi);
            waitingLoader.setVisibility(View.GONE);
        }
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void startCountDown() {
        waitingCodi = true;
       // disableReenviarButton();
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                String timeInMinutes = String.format(Locale.getDefault(),"%d : %d",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                countDown.setText(timeInMinutes);

            }
            public void onFinish() {
               countDown.setText("00:00");
               enableAutomaticCodeParsing(false);
               enableReenviarButton();

            }
        }.start();
    }

    private void enableReenviarButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reEnviarButton.setTextColor(getResources().getColor(R.color.colorAccent, null));
        }
        else{
            reEnviarButton.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        reEnviarButton.setEnabled(true);

        codiEditTextLayout.setVisibility(View.VISIBLE);

    }

    private void disableReenviarButton() {
        enableAutomaticCodeParsing(checkSMSPermission());
        startCountDown();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            reEnviarButton.setTextColor(getResources().getColor(R.color.grayMedium, null));
        }
        else{
            reEnviarButton.setTextColor(getResources().getColor(R.color.grayMedium));
        }

        reEnviarButton.setEnabled(false);

    }



    private boolean checkSMSPermission() {

        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;

    }



    /**
     * Click Login
     */
    @OnClick(R.id.enviarButton)
   public void enviarButtonClick(){
        if (isValid()){
            doLogin();
        }


   }

    /**
     * Click Login
     */
    @OnClick(R.id.reEnviarButton)
    public void reEnviarButtonClick(){
        if (Utils.isOnline(this)){

            //TODO DO LOGIN

            disableReenviarButton();
        }
        else{
            showNeutralAlert(getResources().getString(R.string.no_internet), this);
        }

    }



    /**
     * Click Login from keyboard Action
     * @param actionId int
     * @return boolean
     */
    @OnEditorAction(R.id.codiEditText)
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
            waitingCodi = false;
            //TODO CHECK CODI
        }
        else{
            Snackbar.make(this.findViewById(R.id.activity_login),getResources().getString(R.string.field_required) , Snackbar.LENGTH_SHORT).show();
        }

    }

    /**
     * Checks codiEditText is not empty
     * @return boolean
     */
    private boolean isValid() {
        return !codiEditText.getText().toString().isEmpty();
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
        codiEditText.setText("");

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

        Log.d("onLoginSuccessEvent", "Success: " + event.success);
        Log.d("onLoginSuccessEvent", "Message: " + event.message);
        Log.d("onLoginSuccessEvent", "Waiting Codi: " + waitingCodi);


        if (!event.success){
            resetCode();
            showNeutralAlert(event.message, this);
        }

        if(!event.message.equals(Constants.FROM_CHECK)){
            if (event.success){
                if (waitingCodi){
                    startCountDown();
                }
                else{
                    onSuccessCode();
                }

            }
            else{
                startCountDown();
            }
        }

    }

    /**
     * Handles API Success or Failure Response
     * @param event LoginSuccessEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckCodiSuccessEvent(CheckCodiSuccessEvent event){
        if (event.success){
            Log.d("onCheckCodiSuccessEvent", String.valueOf(event.success));
            onSuccessCode();
        }


    }

    /**
     * Handles API Success or Failure Response
     * @param event LoginSuccessEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendCodiToServerEvent(SendCodiToServerEvent event){
        codiEditText.setText(event.codi);
        doLogin();
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
