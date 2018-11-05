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

package com.smartcitylink.urgellet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.events.StartQRCameraEvent;
import com.smartcitylink.urgellet.helpers.Constants;
import com.smartcitylink.urgellet.helpers.OnRegistrarFragmentInteractionListener;
import com.smartcitylink.urgellet.helpers.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class QRCompatFragment extends Fragment implements ZBarScannerView.ResultHandler{

    private static final String TAG = "QR-FRAGMENT";

    private ZBarScannerView mScannerView;

    private OnRegistrarFragmentInteractionListener mRegistrarListener;
    private boolean isValidating = false;

    private boolean isIncidencia = false;

    private String lastQR = "";
    private boolean timerIsOn = false;

    private CountDownTimer timer;

    public QRCompatFragment() {
        // Required empty public constructor
    }

    private View rootView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_qr_compat, container, false);

        TextView text = (TextView) rootView.findViewById(R.id.qrText);
        mScannerView = (ZBarScannerView) rootView.findViewById(R.id.scanner);
        try{
           String registerType = getArguments().getString(Constants.REG_TYPE);
            assert registerType != null;
            if (registerType.equals(Constants.REG_TYPE_INCIDENCIES)){
                isIncidencia = true;
                text.setText(getActivity().getResources().getString(R.string.qr_incidencia));
            }

        }catch (Exception e){
            Log.e("READED SUCCESS", "No message");
        }

        return rootView;
    }







    /**
     * Starts the timer to avoid duplicated entries
     */
    private void startTimer() {

       if (timer != null){
            timer.cancel();
            timerIsOn = false;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                timer  = new CountDownTimer(Constants.QR_TIMER_MILLISECONDS,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        timerIsOn = false;
                        isValidating = false;
                    }
                }.start();
            }
        });

        timerIsOn = true;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);

        if (context instanceof OnRegistrarFragmentInteractionListener) {
            mRegistrarListener = (OnRegistrarFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
        mRegistrarListener = null;
    }



    /**
     * Shows Success Readed Dialog
     * @param text String
     */
    private void showSuccessReadedDialog(String text) {
        String elementoId =  Utils.getStringElementFromUrl(text);

        if (mRegistrarListener != null){
            mRegistrarListener.showSuccessReadedDialog(elementoId, Constants.REGISTRAR_TIPUS_QR);

        }
    }

    /**
     * Checks is QR is valid
     * @param text String
     * @return boolean
     */
    private boolean qrIsValid(String text) {
        if (!text.contains("http://smartcity.link")){
            return false;
        }

        if (lastQR.equals(Utils.getStringElementFromUrl(text)) && timerIsOn){
            Log.e(TAG, "THE SAME");
            return false;
        }


        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.setFlash(true);  //Per llegir amb el flash
        mScannerView.startCamera();          // Start camera on resume


    }

    @Override
    public void onPause() {

        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause


    }

    /**
     * Handles Start QR Camera Reader Event
     * @param event StartQRCameraEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void oStartQRCameraEvent(StartQRCameraEvent event){
        if (event.success){
            /*if (!qrEader.isCameraRunning()){
                try{
                    qrEader.start();
                }catch (Exception e){
                    Log.e(TAG, e.toString());
                }
            }*/

        }
    }

    /**
     * Handles On QR Readed
     * @param result String
     */

    public void handleResult(String result) {


        isValidating = true;

        if (qrIsValid(result)){
            lastQR = Utils.getStringElementFromUrl(result);
            startTimer();
            if (!isIncidencia){
                showSuccessReadedDialog(result);
            }
            else{
                if (mRegistrarListener != null){
                    mRegistrarListener.goIncidenciesFragment(Utils.getStringElementFromUrl(result), Constants.REGISTRAR_TIPUS_QR);
                }
            }

        }
        else{
            isValidating = false;
            mScannerView.resumeCameraPreview(this);
        }
    }


    @Override
    public void handleResult(Result result) {
        isValidating = true;

        if (qrIsValid(result.getContents())){
            lastQR = Utils.getStringElementFromUrl(result.getContents());
            startTimer();
            if (!isIncidencia){
                showSuccessReadedDialog(result.getContents());
            }
            else{
                if (mRegistrarListener != null){
                    mRegistrarListener.goIncidenciesFragment(Utils.getStringElementFromUrl(result.getContents()), Constants.REGISTRAR_TIPUS_QR);
                }
            }

        }
        else{
            isValidating = false;

        }
    }
}
