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
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.events.LocationChangedEvent;
import com.smartcitylink.urgellet.models.Elemento;
import com.smartcitylink.urgellet.models.Singleton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GeoFragment extends Fragment implements OnMapReadyCallback {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "GeoFragment";

    private String mParam1;
    private String mParam2;

    private View rootView;

    @BindView(R.id.mapview)
    MapView mapView;

    private GoogleMap map;

    private LatLng coordinates;
    private Elemento elemento;


    public GeoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeoFragment.
     */
    public static GeoFragment newInstance(String param1, String param2) {
        GeoFragment fragment = new GeoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_geo, container, false);
        ButterKnife.bind(this, rootView);

        mapView.onCreate(savedInstanceState);

        getLocation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initMap();
            }
        }, 1000);

        return rootView;
    }

    private void getLocation() {
        Location location = Singleton.getInstance().getLocation();
        if (location != null)
        coordinates = new LatLng(location.getLatitude(),location.getLongitude());

    }

    /**
     * Listens on Location Changed Event
     * @param event LocationChangedEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void oLocationChangedEvent(LocationChangedEvent event) {
        if (event.location != null)
        coordinates = new LatLng(event.location.getLatitude(),event.location.getLongitude());
        Log.d("TrackLocationService", " location Changed");
    }

    private void initMap() {
        mapView.setVisibility(View.VISIBLE);
        mapView.getMapAsync(this);
        MapsInitializer.initialize(getActivity());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMinZoomPreference(13);
        updateMap();
    }

    private void updateMap() {
        updateCameraMap();
        addMarker();
    }

    private void addMarker() {
        if (coordinates == null){
           return;
        }
        if(map == null){
            return;
        }
        map.clear();
        map.addMarker(new MarkerOptions()
                .position(coordinates));

    }

    private void updateCameraMap() {
        if(map == null){
            return;
        }
        if(coordinates == null){
            return;
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinates, 13);
        map.animateCamera(cameraUpdate);


    }

    @Override
    public void onDestroy() {
        if(mapView != null){
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        EventBus.getDefault().unregister(this);
        if(mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mapView != null) {
            mapView.onResume();
        }
    }

}
