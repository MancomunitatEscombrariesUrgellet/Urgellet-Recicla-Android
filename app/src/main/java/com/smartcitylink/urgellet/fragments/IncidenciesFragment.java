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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.events.IncidenciesSuccessEvent;
import com.smartcitylink.urgellet.events.RegistrarSuccessEvent;
import com.smartcitylink.urgellet.helpers.OnRegistrarFragmentInteractionListener;
import com.smartcitylink.urgellet.helpers.Utils;
import com.smartcitylink.urgellet.manager.DataManager;
import com.smartcitylink.urgellet.models.Actuacio;
import com.smartcitylink.urgellet.models.Elemento;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IncidenciesFragment extends Fragment {


    private static final int INCIDENCIA_ID_CAMERA = 111000;
    private static final int INCIDENCIA_ID_GALLERY = 222000;


    private OnRegistrarFragmentInteractionListener mListener;

    public IncidenciesFragment() {
        // Required empty public constructor
    }
    private View rootView;
    private ArrayList<Actuacio> actuacionsArr = new ArrayList<>();

    @BindView(R.id.incidenciesContainer)
    LinearLayout incidenciesContainer;
    @BindView(R.id.layoutSucces)
    LinearLayout layoutSucces;
    @BindView(R.id.loader)
    RelativeLayout loader;

    String nfc;
    int modo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_incidencies, container, false);
        ButterKnife.bind(this,rootView);

        try{
            nfc = getArguments().getString("nfc");
            modo = getArguments().getInt("modo");

        }catch (Exception e){
            Log.e("READED SUCCESS", "No message");
        }

        actuacionsArr.addAll(DataManager.getInstance().getActuaciones());
        setupActuaciones();

        Utils.vibrate(getActivity());

        return rootView;
    }


    private void setupActuaciones() {
        if (actuacionsArr == null){
            return;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0,1);
        int i = 0;
        for (Actuacio act :actuacionsArr){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            RelativeLayout inflatedLayout= (RelativeLayout) inflater.inflate(R.layout.incidencia_row_item, null, false);
            inflatedLayout.setOnClickListener(actuacioButton);
            inflatedLayout.setBackgroundColor(getResources().getColor(getColor(i)));
            inflatedLayout.setTag(act.getId());
            TextView tV = (TextView) inflatedLayout.findViewById(R.id.title);
            tV.setText(act.getDescripcio());
            inflatedLayout.setLayoutParams(params);
            incidenciesContainer.addView(inflatedLayout);
            i++;
        }

    }

    private int getColor(int i) {
        switch (i){
            case 0:
                return R.color.blue1;
            case 1:
                return R.color.blue2;
            case 2:
                return R.color.blue3;
            case 3:
                return R.color.blue4;
            case 4:
                return R.color.blue5;
            default:
                return  R.color.blue1;

        }
    }

    private View.OnClickListener actuacioButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Actuacio actuacio = getActuacioById((int)view.getTag());

            if (actuacio!=null){
                manageActuacio(actuacio);
            }
        }
    };

    private void manageActuacio(Actuacio actuacio) {
        if (mListener != null) {
            mListener.setIdActuacion(actuacio.getId());
        }
        if (actuacio.getFoto() == 1){
            openCamera();
        }
        else{
            sendActuacio();
        }
    }

    private void openCamera() {
        if (mListener != null) {
            mListener.crearRegistreAmbFoto(nfc,modo);
        }
    }

    private void sendActuacio() {
        loader.setVisibility(View.VISIBLE);
        if (mListener != null){
            mListener.crearRegistre(Integer.parseInt(nfc),modo);
        }

    }

    private Actuacio getActuacioById(int tag) {
        Actuacio actuacio = null;
        for (Actuacio act :actuacionsArr) {
            if (act.getId() == tag){
                actuacio = act;
            }
        }
            return actuacio;
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



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIncidenciesSuccessEvent(IncidenciesSuccessEvent event){
        if (event.success){

            if (mListener !=null){

                mListener.registeredSuccessMessage(getResources().getString(R.string.gracies_incidencia), getFr());
            }
        }
        else{
            if (mListener != null){
                mListener.showNeutralAlert(getResources().getString(R.string.error_retry));
            }
        }
        loader.setVisibility(View.GONE);
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
    public void onRegistrarSuccessEvent(RegistrarSuccessEvent event){

        if (event.success){
            if (mListener !=null){
                mListener.registeredSuccessMessage(getResources().getString(R.string.gracies_incidencia), getFr());
            }
        }
        else{
            if (mListener != null){
                mListener.showNeutralAlert(getResources().getString(R.string.error_retry));
            }

        }
        loader.setVisibility(View.GONE);
    }



    @Override
    public void onPause(){
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);
    }
}
