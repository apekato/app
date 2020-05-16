package a.apkt.service;

import java.math.BigDecimal;

/**
 * Created by elton on 4/21/15.
 */
public class StaticVars {

    public static final String token = "b003f23b0c8cd2ec9424aa299f330e53";

    public static final String APP_NAME = "APEKATO";

    public static final int DECIMAL_SCALE_BTC = 8;
    public static final String FEE_ORDER_TAKER = "1";
    public static final String FEE_ORDER_MAKER = "1";
    public static final BigDecimal MINING_FEES_SATOSHIS = new BigDecimal(20000);
    public static final BigDecimal SATOSHIS_PER_BITCOIN = new BigDecimal(100000000);
    public static final String LIMIT_AMOUNT_MIN_STRING = "0.03";
    public static final String LIMIT_AMOUNT_MAX_STRING = "0.04";
    public static final BigDecimal LIMIT_AMOUNT_MAX = new BigDecimal(LIMIT_AMOUNT_MAX_STRING);
    // LIMIT_AMOUNT_MIN is set less than 0.02 because method toCompare in BigDecimal
    // sees a "less than" conditional as "iqual to".
    public static final BigDecimal LIMIT_AMOUNT_MIN = new BigDecimal(0.0299999);

    public static final String CURRENCY_CODE_BRL = "BRL";
    public static final String CURRENCY_CODE_BTC = "BTC";

    public static final String DIGCERTLIST = "digCertList";

//    public static final int PYMNT_MTHD_ACTIVITY_RESULT = 1001;
//    public static final int PYMNT_MTHD_EDIT_ACTIVITY_RESULT = 1002;
//    public static final int BUY_ACTIVITY_RESULT = 1003;
//    public static final int SELL_ACTIVITY_RESULT = 1004;
//    public static final int WALLET_ACTIVITY_RESULT = 1005;
//    public static final int WALLET_EDIT_ACTIVITY_RESULT = 1006;
//    public static final int TRANSACTION_ACTIVITY_RESULT = 1007;
    public static final int PASSWORD_FORGOT_ACTIVITY_RESULT = 1008;
    public static final int OP_RETURN_ACTIVITY_RESULT = 1009; //PUBLIC LIST CODE
    public static final int OP_RETURN_ACTIVITY_PUBLIC_RESULT = 1010; //PUBLIC LIST CODE
    public static final int SELECT_BITCOIN_WALLET_ACTIVITY_RESULT = 1011;
    public static final int OPEN_DOCUMENT_NOTARIZE_ACTIVITY_RESULT = 1012;
    public static final int NOTARIZE_RESULT = 1013;
    public static final int NOTARIZE_PUBLIC_RESULT = 1014;
    public static final int OPEN_DOCUMENT_VERIFY_NOTARIZATION_ACTIVITY_RESULT = 1015;
    public static final int OPEN_DOCUMENT_DIGITAL_CERTIFICATE_ACTIVITY_RESULT = 1016;
    public static final int DIG_CERT_ACTIVITY_RESULT = 1018;
    public static final int OPEN_DOCUMENT_SIGNATURE_ACTIVITY_RESULT = 1019;

    public static final String WALLET_LIST_SHARED_PREFERENCES =
            "wallet.list.SHARED_PREFERENCES";
    public static final String BANK_LIST_SHARED_PREFERENCES =
            "bank.list.SHARED_PREFERENCES";
    public static final String IDENTIFICATION_SHARED_PREFERENCES =
            "identification.SHARED_PREFERENCES";
    public static final String ORDER_BUY_LIST_SHARED_PREFERENCES =
            "order.buy.list.SHARED_PREFERENCES";
    public static final String ORDER_SELL_LIST_SHARED_PREFERENCES =
            "order.sell.list.SHARED_PREFERENCES";
    public static final String ORDER_TRANSACTION_LIST_SHARED_PREFERENCES =
            "order.transaction.list.SHARED_PREFERENCES";

    public static final String WALLET_LIST_SHARED_PREFERENCES_KEY =
            "wallet.list.KEY_SHARED_PREFERENCES";
    public static final String BANK_LIST_SHARED_PREFERENCES_KEY =
            "bank.list.KEY_SHARED_PREFERENCES";
    public static final String IDENTIFICATION_SHARED_PREFERENCES_KEY =
            "identification.KEY_SHARED_PREFERENCES";
    public static final String ORDER_BUY_LIST_SHARED_PREFERENCESS_KEY =
            "order.buy.list.KEY_SHARED_PREFERENCES";
    public static final String ORDER_SELL_LIST_SHARED_PREFERENCES_KEY =
            "order.sell.KEY_SHARED_PREFERENCES";
    public static final String ORDER_TRANSACTION_LIST_SHARED_PREFERENCES_KEY =
            "order.transaction.KEY_SHARED_PREFERENCES";

    public static final int TITLE_SECTION_DIGITAL_SIGNATURE = 1;
    public static final int TITLE_SECTION_DIGITAL_CERTIFICATE = 2;
    public static final int TITLE_SECTION_WALL = 3;
    public static final int TITLE_SECTION_NOTARIZE = 4;
    public static final int TITLE_SECTION_VERIFY_NOTARIZATION = 5;
    public static final int TITLE_SECTION_EXIT = 6;

//    public static final int TITLE_SECTION_DIGITAL_SIGNATURE_PUBLIC = 1;
//    public static final int TITLE_SECTION_DIGITAL_CERTIFICATE_PUBLIC = 2;
    public static final int TITLE_SECTION_WALL_PUBLIC = 1;
    public static final int TITLE_SECTION_NOTARIZE_PUBLIC = 2;
    public static final int TITLE_SECTION_VERIFY_NOTARIZATION_PUBLIC = 3;
//    To activate Login search keyword ACTIVATELOGIN: uncomment TITLE_SECTION_LOGIN
    public static final int TITLE_SECTION_LOGIN = 4;

}
