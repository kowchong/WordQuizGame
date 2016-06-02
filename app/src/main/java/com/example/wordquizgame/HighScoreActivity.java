package com.example.wordquizgame;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.wordquizgame.db.DatabaseHelper;

public class HighScoreActivity extends AppCompatActivity {
    private DatabaseHelper mHelper;
    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        mHelper = new DatabaseHelper(this);
        mDatabase = mHelper.getWritableDatabase();

        //ถ้าอยากใช้ภาษา SQL ให้ใช้คำสั่ง rawQuery
        Cursor cursor = mDatabase.query(
                true,
                DatabaseHelper.TABLE_NAME,
                null, //เอาหมดทุก column (บังคับ ไม่งั้น force close)
                null,
                //DatabaseHelper.COL_DIFFICULTY + " = ?",//เงื่อนไข
                //new String[]{"0"},
                null,
                DatabaseHelper.COL_SCORE,
                null,
                DatabaseHelper.COL_SCORE +" DESC",
                null
        );

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.high_score_row,
                cursor,
                new String[]{DatabaseHelper.COL_SCORE},
                new int[]{R.id.score_text_view},
                0
        );

        ListView highScoreListView = (ListView) findViewById(R.id.high_score_list_view);
        highScoreListView.setAdapter(adapter);

    }
}
