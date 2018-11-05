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

package com.smartcitylink.urgellet.mobileinfo.model;

public class ApHeaderModel {

    private Object versionCode;
    private Object model;
    private Object sdkVersion;
    private Object versionName;
    private Object manufacturer;
    private Object appName;
    private Object os;

    public Object getOs() {
        return os;
    }

    public void setOs(Object os) {
        this.os = os;
    }

    public Object getAppName() {
        return appName;
    }

    public void setAppName(Object appName) {
        this.appName = appName;
    }

    public Object getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Object versionCode) {
        this.versionCode = versionCode;
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    public Object getVersionName() {
        return versionName;
    }

    public void setVersionName(Object versionName) {
        this.versionName = versionName;
    }

    public Object getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Object manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Object getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(Object sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    @Override
    public String toString() {
        return "{\"versionCode\": \"" + this.versionCode + "\",\"appName\": \"" + this.appName + "\",\"os\": \"" + this.os + "\",\"manufacturer\": \"" + this.manufacturer +  "\", \"versionName\": \"" + this.versionName + "\", \"model\": \"" + this.model + "\", \"sdkVersion\": \"" + this.sdkVersion + "\"}";
    }
}
