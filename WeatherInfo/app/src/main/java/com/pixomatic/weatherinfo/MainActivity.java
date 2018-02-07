package com.pixomatic.weatherinfo;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.pixomatic.weatherinfo.adapters.InfoWeatherAdapter;
import com.pixomatic.weatherinfo.base.BaseActivity;
import com.pixomatic.weatherinfo.listeners.NetworkRequestListener;
import com.pixomatic.weatherinfo.models.WeatherResponse;
import java.util.LinkedList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity  {


    @BindView(R.id.select_count_relative)
    RelativeLayout selectCountRelative;

    @BindView(R.id.weather_recycler)
    RecyclerView weatherRecycler;

    private InfoWeatherAdapter mInfoWeatherAdapter;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

  }

    private void initViews(){

        final LinkedList<String> mTitleList = new LinkedList<>();
        mTitleList.add("Yerevan,AM");
        mTitleList.add("Moscow,Ru");
        mTitleList.add("New York,US");
        mTitleList.add("Rome,IT");
        mTitleList.add("Tbilisi,GE");

        selectCountRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(MainActivity.this)
                        .items(mTitleList)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                getWeatherManager().getWeatherResponse(new NetworkRequestListener() {
                                    @Override
                                    public void onSuccess(String stringJson) {

                                        WeatherResponse weatherResponse = new Gson().fromJson(stringJson,WeatherResponse.class);
                                        if(weatherResponse != null && weatherResponse.getData()!= null && !weatherResponse.getData().isEmpty()){
                                            if(mInfoWeatherAdapter != null && !mInfoWeatherAdapter.getmDatumList().isEmpty()){
                                                if(weatherResponse.getData()!= null && !weatherResponse.getData().isEmpty()){
                                                    mInfoWeatherAdapter.addDatumList(weatherResponse.getData().get(0));
                                                    mInfoWeatherAdapter.notifyDataSetChanged();
                                                    weatherRecycler.scrollToPosition(mInfoWeatherAdapter.getmDatumList().size()-1);
                                                }

                                            }else {
                                                if(weatherResponse.getData()!=null && !weatherResponse.getData().isEmpty()){
                                                    mInfoWeatherAdapter = new InfoWeatherAdapter(MainActivity.this,weatherResponse.getData());
                                                    weatherRecycler.setAdapter(mInfoWeatherAdapter);
                                                    linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                                                    weatherRecycler.setLayoutManager(linearLayoutManager);
                                                    weatherRecycler.setHasFixedSize(false);
                                                }

                                            }

                                        }

                                    }

                                    @Override
                                    public void onError(String error) {

                                                new MaterialDialog.Builder(MainActivity.this)
                                                        .titleColorRes(R.color.colorPrimary)
                                                        .title(error)
                                                        .positiveText("yes")
                                                        .show();

                                    }
                                },text.toString(),getResources().getString(R.string.weather_bit_io_api_key));
                            }
                        })

                        .show();


            }
        });
    }


    @Override
    protected void onResume() {
        initViews();
        super.onResume();

    }
}
