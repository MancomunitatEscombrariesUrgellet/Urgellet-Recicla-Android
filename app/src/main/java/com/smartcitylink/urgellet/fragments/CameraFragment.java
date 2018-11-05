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

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.events.IncidenciesSuccessEvent;
import com.smartcitylink.urgellet.events.RegistrarSuccessEvent;
import com.smartcitylink.urgellet.events.SendCodiToServerEvent;
import com.smartcitylink.urgellet.helpers.OnRegistrarFragmentInteractionListener;
import com.smartcitylink.urgellet.manager.DataManager;
import com.smartcitylink.urgellet.models.Elemento;
import com.smartcitylink.urgellet.services.ResultHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraFragment extends Fragment implements View.OnLayoutChangeListener {

    private static final String TAG = "CameraOverlayFragment";

    private View rootView;

    @BindView(R.id.camera)
    CameraView camera;

    @BindView(R.id.cameraContainer)
    RelativeLayout cameraContainer;

    @BindView(R.id.buttonsLayout)
    LinearLayout buttonsLayout;

    @BindView(R.id.imagePreview)
    ImageView imagePreview;

    @BindView(R.id.cameraPreview)
    RelativeLayout cameraPreview;

    @BindView(R.id.showFlash)
    ImageButton showFlash;

    @BindView(R.id.loader)
    RelativeLayout loader;

    @BindView(R.id.takePhoto)
    ImageButton takePhoto;

    String nfc;
    int modo;
    private OnRegistrarFragmentInteractionListener mListener;


    private int mCameraWidth;
    private int mCameraHeight;

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        ButterKnife.bind(this,rootView);

        try{
            nfc = getArguments().getString("nfc");
            modo = getArguments().getInt("modo");

        }catch (Exception e){
            Log.e("READED SUCCESS", "No message");
        }


        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        startCamera();

        updateCamera();
    }

    private void startCamera() {
        if (checkPermissionsCamera(113))
        camera.start();
    }

    /**
     * Comprobem permissos Camera
     * @param permissions int
     * @return boolean
     */
    private boolean checkPermissionsCamera(int permissions) {

        boolean permsGranted = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
               // ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!permsGranted) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
              //      Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, permissions);
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            showNeutralAlert(getResources().getString(R.string.no_permissions_granted));
        }
    }

    private void showNeutralAlert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
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

    @OnClick(R.id.takePhoto)
    void capturePhoto() {

        final long startTime = System.currentTimeMillis();
        camera.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                long callbackTime = System.currentTimeMillis();
                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                ResultHolder.dispose();
                ResultHolder.setImage(bitmap);
                ResultHolder.setNativeCaptureSize(
                        camera.getPreviewSize()
                );
                ResultHolder.setTimeToCallback(callbackTime - startTime);

                EventBus.getDefault().post(new SendCodiToServerEvent(""));
            }
        });

        camera.captureImage();

    }


    @OnClick(R.id.fotoRetry)
    void fotoRetryMethod() {
        takePhoto.setEnabled(true);
        previewImageNone();
    }

    @OnClick(R.id.sendFoto)
    void sendFotoMethod() {
        if (mListener != null){
            buttonsLayout.setVisibility(View.GONE);
            showLoader(true);
            mListener.enviarRegistreAmbFoto(nfc,modo,ResultHolder.getFile(getActivity()));
        }
    }

    @OnClick(R.id.showFlash)
    void showFlashMethod() {
        switch (camera.toggleFlash()) {
            case CameraKit.Constants.FLASH_ON:
               // Toast.makeText(getActivity(), "Flash on!", Toast.LENGTH_SHORT).show();
                showFlash.setImageResource(R.drawable.flash);
                break;

            case CameraKit.Constants.FLASH_OFF:
             //   Toast.makeText(getActivity(), "Flash off!", Toast.LENGTH_SHORT).show();
                showFlash.setImageResource(R.drawable.flash_off);
                break;

            case CameraKit.Constants.FLASH_AUTO:
             //   Toast.makeText(getActivity(), "Flash auto!", Toast.LENGTH_SHORT).show();
                showFlash.setImageResource(R.drawable.flash_auto);
                break;
        }
    }

    private void previewImageDone() {
        buttonsLayout.setVisibility(View.VISIBLE);
        imagePreview.setVisibility(View.VISIBLE);
        imagePreview.setImageBitmap(ResultHolder.getImage());
        cameraPreview.setVisibility(View.GONE);
    }

    private void previewImageNone() {
        buttonsLayout.setVisibility(View.GONE);
        imagePreview.setVisibility(View.GONE);
        imagePreview.setImageBitmap(null);
        cameraPreview.setVisibility(View.VISIBLE);
    }



    private void updateCamera() {
        ViewGroup.LayoutParams cameraLayoutParams = camera.getLayoutParams();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = cameraLayoutParams.width;

        int height = size.x;

        cameraLayoutParams.width = height;
        cameraLayoutParams.height = height;

        camera.addOnLayoutChangeListener(this);
        camera.setLayoutParams(cameraLayoutParams);
    }

    @Override
    public void onPause() {
        camera.stop();
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        mCameraWidth = right - left;
        mCameraHeight = bottom - top;

        Log.d(TAG, "mCameraHeight -->" + String.valueOf(mCameraHeight));
        Log.d(TAG, "mCameraWidth -->" + String.valueOf(mCameraWidth));

        camera.removeOnLayoutChangeListener(this);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendCodiToServerEvent(SendCodiToServerEvent event){

        previewImageDone();

    }

    /**
     * Handles Registrar Success Event
     * @param event Event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegistrarSuccessEvent(RegistrarSuccessEvent event){

        if (event.success){
            if (mListener !=null){
                mListener.registeredSuccessMessage(getResources().getString(R.string.gracies_incidencia),getFr());
            }
        }
        else{
            if (mListener != null){
                mListener.showNeutralAlert(getResources().getString(R.string.error_retry));
            }

        }
        showLoader(false);

    }

    private int getFr() {
        Elemento elem = DataManager.getInstance().getElementoByNFC(Integer.parseInt(nfc));
        int fr = -1;
        if (elem != null){
            fr = elem.getFraccion();
        }
        Log.d("incidencFR", String.valueOf(fr));
        return fr;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIncidenciesSuccessEvent(IncidenciesSuccessEvent event){

        if (event.success){
            if (mListener !=null){
                mListener.registeredSuccessMessage(getResources().getString(R.string.gracies_incidencia),getFr());
            }
        }
        else{
            if (mListener != null){
                mListener.showNeutralAlert(getResources().getString(R.string.error_retry));
            }

        }
        showLoader(false);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegistrarFragmentInteractionListener) {
            mListener = (OnRegistrarFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Show a Loader
     * @param show boolean
     */
    private void showLoader(boolean show) {
        if (show){
            loader.setVisibility(View.VISIBLE);
        }
        else{
            loader.setVisibility(View.GONE);
        }
    }
}
