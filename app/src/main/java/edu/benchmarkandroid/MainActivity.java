package edu.benchmarkandroid;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.benchmarkandroid.Benchmark.BenchmarkData;
import edu.benchmarkandroid.utils.BatteryUtils;
import edu.benchmarkandroid.utils.CPUUtils;
import edu.benchmarkandroid.model.UpdateData;
import edu.benchmarkandroid.service.BatteryNotificator;
import edu.benchmarkandroid.service.BenchmarkExecutor;
import edu.benchmarkandroid.service.PollingIntentService;
import edu.benchmarkandroid.service.ServerConnection;
import edu.benchmarkandroid.utils.Cb;
import edu.benchmarkandroid.utils.Logger;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static edu.benchmarkandroid.service.BenchmarkIntentService.END_BENCHMARK_ACTION;
import static edu.benchmarkandroid.service.BenchmarkIntentService.PROGRESS_BENCHMARK_ACTION;
import static edu.benchmarkandroid.service.PollingIntentService.POLLING_ACTION;
import static edu.benchmarkandroid.service.SamplingIntentService.END_SAMPLING_ACTION;
import static edu.benchmarkandroid.service.SamplingIntentService.PROGRESS_SAMPLING_ACTION;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    // CHANGE THIS CONSTANT TO THE VALUE OF YOUR PREFERENCE
    public static final int INTERVAL_OFF_BATTERY_UPDATES = 5000;
    public static final int POLLING_INTERVAL = 5000;
    public static final String NOT_DEFINED = "notdefined";
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 53;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;

    private static String CHARGING = "Charging";
    private static String DISCHARGING = "Discharging";


    public static double THIS_DEVICE_BATTERY_MIN_START_BATTERY_LEVEL = 1d;
    public int deviceCpuMhz;
    public int deviceBatteryMah;

    // Callbacks for errors
    final Cb<String> onError = new Cb<String>() {
        @Override
        public void run(String error) {
            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
        }
    };

    // Callbacks for battery updates
    final Cb<JSONObject> batteryUpdateOnSucess = new Cb<JSONObject>() {
        @Override
        public void run(JSONObject jsonObject) {
            //Toast.makeText(MainActivity.this, "Battery Update Complete :)", Toast.LENGTH_SHORT).show();
            requestBenchmarksButton.setEnabled(true);
        }
    };

    // Condicions for postInitPayload
    public double minBatteryLevel = THIS_DEVICE_BATTERY_MIN_START_BATTERY_LEVEL;
    public String stateOfCharge = NOT_DEFINED;

    // Services
    private ServerConnection serverConnection;
    private BatteryNotificator batteryNotificator;

    // Callbacks for results send
    final Cb<String> resultSendCb = new Cb<String>() {
        @Override
        public void run(String useless) {
            Toast.makeText(MainActivity.this, "send", Toast.LENGTH_SHORT).show();
            serverConnection.postUpdate(new UpdateData(deviceCpuMhz, deviceBatteryMah, minBatteryLevel, batteryNotificator.getCurrentLevel()), batteryUpdateOnSucess, onError, getApplicationContext());
        }
    };
    private long timeOfLastBatteryUpdate;
    private BenchmarkExecutor benchmarkExecutor;


    // Callbacks for benchmarks received
    final Cb<BenchmarkData> benchmarkReceivedOnSucess = new Cb<BenchmarkData>() {
        @Override
        public void run(BenchmarkData benchmarkData) {
            Toast.makeText(MainActivity.this, "Benchmarks received :)", Toast.LENGTH_SHORT).show();
            benchmarkExecutor.setBenchmarkData(benchmarkData);
            minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
            stateOfCharge = benchmarkExecutor.getNeededBatteryState();
            serverConnection.postUpdate(new UpdateData(deviceCpuMhz, deviceBatteryMah, minBatteryLevel, batteryNotificator.getCurrentLevel()), batteryUpdateOnSucess, onError, getApplicationContext());
            startBenchmarksButton.setEnabled(true);
            aSwitch.setEnabled(true);
            if (benchmarkExecutor.isKeepScreenOn())
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            else
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    };

    //mutex
    private Boolean running = false;
    private Boolean evaluating = false;

    // Callbacks for benchmarks started
    final Cb<Object> benchmarkCanStartSucess = new Cb<Object>() {
        @Override
        public void run(Object useless) {
            synchronized (evaluating) {
                if (!running) {
                    Toast.makeText(MainActivity.this, "Benchmark can start :)", Toast.LENGTH_SHORT).show();
                    if (benchmarkExecutor.hasMoreToExecute()) {
                        running = true;
                        benchmarkExecutor.execute(getApplicationContext());
                        minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
                        serverConnection.postUpdate(new UpdateData(deviceCpuMhz, deviceBatteryMah, minBatteryLevel, batteryNotificator.getCurrentLevel()), batteryUpdateOnSucess, onError, getApplicationContext());
                    } else
                        Toast.makeText(MainActivity.this, "There is no more benchmark", Toast.LENGTH_SHORT).show();
                    evaluating = false;
                }
            }
        }
    };

    // Callbacks for error on benchmarks started
    final Cb<String> onErrorBS = new Cb<String>() {
        @Override
        public void run(String error) {
            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            evaluating = false;
        }
    };

    // Suscriber to battery notifications from the OS
    private BroadcastReceiver batteryInfoReceiver;

    // Receiver for updates from the benchmark run
    private ProgressReceiver progressReceiver;


    // Receiver for updates from the polling service
    private PollingReceiver pollingReceiver;

    // View components
    private EditText ipEditText;
    private EditText portEditText;
    private TextView ipTextView;
    private TextView portTextView;
    private TextView modelTextView;
    private Button manuaBatteryUpdateButton;
    private Button setServerButton;
    private Button requestBenchmarksButton;
    private Button startBenchmarksButton;
    private Switch aSwitch;
    private TextView stateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceBatteryMah = BatteryUtils.getBatteryCapacity(this);
        deviceCpuMhz = CPUUtils.getMaxCPUFreqMHz();


        //check for permission to use internet on the device
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        }

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }


        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }


        //set service to interact with the server
        serverConnection = ServerConnection.getService();

        //initialize battery intents receiver
        this.batteryNotificator = BatteryNotificator.getInstance();
        batteryInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                batteryNotificator.updateBatteryLevel((level / (double) scale));
                if ((System.currentTimeMillis() - timeOfLastBatteryUpdate) > INTERVAL_OFF_BATTERY_UPDATES) {
                    timeOfLastBatteryUpdate = System.currentTimeMillis();
                    serverConnection.postUpdate(new UpdateData(deviceCpuMhz, deviceBatteryMah, minBatteryLevel, batteryNotificator.getCurrentLevel()), batteryUpdateOnSucess, onError, getApplicationContext());
                }


                StringBuffer st = new StringBuffer();
                st.append(System.currentTimeMillis());
                st.append(',');
                String status = null;
                if (intent.getExtras().getInt(BatteryManager.EXTRA_STATUS) == BatteryManager.BATTERY_STATUS_CHARGING)
                    status = CHARGING;
                if (intent.getExtras().getInt(BatteryManager.EXTRA_STATUS) == BatteryManager.BATTERY_STATUS_DISCHARGING)
                    status = DISCHARGING;
                st.append(status);
                st.append(',');
                st.append(intent.getExtras().get(BatteryManager.EXTRA_LEVEL));

                try {
                    Logger.getInstance().write(st.toString());
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "battery: Logger not found ");
                }
            }
        };
        this.registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        //set receiver for benchmark updates to the main thread
        IntentFilter filter = new IntentFilter();
        filter.addAction(PROGRESS_BENCHMARK_ACTION);
        filter.addAction(END_BENCHMARK_ACTION);
        filter.addAction(PROGRESS_SAMPLING_ACTION);
        filter.addAction(END_SAMPLING_ACTION);
        progressReceiver = new ProgressReceiver();
        registerReceiver(progressReceiver, filter);

        //set receiver for polling service's updates to the main thread
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(POLLING_ACTION);
        pollingReceiver = new PollingReceiver();
        registerReceiver(pollingReceiver, filter2);

        //initialize benchmark service
        this.benchmarkExecutor = new BenchmarkExecutor();

        //find the view component by their id
        ipEditText = findViewById(R.id.IpText);
        portEditText = findViewById(R.id.portText);
        portTextView = findViewById(R.id.textViewPort);
        ipTextView = findViewById(R.id.textViewIP);
        modelTextView = findViewById(R.id.modelTextView);
        manuaBatteryUpdateButton = findViewById(R.id.manualStateUpdateButton);
        requestBenchmarksButton = findViewById(R.id.requestBenchmarksButton);
        setServerButton = findViewById(R.id.setServerButton);
        startBenchmarksButton = findViewById(R.id.startBenchmarksButton);
        aSwitch = findViewById(R.id.aSwitch);
        stateTextView = findViewById(R.id.stateTextView);


        ipTextView.setText(ipEditText.getText());
        portTextView.setText(portEditText.getText());
        modelTextView.setText(Build.MANUFACTURER + "-" + Build.MODEL);

        //set onChangeListener to display the complete formater url to the user
        bindInputToDisplayText(ipEditText, ipTextView);
        bindInputToDisplayText(portEditText, portTextView);


        //bind button actions
        setServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setServerButton.getText().equals("Edit Server Url")) {
                    setServerButton.setText("Set Server Url");
                    ipEditText.setEnabled(true);
                    portEditText.setEnabled(true);
                    manuaBatteryUpdateButton.setEnabled(false);
                    requestBenchmarksButton.setEnabled(false);
                    startBenchmarksButton.setEnabled(false);
                    aSwitch.setEnabled(false);

                } else {
                    String serverUrl = String.format("http://%s:%s/dewsim/%s", ipTextView.getText(), portTextView.getText(), modelTextView.getText());
                    Log.d(TAG, "onClick: " + serverUrl);
                    serverConnection.registerServerUrl(serverUrl);
                    setServerButton.setText("Edit Server Url");
                    ipEditText.setEnabled(false);
                    portEditText.setEnabled(false);
                    //manuaBatteryUpdateButton.setEnabled(true);

                    serverConnection.postUpdate(new UpdateData(deviceCpuMhz, deviceBatteryMah, minBatteryLevel, batteryNotificator.getCurrentLevel()), batteryUpdateOnSucess, onError, getApplicationContext());

                }
            }
        });

