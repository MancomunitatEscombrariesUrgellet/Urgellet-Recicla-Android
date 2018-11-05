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

package com.smartcitylink.urgellet.models;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {


    public User() {

    }


    @PrimaryKey
    private int id;
    private int idResponsable;
    private int idPlanificado;
    private int sospechoso;
    private String status;
    private int registradoAct;
    private RealmList<Contrato> contratos = new RealmList<>();
    @Ignore
    private ArrayList<Periodo> periodos = new ArrayList<>();
    private String version;

    public ArrayList<Periodo> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(ArrayList<Periodo> periodos) {
        this.periodos = periodos;
    }

    public int getSospechoso() {
        return sospechoso;
    }

    public RealmList<Contrato> getContratos() {
        return contratos;
    }

    public void setContratos(RealmList<Contrato> contratos) {
        this.contratos = contratos;
    }

    public int getRegistradoAct() {
        return registradoAct;
    }

    public void setRegistradoAct(int registradoAct) {
        this.registradoAct = registradoAct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPlanificado() {
        return idPlanificado;
    }

    public void setIdPlanificado(int idPlanificado) {
        this.idPlanificado = idPlanificado;
    }

    public int getIdResponsable() {
        return idResponsable;
    }

    public void setIdResponsable(int idResponsable) {
        this.idResponsable = idResponsable;
    }

    public int isSospechoso() {
        return sospechoso;
    }

    public void setSospechoso(int sospechoso) {
        this.sospechoso = sospechoso;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
