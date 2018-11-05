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

package com.smartcitylink.urgellet.migrations;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class UrgelletMigration implements RealmMigration {


    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        Log.d("UrgelletMigration", "oldVersion -->" + String.valueOf(oldVersion));
        Log.d("UrgelletMigration", "newVersion -->" + String.valueOf(newVersion));

        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            try{
                schema.get("RegistrarOffline")
                        .addField("image", byte[].class);
            }catch(Exception e){
                Log.e("UrgelletMigration", "1 -->" + e.getMessage());
            }


        }

      //  if (oldVersion < 1) {
        try{
            schema.get("RegistrarOffline")
                    .addField("processing", boolean.class);
        }
        catch(Exception e){
            Log.e("UrgelletMigration", "1 -->" + e.getMessage());
        }

     //   }

      //  if (oldVersion < Constants.DB_VERSION) {
            try{
                schema.get("RegistrarOffline")
                        .addField("startProcessingTimestamp", int.class);
            }
            catch(Exception e){
                Log.e("UrgelletMigration", "2 -->" + e.getMessage());
            }

       // }
        try{
            schema.get("ActuacioSaldo").removeField("timestamp");

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            RealmObjectSchema actuacioSaldoSchema = schema.create("ActuacioSaldo");

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            if (!schema.get("ActuacioSaldo").hasPrimaryKey() && schema.get("ActuacioSaldo").hasField("id")){
                schema.get("ActuacioSaldo")
                        .removeField("id");
            }
        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            schema.get("ActuacioSaldo")
                    .addField("id", int.class, FieldAttribute.PRIMARY_KEY);

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            schema.get("ActuacioSaldo")
                    .removeField("timestamp");

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            schema.get("ActuacioSaldo")
                    .addField("timestamp", String.class);

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            schema.get("ActuacioSaldo")
                    .addField("nfc", int.class);

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            schema.get("ActuacioSaldo")
                    .addField("fraccion", int.class);

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            schema.get("Descuento")
                    .addRealmListField("actuacions",  schema.get("ActuacioSaldo"));

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            schema.get("Notificacio")
                    .addField("tipo", int.class);

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "3 -->" + e.getMessage());
        }

        try{
            RealmObjectSchema missatgesSchema = schema.create("Missatge");

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            RealmObjectSchema missatgesSchema = schema.get("Missatge");
            missatgesSchema.addField("id", int.class);
            missatgesSchema.addPrimaryKey("id");


        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            RealmObjectSchema missatgesSchema = schema.get("Missatge");
            missatgesSchema.addField("link", String.class);
            missatgesSchema.addField("missatge", String.class);
            missatgesSchema.addField("imatge", String.class);
            missatgesSchema.addField("shown", Boolean.class).isNullable("shown");
            missatgesSchema.addField("fraccio", int.class);

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            RealmObjectSchema missatgesSchema = schema.get("Missatge");
            missatgesSchema.isNullable("shown");
            missatgesSchema.addField("fraccio", int.class);

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            RealmObjectSchema contratoSchema = schema.create("Contrato");

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            RealmObjectSchema contratoSchema = schema.get("Contrato");
            if (contratoSchema != null) {
                contratoSchema.addField("idVivienda", Integer.class);
                contratoSchema.addField("descripcion", String.class);
            }
        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            RealmObjectSchema userSchema = schema.get("User");
            RealmObjectSchema contratoSchema = schema.get("Contrato");

            if (userSchema != null) {
                userSchema.addRealmListField("contratos",contratoSchema);
            }
        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            RealmObjectSchema SalariDeixalleriaSchema = schema.create("SalariDeixalleria");
            RealmObjectSchema DescuentoDeixalleriaSchema = schema.create("DescuentoDeixalleria");

        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            RealmObjectSchema SalariDeixalleriaSchema = schema.get("SalariDeixalleria");
            if (SalariDeixalleriaSchema != null) {
                SalariDeixalleriaSchema.addField("id", int.class, FieldAttribute.PRIMARY_KEY);
                SalariDeixalleriaSchema.addField("saldoInicial", String.class);
                SalariDeixalleriaSchema.addField("saldoActual", String.class);

            }
        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            RealmObjectSchema DescuentoDeixalleriaSchema = schema.get("DescuentoDeixalleria");
            if (DescuentoDeixalleriaSchema != null) {
                DescuentoDeixalleriaSchema.addField("id", int.class, FieldAttribute.PRIMARY_KEY);
                DescuentoDeixalleriaSchema.addField("fecha", String.class);
                DescuentoDeixalleriaSchema.addField("cantidad", String.class);
                DescuentoDeixalleriaSchema.addField("descripcion", String.class);

            }
        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try{
            RealmObjectSchema SalariDeixalleriaSchema = schema.get("SalariDeixalleria");
            if (SalariDeixalleriaSchema != null) {
                RealmObjectSchema descuentoSchema = schema.get("DescuentoDeixalleria");

                if (descuentoSchema != null) {
                    SalariDeixalleriaSchema.addRealmListField("descuentos",descuentoSchema);
                }

            }
        }
        catch(Exception e){
            Log.e("UrgelletMigration", "2 -->" + e.getMessage());
        }

        try {
            RealmObjectSchema userSchema = schema.get("User");
            if (userSchema != null) {
                userSchema.addField("version", String.class);
            }
        }
        catch(Exception e){
            Log.e("UrgelletMigration", e.getMessage());
        }

        try {
            RealmObjectSchema contratoSchema = schema.get("Contrato");
            if (contratoSchema != null) {
                contratoSchema.addField("codigoC", String.class);
            }
        }
        catch(Exception e){
            Log.e("UrgelletMigration", e.getMessage());
        }

    }
}