//        manuaBatteryUpdateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                serverConnection.postUpdate(new UpdateData(deviceCpuMhz, deviceBatteryMah, minBatteryLevel, batteryNotificator.getCurrentLevel()), batteryUpdateOnSucess, onError, getApplicationContext());
//            }
//        });

        requestBenchmarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverConnection.getBenchmarks(benchmarkReceivedOnSucess, onError, getApplicationContext());
            }
        });

        startBenchmarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBenchmark();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startPolling();
                } else {
                    stopPolling();
                }
            }
        });


        Button testButton = findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxCPUFreqMHz = CPUUtils.getMaxCPUFreqMHz();
                Log.d(TAG, "onClick: " + "maxCPUFreqMHz: " + maxCPUFreqMHz);


                double batteryCapacity = BatteryUtils.getBatteryCapacity(MainActivity.this);
                Log.d(TAG, "onClick: " + "batteryCapacity: " + batteryCapacity);

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Internet Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "We need this permission", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.INTERNET},
                            MY_PERMISSIONS_REQUEST_INTERNET);
                }
                return;
            }
        }
    }


    //L&F related
    private void bindInputToDisplayText(final EditText input, final TextView display) {
        input.addTextChangedListener(
                new TextWatcher() {

                    public void afterTextChanged(Editable s) {
                        display.setText(input.getText());
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                }
        );
    }


    private void startBenchmark() {
        synchronized (evaluating) {
            if (!evaluating && !running) {
                evaluating = true;
                Log.d(TAG, "MainActivity - startBenchmark: CAN START");
                serverConnection.startBenchmarck(benchmarkCanStartSucess, onErrorBS, getApplicationContext(), stateOfCharge);
            }
        }
    }

    private void startPolling() {
        Intent intent = new Intent(this, PollingIntentService.class);
        PollingIntentService.setShouldContinue(true);
        this.startService(intent);
    }

    private void stopPolling() {
        PollingIntentService.setShouldContinue(false);
    }


    //unregister the battery monitor
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.progressReceiver);
        this.unregisterReceiver(this.pollingReceiver);
        this.unregisterReceiver(this.batteryInfoReceiver);
    }


    public class PollingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //polling try
            if (intent.getAction().equals(POLLING_ACTION)) {
                startBenchmark();
            }
        }
    }


    public class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //benchmarck run stage report
            if (intent.getAction().equals(PROGRESS_BENCHMARK_ACTION)) {
                String prog = intent.getStringExtra("msg");
//                Toast.makeText(context, prog, Toast.LENGTH_SHORT).show();
                stateTextView.setText(prog);
                minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
            } else {
                if (intent.getAction().equals(END_BENCHMARK_ACTION)) {
                    Toast.makeText(context, "Run stage finished", Toast.LENGTH_SHORT).show();
                    stateTextView.setText("Run stage finished");
                    String variant = intent.getStringExtra("variant");
                    String fname = intent.getStringExtra("file");
                    byte[] result = null;

                    try {
                        File file = new File(fname);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        result = new byte[(int) file.length()];
                        fileInputStream.read(result);
                    } catch (IOException e) {
                        Toast.makeText(context, "file not found", Toast.LENGTH_SHORT).show();
                    }
                    if (result != null){
                        stateTextView.setText("send results");
                        serverConnection.sendResult(resultSendCb, onError, getApplicationContext(), result, "run", variant);
                    }
                    else
                        Toast.makeText(context, "no results", Toast.LENGTH_SHORT).show();

                    running = false;
                    if (benchmarkExecutor.hasMoreToExecute()) {
                        stateOfCharge = benchmarkExecutor.getNeededBatteryState();
                        minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
                        startBenchmark();
                    } else
                        Toast.makeText(context, "There is no more benchmark", Toast.LENGTH_SHORT).show();
                }
            }


            //benchmarck sampling stage report
            if (intent.getAction().equals(PROGRESS_SAMPLING_ACTION)) {
                String prog = intent.getStringExtra("msg");
//                Toast.makeText(context, prog, Toast.LENGTH_SHORT).show();
                stateTextView.setText(prog);
                minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
            } else {

                if (intent.getAction().equals(END_SAMPLING_ACTION)) {
                    Toast.makeText(context, "Sampling finished", Toast.LENGTH_SHORT).show();
                    stateTextView.setText("Sampling finished");
                    //String result = intent.getStringExtra("payload");
                    String variant = intent.getStringExtra("variant");
                    String fname = intent.getStringExtra("file");
                    byte[] result = null;
                    try {
                        File file = new File(fname);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        result = new byte[(int) file.length()];
                        fileInputStream.read(result);
                    } catch (IOException e) {
                        Toast.makeText(context, "file not found", Toast.LENGTH_SHORT).show();
                    }

                    if (result != null) {
                        Log.d(TAG, "onReceive: " + result.length);
                        Toast.makeText(context, "Send Results Sampling", Toast.LENGTH_SHORT).show();
                        serverConnection.sendResult(resultSendCb, onError, getApplicationContext(), result, "sampling", variant);
                    }
                    else
                        Toast.makeText(context, "no results", Toast.LENGTH_SHORT).show();

                    running = false;
                    if (benchmarkExecutor.hasMoreToExecute()) {
                        stateOfCharge = benchmarkExecutor.getNeededBatteryState();
                        minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
                        startBenchmark();
                    } else
                        Toast.makeText(context, "There is no more benchmark", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
