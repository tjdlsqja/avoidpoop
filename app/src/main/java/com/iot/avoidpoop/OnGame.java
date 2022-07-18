package com.iot.avoidpoop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;
import java.util.WeakHashMap;

public class OnGame extends AppCompatActivity {

    ImageView ddong[];
    ImageView leftperson;
    ImageView rightbtn;
    ImageView leftbtn;
    TextView scoreText;
    MediaPlayer mediaPlayer;
    ConstraintLayout constraintLayout;
    ddongFallHandler ddongFallHandler[];
    crashHander crashHander;
    GameOverHandler gameOverHandler;
    static final int speed = 20;
    int score = 0;
    static final int WHAT = 1;
    static final long DELAY = 33;
    boolean gameover = false;
    long RAMDOM_DELAY;

    Handler leftMoveHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            moveLeft(speed);
            sendEmptyMessageDelayed(WHAT, DELAY);
        }

    };
    Handler rightMoveHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            moveRight(speed);
            sendEmptyMessageDelayed(WHAT, DELAY);
        }
    };

    public void moveLeft(int n) {
        if (ddong[1].getX() < leftperson.getX())
            leftperson.setX(leftperson.getX() - n);
        else
            leftperson.setX(ddong[1].getX());
    }

    public void moveRight(int n) {
        if (ddong[10].getX() + ddong[10].getWidth() > leftperson.getX() + leftperson.getWidth())
            leftperson.setX(leftperson.getX() + n);
        else
            leftperson.setX(ddong[10].getX() + ddong[10].getWidth() - leftperson.getWidth());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_game);

        ddong = new ImageView[11];
        ddong[1] = findViewById(R.id.ddong1);
        ddong[2] = findViewById(R.id.ddong2);
        ddong[3] = findViewById(R.id.ddong3);
        ddong[4] = findViewById(R.id.ddong4);
        ddong[5] = findViewById(R.id.ddong5);
        ddong[6] = findViewById(R.id.ddong6);
        ddong[7] = findViewById(R.id.ddong7);
        ddong[8] = findViewById(R.id.ddong8);
        ddong[9] = findViewById(R.id.ddong9);
        ddong[10] = findViewById(R.id.ddong10);
        leftperson = findViewById(R.id.lperson);
        rightbtn = findViewById(R.id.rightbtn);
        leftbtn = findViewById(R.id.leftbtn);
        scoreText = findViewById(R.id.scoreText);
        constraintLayout = findViewById(R.id.constraintLayout);

        ddongFallHandler = new ddongFallHandler[11];
        for (int i = 1; i <= 10; i++) {
            float ran_y = (float) Math.random() * 500;
            ddong[i].setY(ddong[i].getY() + ran_y);
            RAMDOM_DELAY = (long)(Math.random() * 16) + 15;
            ddongFallHandler[i] = new ddongFallHandler(i, RAMDOM_DELAY);
            ddongFallHandler[i].sendEmptyMessageDelayed(WHAT, RAMDOM_DELAY);
        }

        crashHander = new crashHander();
        crashHander.sendEmptyMessageDelayed(WHAT, DELAY);

        leftbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightMoveHandler.removeMessages(1);
                leftMoveHandler.sendEmptyMessageDelayed(WHAT, DELAY);
            }
        });

        rightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftMoveHandler.removeMessages(1);
                rightMoveHandler.sendEmptyMessageDelayed(WHAT, DELAY);
            }
        });
        gameOverHandler = new GameOverHandler();
        gameOverHandler.sendEmptyMessageDelayed(WHAT, DELAY);
    }

    public class ddongFallHandler extends Handler {
        int num;
        long delay;

        public ddongFallHandler(int n, long r) {
            num = n;
            delay = r;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if ((ddong[num].getY() + ddong[num].getHeight()) >= leftperson.getY() + leftperson.getHeight())//똥이 다 떨어지면
            {
                delay = setddongTop(num);
            }
            else{
                ddong[num].setY(ddong[num].getY() + 10);
            }
            sendEmptyMessageDelayed(WHAT, delay);
        }
    }

    private long setddongTop(int num) {
        long delay = (long)(Math.random() * 16) + 15;
        ddong[num].setY(0);
        score++;
        scoreText.setText(String.valueOf(score));

        return delay;
    }

    public class crashHander extends Handler{
        public crashHander(){

        }
        @Override
        public void handleMessage(Message Msg){
            crashObserber();
            if(gameover == true){
                removeMessages(WHAT);
            }
            crashHander.sendEmptyMessageDelayed(WHAT, DELAY);
        }


    }

    public boolean crashObserber(){
        boolean Obserber = false;
        Rect person = new Rect();
        person.left = (int) leftperson.getX();
        person.right = (int) leftperson.getX() + leftperson.getWidth();
        person.top = (int) leftperson.getY();
        person.bottom = (int) leftperson.getY() + leftperson.getHeight();

        for(int i = 1; i < 11; i++){
            Obserber =person.contains((int)ddong[i].getX()+ddong[i].getWidth()/2, (int)ddong[i].getY()+ddong[i].getHeight());
            if(Obserber == true){
                gameover = true;
            }

        }
        return gameover;
    }


    public class GameOverHandler extends Handler{
        public GameOverHandler(){
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            gameover(gameover);
            sendEmptyMessageDelayed(WHAT, DELAY);
        }
    }
    public void gameover(boolean gameover){
        if(gameover == true){
            Intent intent = new Intent(OnGame.this, Overgame.class);
            startActivity(intent);
        }
    }

    public int getScore(){
        return score;
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameOverHandler.removeMessages(WHAT);
        leftMoveHandler.removeMessages(WHAT);
        rightMoveHandler.removeMessages(WHAT);
        for(int i = 1; i < 11; i++) {
            ddongFallHandler[i].removeMessages(WHAT);
            mediaPlayer = MediaPlayer.create(OnGame.this, R.raw.dead);
            mediaPlayer.start();
        }
        finish();
    }

}





