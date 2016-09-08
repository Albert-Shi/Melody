package com.shishuheng.melody;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;

/**
 * Created by 史书恒 on 2016/8/27.
 */

public class MainBroadcast {
    private static String B_JUMP = "TIME";
    private static String B_FILE = "NAME";
    private static String B_CONTROL = "PLAY_CONTROL";
    private static String CURRENT_POSITION = "CURRENT_POSITION";
    public static String GET_POSITION = "GET_POSITION";
    private static int RESUME_PLAY = 0;
    private static int PAUSE_PLAY = 1;

    class MPlayBroadcastReceiver extends BroadcastReceiver{
        private MediaPlayer player;
        MPlayBroadcastReceiver(MediaPlayer p) {
            player = p;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String file = intent.getStringExtra(B_FILE);
            ProjectFunctions.playMusic(file, player);
        }
    }

    class MSeekPlayBroadcastReceiver extends BroadcastReceiver {
        private MediaPlayer player;
        MSeekPlayBroadcastReceiver(MediaPlayer p) {
            player = p;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int time = intent.getExtras().getInt(B_JUMP);
            player.seekTo(time);
        }
    }

    class MPauseResumeBroadcastReceiver extends BroadcastReceiver {
        MediaPlayer player;
        MPauseResumeBroadcastReceiver (MediaPlayer p) {
            player = p;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int flag = intent.getExtras().getInt(B_CONTROL);
            if(flag == RESUME_PLAY) {
                player.start();
            } else if (flag == PAUSE_PLAY) {
                player.pause();
            }
        }
    }

    class MGetPositionBroadcastReceiver extends BroadcastReceiver {
        private int position;

        @Override
        public void onReceive(Context context, Intent intent) {
            position = intent.getExtras().getInt(GET_POSITION);
        }

        public int getPosition () {
            return position;
        }
    }

    void sendCurrentPosition(MediaPlayer player, Service service) {
        Intent intent = new Intent(CURRENT_POSITION);
        intent.putExtra(GET_POSITION, player.getCurrentPosition());
        service.sendBroadcast(intent);
    }

    /*
    void getCurrentPosition (int position, Activity activity) {
        IntentFilter intentFilter = new IntentFilter(CURRENT_POSITION);
        MGetPositionBroadcastReceiver broadcastReceiver = new MGetPositionBroadcastReceiver();
        activity.registerReceiver(broadcastReceiver, intentFilter);
    }
    */
}
