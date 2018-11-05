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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.activities.BaseActivity;
import com.smartcitylink.urgellet.fragments.ConeixFragment;
import com.smartcitylink.urgellet.fragments.HistorialDescomptesFragment;
import com.smartcitylink.urgellet.helpers.Constants;
import com.smartcitylink.urgellet.models.PerfilItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PerfilListAdapter extends RecyclerView.Adapter<PerfilListAdapter.SimpleViewHolder> {

    private static final int TYPE_DESCUENTO = 1;
    private final Context mContext;
    private List<PerfilItem> mData;

    public void add(PerfilItem p, int position) {
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
        public final TextView header,title,subtitle;
        public final ImageView icon;


        public SimpleViewHolder(View view) {
            super(view);
            header = (TextView) view.findViewById(R.id.header);
            title = (TextView) view.findViewById(R.id.title);
            subtitle = (TextView) view.findViewById(R.id.subtitle);
            icon = (ImageView) view.findViewById(R.id.icon);
        }
    }

    public PerfilListAdapter(Context context, ArrayList<PerfilItem> data) {
        mContext = context;
        if (data != null)
            mData = data;
        else mData = new ArrayList<PerfilItem>();
    }

    public PerfilListAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.perfil_row_item, parent, false);
        return new PerfilListAdapter.SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PerfilListAdapter.SimpleViewHolder holder, int position) {

        final PerfilItem perfilItem = mData.get(position);

        //header
        if (perfilItem.getHeaderTitle() == null){
            holder.header.setVisibility(View.GONE);
        }
        else{
            holder.header.setVisibility(View.VISIBLE);
            holder.header.setText(perfilItem.getHeaderTitle());
        }

        //title
        holder.title.setText(perfilItem.getTitle());

        //subtitle
        if (perfilItem.getSubtitle() == null){
            holder.subtitle.setVisibility(View.GONE);
        }
        else{
            holder.subtitle.setVisibility(View.GONE);
            holder.subtitle.setText(perfilItem.getSubtitle());
        }

        //icon
        Picasso.with(mContext).load(perfilItem.getIcon()).into(holder.icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (perfilItem.getAction()){
                    case Constants.ACTION_PERFIL_RESIDUS:
                        Log.d("PERFIL_ADAPTER", "GORESIDUS");
                        ((BaseActivity)mContext).goCalendariServei("https://admin.smartcity.link/pdf/recollida-mobles-voluminosos-urgellet.pdf");
                        break;
                    case Constants.ACTION_PERFIL_DESCOMPTES:
                        Log.d("PERFIL_ADAPTER", "GODESCOMPTES");
                        ((BaseActivity)mContext).addFragment(HistorialDescomptesFragment.class, null);
                        break;
                    case Constants.ACTION_PERFIL_CONEIX:
                        Log.d("PERFIL_ADAPTER", "GOCONEIX");
                        ((BaseActivity)mContext).addFragment(ConeixFragment.class, null);
                        break;
                    case Constants.ACTION_HISTORIAL_DESCOMPTES:
                        Log.d("PERFIL_ADAPTER", "HISTORIAL_DESCOMPTES");

                        Toast.makeText(mContext, mContext.getString(R.string.downloading), Toast.LENGTH_SHORT).show();
                        //TODO GET RECIBO

                    break;
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

}