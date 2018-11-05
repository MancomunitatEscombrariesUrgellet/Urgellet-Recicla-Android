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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.helpers.OnRegistrarFragmentInteractionListener;
import com.smartcitylink.urgellet.helpers.Utils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReadedSuccessFragment extends Fragment {

    private View rootView;
    private String message;
    private boolean placeholder = true;
    private OnRegistrarFragmentInteractionListener mListener;


    public ReadedSuccessFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.successImage)
    ImageView successImage;
    @BindView(R.id.textSuccess)
    TextView textSuccess;
    @BindView(R.id.tornaInici)
    Button tornaInici;

    private int imageFraccio;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_readed_success, container, false);

        ButterKnife.bind(this,rootView);

        try{
            message = getArguments().getString("message");
            placeholder = getArguments().getBoolean("placeholder");
            imageFraccio = getArguments().getInt("image");
        }catch (Exception e){
            Log.e("READED SUCCESS", "No message");
        }

        if (message == null){
            message = getResources().getString(R.string.gracies_generic);
        }



        if (placeholder){
            //textSuccess.setText(message);
            spannableMessage();
            YoYo.with(Techniques.Bounce)
                    .duration(700)
                    .repeat(1)
                    .playOn(successImage);

            Utils.vibrate(getActivity());
            tornaInici.setVisibility(View.GONE);
            Picasso.with(getActivity()).load(imageFraccio).into(successImage);

        }
        else{
            Picasso.with(getActivity()).load(imageFraccio).into(successImage);
            //textSuccess.setText(message);
            spannableMessage();
            tornaInici.setVisibility(View.VISIBLE);

        }



        return rootView;
    }

    private void spannableMessage() {
        String s= message;
        String[] each = s.split(" ");

        String last_text = each[each.length-1];


        SpannableString ss1=  new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(1f), (s.length()-last_text.length()),s.length(), 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.WHITE), (s.length()-last_text.length()),s.length(), 0);// set color
        textSuccess.setText(ss1);
    }

    @OnClick(R.id.tornaInici)
    public void setTornaInici(View v){
        if (mListener != null){
            mListener.tornaInici(R.id.action_inicio);
        }
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


}
