package co.yodo.launcher.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import co.yodo.launcher.R;
import co.yodo.launcher.component.AES;

/**
 * Created by luis on 15/12/14.
 * Utilities for the App, Mainly shared preferences
 */
public class AppUtils {
    /** DEBUG */
    @SuppressWarnings( "unused" )
    private static final String TAG = AppUtils.class.getSimpleName();

    /**
     * A simple check to see if a string is a valid number before inserting
     * into the shared preferences.
     *
     * @param s The number to be checked.
     * @return true  It is a number.
     *         false It is not a number.
     */
    public static Boolean isNumber(String s) {
        try {
            Integer.parseInt(s);
        }
        catch( NumberFormatException e ) {
            return false;
        }
        return true;
    }

    /**
     * A helper class just o obtain the config file for the Shared Preferences
     * using the default values for this Shared Preferences app.
     * @param c The Context of the Android system.
     * @return Returns the shared preferences with the default values.
     */
    private static SharedPreferences getSPrefConfig( Context c ) {
        return c.getSharedPreferences( AppConfig.SHARED_PREF_FILE, Context.MODE_PRIVATE );
    }

    /**
     * Generates the mobile hardware identifier either
     * from the Phone (IMEI) or the Bluetooth (MAC)
     * @param c The Context of the Android system.
     */
    public static String generateHardwareToken( Context c ) {
        String HARDWARE_TOKEN = null;

        TelephonyManager telephonyManager  = (TelephonyManager) c.getSystemService( Context.TELEPHONY_SERVICE );
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if( telephonyManager != null ) {
            String tempMAC = telephonyManager.getDeviceId();
            if( tempMAC != null )
                HARDWARE_TOKEN = tempMAC.replace( "/", "" );
        }

        if( HARDWARE_TOKEN == null && mBluetoothAdapter != null ) {
            if( mBluetoothAdapter.isEnabled() ) {
                String tempMAC = mBluetoothAdapter.getAddress();
                HARDWARE_TOKEN = tempMAC.replaceAll( ":", "" );
            }
        }

        return HARDWARE_TOKEN;
    }

