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


import com.smartcitylink.urgellet.BuildConfig;

public class Constants {

    public static final long DB_VERSION = 11;

    public static final int REGISTRAR_TIPUS_LOCALITZACIO = 3;
    public final static int REGISTRAR_TIPUS_QR = 2;
    public final static int REGISTRAR_TIPUS_NFC = 1;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static final String SHARED_NAME = "urgellet_shared";
    public static final String SHARED_LOGGED = "loggedIn";
    public static final String SHARED_TOKEN = "auth_token";
    public static final String SHARED_REFRESH_TOKEN = "auth_refresh";
    public static final String SHARED_TYPE = "reg_type";

    public static final int FRACCIO_RESTA = 1;
    public static final int FRACCIO_ORGANICA = 4;
    public static final int FRACCIO_ENVASOS = 15;
    public static final int FRACCIO_PAPER = 27;
    public static final int FRACCIO_VIDRE = 33;
    public static final int FRACCIO_DEIXALLERIA = 34;
    public static final int FRACCIO_GENERAL = 35;


    public static final String REG_TYPE = "REGISTER_TYPE";
    public static final String REG_TYPE_INCIDENCIES = "INCIDENCIES";
    public static final String REG_TYPE_DEIXALLA = "DEIXALLA";

    //PERMISSIONS
    public static final int PERMISSIONS_GEO = 1234;
    public final static int PERMISSIONS_FROM_FOTO_CHOOSER = 11;
    public final static int PERMISSIONS_FROM_TAB_CLICK = 12;
    public final static int PERMISSIONS_FROM_SINGLE_LOCATION = 13;
    public final static int PERMISSIONS_FROM_TAB_CLICK_INCIDENCIA = 112;
    public final static int PERMISSIONS_FROM_TAB_CLICK_INICIO = 121;
    public final static int PERMISSIONS_FROM_INIT_GEO = 1122;
    public final static int PERMISSIONS_STORAGE = 1133;

    //TIMERS
    public static final long QR_TIMER_MILLISECONDS = 5000;
    public static final long NFC_TIMER_MILLISECONDS = 15000;


    public static final String FROM_CHECK = "FROM_CHECK";

    public static final String RECIBO_NAME = "recibo.pdf";

    public static final String SHARED_TIMES = "times_shared";


    //PERFIL
    public static final String ACTION_PERFIL_RESIDUS = "action_perfil_residus";
    public static final String ACTION_PERFIL_DESCOMPTES = "action_perfil_descomptes";
    public static final String ACTION_PERFIL_CONEIX = "action_perfil_coneix";
    public static final String SHARED_CONTRATO = "shared_contrato";
    public static final String ACTION_HISTORIAL_DESCOMPTES = "action_historial_descomptes";
    public static final String IS_DEIXALLERIA = "is_deixalleria";

}
