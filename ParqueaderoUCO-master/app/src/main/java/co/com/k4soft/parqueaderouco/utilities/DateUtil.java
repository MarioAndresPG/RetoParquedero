package co.com.k4soft.parqueaderouco.utilities;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String DATE_FORMAT_HORA = "yyyy-MM-dd HH:mm:ss";
    static String DATE_FORMAT = "yyyy-MM-dd";


    public static Date convertStringToDate(String stringDate) {
        Date date = null;
        try {
            DateFormat format = new SimpleDateFormat(DATE_FORMAT_HORA, Locale.ENGLISH);
            date = format.parse(stringDate);
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        return date;
    }


    public static String convertDateToStringNotHour(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT,Locale.ENGLISH);
        return dateFormat.format(date);
    }


    public static String timeFromDates(String fechaInicialString, String fechaFinalString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_HORA, Locale.ENGLISH);
        Date fechaInicial = dateFormat.parse(fechaInicialString);
        Date fechaFinal = dateFormat.parse(fechaFinalString);

        int diferencia = (int) ((fechaFinal.getTime() - fechaInicial.getTime()) / 1000);

        int dias = 0;
        int horas = 0;
        int minutos = 0;
        if (diferencia > 86400) {
            dias = (int) Math.floor(diferencia / 86400);
            diferencia = diferencia - (dias * 86400);
        }
        if (diferencia > 3600) {
            horas = (int) Math.floor(diferencia / 3600);
            diferencia = diferencia - (horas * 3600);
        }
        if (diferencia > 60) {
            minutos = (int) Math.floor(diferencia / 60);
            diferencia = diferencia - (minutos * 60);
        }
        String tiempoCalculado =  dias + " dias, " + horas + " horas, " + minutos + " minutos y " + diferencia + " segundos";
        return tiempoCalculado;

    }
    public static int hoursFromDates(String fechaInicialString, String fechaFinalString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_HORA, Locale.ENGLISH);
        Date fechaInicial = dateFormat.parse(fechaInicialString);
        Date fechaFinal = dateFormat.parse(fechaFinalString);

        int diferencia = (int) ((fechaFinal.getTime() - fechaInicial.getTime()) / 1000);

        int dias = 0;
        int horas = 0;
        int minutos = 0;
        if (diferencia > 86400) {
            dias = (int) Math.floor(diferencia / 86400);
            diferencia = diferencia - (dias * 86400);
        }
        if (diferencia > 3600) {
            horas = (int) Math.floor(diferencia / 3600);
            diferencia = diferencia - (horas * 3600);
        }
        if (diferencia > 60) {
            minutos = (int) Math.floor(diferencia / 60);
            diferencia = diferencia - (minutos * 60);
        }
        int tiempoCalculado=horas+dias*24;
        if (minutos>0){
            tiempoCalculado++;
        }
        return tiempoCalculado;

    }


    public  static String getCurrenDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_HORA,Locale.ENGLISH);
        Date date = new Date();
        return dateFormat.format(date);
    }

}
