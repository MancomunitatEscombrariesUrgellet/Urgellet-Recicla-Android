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

package com.smartcitylink.urgellet.helpers;

import android.content.Context;
import android.util.Log;

import com.smartcitylink.urgellet.models.Missatge;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MissatgesSeeder {

    public static ArrayList<Missatge> init(Context context){
        ArrayList<Missatge> missatges = new ArrayList<>();
        JSONArray missatgesJson = new JSONArray();
        try {
            missatgesJson = getJsonArray(context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(missatgesJson == null){
            return missatges;
        }
        try {
            for (int i = 0; i < missatgesJson.length(); i++) {
                Missatge miss = new Missatge();
                miss.setId(missatgesJson.getJSONObject(i).getInt("id"));
                miss.setLink(missatgesJson.getJSONObject(i).getString("link"));
                miss.setMissatge(missatgesJson.getJSONObject(i).getString("missatge"));
                miss.setImatge(missatgesJson.getJSONObject(i).getString("imatge"));
                miss.setFraccio(missatgesJson.getJSONObject(i).getInt("fraccio"));
                missatges.add(miss);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("missatgesSeeder", String.valueOf(missatges.size()));
        return missatges;
    }

    private static JSONArray getJsonArray(Context context) throws IOException {
        //Get Data From Text Resource File Contains Json Data.

        String json = null;
        try {
            InputStream is = context.getResources().getAssets().open("missatges.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();

        }

        Log.v("Text Data", json);
        try {
            return new JSONArray(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
