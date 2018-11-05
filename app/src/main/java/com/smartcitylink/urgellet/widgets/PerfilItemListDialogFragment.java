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

package com.smartcitylink.urgellet.widgets;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.events.ChangedContratoEvent;
import com.smartcitylink.urgellet.manager.DataManager;
import com.smartcitylink.urgellet.models.Contrato;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PerfilItemListDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_ITEM_COUNT = "item_count";
    public Context ctx;
    //private Listener mListener;

    public static PerfilItemListDialogFragment newInstance(int itemCount) {
        final PerfilItemListDialogFragment fragment = new PerfilItemListDialogFragment();
        final Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, itemCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ctx = getActivity();
        return inflater.inflate(R.layout.fragment_perfilitem_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new PerfilItemAdapter(DataManager.getInstance().getContratos()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
         //   mListener = (Listener) parent;
        } else {
          //  mListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
      //  mListener = null;
        super.onDetach();
    }

    public interface Listener {
        void onPerfilItemClicked(int position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView contracte_nom,contracte_num;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_perfilitem_list_dialog_item, parent, false));
            contracte_nom = (TextView) itemView.findViewById(R.id.contracte_nom);
            contracte_num = (TextView) itemView.findViewById(R.id.contracte_num);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataManager.getInstance().setDefaultContrato(getAdapterPosition());
                    //if (mListener != null) {
                      //  mListener.onPerfilItemClicked(getAdapterPosition());
                    //    dismiss();
                   // }
                    dismiss();
                }
            });
        }

    }

    private class PerfilItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Contrato> contratos = new ArrayList<>();

        PerfilItemAdapter(List<Contrato> contratos) {
            this.contratos = contratos;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
           final Contrato contrato = contratos.get(position);
            Contrato selectedContrato = DataManager.getInstance().getDefaultContrato();

            if (contrato.getIdVivienda().equals(selectedContrato.getIdVivienda())){
                    holder.itemView.setBackgroundColor(getResources().getColor(R.color.grayLight));
            }
            else{
                holder.itemView.setBackgroundDrawable(getResources().getDrawable(R.drawable.contracte_button));
            }
            holder.contracte_nom.setText(contrato.getDescripcion());
            holder.contracte_num.setText("Contracte "+ String.valueOf(contrato.getCodigoC()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new ChangedContratoEvent());
                    //lanzar eventBuss de click contrato, para cargar el loader
                    DataManager.getInstance().setDefaultContrato(contrato.getIdVivienda());
                    dismiss();
                }
            });

        }

        @Override
        public int getItemCount() {
            return contratos.size();
        }

    }

}
