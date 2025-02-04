package co.yodo.launcher.utils;

import co.yodo.restapi.YodoApi;

/**
 * Created by luis on 15/12/14.
 * Keys and defaults
 */
public class AppConfig {
    /** ID of the shared preferences file */
    public static final String SHARED_PREF_FILE = "YodoRocketSharedPref";

    /**
     * Keys used with the Shared Preferences (SP) and default values.
     * {{ ======================================================================
     */

    /* Device state: legacy or not.
	 * type -- Boolean
	 *
	 * __Values__
	 * true  -- The device is legacy (doesn't have google services)
	 * false -- The device supports google services
	 */
    static final String SPREF_LEGACY = "SPLegacy";

    /* Login status.
	 * type -- Boolean
	 *
	 * __Values__
	 * true  -- Application registered and authorized
	 * false -- Application not registered or authorized
	 */
    static final String SPREF_LOGIN_STATE = "SPLoginState";

    /* First Login status.
	 * type -- Boolean
	 *
	 * __Values__
	 * true  -- First time that the user is logged in
	 * false -- It was already logged in several times
	 */
    static final String SPREF_FIRST_LOGIN = "SPFirstLogin";

    /* The current logo url.
    * type -- String
    */
    static final String SPREF_CURRENT_LOGO = "SPCurrentLogo";

    /* The current language.
	 * type -- Integer
	 */
    public static final String SPREF_CURRENT_LANGUAGE = "SPCurrentLanguage";

    /* The current beacon.
    * type -- String
    */
    public static final String SPREF_CURRENT_BEACON = "SPCurrentBeacon";

    /* The current ic_discount to the purchase.
    * type -- String
    */
    static final String SPREF_DISCOUNT = "SPDiscount";

    /* The current currency.
    * type -- Integer
    */
    static final String SPREF_CURRENT_CURRENCY = "SPCurrentCurrency";

    /* The current currency.
    * type -- Integer
    */
    static final String SPREF_MERCHANT_CURRENCY = "SPMerchantCurrency";

    /* The current scanner position.
    * type -- Integer
    */
    static final String SPREF_CURRENT_SCANNER = "SPCurrentScanner";

    /* Cash payments status.
	 * type -- Boolean
	 *
	 * __Values__
	 * true  -- Accept cash payments
	 * false -- Don't Accept cash payments
	 */
    static final String SPREF_PAYMENT_CASH = "SPYodoPaymentCash";

    /* Advertising service status.
	 * type -- Boolean
	 *
	 * __Values__
	 * true  -- Service is running
	 * false -- Service not running
	 */
    public static final String SPREF_ADVERTISING_SERVICE = "SPAdvertisingService";

    /* Location service status.
	 * type -- Boolean
	 *
	 * __Values__
	 * true  -- Service is running
	 * false -- Service not running
	 */
    public static final String SPREF_LOCATION_SERVICE = "SPLocationService";

    /*
     * Resets everything
	 */
    public static final String SPREF_RESET = "SPReset";

    /* The current background of the Rocket.
    * type -- Integer
    */
    static final String SPREF_PORTRAIT_MODE = "SPPortraitMode";

    /* The current background of the Rocket.
    * type -- Integer
    */
    static final String SPREF_CURRENT_BACKGROUND = "SPBackgroundColor";

    /* State for the printing of yodo transactions receipts.
    * type -- Boolean
    */
    static final String SPREF_YODO_RECEIPTS = "SPYodoReceipts";

    /* State for the printing of static transactions receipts.
    * type -- Boolean
    */
    static final String SPREF_STATIC_RECEIPTS = "SPStaticReceipts";

    /* State for the printing of cash transactions receipts.
    * type -- Boolean
    */
    static final String SPREF_CASH_RECEIPTS = "SPCashReceipts";

    /* Task id for the splash image.
    * type -- Long
    */
    static final String SPREF_SPLASH_IMAGE = "SPSplashImage";

    /**
     * Default values
     * {{ ======================================================================
     */

    /*
	 * Default value position for the language
	 *
	 * Default: en (English)
	 */
    static final String DEFAULT_LANGUAGE = "en";

    /*
	 * Default value for the ic_discount (%)
	 */
    static final String DEFAULT_DISCOUNT = "0.00";

    /*
	 * Default value position for the scanner
	 *
	 * Default: position 0 (BarcodeScanner)
	 */
    static final Integer DEFAULT_SCANNER = 0;

    /* Logo URL */
    public static final String LOGO_PATH = YodoApi.getIp() + "/yodo-merch/uploads/img/logo/";

    /* Type of transaction sounds  */
    public static final int ERROR = 0;
    static final int SUCCESSFUL = 1;
}
