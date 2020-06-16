package com.bytedance.component.vedioplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private LinearLayout Linearlayout;
    private videoView videoView;
    private ImageView Pauseimg;
    private TextView TotalTime;
    private TextView CurTime;

    private int state;
    private int duration;
    private SeekBar VideoSeekBar;

    //当前屏幕的宽和屏幕的高
    private int screenWidth, screenHeight;

    //刷新标志
    private final int UPDATE_flag = 1;

    /**
     * 定义Handler刷新时间
     * 得到并设置当前视频播放的时间
     * 得到并设置视频播放的总时间
     * 设置SeekBar总进度和当前视频播放的进度
     * 并反复执行Handler刷新时间
     * 指定标识用于关闭Handler
     */

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("DefaultLocale")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_flag) {

                int currentPosition = videoView.getCurrentPosition();
                int total_duration = videoView.getDuration();

                TotalTime.setText(String.format("%02d:%2d",total_duration/1000/60,total_duration/1000%60));

                VideoSeekBar.setMax(total_duration);
                VideoSeekBar.setProgress(currentPosition);

                mHandler.sendEmptyMessageDelayed(UPDATE_flag, 500);

            }
        }
    };

    private String getVideoPath(int resId) {
        return "android.resource://" + this.getPackageName() + "/" + resId;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Linearlayout = findViewById(R.id.video_layout);
        videoView = findViewById(R.id.video_view);
        videoView.requestFocus();
        videoView.setVideoPath(getVideoPath(R.raw.bytedance));

        Pauseimg = findViewById(R.id.video_pause_img);
        TotalTime = findViewById(R.id.video_total_time);
        CurTime = findViewById(R.id.video_cur_time);
        VideoSeekBar = findViewById(R.id.video_seek_bar);

        state = 1;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        Pauseimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    mHandler.removeMessages(UPDATE_flag);
                    Pauseimg.setImageResource(R.mipmap.icon_video_play);
                } else {
                    videoView.start();
                    mHandler.sendEmptyMessageDelayed(UPDATE_flag, 500);
                    Pauseimg.setImageResource(R.mipmap.icon_video_pause);
                }
            }
        });

        VideoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CurTime.setText(String.format("%02d:%02d", progress / 1000 / 60, progress / 1000 % 60));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //拖动到时候关闭刷新机制
                mHandler.removeMessages(UPDATE_flag);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 当进度条停止修改的时候触发
                int progress_new = seekBar.getProgress();
                // 当前播放位置
                videoView.seekTo(progress_new);
                mHandler.sendEmptyMessage(UPDATE_flag);

            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onPrepared(final MediaPlayer mediaPlayer) {
                //获取时间
                duration = videoView.getDuration() / 1000;
                TotalTime.setText(String.format("%02d:%02d", duration / 60, duration % 60));
                VideoSeekBar.setMax(duration);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onCompletion(MediaPlayer mp) {
                Pauseimg.setImageResource(R.mipmap.icon_video_play);
                mHandler.removeMessages(UPDATE_flag);
                duration = videoView.getDuration() / 1000;
                CurTime.setText(String.format("%02d:%02d", duration / 60, duration % 60));
            }
        });
    }

    // 检测屏幕的方向并调整播放画面
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
         if (this.getResources().getConfiguration().orientation
               == Configuration.ORIENTATION_LANDSCAPE){
             setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
             getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN));
             getWindow().addFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
         }
         else if (this.getResources().getConfiguration().orientation
               == Configuration.ORIENTATION_PORTRAIT) {

             DensityUtils densityUtils = new DensityUtils();
             setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, densityUtils.dipTO2px(this, 240));
             getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
             getWindow().addFlags((WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN));
         }
    }

    private void setVideoViewScale(int width, int height) {
        //获取VideoView宽和高
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        videoView.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParams1 = Linearlayout.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        Linearlayout.setLayoutParams(layoutParams1);
    }

    private static class DensityUtils {
        // dip转成为px
        private static int dipTO2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }
    }


}