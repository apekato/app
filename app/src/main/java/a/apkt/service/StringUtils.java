package a.apkt.service;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;

import a.apkt.R;

/**
 * Created by elton on 07/04/15.
 */
public class StringUtils {

    public static BigDecimal satoshiToBtc(BigDecimal satoshi){

        return satoshi.divide(StaticVars.SATOSHIS_PER_BITCOIN);
    }

    public static String formatTitleCurrencyCode(String title, String currencyCode) {
//        String currencyCode = getCurrencyCode();
        return concatTitleCurrencyCode(title, currencyCode);
    }

    public static void formatTitleCurrencyCodeBTC(Activity activity, TextView tv, String fee) {

        String currencyCodeBTC = activity.getResources().getString(R.string.currency_code_btc);
        String feeTitle = activity.getResources().getString(R.string.fee_title);
        feeTitle = StringUtils.currencyMaskBTCFeeTitle(fee, feeTitle);
        feeTitle = concatTitleCurrencyCode(feeTitle, currencyCodeBTC);
        tv.setText(feeTitle);

    }

    public static String concatTitleCurrencyCode(String title, String currencyCode){
        return title.concat(" (".concat(currencyCode).concat("):"));
    }

    public static String getCurrencyCode () {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String currencyCode = nf.getCurrency().getCurrencyCode();
        return currencyCode;
    }

    public static void currencyMaskBTC(Activity activity, String str, TextView tv) {

        /*int integerIdx = str.indexOf(".");
        String integerSubStr = str.substring(0, integerIdx);
        String decimalSubStr = str.substring(integerIdx);*/

        String symbolBTC = activity.getResources().getString(R.string.symbol_btc);

        DecimalFormat formatter = new DecimalFormat("###,##0.00000000");
        Double strDouble = Double.parseDouble(str);
        String integerStr = formatter.format(strDouble);
        tv.setText(symbolBTC.concat(integerStr));

    }

    public static void currencyMask(CharSequence s, TextView campo, String currency) {
        currencyMaskSubMethod(s, campo, currency);

    }

    // created submethod because this method cannot return a TextView because
    // it has a return inside "if (isUpdating)", which messes up a method that
    // returns something
    private static void currencyMaskSubMethod(CharSequence s, TextView campo, String currency) {

        boolean isUpdating = false;

        // Pega a formatacao do sistema, se for brasil R$ se EUA US$
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setCurrency(Currency.getInstance(currency));
        nf.setRoundingMode(RoundingMode.HALF_DOWN);



//        if (!nf.getCurrency().getCurrencyCode().equals(StaticVars.CURRENCY_CODE_BRL)){
//            nf.setCurrency(Currency.getInstance(Locale.US));
//        }

        // Evita que o método seja executado varias vezes.
        // Se tirar ele entre em loop
        if (isUpdating) {
            isUpdating = false;
            return;
        }

        isUpdating = true;
        String str = s.toString();
        // Verifica se já existe a máscara no texto.
        boolean hasMask = (str.indexOf(nf.getCurrency().getSymbol()) > -1 &&
                (str.indexOf(".") > -1 || str.indexOf(",") > -1));
        // Verificamos se existe máscara
        if (hasMask) {
            // Retiramos a máscara.
            str = str.replaceAll("[nf.getCurrency().getSymbol()]", "")
                    .replaceAll("[,]", "")
                    .replaceAll("[.]", "");
        }

        try {
            // Transformamos o número que está escrito no EditText em
            // monetário.
            str = nf.format(Double.parseDouble(str));
            /*str = str.replace("R$", "").replace("$","");*/
            campo.setText(str);
                /*campo.setSelection(campo.getText().length());*/
        } catch (NumberFormatException e) {
            s = "";
        }
    }

