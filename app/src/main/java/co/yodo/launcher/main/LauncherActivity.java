package co.yodo.launcher.main;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.HashMap;
import java.util.Locale;

import co.yodo.launcher.R;
import co.yodo.launcher.adapter.CurrencyAdapter;
import co.yodo.launcher.component.ClearEditText;
import co.yodo.launcher.component.ImageLoader;
import co.yodo.launcher.component.ToastMaster;
import co.yodo.launcher.component.YodoHandler;
import co.yodo.launcher.data.Currency;
import co.yodo.launcher.helper.AlertDialogHelper;
import co.yodo.launcher.helper.AppConfig;
import co.yodo.launcher.helper.AppUtils;
import co.yodo.launcher.data.ServerResponse;
import co.yodo.launcher.net.YodoRequest;
import co.yodo.launcher.scanner.QRScanner;
import co.yodo.launcher.scanner.QRScannerFactory;
import co.yodo.launcher.scanner.QRScannerListener;

public class LauncherActivity extends ActionBarActivity implements YodoRequest.RESTListener, QRScannerListener {
    /** DEBUG */
    private static final String TAG = LauncherActivity.class.getSimpleName();

    /** The context object */
    private Context ac;

    /** Bluetooth Admin */
    private BluetoothAdapter mBluetoothAdapter;

    /** Hardware Token */
    private String hardwareToken;

    /** Gui controllers */
    private SlidingPaneLayout mSlidingLayout;
    private TextView mBalanceView;
    private Spinner mScannersSpinner;
    private TextView mTotalView;
    private TextView mCashTenderView;
    private TextView mCashBackView;

    /** Selected Text View */
    private TextView selectedView;

    /** Messages Handler */
    private static YodoHandler handlerMessages;

    /** ImageLoader */
    ImageLoader imageLoader;

    /** Balance Temp */
    private HashMap<String, String> historyData;
    private HashMap<String, String> todayData;

