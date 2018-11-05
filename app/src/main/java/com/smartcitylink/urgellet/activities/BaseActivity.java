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

package com.smartcitylink.urgellet.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.manager.DataManager;
import com.smartcitylink.urgellet.models.Contrato;

public class BaseActivity extends AppCompatActivity {

    public Toolbar toolbar;

    /**
     * Sets Status Bar Transparent
     */
    protected void setStatusBarTransparent(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    /**
     * Goes to Login Activity
     */
    public void goToLoginActivity(){
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Goes to Main Activity
     */
    protected void goToMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    /**
     * Goes to CheckCodiActivity
     */

    protected void goCheckCodiActivity(){
        Intent i = new Intent(this, CheckCodiActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Shows Neutral Alert
     * @param message String
     * @param context Context
     */
    public void showNeutralAlert(String message, Context context){

        new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage(message)
                .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    /**
     * Mètode per canviar de Fragment
     * @param fragmentClass Fragment de destí
     * @param data Informació extra
     */
    public void addFragment(Class fragmentClass, Bundle data) {
        Fragment fragment = null;

        if (fragmentClass != null) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                if (data != null) {
                    fragment.setArguments(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.flContent, fragment);
                fragmentTransaction.commitAllowingStateLoss();
                fragmentTransaction.addToBackStack(fragmentClass.getSimpleName());
            }
        }
    }


    public void fillHeader(View rootView, Context con) {

        //GET CONTRACTE SELECCIONAT
        Contrato contrato = DataManager.getInstance().getDefaultContrato();

        TextView nom = (TextView)rootView.findViewById(R.id.contracte_nom);

        TextView num = (TextView)rootView.findViewById(R.id.contracte_num);

        if (contrato == null){
            nom.setText("");
            num.setText("");
        }
        else{
            nom.setText(contrato.getDescripcion());
            num.setText("Contracte " + contrato.getCodigoC());
        }

    }

    public void goCalendariServei(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }
}