    public static String localCurrencyFormat(String value){
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        try {
            value = nf.format(Double.parseDouble(value));
            int i = value.indexOf("0");
            value = value.substring(i);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;

    }

    public static String currencyMaskBTCFeeTitle(CharSequence s, String title) {

        String str = s.toString();

        // Pega a formatacao do pais do sistema
        NumberFormat nf = NumberFormat.getCurrencyInstance();

        try {

            // format the string to country's currency
            str = nf.format(Double.parseDouble(str));

            // get string index where number "1" is located
            int i = str.indexOf("1");

            // get substring without country's currency symbol
            str = str.substring(i);

            if (str.contains("00")) {
                str = str.replace(",00", "%");
                str = str.replace(".00", "%");
            } else if (str.contains("0")) {
                str = str.replace("0", "%");
            } else {
                str = str.concat("%");
            }

            str = title + " " + str;

        } catch (NumberFormatException e) {
            str = "";
        }

        return str;
    }

    public static String stripsNonNumeralCharsFromMobNum(String mobNum) {

        // strips away non-numeral characters
        mobNum = mobNum.replace("(", "");
        mobNum = mobNum.replace(")", "");
        mobNum = mobNum.replace("-", "");

        return mobNum;

    }

    public static String getSubStringMobileNumState(String mobNum) {

        mobNum = mobNum.substring(0, 2);

        return mobNum;

    }

    public static String getSubStringMobileNum(String mobNum) {

        mobNum = mobNum.substring(2);

        return mobNum;

    }

    public static String formatJsonSpecialCharacters(String gson) {

        // http://www.w3schools.com/tags/ref_gsonencode.asp
        // replacing "%" with "%25" must be located before
        // before all the other replace methods that contain
        // %XX, because, otherwise, it will end up replacing
        // the "%" of all other already replaced characters.
        gson = gson.replace("%", "%25");

        gson = gson.replace(" ", "%20");
        gson = gson.replace("\"", "%22");
        gson = gson.replace("{", "%7B");
        gson = gson.replace("}", "%7D");

        // the character "&" gets replaced by "\u0026",
        // when the string is processed on Android.
        // So it must be replaced by "%26", in order
        // to send it via web service.
        // The same applies to all others with
        // similar formats bellow.
        gson = gson.replace("\\u0026", "%26"); // &
        gson = gson.replace("\\u003d", "%3D"); // =
        gson = gson.replace("\\u003c", "%3C"); // <
        gson = gson.replace("\\u003e", "%3E"); // >
        gson = gson.replace("\\u0027", "%27"); // '

        gson = gson.replace("/", "");
        gson = gson.replace("\\", "");
        gson = gson.replace("#", "%23");
        gson = gson.replace("?", "%3F");
        gson = gson.replace(";", "%3B");

        gson = gson.replace("|", "%7C");
        gson = gson.replace("`", "%60");
        gson = gson.replace("^", "%5E");

        return gson;
    }

    // used for password creation
    public static final boolean containsDigit(final String s) {
        boolean containsDigit = false;

        if (s != null) {
            for (char c : s.toCharArray()) {
                if (Character.isDigit(c)) {
                    containsDigit = true;
                    break;
                }
            }
        }

        return containsDigit;
    }

    // used for password creation
    public static final boolean containsUpperCase(final String s) {
        boolean containsUpperCase = false;

        if (s != null) {
            for (char c : s.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    containsUpperCase = true;
                    break;
                }
            }
        }

        return containsUpperCase;
    }

    // used for password creation
    public static boolean containsSpecialCharacter(String s) {
        boolean containsSpecialCharacter = false;

        if (s != null) {
            for (char c : s.toCharArray()) {

                if (Character.isLetter(c)) {

                } else if (Character.isDigit(c)) {

                } else {
                    containsSpecialCharacter = true;
                }
            }
        }

        return containsSpecialCharacter;
    }

    public static String getLanguage(Context context){
        String locale = context.getResources().getConfiguration().locale.getLanguage();
        return locale;
    }

    public static String parseStringToDate (String dateInString){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date = null;
        try {
            date = formatter.parse(dateInString.replaceAll("Z$", "+0000"));
        } catch (ParseException e) {

        }
        return date.toLocaleString();
    }
}
