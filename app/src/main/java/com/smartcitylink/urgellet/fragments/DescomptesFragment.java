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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.activities.MainActivity;
import com.smartcitylink.urgellet.helpers.Constants;
import com.smartcitylink.urgellet.manager.DataManager;
import com.smartcitylink.urgellet.models.Salari;
import com.smartcitylink.urgellet.models.SalariDeixalleria;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DescomptesFragment extends Fragment {

    private View rootView;
    private Salari salariContenidors;
    private SalariDeixalleria salariDeixalleria;

    @BindView(R.id.descompteContenidors)
    TextView descompteContenidors;
    @BindView(R.id.descompteDeixalleria)
    TextView descompteDeixalleria;

    public DescomptesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_descomptes, container, false);
        ButterKnife.bind(this, rootView);

        salariContenidors = DataManager.getInstance().getSalari();
        salariDeixalleria = DataManager.getInstance().getSalariDeixalleria();

        fillScreen();
        return rootView;
    }

    private void fillScreen() {
        if (salariContenidors != null){
            descompteContenidors.setText(salariContenidors.getSaldoActual());
        }
        if (salariDeixalleria != null){
            descompteDeixalleria.setText(salariDeixalleria.getSaldoActual());
        }

    }


    @OnClick(R.id.descContenidorsLayout)
    void descContenidorsLayoutMethod(){
        ((MainActivity)getActivity()).addFragment(SalariFragment.class, null);

    }
    @OnClick(R.id.descDeixalleriaLayout)
    void descDeixalleriaLayoutMethod(){
        Bundle args = new Bundle();
        args.putBoolean(Constants.IS_DEIXALLERIA, true);
        ((MainActivity)getActivity()).addFragment(SalariFragment.class, args);

    }
    @OnClick(R.id.historialDescomptesLayout)
    void historialDescomptesLayout(){
        ((MainActivity)getActivity()).addFragment(HistorialDescomptesFragment.class, null);
    }



}
