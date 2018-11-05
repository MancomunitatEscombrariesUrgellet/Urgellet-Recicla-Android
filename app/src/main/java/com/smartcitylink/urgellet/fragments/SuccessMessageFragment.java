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


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.helpers.Utils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SuccessMessageFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_FRACCIO_TEXT = "fraccioText";
    private static final String ARG_LINK = "link";
    private static final String ARG_IMAGE = "image";
    private static final String ARG_ICON = "icon";

    private String message;
    private String fraccioText;
    private String link;
    private int image;
    private int icon;


    @BindView(R.id.fraccio)
    TextView fraccio;

    @BindView(R.id.missatgeText)
    TextView missatgeText;

    @BindView(R.id.fraccioImage)
    ImageView fraccioImage;

    @BindView(R.id.fraccioCircleMini)
    RelativeLayout fraccioCircleMini;

    @BindView(R.id.buttonLink)
    ImageButton buttonLink;

    @BindView(R.id.successImage)
    ImageView successImage;

    @BindView(R.id.has_reciclat)
    TextView hasReciclat;


    private View rootView;

    public SuccessMessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SuccessMessageFragment.
     */
    public static SuccessMessageFragment newInstance(String param1, String param2) {
        SuccessMessageFragment fragment = new SuccessMessageFragment();
        Bundle args = new Bundle();
       // args.putString(ARG_PARAM1, param1);
       // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           // mParam1 = getArguments().getString(ARG_PARAM1);
           // mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_success_message, container, false);
        ButterKnife.bind(this, rootView);


        try{
            image = getActivity().getResources().getIdentifier(getArguments().getString(ARG_IMAGE), "drawable", getActivity().getPackageName());
            Picasso.with(getActivity()).load(image).into(successImage);
        }
        catch (Exception e){
            Log.e("SuccesMessage", e.getMessage());
        }

        try{
            message = getArguments().getString(ARG_MESSAGE);
            fraccioText = getArguments().getString(ARG_FRACCIO_TEXT);

            if (fraccioText.equals(getResources().getString(R.string.resta))){
                hasReciclat.setText(getResources().getString(R.string.Has_llensat));
            }

            link = getArguments().getString(ARG_LINK);
            icon = getArguments().getInt(ARG_ICON);

        }catch (Exception e){
            Log.e("Message SUCCESS", "No message");

        }

        fraccio.setText(fraccioText);
        try{
            Picasso.with(getActivity()).load(icon).into(fraccioImage);

        }
        catch (Exception e){
            Log.e("SuccesMessage", e.getMessage());
        }


        missatgeText.setText(message);

        if (link != null && !link.equals("null")){
            buttonLink.setVisibility(View.VISIBLE);
            buttonLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //OpenUrl
                    try{
                        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        startActivity(intent);
                    }catch (Exception e){
                        Log.e("OpenUrl", e.getMessage());
                    }
                }
            });

        }
        else{
            buttonLink.setVisibility(View.GONE);
        }

        Utils.vibrate(getActivity());

        return rootView;

    }

}
