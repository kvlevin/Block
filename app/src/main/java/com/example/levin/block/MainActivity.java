package com.example.levin.block;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    private int tableWidth;
    private int tableHeight;
    private static int BLOCK_WIDTH;
    private static int BLOCK_HEIGHT;
    private  Block[] blocks=new Block[5];
    private int firstBlock=0;
    private int currentPosition;
    private Random rand=new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        for(int i=0;i<5;i++)
            blocks[i]=new Block();
        final GameView gameView=new GameView(this);
        setContentView(gameView);
        WindowManager windowManager=getWindowManager();
        Display display=windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics=new DisplayMetrics();
        display.getMetrics(displayMetrics);
        tableWidth=displayMetrics.widthPixels;
        tableHeight=displayMetrics.heightPixels;
        BLOCK_HEIGHT=tableHeight/4;
        BLOCK_WIDTH=tableWidth/4;
        final Handler mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0x123)
                    gameView.invalidate();
            }
        };
        SensorManager sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size();
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    float x=event.getX();
                    float y=event.getY();
                    if(x>blocks[firstBlock].position*BLOCK_WIDTH&&x<(blocks[firstBlock].position*BLOCK_WIDTH+BLOCK_WIDTH)&&y<currentPosition&&y>(currentPosition-BLOCK_HEIGHT)){
                        blocks[firstBlock].newPosition();
                        firstBlock=(firstBlock+1)%5;
                        currentPosition-=BLOCK_HEIGHT;
                    }
                }
                return true;
            }
        });
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                currentPosition+=3;
                //mHandler.sendEmptyMessage(0x123);
                if(currentPosition>tableHeight){
                    blocks[firstBlock].newPosition();
                    firstBlock=(firstBlock+1)%5;
                    currentPosition-=BLOCK_HEIGHT;
                    //cancel();
                }
            }
        },0,3);
    }
    class GameView extends View{
        private Paint paint;
        public GameView(Context context) {
            super(context);
            paint=new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for(int i=0;i<5;i++){
                canvas.drawRect(blocks[(i+firstBlock)%5].position*BLOCK_WIDTH,
                        currentPosition-BLOCK_HEIGHT*i-BLOCK_HEIGHT,
                        blocks[(i+firstBlock)%5].position*BLOCK_WIDTH+BLOCK_WIDTH,
                        currentPosition-BLOCK_HEIGHT*i,
                        paint);
            }
            invalidate();
        }
    }
     class Block{
        public int position;
        public void newPosition(){
            position=Math.abs(rand.nextInt(4));
        }
        public Block(){
            position=Math.abs(rand.nextInt(4));
        }
    }

}
