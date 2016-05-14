package com.apache.currencyconverter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

public class CurrencyConverterActivity extends AppCompatActivity {

    TextView lblInput,lblOutput;
    EditText currencyInput,currencyOutput;
    RadioGroup radioGroupConversion;
    RadioButton radioBtnUsdBdt,radioBtnBdtUsd;
    Button btnConvert;
    ResponseReceiver receiver;

    String siteDataHRHAFIZ;
    String siteDataUSDBDT;
    String siteDataBDTUSD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lblInput= (TextView) findViewById(R.id.lblInput);
        lblOutput= (TextView) findViewById(R.id.lblOutput);
        currencyInput = (EditText) findViewById(R.id.editTextInput);
        currencyOutput = (EditText) findViewById(R.id.editTextOutput);
        radioGroupConversion = (RadioGroup) findViewById(R.id.radioGroup);
        radioBtnUsdBdt = (RadioButton) findViewById(R.id.radioButtonUsdBdt);
        radioBtnBdtUsd =(RadioButton) findViewById(R.id.radioButtonBdtUsd);
        btnConvert =(Button) findViewById(R.id.buttonConvert);


    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter broadcastFilter = new IntentFilter("com.apache.currencyconverter.currencyConverterIntentService");
        receiver = new ResponseReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(receiver,broadcastFilter);


        final Intent intent;
        intent = new Intent(this,currencyConverterIntentService.class);
        String hrhafiz = "http://hrhafiz.com/converter/";
        intent.putExtra("hrhafizSite", hrhafiz);

        currencyInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!currencyInput.getText().toString().isEmpty()) {
                    doConvertion();



                if (isNetworkAvailable()==true && siteDataHRHAFIZ == null)
                {
                    // Log.d("Internet",""+isNetworkAvailable());
                    startService(intent);
                }
                else if ( siteDataHRHAFIZ != null)
                {

                 //   doConvertion();

                }
                else {
                    Log.d("Internet",""+isNetworkAvailable());
                    Context context = getApplicationContext();
                    CharSequence text = "INTERNET CONNECTION REQUIRED FIRST TIME";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
                else {
                    currencyOutput.setText("");
                }
            }
        });

        radioGroupConversion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioBtnUsdBdt.isChecked()==true) {
                    lblInput.setText("USD");
                    lblOutput.setText("BDT");

                    doConvertion();
                }
                else {
                    lblInput.setText("BDT");
                    lblOutput.setText("USD");
                    doConvertion();
                }

            }
        });
        btnConvert.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(receiver);
    }
    public class ResponseReceiver extends BroadcastReceiver {


        //public static final String LOCAL_ACTION = "com.apache.currencyconverter.currencyConverterIntentService";

        @Override
        public void onReceive(Context context, Intent intent) {

            siteDataHRHAFIZ = intent.getStringExtra("siteData");
            siteDataUSDBDT = siteDataHRHAFIZ.substring(9, 14);
            siteDataBDTUSD = siteDataHRHAFIZ.substring(25, 30);
            Log.d("Received", "" + siteDataHRHAFIZ + " " + siteDataUSDBDT + " " + siteDataBDTUSD);

            doConvertion();
        }
    }
    public void converter(float conversionRate){

        String getInput = currencyInput.getText().toString();
        Log.d("Input","is "+getInput);

        if (!getInput.isEmpty() && !getInput.equals(".")) {
            float cInput = Float.parseFloat(getInput);
            float result = conversionRate * cInput;
            currencyOutput.setText(new BigDecimal(String.valueOf(result)).toPlainString());
        }
        else {
            currencyInput.setText("");
            currencyOutput.setText("");
        }
    }

    public void doConvertion(){
        if (siteDataHRHAFIZ != null) {

            if (radioBtnUsdBdt.isChecked() == true) {
                converter(Float.parseFloat(String.valueOf(siteDataUSDBDT)));
            } else if (radioBtnBdtUsd.isChecked() == true) {
                converter(Float.parseFloat(String.valueOf(siteDataBDTUSD)));
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        else return false;
    }
}


