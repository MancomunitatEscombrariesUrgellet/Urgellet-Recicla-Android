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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.adapters.SalariListAdapter;
import com.smartcitylink.urgellet.events.UserDataSuccessEvent;
import com.smartcitylink.urgellet.helpers.Constants;
import com.smartcitylink.urgellet.manager.DataManager;
import com.smartcitylink.urgellet.models.Salari;
import com.smartcitylink.urgellet.models.SalariDeixalleria;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SalariFragment extends Fragment {

    View rootView;

    private SalariListAdapter salariListAdapter;
    private ArrayList<Object> salariArr = new ArrayList<>();
    private Salari salari;
    private SalariDeixalleria salariDeixalleria;


    @BindView(R.id.salariBuit)
    RelativeLayout salariBuit;

    @BindView(R.id.salariList)
    RecyclerView salariList;

    @BindView(R.id.saldoActual)
    TextView saldoActual;

    @BindView(R.id.desc_fecha)
    TextView desc_fecha;

    @BindView(R.id.saldoInicial)
    TextView saldoInicial;

    @BindView(R.id.screenTitle)
    TextView screenTitle;

    private boolean isDeixalleria = false;

    /**
     * Download PDF Salari
     */
    @OnClick(R.id.rebutButton)
    public void downloadSalari (){
        Toast.makeText(getActivity(), getContext().getString(R.string.downloading), Toast.LENGTH_SHORT).show();
        //TODO GET RECIBO
    }


    public SalariFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_salari, container, false);
        ButterKnife.bind(this, rootView);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            isDeixalleria = bundle.getBoolean(Constants.IS_DEIXALLERIA, false);
        }

        if (!isDeixalleria){
            salari = DataManager.getInstance().getSalari();
            setupList();
            fillScreen();
        }
        else{
            salariDeixalleria = DataManager.getInstance().getSalariDeixalleria();
            setupListDeixalleria();
            fillScreenDeixalleria();
        }




        return rootView;
    }

    /**
     * Omplim la pantalla
     */
    private void fillScreen() {
        if (salari == null){
            showSalariBuit(true);
            return;
        }
        salariArr.clear();
        if (salari.getDescuentos() != null){
            salariArr.addAll(salari.getDescuentos());
        }
        salariListAdapter.notifyDataSetChanged();

        saldoActual.setText(salari.getSaldoActual());
        saldoInicial.setText(salari.getSaldoInicial());
        screenTitle.setText(getResources().getString(R.string.descompte_contenidors));
        desc_fecha.setText("");

    }

    /**
     * Omplim la pantalla
     */
    private void fillScreenDeixalleria() {
        if (salariDeixalleria == null){
            showSalariBuit(true);
            return;
        }
        salariArr.clear();
        if (salariDeixalleria.getDescuentos() != null){
            salariArr.addAll(salariDeixalleria.getDescuentos());
        }
        salariListAdapter.notifyDataSetChanged();

        saldoActual.setText(salariDeixalleria.getSaldoActual());
        screenTitle.setText(getResources().getString(R.string.descompte_deixalleria));
        desc_fecha.setText(getResources().getString(R.string.actualitzacio_quatrimestral));


    }

    /**
     * Mostrem/amaguem missatge
     * @param show boolean
     */
    private void showSalariBuit(boolean show) {
        if (show){
            salariBuit.setVisibility(View.VISIBLE);
        }
        else{
            salariBuit.setVisibility(View.GONE);
        }
    }


    /**
     * Inicialitzem la llista i el seu adapter
     */
    private void setupList() {

        salariList.setHasFixedSize(true);
        salariList.setLayoutManager(new LinearLayoutManager(getActivity()));
        salariListAdapter = new SalariListAdapter(getActivity(),salariArr);
        salariList.setAdapter(salariListAdapter);

    }

    /**
     * Inicialitzem la llista i el seu adapter
     */
    private void setupListDeixalleria() {

        salariList.setHasFixedSize(true);
        salariList.setLayoutManager(new LinearLayoutManager(getActivity()));
        salariListAdapter = new SalariListAdapter(getActivity(),salariArr);
        salariList.setAdapter(salariListAdapter);

    }

    /**
     * Event per actualitzacio de dades
     * @param event UserDataSuccessEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccessEvent(UserDataSuccessEvent event){
        if (event.success){
            salari = DataManager.getInstance().getSalari();
            fillScreen();
        }
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
