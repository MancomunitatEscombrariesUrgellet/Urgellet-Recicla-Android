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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.smartcitylink.urgellet.BuildConfig;
import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.models.Singleton;

public class AppVersionManage {

    private static final String TAG = AppVersionManage.class.getName();
    private Activity mActivity;

    public AppVersionManage(Activity activity) {
        mActivity = activity;
    }

    public Boolean isMyVersionOld() {

        Boolean oldVersion = false;
        int serverVersionCode = 0;

        int myVersionCode = BuildConfig.VERSION_CODE;
        String returnVersionCode = Singleton.getInstance().getVersion();

        if (returnVersionCode != null) {
            serverVersionCode = Integer.parseInt(returnVersionCode);
        }

        if (returnVersionCode != null && (myVersionCode < serverVersionCode)) {
            oldVersion = true;
        }

        return oldVersion;
    }

    public void updateLinkVersionDialog() {
        final String appPackageName = mActivity.getPackageName();

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setMessage(R.string.key_new_version);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }
}
