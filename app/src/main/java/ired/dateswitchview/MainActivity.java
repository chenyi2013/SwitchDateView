package ired.dateswitchview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<DateSwitchView.GraphData> data = new ArrayList<>();

        data.add(new DateSwitchView.GraphData("01", "明天", true));
        data.add(new DateSwitchView.GraphData("02", "周二", true));
        data.add(new DateSwitchView.GraphData("03", "周三", false));
        data.add(new DateSwitchView.GraphData("04", "周四", true));
        data.add(new DateSwitchView.GraphData("05", "周五", false));
        data.add(new DateSwitchView.GraphData("06", "周六", true));
        data.add(new DateSwitchView.GraphData("07", "周日", true));
        data.add(new DateSwitchView.GraphData("08", "周一", true));

        DateSwitchView dateSwitchView = findViewById(R.id.date_switch_view);
        //可以不传默认为7
        dateSwitchView.setShowDateCount(7);
        //设置默认选中第二个
        dateSwitchView.setCurrentIndex(2);
        dateSwitchView.setData(data);

        dateSwitchView.setOnItemClickListener(new DateSwitchView.OnItemClickListener() {
            @Override
            public void onClick(int position, DateSwitchView.GraphData data) {
                Log.i("kevin", "position:" + position + " date:" + data.date);
            }
        });


    }
}
