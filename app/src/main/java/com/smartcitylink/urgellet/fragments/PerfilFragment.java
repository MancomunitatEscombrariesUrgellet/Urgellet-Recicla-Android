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
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.activities.BaseActivity;
import com.smartcitylink.urgellet.activities.MainActivity;
import com.smartcitylink.urgellet.adapters.PerfilListAdapter;
import com.smartcitylink.urgellet.events.ChangedContratoEvent;
import com.smartcitylink.urgellet.helpers.Constants;
import com.smartcitylink.urgellet.models.PerfilItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PerfilFragment extends Fragment {

    private View rootView;

    @BindView(R.id.perfilRV)
    RecyclerView perfilRV;

    @BindView(R.id.loader)
    RelativeLayout loader;

    private PerfilListAdapter perfilListAdapter;

    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_perfil, container, false);

        ButterKnife.bind(this, rootView);
        ((BaseActivity)getActivity()).fillHeader(rootView, getActivity());
        setupList();

        return rootView;
    }



    @OnClick(R.id.changeContracte)
    void changeContracteMethod(){
        ((MainActivity)getActivity()).showContractModal();
    }

    /**
     * Setups the list
     */
    private void setupList() {

        perfilRV.setHasFixedSize(true);
        perfilRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        perfilListAdapter = new PerfilListAdapter(getActivity(),getPerfilListData());
        perfilRV.setAdapter(perfilListAdapter);

    }

    private ArrayList<PerfilItem> getPerfilListData() {

        ArrayList<PerfilItem> items = new ArrayList<>();

        items.add(new PerfilItem(0,"Recollida de residus", "Calendari del servei",null,  R.drawable.calendar, Constants.ACTION_PERFIL_RESIDUS));
        items.add(new PerfilItem(1,"Descomptes", "Historial de descomptes", null, R.drawable.folder, Constants.ACTION_PERFIL_DESCOMPTES));
        items.add(new PerfilItem(2,"Coneix més", "Atenció a l'usuari", null, R.drawable.question, Constants.ACTION_PERFIL_CONEIX));

        return items;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangedContratoEvent(ChangedContratoEvent event){
        ((BaseActivity)getActivity()).fillHeader(rootView, getActivity());
        //sleep de 3 segundos para cargar la info del user.
        showLoader(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoader(false);
            }
        }, 3000);
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
