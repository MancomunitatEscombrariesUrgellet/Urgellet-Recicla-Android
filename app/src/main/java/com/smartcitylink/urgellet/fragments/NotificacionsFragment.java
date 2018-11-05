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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.adapters.NotificacionsListAdapter;
import com.smartcitylink.urgellet.events.NotificationsSuccessEvent;
import com.smartcitylink.urgellet.manager.DataManager;
import com.smartcitylink.urgellet.models.Notificacio;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class NotificacionsFragment extends Fragment {


    View rootView;
    private RecyclerView notificationsList;
    private NotificacionsListAdapter notificationsListAdapter;
    private ArrayList<Notificacio> notificationsArr = new ArrayList<>();
    private RelativeLayout notificacionsBuit;

    public NotificacionsFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_notificacions, container, false);
        notificacionsBuit  = (RelativeLayout) rootView.findViewById(R.id.notificacionsBuit);
        notificacionsBuit.setVisibility(View.GONE);
        notificationsList = (RecyclerView) rootView.findViewById(R.id.notificacionsList);

        setupList();
        fillScreen();



        return rootView;
    }


    /**
     * Fills the list
     */
    private void fillScreen() {
        notificationsArr.clear();
        notificationsArr.addAll(DataManager.getInstance().getNotificacions());
        notificationsListAdapter.notifyDataSetChanged();
        if (notificationsArr.isEmpty()){
            notificacionsBuit.setVisibility(View.VISIBLE);
        }
        else{
            DataManager.getInstance().setReadedNotifications();
            notificacionsBuit.setVisibility(View.GONE);
        }

    }


    /**
     * Setups the list
     */
    private void setupList() {

        notificationsList.setHasFixedSize(true);
        notificationsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        notificationsListAdapter = new NotificacionsListAdapter(getActivity(),notificationsArr);
        notificationsList.setAdapter(notificationsListAdapter);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Handles Notification Success Event
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccessEvent(NotificationsSuccessEvent event){
        if (event.success){
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
        //TODO NOTIFICACIONS
    }
}
