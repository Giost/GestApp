package com.mcteam.gestapp.Utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ParseException;
import android.support.v7.app.AppCompatActivity;

import com.mcteam.gestapp.Application.MyApp;
import com.mcteam.gestapp.Moduli.Login.LoginActivity;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Created by Riccardo Rossi on 12/01/2016.
 */
public class Functions {

    private static final DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    public static boolean validateDate(final String date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        //dateFormat.setLenient(false);
        try {
            //dateFormat.parse(date.trim());
            dateFormat.parse(date);
        } catch (java.text.ParseException e) {
            return false;
        }
        return true;
    }

    public static boolean validateReverseDate(final String date) {

        Date parsedDate;
        try {
            parsedDate = java.sql.Date.valueOf(date);
        } catch (Exception e) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedDate);

        return calendar.get(Calendar.YEAR) < 1900;
    }

    public static String getFormattedDate(String sqlDateFormat) throws ParseException {

        Date parsedDate = java.sql.Date.valueOf(sqlDateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedDate);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

        return format1.format(calendar.getTime());

    }

    public static boolean isDateValid(String stringDate) {
        try {
            Date date = df.parse(stringDate);
            if (!stringDate.equals(df.format(date)))
                return false;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String fromDateToSql(String date) throws java.text.ParseException {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        java.util.Date parsed = format.parse(date);
        java.sql.Date sql = new java.sql.Date(parsed.getTime());
        return sql.toString();
    }

    public static <T> List<T> union(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<>();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    public static boolean validateNormalDate(final String date) {


        final String DATE_VALIDATION_PATTERN = "(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)";

        Pattern pattern = Pattern.compile(DATE_VALIDATION_PATTERN);

        Matcher matcher = pattern.matcher(date);


        System.out.println("ValidateNormalDate -> before matches");

        if (matcher.matches()) {
            System.out.println("ValidateNormalDate -> matches");

            matcher.reset();

            if (matcher.find()) {

                System.out.println(" DD -> " + matcher.group(1) + " MM -> " + matcher.group(2) + "YYYY ->" + matcher.group(3));

                String dd = matcher.group(1);
                String mm = matcher.group(2);
                int yy = Integer.parseInt(matcher.group(3));

                if (dd.equals("31") && (mm.equals("4") || mm.equals("6") || mm.equals("9") ||
                        mm.equals("11") || mm.equals("04") || mm.equals("06") ||
                        mm.equals("09"))) {
                    return false;
                } else if (mm.equals("2") || mm.equals("02")) {

                    if (yy % 4 == 0) {
                        return !(dd.equals("30") || dd.equals("31"));
                    } else {
                        return !(dd.equals("29") || dd.equals("30") || dd.equals("31"));
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public static String format(float f) {
        NumberFormat nb = NumberFormat.getInstance(Locale.GERMAN);
        nb.setMinimumFractionDigits(2);
        nb.setMaximumFractionDigits(2);

        return nb.format(f);
    }

    public static float parse(String s) {
        NumberFormat nb = NumberFormat.getInstance(Locale.GERMAN);
        try {
            return nb.parse(s).floatValue();
        } catch (java.text.ParseException e) {
            return Float.parseFloat(s);
        }
    }

    public static boolean strDateGreaterThan(String date1, String date2) {
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date newDate1, newDate2;
        try {
            newDate1 = df.parse(date1);
            newDate2 = df.parse(date2);
        } catch (java.text.ParseException e) {
            System.out.println("Stringhe inserite non sono delle date valide");
            return false;
        }

        return newDate2.compareTo(newDate1) >= 0;
    }

    /**
     * Metodo per fare il logout, si viene mandati alla pagina di login
     * @param oggetto  da cui viene fatto il logout
     */
    public static void logout(AppCompatActivity oggetto) {
        ((MyApp) oggetto.getApplication()).setCurrentUser(null); //Remove user in MyApp
        Intent goLogin = new Intent(oggetto, LoginActivity.class);
        goLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        oggetto.startActivity(goLogin);
        oggetto.finish();
    }
}
