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
import android.os.Build;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.smartcitylink.urgellet.R;
import com.smartcitylink.urgellet.helpers.Utils;
import com.smartcitylink.urgellet.manager.DataManager;

import java.lang.reflect.Field;

public class UrgelletBottomNavigationView extends BottomNavigationView {

    private static final Object TAG = "COUNTER";
    private Context context;

    public UrgelletBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        centerMenuIcon();
        updateBadge();

    }

    /**
     * Deletes the badge
     */
    public void deleteBadge() {
        BottomNavigationMenuView menuView = getBottomMenuView();
        if (menuView != null) {
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView menuItemView = (BottomNavigationItemView) menuView.getChildAt(i);
                if (menuItemView.getId() == R.id.action_notifications) {

                    TextView txt = (TextView) menuItemView.findViewWithTag(TAG);
                    if (txt == null){
                       return;
                    }
                    menuItemView.removeView(txt);
                }
            }
        }
    }

    /**
     * Updates de Badge
     */
    public void updateBadge() {
        BottomNavigationMenuView menuView = getBottomMenuView();
        if (menuView != null) {
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView menuItemView = (BottomNavigationItemView) menuView.getChildAt(i);
                if (menuItemView.getId() == R.id.action_notifications) {
                    int newNoti = DataManager.getInstance().getNewNotifications();
                    if (newNoti == 0){
                        deleteBadge();
                        return;
                    }
                    TextView txt = (TextView) menuItemView.findViewWithTag(TAG);
                    if (txt == null){
                        txt = createTextView();
                        menuItemView.addView(txt);
                    }
                    txt.setText(String.valueOf(newNoti));

                }
            }
        }
    }

    /**
     * Creates the Badge TextView
     * @return TextView
     */
    private TextView createTextView() {
        TextView txt = new TextView(context);
        txt.setTag(TAG);
        txt.setId(R.id.notificacionsBuit);
        LayoutParams paramsCounter = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        paramsCounter.height = Utils.dpToPx(context,20);
        paramsCounter.width = Utils.dpToPx(context,20);
        paramsCounter.gravity = Gravity.CENTER_HORIZONTAL;
        paramsCounter.leftMargin = Utils.dpToPx(context,20);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setLayoutParams(paramsCounter);
        txt.setTextSize(13);
        txt.setText("");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            txt.setTextColor(context.getResources().getColor(R.color.white, null));
        }
        else{
            txt.setTextColor(context.getResources().getColor(R.color.white));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            txt.setBackground(context.getResources().getDrawable(R.drawable.counter_bg, null));

        }
        else{
            txt.setBackground(context.getResources().getDrawable(R.drawable.counter_bg));

        }

        return txt;
    }

    /**
     * Centers the Menu Icon
     */
    private void centerMenuIcon() {
        BottomNavigationMenuView menuView = getBottomMenuView();

        if (menuView != null) {
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView menuItemView = (BottomNavigationItemView) menuView.getChildAt(i);

                AppCompatImageView icon = (AppCompatImageView) menuItemView.getChildAt(0);
                icon.setPadding(0,0,0,0);
                LayoutParams params = (LayoutParams) icon.getLayoutParams();
                params.gravity = Gravity.CENTER;
                params.height = 100;
                params.setMargins(0,0,0,0);

                menuItemView.setShiftingMode(false);
            }
        }
    }

    /**
     * Gets the View
     * @return BottomNavigationMenuView
     */
    private BottomNavigationMenuView getBottomMenuView() {
        Object menuView = null;
        try {
            Field field = BottomNavigationView.class.getDeclaredField("mMenuView");
            field.setAccessible(true);
            menuView = field.get(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }

        return (BottomNavigationMenuView) menuView;
    }



}