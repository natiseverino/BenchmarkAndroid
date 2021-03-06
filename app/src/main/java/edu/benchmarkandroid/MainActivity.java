package edu.benchmarkandroid;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import edu.benchmarkandroid.Benchmark.jsonConfig.BenchmarkData;
import edu.benchmarkandroid.connection.ConnectionHandler;
import edu.benchmarkandroid.connection.ServerListener;
import edu.benchmarkandroid.model.UpdateData;
import edu.benchmarkandroid.service.BatteryNotificator;
import edu.benchmarkandroid.service.BenchmarkExecutor;
import edu.benchmarkandroid.utils.BatteryUtils;
import edu.benchmarkandroid.utils.CPUUtils;
import edu.benchmarkandroid.utils.LogGUI;
import edu.benchmarkandroid.utils.Logger;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.PARTIAL_WAKE_LOCK;
import static android.os.PowerManager.SCREEN_DIM_WAKE_LOCK;
import static edu.benchmarkandroid.service.BenchmarkIntentService.END_BENCHMARK_ACTION;
import static edu.benchmarkandroid.service.BenchmarkIntentService.PROGRESS_BENCHMARK_ACTION;
import static edu.benchmarkandroid.service.SamplingIntentService.END_SAMPLING_ACTION;
import static edu.benchmarkandroid.service.SamplingIntentService.PROGRESS_SAMPLING_ACTION;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";


    // CHANGE THIS CONSTANTS TO THE VALUE OF YOUR PREFERENCE
    public static final String PATH = "/sdcard/Download/";
    public static final int INTERVAL_OFF_BATTERY_UPDATES = 5000;


    public static final String NOT_DEFINED = "notdefined";
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 53;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 3;


    // Services
    private BatteryNotificator batteryNotificator;

    private long timeOfLastBatteryUpdate;
    private BenchmarkExecutor benchmarkExecutor;

    // Condicions for postInitPayload
    public String stateOfCharge = NOT_DEFINED;
    public double minBatteryLevel;


    private static final String CHARGING = "Charging";
    private static final String DISCHARGING = "Discharging";


    //mutex
    private Boolean running = false;
    private Boolean evaluating = false;


    // Suscriber to battery notifications from the OS
    private ProgressReceiver progressReceiver;

    // Receiver for updates from the benchmark run
    private BatteryInfoReceiver batteryInfoReceiver;


    // View components
    private EditText ipEditText;
    private EditText portEditText;
    private TextView ipTextView;
    private TextView portTextView;
    private TextView modelTextView;
    private Button startButton;
    private Button stopButton;
    private TextView stateTextView;
    private TextView logTextView;
    private ScrollView scrollView;


    public static PowerManager.WakeLock WAKE_LOCK;
    public static PowerManager.WakeLock LOCK_SCREEN;

    private static final String POWER_MANAGER_TAG = "MainActivity:PowerManagerTag";
    private boolean doPolling = false;

    public int deviceCpuMhz;
    public int deviceBatteryMah;

    private static String model;
    private String httpAddress = "192.168.0.";
    private String httpPort = "1080";


    boolean firstSuccess = false;
    ConnectionHandler connectionHandler;

    private static String FILENAME;
    private static String FNAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        ComponentName mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        this.startActivityForResult(intent, 0);

        acquirePowerManagerWakeLock();

        model = Build.MANUFACTURER + "_" + Build.MODEL;
        model = model.replace(" ", "_")
                .replace(")", "")
                .replace("(", "")
                .replace("-", "_");


        ipEditText = findViewById(R.id.IpText);
        ipTextView = findViewById(R.id.ipTextView);
        portTextView = findViewById(R.id.portTextView);
        portEditText = findViewById(R.id.portText);
        modelTextView = findViewById(R.id.modelTextView);
        stateTextView = findViewById(R.id.stateTextView);
        logTextView = findViewById(R.id.logTextView);
        scrollView = findViewById(R.id.scrollView);

        LogGUI.init(logTextView, scrollView);

        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        stopButton.setEnabled(false);

        ipTextView.setText(httpAddress);
        portTextView.setText(httpPort);
        modelTextView.setText(model);
        stateTextView.setText("Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);
        LogGUI.log("Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);


        deviceBatteryMah = BatteryUtils.getBatteryCapacity(this);
        deviceCpuMhz = CPUUtils.getMaxCPUFreqMHz();

        //set onChangeListener to display the complete formater url to the user
        bindInputToDisplayText(ipEditText, ipTextView);
        bindInputToDisplayText(portEditText, portTextView);

        loadServerConfigProperties();

        //initialize battery intents receiver
        this.batteryNotificator = BatteryNotificator.getInstance();
        batteryInfoReceiver = new BatteryInfoReceiver();
        this.registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        //set receiver for benchmark updates to the main thread
        IntentFilter filterProgressReceiver = new IntentFilter();
        filterProgressReceiver.addAction(PROGRESS_BENCHMARK_ACTION);
        filterProgressReceiver.addAction(END_BENCHMARK_ACTION);
        filterProgressReceiver.addAction(PROGRESS_SAMPLING_ACTION);
        filterProgressReceiver.addAction(END_SAMPLING_ACTION);
        progressReceiver = new ProgressReceiver();
        registerReceiver(progressReceiver, filterProgressReceiver);

        //initialize benchmark service
        this.benchmarkExecutor = new BenchmarkExecutor(getBaseContext());
        benchmarkExecutor.setStateTextView(stateTextView);


        final ServerListener listener = new ConnectionListener();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!firstSuccess) {
                    httpAddress = String.valueOf(ipTextView.getText());
                    httpPort = String.valueOf(portTextView.getText());
                    String serverUrl = String.format("http://%s:%s/dewsim/", httpAddress, httpPort);
                    connectionHandler = new ConnectionHandler(listener, serverUrl, model);
                    connectionHandler.putUpdateBatteryState(new UpdateData(deviceCpuMhz, deviceBatteryMah, minBatteryLevel, batteryNotificator.getCurrentLevel()));
                } else {
                    connectionHandler.getBenchmarks();
                }
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
            }

            case MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "STORAGE Permission Granted", Toast.LENGTH_SHORT).show();
                    loadServerConfigProperties();
                } else {
                    Toast.makeText(this, "We need this permission", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                }
        }
    }


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

    private void startBenchmark(Context context) {
        synchronized (evaluating) {
            if (!evaluating && !running) {
                evaluating = true;
                Log.d(TAG, "MainActivity - startBenchmark: CAN START");

                if (benchmarkExecutor.isKeepScreenOn()) {
                    turnScreenOn();
                } else {
                    turnScreenOff();
                }
                connectionHandler.startBenchmark(stateOfCharge);
            }
        }
    }

    //TODO notificaciones de please connect and disconncect the device


    //unregister the battery monitor
    @Override
    protected void onDestroy() {
        super.onDestroy();
        benchmarkExecutor.stopBenchmark();
        if (LOCK_SCREEN.isHeld())
            LOCK_SCREEN.release();
        if (WAKE_LOCK.isHeld())
            WAKE_LOCK.release();
        this.unregisterReceiver(this.progressReceiver);
        this.unregisterReceiver(this.batteryInfoReceiver);
    }


    // Private init methods ------------------------------------------------------------------------

    private void loadServerConfigProperties() {
        try {
            Properties serverConfigProperties = new Properties();
            serverConfigProperties.load(new FileInputStream(new File(PATH + "serverConfig.properties")));
            httpAddress = serverConfigProperties.getProperty("httpAddress");
            httpPort = serverConfigProperties.getProperty("httpPort");
            ipEditText.setText(httpAddress);
            portEditText.setText(httpPort);
        } catch (IOException e) {
            Log.e(TAG, "onCreate: Doesn't found " + PATH + "serverConfig.properties", e);
            ipEditText.setText(httpAddress);
            portEditText.setText(httpPort);
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        }

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void acquirePowerManagerWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        WAKE_LOCK = powerManager.newWakeLock(PARTIAL_WAKE_LOCK, "Service:Lock");
        LOCK_SCREEN = powerManager.newWakeLock(SCREEN_DIM_WAKE_LOCK | ACQUIRE_CAUSES_WAKEUP, "Service:Screen");


        if (WAKE_LOCK.isHeld()) {
            WAKE_LOCK.release();
        }
        WAKE_LOCK.acquire();
    }


    private void turnScreenOn() {
        Log.d(TAG, "turnScreenOn: ");

        if (!LOCK_SCREEN.isHeld())
            LOCK_SCREEN.acquire();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    private void turnScreenOff() {
        Log.d(TAG, "turnScreenOff: ");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (LOCK_SCREEN.isHeld()) {
            LOCK_SCREEN.release();
        }


        if (!WAKE_LOCK.isHeld()) {
            WAKE_LOCK.acquire(); // En caso de que lo alla perdido
        }

        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        devicePolicyManager.lockNow();
    }

    // Receiver Clases -----------------------------------------------------------------------------

    public class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //benchmarck run stage report
            if (intent.getAction().equals(PROGRESS_BENCHMARK_ACTION)) {
                String prog = intent.getStringExtra("msg");
                stateTextView.setText(prog);
                LogGUI.log(prog);
                minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
            } else {
                if (intent.getAction().equals(END_BENCHMARK_ACTION)) {

                    Toast.makeText(context, "Run stage finished", Toast.LENGTH_SHORT).show();
                    stateTextView.setText("Run stage finished");
                    LogGUI.log("Run stage finished");
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    String variant = intent.getStringExtra("variant");
                    String fname = intent.getStringExtra("file");

                    FNAME = intent.getStringExtra("file");

                    byte[] result = null;

                    try {
                        File file = new File(fname);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        result = new byte[(int) file.length()];
                        fileInputStream.read(result);
                    } catch (IOException e) {
                        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
                    }

                    String stage = "run";
//                    String filename = stage + "-" + variant + ".txt";
//                    connectionHandler.postResult(filename, fname);
                    FILENAME = stage + "-" + variant + ".txt";
                    connectionHandler.postResult(FILENAME, FNAME);

                }
            }


            //benchmarck sampling stage report
            if (intent.getAction().equals(PROGRESS_SAMPLING_ACTION)) {
                String prog = intent.getStringExtra("msg");
                stateTextView.setText(prog);
                LogGUI.log(prog);
                minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
            } else {

                if (intent.getAction().equals(END_SAMPLING_ACTION)) {

                    Toast.makeText(context, "Sampling finished", Toast.LENGTH_SHORT).show();
                    stateTextView.setText("Sampling finished");
                    LogGUI.log("Sampling finished");
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    String variant = intent.getStringExtra("variant");
                    String fname = intent.getStringExtra("file");

                    FNAME = intent.getStringExtra("file");

                    byte[] result = null;
                    try {
                        File file = new File(fname);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        result = new byte[(int) file.length()];
                        fileInputStream.read(result);
                    } catch (IOException e) {
                        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
                    }


                    String stage = "sampling";
//                    String filename = stage + "-" + variant + ".txt";
//                    connectionHandler.postResult(filename, fname);
                    FILENAME = stage + "-" + variant + ".txt";
                    connectionHandler.postResult(FILENAME, FNAME);


                }

            }
        }
    }


    public class BatteryInfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryNotificator.updateBatteryLevel((level / (double) scale));
            if ((System.currentTimeMillis() - timeOfLastBatteryUpdate) > INTERVAL_OFF_BATTERY_UPDATES) {
                timeOfLastBatteryUpdate = System.currentTimeMillis();
                if (connectionHandler != null)
                    connectionHandler.putUpdateBatteryState(new UpdateData(deviceCpuMhz, deviceBatteryMah, minBatteryLevel, batteryNotificator.getCurrentLevel()));
            }

            if (running) {


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
                    LogGUI.log(st.toString());
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "battery: Logger not found - fname: " + Logger.fname);
                }
            }


        }
    }


    private class ConnectionListener implements ServerListener {

        private static final String TAG = "ConnectionListener";

        @Override
        public void onSuccessUpdateBatteryState() {
            if (!firstSuccess) {
                firstSuccess = true;
                connectionHandler.getBenchmarks();
            }

        }

        @Override
        public void onFailureUpdateBatteryState() {

        }


        @Override
        public void onSuccessGetBenchmarks(BenchmarkData benchmarkData) {
            Toast.makeText(MainActivity.this, "Benchmarks received :)", Toast.LENGTH_SHORT).show();
            benchmarkExecutor.setBenchmarkData(benchmarkData);
            minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
            stateOfCharge = benchmarkExecutor.getNeededBatteryState();
            connectionHandler.putUpdateBatteryState(new UpdateData(deviceCpuMhz, deviceBatteryMah, minBatteryLevel, batteryNotificator.getCurrentLevel()));
            startBenchmark(getBaseContext());

        }

        @Override
        public void onFailureGetBenchmarks() {
            Log.d(TAG, "onFailureGetBenchmarks: retrying");
            connectionHandler.getBenchmarks();
        }


        @Override
        public void onSuccessStartBenchmark() {
            synchronized (evaluating) {
                if (!running) {
                    if (benchmarkExecutor.hasMoreToExecute()) {
                        running = true;
                        benchmarkExecutor.execute();
                        minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
                        connectionHandler.putUpdateBatteryState(new UpdateData(deviceCpuMhz, deviceBatteryMah, minBatteryLevel, batteryNotificator.getCurrentLevel()));
                    } else
                        Toast.makeText(MainActivity.this, "There is no more benchmarks", Toast.LENGTH_LONG).show();
                    evaluating = false;
                }
            }
        }

        @Override
        public void onFailureStartBenchmark() {
            evaluating = false;
        }


        @Override
        public void onSuccessPostResult() {
            running = false;
            if (benchmarkExecutor.hasMoreToExecute()) {
                stateOfCharge = benchmarkExecutor.getNeededBatteryState();
                minBatteryLevel = benchmarkExecutor.getNeededBatteryLevelNextStep();
                startBenchmark(getBaseContext());
            } else {
                Toast.makeText(getBaseContext(), "There are no more benchmarks", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        public void onFailurePostResult() {
            Toast.makeText(getBaseContext(), "error on post", Toast.LENGTH_LONG).show();
            connectionHandler.postResult(FILENAME, FNAME);
        }
    }


}