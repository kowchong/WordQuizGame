package com.example.wordquizgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TestListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        String [] items = new String[] {"aaa","bbb","ccc"} ;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                items
        );

        ListView list = (ListView)findViewById(R.id.list_view);
        list.setAdapter(adapter); //ผูก android กับ object ของ java เข้าด้วยกัน
    }

}
