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

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.activities.BaseActivity;
import com.smartcitylink.urgellet.adapters.PerfilListAdapter;
import com.smartcitylink.urgellet.helpers.Constants;
import com.smartcitylink.urgellet.models.PerfilItem;
import com.smartcitylink.urgellet.models.Periodo;
import com.smartcitylink.urgellet.models.Singleton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistorialDescomptesFragment extends Fragment {

    private View rootView;
    @BindView(R.id.historialRV)
    RecyclerView historialRV;

    private PerfilListAdapter perfilListAdapter;

    public HistorialDescomptesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_historial_descomptes, container, false);

        ButterKnife.bind(this, rootView);
        ((BaseActivity)getActivity()).fillHeader(rootView, getActivity());
        setupList();
        return rootView;
    }

    /**
     * Setups the list
     */
    private void setupList() {

        historialRV.setHasFixedSize(true);
        historialRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        perfilListAdapter = new PerfilListAdapter(getActivity(),getPerfilListData());
        historialRV.setAdapter(perfilListAdapter);

    }

    private ArrayList<PerfilItem> getPerfilListData() {

        ArrayList<Periodo> periodos = Singleton.getInstance().getPeriodos();
        ArrayList<PerfilItem> items = new ArrayList<>();

        for (Periodo periodo :
                periodos) {
            items.add(new PerfilItem(0,null, periodo.getDescripcion(),null,  R.drawable.ic_recibo, Constants.ACTION_HISTORIAL_DESCOMPTES));

        }

        return items;
    }

}
