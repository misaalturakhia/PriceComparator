package com.misaal.pricecomparator.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Created by Misaal on 21/04/2015.
 */
public abstract class UtilityMethods {


    /**
     * Checks if the phone has any active network connection and returns true or false
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;// There are no active networks.
        } else
            return true;
    }


    /**
     * Shows a short timed toast with the string identified by the given resource id
     * @param context : Context
     * @param resourceId : id of the string resource
     */
    public static void showShortToast(Context context, int resourceId){
        Toast.makeText(context, context.getResources().getString(resourceId), Toast.LENGTH_SHORT).show();
    }


    /**
     * Shows a long lasting toast with the string identified by the given resource id
     * @param context : Context
     * @param resourceId : id of the string resource
     */
    public static void showLongToast(Context context, int resourceId){
        Toast.makeText(context, context.getResources().getString(resourceId), Toast.LENGTH_LONG).show();
    }


    /**
     * Takes an input integer price and returns a string of the format 'Rs. 5,00,000'
     * @param price
     * @return
     */
    public static String createPriceText(int price) {
        DecimalFormat format = new DecimalFormat("#,###");
        String priceText = "Rs. " + format.format(price);
        return priceText;
    }


}
