package com.example.wordquizgame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            String msg = null;
            switch (id) {
                case R.id.play_game_button:
                    msg = "Play Game";
                    //showChooseDiffDialog();
                    showCustomChooseDiffdialog();

                    break;
                case R.id.high_score_button:
                    msg = "High Score";
                    Intent intent = new Intent(MainActivity.this, HighScoreActivity.class);
                    startActivity(intent);
                    break;
            }

            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    };

    private void showCustomChooseDiffdialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("เลือกระดับความยาก");

        final String[] items = new String[]{"ง่าย", "ปานกลาง", "ยาก"};
        DifficultyOptionsAdapter adapter = new DifficultyOptionsAdapter(
                this,
                R.layout.difficulty_row,
                items
        );
        dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "You choosed " + items[which]);

                Intent i = new Intent(MainActivity.this, GameActivity.class);
                i.putExtra(GameActivity.KEY_DIFFICULTY, which);
                startActivity(i);
            }
        });
        dialog.show();
    }

    // ทำ
    private static class DifficultyOptionsAdapter extends ArrayAdapter<String> {

        private Context context;
        private int itemLayoutId;
        private String[] items;

        public DifficultyOptionsAdapter(Context context, int itemLayoutId, String[] items) {
            super(context, itemLayoutId, items);
            this.context = context;
            this.itemLayoutId = itemLayoutId;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(itemLayoutId, parent, false);

            TextView difficultyTextView = (TextView) row.findViewById(R.id.difficultyTextView);
            ImageView difficultyImageView = (ImageView) row.findViewById(R.id.difficultyImageView);

            String diffText = items[position];
            difficultyTextView.setText(diffText);

            if (diffText.equals("ง่าย")) {
                difficultyImageView.setImageResource(R.drawable.dog_easy);
            } else if (diffText.equals("ปานกลาง")) {
                difficultyImageView.setImageResource(R.drawable.dog_medium);
            } else if (diffText.equals("ยาก")) {
                difficultyImageView.setImageResource(R.drawable.dog_hard);
            }

            return row;
        }
    }

    private void showChooseDiffDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("เลือกระดับความยาก");
        final String[] items = new String[]{"ง่าย", "ปานกลาง", "ยาก"};
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "You choosed " + items[which]);

                Intent i = new Intent(MainActivity.this, GameActivity.class);
                i.putExtra(GameActivity.KEY_DIFFICULTY, which);
                startActivity(i);

            }
        });

        dialog.show();
    }

    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playGameButton = (Button) findViewById(R.id.play_game_button);
        playGameButton.setOnClickListener(listener);

        Button highScoreButton = (Button) findViewById(R.id.high_score_button);
        highScoreButton.setOnClickListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        Music.play(this, R.raw.main);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        Music.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }
}
