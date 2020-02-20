package edu.benchmarkandroid.connection;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;

import edu.benchmarkandroid.Benchmark.jsonConfig.BenchmarksResponse;
import edu.benchmarkandroid.model.UpdateData;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConnectionHandler {

    private static final String TAG = "ConnectionHandler";


    private Retrofit retrofit;
    private ServerListener listener;
    private String model;
    private ConnectionClient connectionClient;


    public ConnectionHandler(ServerListener listener, String baseUrl, String model) {
        this.listener = listener;
        this.model = model;

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        connectionClient = retrofit.create(ConnectionClient.class);
    }


    public void getBenchmarks() {

        final Call<BenchmarksResponse> response = connectionClient.getBenchmarks(model, "init");

        response.enqueue(new Callback<BenchmarksResponse>() {
            @Override
            public void onResponse(Call<BenchmarksResponse> call, Response<BenchmarksResponse> response) {
                Log.d(TAG, "onResponse: getBenchmarks");
                if (response.isSuccessful()) {
                    BenchmarksResponse benchmarksResponse = response.body();
                    if (benchmarksResponse != null) {
                        Log.d(TAG, "onResponse: benchmarksResponse: \n" + benchmarksResponse.toString());
                        listener.onSuccessGetBenchmarks(benchmarksResponse.getBenchmarkData());
                    } else
                        Log.d(TAG, "onResponse: benchmarksResponse: null");


                } else {
                    Log.d(TAG, "onResponse: benchmarksResponse: response.isSuccessful() == false");
                }

            }

            @Override
            public void onFailure(Call<BenchmarksResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: getBenchmarks");
                listener.onFailureGetBenchmarks();
            }
        });
    }


    public void startBenchmark(String stateOfCharge) {

        final Call<JsonObject> response = connectionClient.startBenchmark(model, "postinit", stateOfCharge);
        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful() && response.body().get("message").getAsBoolean() == true) {
                    listener.onSuccessStartBenchmark();

                } else {
                    listener.onFailureStartBenchmark();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: startBenchmark");
                listener.onFailureStartBenchmark();
            }
        });
    }


    public void putUpdateBatteryState(UpdateData updateData) {

        final Call<JsonObject> response = connectionClient.putUpdateBatteryState(model, updateData);

        response.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG, "onResponse: putUpdateBatteryState ");
                if (response.isSuccessful()) {
                    listener.onSuccessUpdateBatteryState();
                    Log.d(TAG, "onResponse: response: \n" + response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure:  putUpdateBatteryState ");
                listener.onFailureUpdateBatteryState();
            }
        });
    }


    public void postResult(String filename, String localFilePath) {

        File file = new File(localFilePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("data", file.getName(), requestFile);


        Call<ResponseBody> call = connectionClient.postResult(model, filename, body);
        Log.d(TAG, "postResult: FILE EXISTS:" + file.exists() +" - "+ file.length());
        Log.d(TAG, "postResult: fname: "+ filename+ " path: "+ localFilePath);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: postResult: isSuccessful");
                    listener.onSuccessPostResult();
                } else {
                    Log.d(TAG, "onResponse_ postResult: notSuccessful");
                    listener.onFailurePostResult();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: postResult: ");
                listener.onFailurePostResult();
            }
        });


    }

}
