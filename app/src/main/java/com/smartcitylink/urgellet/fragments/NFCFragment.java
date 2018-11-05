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
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.helpers.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NFCFragment extends Fragment {

    private static final String ARG_IS_INCIDENCIA = "param1";
    private static final String TAG = "NFCFragment";

    private Boolean isIncidencia;

    private NfcAdapter mNfcAdapter;
    private View rootView;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.nfcText)
    TextView nfcText;

    String type;

    public NFCFragment() {
        // Required empty public constructor
    }


    public static NFCFragment newInstance(boolean isIncidencia) {
        NFCFragment fragment = new NFCFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_INCIDENCIA, isIncidencia);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isIncidencia = getArguments().getBoolean(ARG_IS_INCIDENCIA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_nfc, container, false);
        ButterKnife.bind(this, rootView);

        try{
             type = getArguments().getString(Constants.REG_TYPE);

        }catch (Exception e){
            Log.e("READED SUCCESS", "No message");
        }


        assert type != null;
        if (type.equals(Constants.REG_TYPE_INCIDENCIES)){
            nfcText.setText(getResources().getString(R.string.nfc_incidencia));
        }
        else{
            nfcText.setText(getResources().getString(R.string.apropa));
        }

        progress.setVisibility(View.GONE);


        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



}