    public static Boolean saveHardwareToken( Context c, String hardwareToken ) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putString( AppConfig.SPREF_HARDWARE_TOKEN, hardwareToken );
        return writer.commit();
    }

    public static String getHardwareToken( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        String token = config.getString( AppConfig.SPREF_HARDWARE_TOKEN, "" );
        return ( token.equals( "" ) ) ? null : token;
    }

    /**
     * Sets if the device is legacy or not
     * @param c The Android application context
     * @param legacy True or false, (if legacy or not)
     * @return true  The flag was saved successfully.
     *         false The flag was not saved successfully.
     */
    public static Boolean setLegacy( Context c, Boolean legacy ) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putBoolean( AppConfig.SPREF_LEGACY, legacy );
        return writer.commit();
    }

    /**
     * Returns if the device is leagacy (doesn't support Google Service)
     * @param c The Android application context
     * @return True if legacy
     *         False if not
     */
    public static Boolean isLegacy( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getBoolean( AppConfig.SPREF_LEGACY, false );
    }

    /**
     * It saves the status of login.
     * @param c The Context of the Android system.
     * @param flag The status of the login.
     * @return true  The flag was saved successfully.
     *         false The flag was not saved successfully.
     */
    public static Boolean saveLoginStatus(Context c, Boolean flag) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putBoolean( AppConfig.SPREF_LOGIN_STATE, flag );
        return writer.commit();
    }

    /**
     * It gets the status of the login.
     * @param c The Context of the Android system.
     * @return true  It is logged in.
     *         false It is not logged in.
     */
    public static Boolean isLoggedIn(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getBoolean( AppConfig.SPREF_LOGIN_STATE, false );
    }

    /**
     * It saves the logo url to the preferences.
     * @param c The Context of the Android system.
     * @param s The logo url.
     * @return true  If it was saved.
     *         false If it was not saved.
     */
    public static Boolean saveLogoUrl(Context c, String s) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putString( AppConfig.SPREF_CURRENT_LOGO, s );
        return writer.commit();
    }

    /**
     * It gets the logo url.
     * @param c The Context of the Android system.
     * @return int It returns the beacon name.
     */
    public static String getLogoUrl(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getString( AppConfig.SPREF_CURRENT_LOGO, "" );
    }

    /**
     * It gets the language position.
     * @param c The Context of the Android system.
     * @return int It returns the language position.
     */
    public static String getLanguage( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getString( AppConfig.SPREF_CURRENT_LANGUAGE, AppConfig.DEFAULT_LANGUAGE );
    }

    /**
     * It saves the currency array position to the preferences.
     * @param c The Context of the Android system.
     * @param n The currency position on the array.
     * @return true  If it was saved.
     *         false If it was not saved.
     */
    public static Boolean saveCurrency( Context c, int n ) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putInt( AppConfig.SPREF_CURRENT_CURRENCY, n );
        return writer.commit();
    }

    /**
     * It gets the currency position.
     * @param c The Context of the Android system.
     * @return int It returns the currency position.
     */
    public static int getCurrency( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        int position = config.getInt( AppConfig.SPREF_CURRENT_CURRENCY, AppConfig.DEFAULT_CURRENCY );
        if( position < 0 ) {
            position = AppConfig.DEFAULT_CURRENCY;
            saveCurrency( c, position );
        }
        return position;
    }

    /**
     * It saves the merchant currency to the preferences.
     * @param c The Context of the Android system.
     * @param n The currency of the merchant.
     * @return true  If it was saved.
     *         false If it was not saved.
     */
    public static Boolean saveMerchantCurrency( Context c, String n ) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putString( AppConfig.SPREF_MERCHANT_CURRENCY, n );
        return writer.commit();
    }

    /**
     * It gets the merchant currency.
     * @param c The Context of the Android system.
     * @return int It returns the currency.
     */
    public static String getMerchantCurrency( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getString( AppConfig.SPREF_MERCHANT_CURRENCY, null );
    }

    /**
     * It saves the scanner array position to the preferences.
     * @param c The Context of the Android system.
     * @param n The scanner position on the array.
     * @return true  If it was saved.
     *         false If it was not saved.
     */
    public static Boolean saveScanner( Context c, int n ) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putInt( AppConfig.SPREF_CURRENT_SCANNER, n );
        return writer.commit();
    }

    /**
     * It gets the scanner position.
     * @param c The Context of the Android system.
     * @return int It returns the scanner position.
     */
    public static int getScanner( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getInt( AppConfig.SPREF_CURRENT_SCANNER, AppConfig.DEFAULT_SCANNER );
    }

    /**
     * It gets the beacon name.
     * @param c The Context of the Android system.
     * @return int It returns the beacon name.
     */
    public static String getBeaconName( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getString( AppConfig.SPREF_CURRENT_BEACON, "" );
    }

    /**
     * It gets the current tip (%).
     * @param c The Context of the Android system.
     * @return int It returns the tip percentage.
     */
    public static String getCurrentTip( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getString( AppConfig.SPREF_CURRENT_TIP, AppConfig.DEFAULT_TIP );
    }

    /**
     * It saves the discount for the payments to the preferences.
     * @param c The Context of the Android system.
     * @param n The scanner position on the array.
     * @return true  If it was saved.
     *         false If it was not saved.
     */
    public static Boolean saveDiscount( Context c, String n ) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putString( AppConfig.SPREF_DISCOUNT, n );
        return writer.commit();
    }

    /**
     * It gets the current tip (%).
     * @param c The Context of the Android system.
     * @return int It returns the tip percentage.
     */
    public static String getDiscount( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getString( AppConfig.SPREF_DISCOUNT, AppConfig.DEFAULT_DISCOUNT );
    }

    /**
     * It saves the password to the preferences.
     * @param c The Context of the Android system.
     * @param s The password.
     * @return true  If it was saved.
     *         false If it was not saved.
     */
    public static Boolean savePassword( Context c, String s ) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();

        if( s != null ) {
            try {
                String encryptPip = AES.encrypt( s );
                writer.putString( AppConfig.SPREF_CURRENT_PASSWORD, encryptPip );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            writer.remove(AppConfig.SPREF_CURRENT_PASSWORD);
        }

        return writer.commit();
    }

    /**
     * It gets the password if saved.
     * @param c The Context of the Android system.
     * @return int It returns the password or nul.
     */
    public static String getPassword( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        String password = config.getString( AppConfig.SPREF_CURRENT_PASSWORD, null );

        if( password != null ) {
            try {
                password = AES.decrypt( password );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return password;
    }

    /**
     * It saves if it is the first login.
     * @param c The Context of the Android system.
     * @param flag If it is the first login or not.
     * @return true  The flag was saved successfully.
     *         false The flag was not saved successfully.
     */
    public static Boolean saveFirstLogin(Context c, Boolean flag) {
        SharedPreferences config = getSPrefConfig( c );
        SharedPreferences.Editor writer = config.edit();
        writer.putBoolean( AppConfig.SPREF_FIRST_LOGIN, flag );
        return writer.commit();
    }

    /**
     * It gets if it is the first login.
     * @param c The Context of the Android system.
     * @return true  It is logged in.
     *         false It is not logged in.
     */
    public static Boolean isFirstLogin(Context c) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getBoolean( AppConfig.SPREF_FIRST_LOGIN, true );
    }

    /**
     * It gets the status of the advertising service.
     * @param c The Context of the Android system.
     * @return true  Advertising service is on.
     *         false Advertising service is off.
     */
    public static Boolean isAdvertisingServiceRunning( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getBoolean( AppConfig.SPREF_ADVERTISING_SERVICE, false );
    }

    public static int getCurrentBackground( Context c ) {
        SharedPreferences config = getSPrefConfig( c );
        return config.getInt( AppConfig.SPREF_CURRENT_BACKGROUND, -0x1 );
    }

    /**
     * Gets the bluetooth adapter
     * @return The bluetooth adapter
     */
    private static BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Check if the device possess bluetooth
     * @return true if it possess bluetooth otherwise false
     */
    public static boolean hasBluetooth() {
        return getBluetoothAdapter() != null;
    }

    /**
     * Set-up bluetooth for advertising
     * @param start To start or stop
     * @param force To force the require for being discoverable
     */
    public static void setupAdvertising( Context ac, boolean start, boolean force ) {
        if( !hasBluetooth() )
            return;

        BluetoothAdapter mBluetoothAdapter = getBluetoothAdapter();
        if( start ) {
            if( !mBluetoothAdapter.isEnabled() || force ) {
                mBluetoothAdapter.enable();

                Intent discoverableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE );
                discoverableIntent.putExtra( BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0 );
                ac.startActivity( discoverableIntent );
            }

            mBluetoothAdapter.setName( AppConfig.YODO_POS + AppUtils.getBeaconName( ac ) );
        } else {
            mBluetoothAdapter.setName( Build.MODEL );
        }
    }

    /**
     * Get the drawable based on the name
     * @param c The Context of the Android system.
     * @param name The name of the drawable
     * @return The drawable
     */
    public static Drawable getDrawableByName( Context c, String name ) throws Resources.NotFoundException {
        Resources resources = c.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable", c.getPackageName());
        Drawable image = ContextCompat.getDrawable( c, resourceId );
        int h = image.getIntrinsicHeight();
        int w = image.getIntrinsicWidth();
        image.setBounds( 0, 0, w, h );
        return image;
    }

    /**
     * Show or hide the password depending on the checkbox
     * @param state The checkbox
     * @param password The EditText for the password
     */
    public static void showPassword(CheckBox state, EditText password) {
        if( state.isChecked() )
            password.setInputType( InputType.TYPE_TEXT_VARIATION_PASSWORD );
        else
            password.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
        password.setTypeface( Typeface.MONOSPACE );
    }

    /**
     * Hides the soft keyboard
     * @param a The activity where the keyboard is open
     */
    public static void hideSoftKeyboard(Activity a) {
        View v = a.getCurrentFocus();
        if( v != null ) {
            InputMethodManager imm = (InputMethodManager) a.getSystemService( Context.INPUT_METHOD_SERVICE );
            imm.hideSoftInputFromWindow( v.getWindowToken(), 0 );
        }
    }

    /**
     * Plays a sound of error
     * @param c The Context of the Android system.
     */
    public static void errorSound(Context c) {
        MediaPlayer mp = MediaPlayer.create( c, R.raw.error );
        mp.setOnCompletionListener( new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mp.start();
    }

    public static void setLanguage( Context c ) {
        Locale appLoc = new Locale( getLanguage( c ) );

        Resources res = c.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();

        Locale.setDefault( appLoc );
        Configuration config = new Configuration( res.getConfiguration() );
        config.locale = appLoc;

        res.updateConfiguration( config, dm );
    }

    /**
     * Verify if a service is running
     * @param c The Context of the Android system.
     * @param serviceName The name of the service.
     * @return Boolean true if is running otherwise false
     */
    public static boolean isMyServiceRunning(Context c, String serviceName) {
        ActivityManager manager = (ActivityManager) c.getSystemService( Context.ACTIVITY_SERVICE );
        for( ActivityManager.RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE ) )  {
            if( serviceName.equals( service.service.getClassName() ) )
                return true;
        }
        return false;
    }

    /**
     * Method to verify google play services on the device
     * @param activity The activity that
     * @param code The code for the activity result
     * */
    public static boolean isGooglePlayServicesAvailable( Activity activity, int code ) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable( activity );
        if( resultCode != ConnectionResult.SUCCESS ) {
            if( googleAPI.isUserResolvableError( resultCode ) ) {
                googleAPI.getErrorDialog( activity, resultCode, code ).show();
            } else {
                AppUtils.setLegacy( activity, true );
            }
            return false;
        }
        return true;
    }

    /**
     * Verify if the device has GPS
     * @param c The Context of the Android system.
     * @return Boolean true if it has GPS
     */
    public static boolean hasLocationService( Context c ) {
        LocationManager locManager = (LocationManager) c.getSystemService( Context.LOCATION_SERVICE );
        return locManager.getProvider( LocationManager.GPS_PROVIDER ) != null;
    }

    /**
     * Verify if the location services are enabled (any provider)
     * @param c The Context of the Android system.
     * @return Boolean true if is running otherwise false
     */
    public static boolean isLocationEnabled( Context c ) {
        LocationManager lm = (LocationManager) c.getSystemService( Context.LOCATION_SERVICE );
        String provider    = lm.getBestProvider( new Criteria(), true );
        return ( ( !provider.isEmpty() ) && !LocationManager.PASSIVE_PROVIDER.equals( provider ) );
    }

    /**
     * Requests a permission for the use of a phone's characteristic (e.g. Camera, Phone info, etc)
     * @param ac The application context
     * @param message A message to request the permission
     * @param permission The permission
     * @param requestCode The request code for the result
     * @return If the permission was already allowed or not
     */
    public static boolean requestPermission( final Activity ac, final int message, final String permission, final int requestCode ) {
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission( ac, permission );
        if( permissionCheck != PackageManager.PERMISSION_GRANTED ) {
            if( ActivityCompat.shouldShowRequestPermissionRationale( ac, permission ) ) {
                DialogInterface.OnClickListener onClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        ActivityCompat.requestPermissions(
                                ac,
                                new String[]{permission},
                                requestCode
                        );
                    }
                };

                AlertDialogHelper.showAlertDialog(
                        ac,
                        message,
                        onClick
                );
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions( ac, new String[]{permission}, requestCode );
            }
            return false;
        }
        return true;
    }

    /**
     * Rotates an image by 360 in 1 second
     * @param image The image to rotate
     */
    public static void rotateImage(View image) {
        RotateAnimation rotateAnimation1 = new RotateAnimation( 0, 90,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f );
        rotateAnimation1.setInterpolator( new LinearInterpolator() );
        rotateAnimation1.setDuration( 500 );
        rotateAnimation1.setRepeatCount( 0 );

        image.startAnimation( rotateAnimation1 );
    }

    /**
     * Modify the size of the drawable for a TextView
     * @param c The Context of the Android system.
     * @param v The view to modify the drawable
     * @param d if Default or not
     */
    public static void setCurrencyIcon( Context c, TextView v, boolean d ) {
        String[] icons = c.getResources().getStringArray( R.array.currency_icon_array );
        Drawable icon;

        if( !d )
            icon  = AppUtils.getDrawableByName( c, icons[ AppUtils.getCurrency( c ) ] );
        else
            icon  = AppUtils.getDrawableByName( c, icons[ AppConfig.DEFAULT_CURRENCY ] );
        icon.setBounds( 3, 0, v.getLineHeight(), (int) ( v.getLineHeight() * 0.9 ) );
        v.setCompoundDrawables( icon, null, null, null );
    }

    /**
     * Modify the size of the drawable for a TextView
     * @param c The Context of the Android system.
     * @param v The view to modify the drawable
     */
    public static boolean setViewIcon( Context c, TextView v, Integer resource ) {
        // Clean an icon from the view
        if( resource == null ) {
            v.setCompoundDrawables( null, null, null, null );
            return true;
        }
        // Try to set an icon to a view
        Drawable icon = ContextCompat.getDrawable( c, resource );
        if( icon != null ) {
            icon.setBounds( 3, 0, v.getLineHeight(), (int) ( v.getLineHeight() * 0.9 ) );
            v.setCompoundDrawables( icon, null, null, null );
            return true;
        }
        return false;
    }

    /**
     * Modify the size of the drawable for a TextView
     * @param c The Context of the Android system.
     * @param v The view to modify the drawable
     */
    public static void setMerchantCurrencyIcon( Context c, TextView v ) {
        final String[] icons = c.getResources().getStringArray( R.array.currency_icon_array );
        final String[] currencies = c.getResources().getStringArray( R.array.currency_array );
        final String cur = AppUtils.getMerchantCurrency( c );
        final int position = Arrays.asList( currencies ).indexOf( cur );

        Drawable icon  = AppUtils.getDrawableByName( c, icons[ position ] );
        icon.setBounds( 3, 0, v.getLineHeight(), (int)( v.getLineHeight() * 0.9 ) );
        v.setCompoundDrawables( icon, null, null, null );
    }

    private static void appendLog( String text ) {
        File logFile = new File( Environment.getExternalStorageDirectory() + "/" + AppConfig.LOG_FILE );

        try {
            if( !logFile.exists() )
                logFile.createNewFile();

            BufferedWriter buf = new BufferedWriter( new FileWriter( logFile, true ) );
            buf.append( text );
            buf.newLine();
            buf.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logger for Android
     * @param TAG The String of the TAG for the log
     * @param text The text to print on the log
     */
    public static void Logger( String TAG, String text ) {
        if( AppConfig.DEBUG ) {
            if( text == null )
                Log.e( TAG, "Null Text" );
            else
                Log.e( TAG, text );
        }

        if( AppConfig.FDEBUG ) {
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd_HHmmss", Locale.US );
            String currentDate   = sdf.format( new Date() );

            if( text == null )
                appendLog( currentDate + "\t/D" + TAG + ": Null Text" );
            else
                appendLog( currentDate + "\t/D" + TAG + ": " + text );
        }
    }
}
