package com.example.wordquizgame;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wordquizgame.db.DatabaseHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = GameActivity.class.getSimpleName();
    public static final String KEY_DIFFICULTY = "diff";
    private static final int NUM_QUESTIONS_PER_QUIZ = 3;


    private TextView mQuestionNumberTextView;
    private ImageView mQuestionImageView;
    private TableLayout mButtonTableLayout;
    private TextView mAnswerTextView;

    private int mDifficulty;
    private int mNumChoices;

    private ArrayList<String> mFileNameList = new ArrayList<>();
    private ArrayList<String> mQuizWordList = new ArrayList<>();
    private ArrayList<String> mChoiceWordList = new ArrayList<>();

    private String mAnswerFileName;
    private int mTotalGuesses;
    private int mScore;

    private Random mRandom = new Random();
    private Handler mHandler = new Handler();

    private Animation shakeAnimation;

    private DatabaseHelper mHelper;
    private SQLiteDatabase mDatabase;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //------เชื่อมต่อฐานข้อมูล-------
        mHelper = new DatabaseHelper(this);
        mDatabase = mHelper.getWritableDatabase();
        //--------------------------

        if (savedInstanceState == null) {
            Toast.makeText(this, "Saved instance state = NULL.", Toast.LENGTH_SHORT).show();
        } else {
            String answer = savedInstanceState.getString("answer");
            Toast.makeText(this, answer, Toast.LENGTH_SHORT).show();
        }

        setupViews();

        Intent i = getIntent();
        mDifficulty = i.getIntExtra(KEY_DIFFICULTY, -1);
        Log.i(TAG, "diff is " + mDifficulty + ".");
        switch (mDifficulty) {
            case 0: //ง่าย
                mNumChoices = 2;
                break;
            case 1: //ปานกลาง
                mNumChoices = 4;
                break;
            case 2: //ยาก
                mNumChoices = 6;
                break;
        }

        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);// เก็บ animation ที่เป็น xml ไฟล์เอามาใช้
        shakeAnimation.setRepeatCount(3);

        getImageFileNames(); //เก็บชื่อรูปภาพทั้งหมด เพื่อจะเอามาเป็นคำตอบต่อไป
    }

    private void setupViews() {
        mQuestionNumberTextView = (TextView) findViewById(R.id.question_number_text_view);
        mQuestionImageView = (ImageView) findViewById(R.id.question_image_view);
        mButtonTableLayout = (TableLayout) findViewById(R.id.button_table_layout);
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
    }

    private void getImageFileNames() {
        String[] categories = new String[]{
                "animals", "body", "colors", "numbers", "objects"
        };

        AssetManager am = getAssets();

        for (String c : categories) {
            try {
                String[] fileNames = am.list(c);

                for (String f : fileNames) {
                    mFileNameList.add(f.replace(".png", ""));//ตัดString .png ทิ้ง(แทนที่ด้วย String เปล่า)
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error listing file name in " + c + " .");
            }
        }

        Log.i(TAG, "***** รายชื่อไฟล์ภาพทั้งหมด *****");
        for (String f : mFileNameList) {
            Log.i(TAG, f);
        }

        startQuiz();
    }

    private void startQuiz() {
        mScore = 0;
        mTotalGuesses = 0;
        mQuizWordList.clear();
        while (mQuizWordList.size() < NUM_QUESTIONS_PER_QUIZ) {
            int randomIndex = mRandom.nextInt(mFileNameList.size());
            String fileName = mFileNameList.get(randomIndex);
            if (!mQuizWordList.contains(fileName)) {
                mQuizWordList.add(fileName);
            }
        }
        Log.i(TAG, "***** รายชื่อไฟล์คำถามที่สุ่มได้ *****");
        for (String s : mQuizWordList) {
            Log.i(TAG, s);
        }

        loadNextQuestion();
    }

    private void loadNextQuestion() {
        mAnswerTextView.setText(null);
        mAnswerFileName = mQuizWordList.remove(0);

        String msg = String.format(
                "คำถาม %d จาก %d",
                mScore + 1,
                NUM_QUESTIONS_PER_QUIZ);
        mQuestionNumberTextView.setText(msg);

        Log.i(TAG, "***** ชื่อไฟล์รูปภาพคำถามคือ " + mAnswerFileName + " *****");
        loadQuestionImage();
        prepareChoiceWord();
        //String category =
    }


    private void loadQuestionImage() {
        String category = mAnswerFileName.substring(0, mAnswerFileName.indexOf('-'));
        String filePath = category + "/" + mAnswerFileName + ".png";
        AssetManager am = getAssets();

        try {
            InputStream stream = am.open(filePath);
            Drawable image = Drawable.createFromStream(stream, filePath);
            mQuestionImageView.setImageDrawable(image);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error loading image file: " + filePath);
        }


    }

    private void prepareChoiceWord() {
        mChoiceWordList.clear();
        String answerWord = getWord(mAnswerFileName);

        while (mChoiceWordList.size() < mNumChoices) {
            int randomIndex = mRandom.nextInt(mFileNameList.size());
            String randomWord = getWord(mFileNameList.get(randomIndex));
            if (!mChoiceWordList.contains(randomWord) && !answerWord.equals(randomWord)) {
                mChoiceWordList.add(randomWord);
            }
        }
        int randomIndex = mRandom.nextInt(mChoiceWordList.size());
        mChoiceWordList.set(randomIndex, answerWord);

        Log.i(TAG, "***** คำศัพท์ตัวเลือกที่สุ่มได้ *****");
        for (String w : mChoiceWordList) {
            Log.i(TAG, w);
        }

        createChoiceButtons();

    }

    private void createChoiceButtons() {
        for (int row = 0; row < mButtonTableLayout.getChildCount(); row++) {
            TableRow tr = (TableRow) mButtonTableLayout.getChildAt(row);// เข้าถึง tableRow
            tr.removeAllViews(); //ลบปุ่มในแถวนั้นทิ้ง
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int row = 0; row < mNumChoices / 2; row++) {
            TableRow tr = (TableRow) mButtonTableLayout.getChildAt(row);
            for (int column = 0; column < 2; column++) {
                Button guessButton = (Button) inflater.inflate(R.layout.guess_button, tr, false); // ให้ inflater คำนวณ แต่ false คืออย่าเพิ่งใส่
                guessButton.setText(mChoiceWordList.remove(0));
                guessButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitGuess((Button) v);
                    }
                });
                tr.addView(guessButton);
            }
        }
    }

    private void submitGuess(Button guessButton) {
        Log.i(TAG, "You selected " + guessButton.getText().toString());
        mTotalGuesses++;

        String guessWord = guessButton.getText().toString();
        String answerWord = getWord(mAnswerFileName);

        //ตอบถูก
        if (guessWord.equals(answerWord)) {
            mScore++;

            //เล่นไฟล์เสียง
            MediaPlayer mp = MediaPlayer.create(this, R.raw.applause);
            mp.start();

            String msg = guessWord + " ถูกต้องนะคร้าบบ";
            mAnswerTextView.setText(msg);
            mAnswerTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));

            disableAllButtons();

            //ตอบถูกและเล่นครบทุกข้อแล้ว
            if (mScore == NUM_QUESTIONS_PER_QUIZ) {
                saveScore();

                String msgResult = String.format(
                        "จำนวนครั้งที่ทาย: %d\nเปอร์เซ็นต์ความถูกต้อง: %.1f",
                        mTotalGuesses,
                        100 * NUM_QUESTIONS_PER_QUIZ / (double) mTotalGuesses
                );

                new AlertDialog.Builder(this)
                        .setTitle("สรุปผล")
                        .setMessage(msgResult)
                        .setCancelable(false) //ตั้งให้ไม่สามารถกดปุ่ม Back ได้
                        .setPositiveButton("เริ่มเกมใหม่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startQuiz();
                            }
                        })
                        .setNegativeButton("กลับหน้าหลัก", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();

            }
            //ตอบถูกแต่ยังไม่จบเกม
            else {
                mHandler.postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                loadNextQuestion(); // ดีเลย์ 2000ms แล้วเรียก loadNextQuestion()
                            }
                        },
                        2000
                );
            }

        }
        //ตอบผิด
        else {
            mQuestionImageView.startAnimation(shakeAnimation);

            MediaPlayer mp = MediaPlayer.create(this, R.raw.fail);
            mp.start();

            String msg = "ผิดครับ ลองใหม่นะครับ";
            mAnswerTextView.setText(msg);
            mAnswerTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            guessButton.setEnabled(false);

        }

    }


    private String getWord(String fileName) {
        return fileName.substring(fileName.indexOf('-') + 1);
    }

    private void disableAllButtons() {
        for (int row = 0; row < mButtonTableLayout.getChildCount(); row++) {
            TableRow tr = (TableRow) mButtonTableLayout.getChildAt(row);

            for (int column = 0; column < tr.getChildCount(); column++) {
                Button b = (Button) tr.getChildAt(column);
                b.setEnabled(false);
            }
        }
    }

    private void saveScore() { //เก็บคะแนนลง Database
        ContentValues cv = new ContentValues();

        double percentScore = 100 * NUM_QUESTIONS_PER_QUIZ / (double) mTotalGuesses;
        cv.put(DatabaseHelper.COL_SCORE, percentScore);
        cv.put(DatabaseHelper.COL_DIFFICULTY, mDifficulty);

        long result = mDatabase.insert(DatabaseHelper.TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(this, "Error saving data.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Save data successfully. ID: " + result, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("answer", mAnswerFileName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Music.play(this, R.raw.game);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Music.stop();
    }
}
