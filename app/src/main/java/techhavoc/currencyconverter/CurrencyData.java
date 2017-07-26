package techhavoc.currencyconverter;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * Created by Ghost Karl on 12/06/2016.
 */

public class CurrencyData {

    String currencyName;
    String toUSD;

    public String getCuID() {
        return cuID;
    }

    public void setCuID(String cuID) {
        this.cuID = cuID;
    }

    String cuID;

    public CurrencyData(String currencyName, String toUSD, String cuID){

        this.setcurrencyName(currencyName);
        this.setUSD(toUSD);
        this.setCuID(cuID);

    }

    public String getcurrencyName() {
        return currencyName;
    }

    public void setcurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getUSD() {
        return toUSD;
    }

    public void setUSD(String toUSD) {
        this.toUSD = toUSD;
    }


}