    /** Current Scanners */
    private QRScanner currentScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        setupGUI();
        updateData();
    }

    @Override
    public void onResume() {
        super.onResume();

        if( AppUtils.isAdvertisingServiceRunning(ac) )
            setupAdvertising( false );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        imageLoader.clearCache();
        QRScannerFactory.destroy();
    }

    @Override
    public void onBackPressed() {
        if(currentScanner != null && currentScanner.isScanning()) {
            currentScanner.destroy();
            return;
        }

        super.onBackPressed();
    }

    /**
     * Initialized all the GUI main components
     */
    private void setupGUI() {
        ac = LauncherActivity.this;
        imageLoader = new ImageLoader( ac );

        // Globals
        mSlidingLayout   = (SlidingPaneLayout) findViewById( R.id.sliding_panel_layout );
        mBalanceView     = (TextView) findViewById( R.id.balanceText );
        mScannersSpinner = (Spinner) findViewById(R.id.scannerSpinner);
        mTotalView       = (TextView) findViewById( R.id.totalText );
        mCashTenderView  = (TextView) findViewById( R.id.cashTenderText );
        mCashBackView    = (TextView) findViewById( R.id.cashBackText );

        // Only used at creation
        CheckBox mAdvertisingCheckBox = (CheckBox) findViewById(R.id.advertisingCheckBox);

        mScannersSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if( mSlidingLayout.isOpen() )
                    mSlidingLayout.closePane();

                ( (TextView) parentView.getChildAt( 0 ) ).setTextColor( Color.WHITE );
                AppUtils.saveScanner( ac, position );
                AppUtils.Logger(TAG, ((TextView) selectedItemView).getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<QRScannerFactory.SupportedScanners> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                QRScannerFactory.SupportedScanners.values()
        );
        mScannersSpinner.setAdapter( adapter );
        mScannersSpinner.setSelection( AppUtils.getScanner( ac ) );

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        handlerMessages   = new YodoHandler( LauncherActivity.this );
        YodoRequest.getInstance().setListener( this );

        if( AppUtils.isFirstLogin( ac ) ) {
            mSlidingLayout.openPane();

            new ShowcaseView.Builder( this )
                    .setTarget( new ViewTarget( R.id.optionsView, this ) )
                    .setContentTitle( R.string.tutorial_title )
                    .setContentText( R.string.tutorial_message )
                    .build();

            AppUtils.saveFirstLogin(ac, false);
        }

        if( mBluetoothAdapter == null )
            mAdvertisingCheckBox.setEnabled(false);
        else {
            final boolean isAdvertisingRunning =  AppUtils.isAdvertisingServiceRunning( ac );
            mAdvertisingCheckBox.setChecked( isAdvertisingRunning );

            if( isAdvertisingRunning )
                setupAdvertising( false );
        }

        selectedView = mTotalView;
        selectedView.setBackgroundResource( R.drawable.selected_text_field );
    }

    /**
     * Set-up the basic information
     */
    private void updateData() {
        hardwareToken = AppUtils.getHardwareToken( ac );

        YodoRequest.getInstance().requestLogo( LauncherActivity.this, hardwareToken );

        String[] icons = getResources().getStringArray( R.array.currency_icon_array );
        Drawable icon  = AppUtils.getDrawableByName( ac, icons[ AppUtils.getCurrency( ac ) ] );
        icon.setBounds( 0, 0, mCashTenderView.getLineHeight(), (int)(mCashTenderView.getLineHeight() * 0.9 ) );
        mCashTenderView.setCompoundDrawables(icon, null, null, null);

        mBalanceView.setText( getCurrentBalance() );
    }

    /**
     * Set-up bluetooth for advertising
     * @param force To force the require for being discoverable
     */
    private void setupAdvertising(boolean force) {
        if( !mBluetoothAdapter.isEnabled() || force ) {
            mBluetoothAdapter.enable();

            Intent discoverableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE );
            discoverableIntent.putExtra( BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0 );
            startActivity( discoverableIntent );
        }

        mBluetoothAdapter.setName( AppConfig.YODO_POS + AppUtils.getBeaconName( ac ) );
    }

    /**
     * Gets the current balance
     * @return String The current balance with two decimals format
     */
    private String getCurrentBalance() {
        String totalPurchase = mTotalView.getText().toString();
        String cashTender    = mCashTenderView.getText().toString();
        String cashBack      = mCashBackView.getText().toString();

        double total = Double.valueOf( cashTender ) - Double.valueOf( totalPurchase ) - Double.valueOf( cashBack );
        return String.format( Locale.US, "%.2f", total );
    }

    /**
     * Creates a dialog to show the balance information
     */
    private void balanceDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View layout = inflater.inflate( R.layout.dialog_balance, new LinearLayout( this ), false );

        TextView todayCreditsText = ( (TextView) layout.findViewById( R.id.yodoTodayCashTextView ) );
        TextView todayDebitsText  = ( (TextView) layout.findViewById( R.id.yodoTodayDebitsTextView ) );
        TextView todayBalanceText = ( (TextView) layout.findViewById( R.id.yodoTodayBalanceTextView ) );

        TextView creditsText = ( (TextView) layout.findViewById( R.id.yodoCashTextView ) );
        TextView debitsText  = ( (TextView) layout.findViewById( R.id.yodoDebitsTextView ) );
        TextView balanceText = ( (TextView) layout.findViewById( R.id.yodoBalanceTextView ) );

        Double total = 0.0, result;

        result = Double.valueOf( historyData.get( ServerResponse.DEBIT ) );
        debitsText.setText( String.format( "%.2f", result ) );
        total -= result;

        result = Double.valueOf( historyData.get( ServerResponse.CREDIT ) );
        creditsText.setText( String.format( "%.2f", result ) );
        total += result;

        balanceText.setText( String.format( "%.2f", total * -1 ) );
        total = 0.0;

        result = Double.valueOf( todayData.get( ServerResponse.DEBIT ) );
        todayDebitsText.setText( String.format( "%.2f", result ) );
        total -= result;

        result = Double.valueOf( todayData.get( ServerResponse.CREDIT ) );
        todayCreditsText.setText( String.format( "%.2f", result ) );
        total += result;

        todayBalanceText.setText( String.format( "%.2f", total * -1 ) );

        AlertDialogHelper.showAlertDialog( ac, getString( R.string.yodo_title ), layout );
        todayData = historyData = null;
    }

    /**
     * Changes the current language
     * @param v View, used to get the title
     */
    public void setLanguageClick(View v) {
        mSlidingLayout.closePane();

        final String title       = ((Button) v).getText().toString();
        final String[] languages = getResources().getStringArray( R.array.languages_array );
        final int current        = AppUtils.getLanguage( ac );

        DialogInterface.OnClickListener onClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                dialog.dismiss();

                ToastMaster.makeText( ac, languages[item], Toast.LENGTH_SHORT ).show();
                AppUtils.saveLanguage( ac, item );

                Intent intent = new Intent( LauncherActivity.this, MainActivity.class );
                startActivity( intent );
                finish();
            }
        };

        AlertDialogHelper.showAlertDialog(
                ac,
                title,
                languages,
                current,
                onClick
        );
    }

    /**
     * Changes the current currency
     * @param v View, used to get the title
     */
    public void setCurrencyClick(View v) {
        mSlidingLayout.closePane();

        final String[] currency = getResources().getStringArray( R.array.currency_array );
        final String[] icons    = getResources().getStringArray( R.array.currency_icon_array );

        Currency[] currencyList = new Currency[currency.length];
        for( int i = 0; i < currency.length; i++ )
            currencyList[i] = new Currency( currency[i], AppUtils.getDrawableByName( ac, icons[i] ) );

        final String title        = ((Button) v).getText().toString();
        final ListAdapter adapter = new CurrencyAdapter( ac, currencyList );
        final int current         = AppUtils.getCurrency( ac );

        DialogInterface.OnClickListener onClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                AppUtils.saveCurrency( ac, item );

                Drawable icon = AppUtils.getDrawableByName( ac, icons[ item ] );
                icon.setBounds( 0, 0, mCashTenderView.getLineHeight(), (int)(mCashTenderView.getLineHeight() * 0.9 ) );
                mCashTenderView.setCompoundDrawables(icon, null, null, null);

                dialog.dismiss();
            }
        };

        AlertDialogHelper.showAlertDialog(
                ac,
                title,
                adapter,
                current,
                onClick
        );
    }

    /**
     * Changes the current username for bluetooth
     * @param v View, not used
     */
    public void setUsernameClick(View v) {
        mSlidingLayout.closePane();

        final String title  = ((Button) v).getText().toString();
        final String beacon = AppUtils.getBeaconName( ac );

        final EditText inputBox = new ClearEditText( ac );
        inputBox.setText( beacon );

        DialogInterface.OnClickListener onClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                AppUtils.saveBeaconName( ac, inputBox.getText().toString() );
            }
        };

        AlertDialogHelper.showAlertDialog(
                ac,
                title,
                inputBox,
                onClick
        );
    }

    /**
     * Enables or disables advertising
     * @param v View, checkbox for enable or disable
     */
    public void setAdvertisingClick(View v) {
        mSlidingLayout.closePane();

        final boolean isAdvertisingRunning = ((CheckBox) v).isChecked();
        AppUtils.saveAdvertisingServiceRunning( ac , isAdvertisingRunning );

        if( isAdvertisingRunning )
            setupAdvertising( true );
    }

    /**
     * Gets the balance of the POS
     * @param v View, not used
     */
    public void getBalanceClick(View v) {
        mSlidingLayout.closePane();

        final String title      = getString( R.string.input_pip );
        final EditText inputBox = new ClearEditText( ac );
        final CheckBox remember = new CheckBox( ac );
        remember.setText( R.string.remember_pass);

        DialogInterface.OnClickListener onClick = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String pip = inputBox.getText().toString();
                AppUtils.hideSoftKeyboard( LauncherActivity.this );

                if( remember.isChecked() )
                    AppUtils.savePassword( ac, pip );
                else
                    AppUtils.savePassword( ac, null );

                YodoRequest.getInstance().createProgressDialog(
                        LauncherActivity.this ,
                        YodoRequest.ProgressDialogType.NORMAL
                );

                YodoRequest.getInstance().requestHistory(
                        LauncherActivity.this,
                        hardwareToken, pip
                );

                YodoRequest.getInstance().requestDailyHistory(
                        LauncherActivity.this,
                        hardwareToken, pip
                );
            }
        };

        AlertDialogHelper.showAlertDialog(
                ac,
                title,
                inputBox, true, true,
                remember,
                onClick
        );
    }

    /**
     * Shows some basic information about the POS
     * @param v View, not used
     */
    public void aboutClick(View v) {
        mSlidingLayout.closePane();

        final String title   = ((Button) v).getText().toString();
        final String message = getString( R.string.imei )       + " " +
                               AppUtils.getHardwareToken( ac ) + "\n" +
                               getString( R.string.version );

        AlertDialogHelper.showAlertDialog(
                ac,
                title,
                message
        );
    }

    /**
     * Logout the user
     * @param v View, not used
     */
    public void logoutClick(View v) {
        finish();
        AppUtils.saveLoginStatus( ac, false );
    }

    /**
     * Change the selected view (purchase, cash tender, or cash back)
     * @param v The selected view
     */
    public void viewClick(View v) {
        if( selectedView != null )
            selectedView.setBackgroundResource( R.drawable.show_text_field );

        selectedView = ((TextView) v);
        selectedView.setBackgroundResource( R.drawable.selected_text_field );
    }

    /**
     * Resets the values to 0.00
     * @param v The View, not used
     */
    public void resetClick(View v) {
        String zero = getString( R.string.zero );

        mTotalView.setText( zero );
        mCashTenderView.setText( zero );
        mCashBackView.setText( zero );

        mBalanceView.setText( getCurrentBalance() );
    }

    /** Handle numeric button clicked
     *  @param v View, used to get the number
     */
    public void valueClick(View v) {
        final String value   = ((Button)v).getText().toString();
        final String current = selectedView.getText().toString();

        Double result = Double.valueOf( ( current + value ).replace( ".", "" ) ) / 100.00;
        selectedView.setText( String.format( Locale.US, "%.2f", result ) );

        mBalanceView.setText( getCurrentBalance() );
    }

    /** Handle numeric add clicked
     *  @param v The View, used to get the amount
     */
    public void addClick(View v) {
        final String amount  = ((Button)v).getText().toString();
        final String current = selectedView.getText().toString();

        double value = Double.valueOf( current ) + Double.valueOf( amount );
        selectedView.setText( String.format( Locale.US, "%.2f", value ) );

        mBalanceView.setText( getCurrentBalance() );
    }

    /**
     * Handles on back pressed
     * @param v View, not used
     */
    public void backClick(View v) {
        onBackPressed();
    }

    /**
     * Opens the scanner to realize a payment
     * @param v The View, not used
     */
    public void yodoPayClick(View v) {
        if( currentScanner != null && currentScanner.isScanning() ) {
            currentScanner.destroy();
            return;
        }

        currentScanner = QRScannerFactory.getInstance(
                this,
                (QRScannerFactory.SupportedScanners) mScannersSpinner.getSelectedItem()
        );

        currentScanner.startScan();
    }

    @Override
    public void onResponse(YodoRequest.RequestType type, ServerResponse response) {
        YodoRequest.getInstance().destroyProgressDialog();
        String code, message;

        switch( type ) {
            case ERROR_NO_INTERNET:
                handlerMessages.sendEmptyMessage( YodoHandler.NO_INTERNET );
                break;

            case ERROR_GENERAL:
                handlerMessages.sendEmptyMessage( YodoHandler.GENERAL_ERROR );
                break;

            case QUERY_BAL_REQUEST:
                code = response.getCode();

                if( code.equals( ServerResponse.AUTHORIZED ) ) {
                    historyData = response.getParams();
                    if( todayData != null )
                        balanceDialog();
                } else if( code.equals( ServerResponse.ERROR_FAILED ) ) {
                    AppUtils.errorSound( ac );

                    Message msg = new Message();
                    msg.what = YodoHandler.SERVER_ERROR;
                    message  = response.getMessage() + "\n" + response.getParam( ServerResponse.PARAMS );

                    Bundle bundle = new Bundle();
                    bundle.putString( YodoHandler.CODE, code );
                    bundle.putString( YodoHandler.MESSAGE, message );
                    msg.setData( bundle );

                    handlerMessages.sendMessage( msg );
                    todayData = historyData = null;
                }
                break;

            case QUERY_DAY_REQUEST:
                code = response.getCode();

                if( code.equals( ServerResponse.AUTHORIZED ) ) {
                    todayData = response.getParams();
                    if( historyData != null )
                        balanceDialog();
                } else if( code.equals( ServerResponse.ERROR_FAILED ) ) {
                    AppUtils.errorSound( ac );

                    Message msg = new Message();
                    msg.what = YodoHandler.SERVER_ERROR;
                    message  = response.getMessage() + "\n" + response.getParam( ServerResponse.PARAMS );

                    Bundle bundle = new Bundle();
                    bundle.putString( YodoHandler.CODE, code );
                    bundle.putString( YodoHandler.MESSAGE, message );
                    msg.setData( bundle );

                    handlerMessages.sendMessage( msg );
                    todayData = historyData = null;
                }
                break;

            case QUERY_LOGO_REQUEST:
                code = response.getCode();

                if( code.equals( ServerResponse.AUTHORIZED ) ) {
                    String logoName = response.getParam( ServerResponse.LOGO );

                    if( logoName != null ) {
                        ImageView logoImage = (ImageView) findViewById( R.id.companyLogo );
                        imageLoader.DisplayImage( AppConfig.LOGO_PATH + logoName, logoImage );
                    }
                }
                break;

            case EXCH_MERCH_REQUEST:
            case ALT_MERCH_REQUEST:
                code = response.getCode();

                if( code.equals( ServerResponse.AUTHORIZED ) ) {
                    message = getString( R.string.exchange_auth ) + " " + response.getAuthNumber() + "\n" +
                              getString( R.string.exchange_message ) + " " + response.getMessage();

                    AlertDialogHelper.showAlertDialog( ac, response.getCode(), message );
                } else {
                    AppUtils.errorSound( ac );

                    Message msg = new Message();
                    msg.what = YodoHandler.SERVER_ERROR;
                    message  = response.getMessage() + "\n" + response.getParam( ServerResponse.PARAMS );

                    Bundle bundle = new Bundle();
                    bundle.putString( YodoHandler.CODE, code );
                    bundle.putString( YodoHandler.MESSAGE, message );
                    msg.setData( bundle );

                    handlerMessages.sendMessage( msg );
                }
                break;
        }
    }

    @Override
    public void onNewData(String data) {
        String totalPurchase = mTotalView.getText().toString();
        String cashTender    = mCashTenderView.getText().toString();
        String cashBack      = mCashBackView.getText().toString();

        final String[] currency = getResources().getStringArray( R.array.currency_array );

        switch( data.length() ) {
            case AppConfig.SKS_SIZE:
                YodoRequest.getInstance().createProgressDialog(
                        LauncherActivity.this,
                        YodoRequest.ProgressDialogType.TRANSPARENT
                );

                YodoRequest.getInstance().requestExchange(
                        LauncherActivity.this,
                        hardwareToken,
                        data,
                        totalPurchase,
                        cashTender,
                        cashBack,
                        currency[ AppUtils.getCurrency( ac ) ]
                );
                break;

            case AppConfig.ALT_SIZE:
                YodoRequest.getInstance().createProgressDialog(
                        LauncherActivity.this,
                        YodoRequest.ProgressDialogType.TRANSPARENT
                );

                YodoRequest.getInstance().requestAlternate(
                        LauncherActivity.this,
                        hardwareToken,
                        data,
                        totalPurchase,
                        cashTender,
                        cashBack,
                        currency[ AppUtils.getCurrency( ac ) ]
                );
                break;

            default:
                ToastMaster.makeText( LauncherActivity.this, R.string.exchange_error, Toast.LENGTH_LONG ).show();
                break;
        }
    }
}
