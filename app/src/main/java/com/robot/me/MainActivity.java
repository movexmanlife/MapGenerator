package com.robot.me;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.robot.me.R;
import com.robotlife.compiler.inject.BMUtils;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Worker worker = new Worker();
        worker.age = 12;
        worker.name = "jack";
        Map<String, String> maps = BMUtils.toMap(worker);

        showContent(maps);
    }

    private void showContent(Map<String, String> maps) {
        String content = "";
        for (Map.Entry<String, String> map: maps.entrySet()) {
            String str = map.getKey() + ":" + map.getValue() + " ";
            content += str;
        }
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
