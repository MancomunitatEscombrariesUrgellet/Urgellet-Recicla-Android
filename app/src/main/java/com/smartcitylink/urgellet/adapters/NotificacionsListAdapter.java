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

package com.smartcitylink.urgellet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.models.Notificacio;

import java.util.ArrayList;
import java.util.List;

public class NotificacionsListAdapter extends RecyclerView.Adapter<NotificacionsListAdapter.SimpleViewHolder> {

    private static final int TYPE_DESCUENTO = 1;
    private final Context mContext;
    private List<Notificacio> mData;

    public void add(Notificacio p, int position) {
        position = position == -1 ? getItemCount() : position;
        mData.add(position, p);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        if (position < getItemCount()) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView text,data;


        public SimpleViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.text);
            data = (TextView) view.findViewById(R.id.data);


        }
    }

    public NotificacionsListAdapter(Context context, ArrayList<Notificacio> data) {
        mContext = context;
        if (data != null)
            mData = data;
        else mData = new ArrayList<Notificacio>();
    }

    public NotificacionsListAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.notificacio_row_item, parent, false);
        return new NotificacionsListAdapter.SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotificacionsListAdapter.SimpleViewHolder holder, int position) {

        final Notificacio notificacio = mData.get(position);

        //setmana
        holder.text.setText(notificacio.getText());

        //salari
        holder.data.setText(notificacio.getData());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notificacio.getTipo() == TYPE_DESCUENTO){
                    Toast.makeText(mContext, mContext.getString(R.string.downloading), Toast.LENGTH_SHORT).show();
                    //TODO GET RECIBO
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}