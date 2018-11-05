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
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.pavlospt.CircleView;
import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.helpers.Utils;
import com.smartcitylink.urgellet.models.ActuacioSaldo;
import com.smartcitylink.urgellet.models.Descuento;
import com.smartcitylink.urgellet.models.DescuentoDeixalleria;

import java.util.ArrayList;
import java.util.List;

public class SalariListAdapter extends RecyclerView.Adapter<SalariListAdapter.SimpleViewHolder> {

    private final Context mContext;
    private List<Object> mData;

    public SalariListAdapter(Context context, ArrayList<Object> data) {
        this.mContext = context;
        if (data != null)
            mData = data;
        else mData = new ArrayList<Object>();

    }

    public void add(Descuento p, int position) {
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
        public final TextView semana,salari;
        public final LinearLayout actuacionesContainer;


        public SimpleViewHolder(View view) {
            super(view);
            semana = (TextView) view.findViewById(R.id.semana);
            salari = (TextView) view.findViewById(R.id.salari);
            actuacionesContainer = (LinearLayout) view.findViewById(R.id.actuacionesContainer);


        }
    }

    public SalariListAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.salari_row_item, parent, false);
        return new SalariListAdapter.SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, int position) {

        if (mData.get(position) instanceof Descuento){
            final Descuento salari = (Descuento) mData.get(position);
            //setmana
            holder.semana.setText(salari.getSemana());
            //salari
            holder.salari.setText(salari.getSalari());

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            holder.actuacionesContainer.removeAllViews();
            for (ActuacioSaldo actuacioSaldo : salari.getActuacions()){
                View view = inflater.inflate(R.layout.salari_act_item,null);
                TextView tx = (TextView) view.findViewById(R.id.semana);

                int fraccion = Utils.getFraccionName(actuacioSaldo.getFraccion());
                tx.setText(mContext.getString(fraccion));

                CircleView circle = (CircleView) view.findViewById(R.id.boleta);
                int color = Utils.getFraccionColor(actuacioSaldo.getFraccion());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    circle.setFillColor(mContext.getResources().getColor(color, null));
                }
                else{
                    circle.setFillColor(mContext.getResources().getColor(color));

                }
                TextView tx2 = (TextView) view.findViewById(R.id.salari);
                tx2.setText(actuacioSaldo.getTimestamp());
                holder.actuacionesContainer.addView(view);
            }

            holder.actuacionesContainer.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (salari.getActuacions().size() == 0){
                        return;
                    }
                    if (holder.actuacionesContainer.getVisibility() == View.VISIBLE){
                        holder.actuacionesContainer.setVisibility(View.GONE);
                        holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                    }
                    else{
                        holder.actuacionesContainer.setVisibility(View.VISIBLE);
                        holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.accentLight));
                    }

                }
            });
        }
        else if(mData.get(position) instanceof DescuentoDeixalleria){
            final DescuentoDeixalleria salari = (DescuentoDeixalleria) mData.get(position);
            //setmana
            holder.semana.setText(salari.getFecha());
            //salari
            holder.salari.setText(salari.getCantidad());

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            holder.actuacionesContainer.removeAllViews();

                View view = inflater.inflate(R.layout.salari_act_item,null);
                TextView tx = (TextView) view.findViewById(R.id.semana);

                tx.setText(salari.getDescripcion());

                CircleView circle = (CircleView) view.findViewById(R.id.boleta);
                int color = R.color.black;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    circle.setFillColor(mContext.getResources().getColor(color, null));
                }
                else{
                    circle.setFillColor(mContext.getResources().getColor(color));

                }
                TextView tx2 = (TextView) view.findViewById(R.id.salari);
                tx2.setVisibility(View.INVISIBLE);
                holder.actuacionesContainer.addView(view);


            holder.actuacionesContainer.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.actuacionesContainer.getVisibility() == View.VISIBLE){
                        holder.actuacionesContainer.setVisibility(View.GONE);
                        holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                    }
                    else{
                        holder.actuacionesContainer.setVisibility(View.VISIBLE);
                        holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.accentLight));
                    }

                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}