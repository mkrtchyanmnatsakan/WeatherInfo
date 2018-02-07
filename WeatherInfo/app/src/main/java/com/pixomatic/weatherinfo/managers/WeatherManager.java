package com.pixomatic.weatherinfo.managers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.pixomatic.weatherinfo.R;
import com.pixomatic.weatherinfo.base.BaseActivity;
import com.pixomatic.weatherinfo.listeners.NetworkRequestListener;
import com.pixomatic.weatherinfo.models.api.ApiRequest;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by mno on 2/6/18.
 */

public class WeatherManager {

    private BaseActivity mBaseActivity;
    KProgressHUD hud;

    public WeatherManager(BaseActivity baseActivity) {
        mBaseActivity = baseActivity;
    }

    public void getWeatherResponse(final NetworkRequestListener listener, String id,String appId) {


        if(isOnline()){
            showHud();

            ApiRequest apiRequest = mBaseActivity.retrofit.create(ApiRequest.class);
            Call<JsonElement> call = apiRequest.getWeatherResponse(id,appId);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull retrofit2.Response<JsonElement> response) {
                    if (hud != null) {
                        hud.dismiss();
                    }

                    if ( response.body() != null && response.body().getAsJsonObject()!= null)  {

                        if(response.body().getAsJsonObject().get("data")!= null )
                            listener.onSuccess(new Gson().toJson(response.body()));
                    } else {

                        listener.onError("Error");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    if (hud != null) {
                        hud.dismiss();
                    }


                        listener.onError("Error: " + t.getMessage());

                }
            });
        }else {


                    new MaterialDialog.Builder(mBaseActivity)
                            .titleColorRes(R.color.colorPrimary)
                            .title("Please turn on the internet")
                            .positiveText("yes")
                            .show();
        }


    }

    private void showHud() {
        if (hud != null) {
            hud.dismiss();
        }

        hud = KProgressHUD.create(mBaseActivity)
                .setWindowColor(mBaseActivity.getResources().getColor(R.color.colorPrimary))
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        hud.show();
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mBaseActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
