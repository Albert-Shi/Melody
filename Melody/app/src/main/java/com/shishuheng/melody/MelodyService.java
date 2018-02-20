package com.shishuheng.melody;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class MelodyService extends Service {
    public static MediaPlayer player;
    private boolean isSendPositon = true;
    private int id = 0;
    public MelodyService() {
        player = new MediaPlayer();
    }

    @Override
    public void onCreate() {
        setButtonReceiver();
        setPlayReceiver();
        setSeekReceiver();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setPlayReceiver () {
        IntentFilter filter = new IntentFilter(CommandKey.play_file);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String n = intent.getStringExtra(CommandKey.file_key);
                Log.v("SONG_URL", n);
                isSendPositon = false;
                try {
                    player.reset();
                    player.setDataSource(n);
                    player.prepareAsync();
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                            id++;
                            sendTime();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                ProjectFunctions.playMusic(n, player);
//                id++;
//                sendTime();
            }
        };
        registerReceiver(receiver, filter);
    }

    public void setSeekReceiver () {
        IntentFilter filter = new IntentFilter(CommandKey.seek);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int n = intent.getExtras().getInt(CommandKey.seek_key);
                player.seekTo(n);
            }
        };
        registerReceiver(receiver, filter);
    }

    public void setButtonReceiver () {
        IntentFilter filter = new IntentFilter(CommandKey.button_control);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String n = intent.getStringExtra(CommandKey.button_key);
                String a = CommandKey.play_resume;
                String b = CommandKey.pause_button;
                if(n.equals(CommandKey.pause_button)) {
                    player.pause();
                    Log.v("PAUSE_COMMAND", "RUN");
                    isSendPositon = false;
                }else if (n.equals(CommandKey.play_resume)) {
                    player.start();
                    sendTime();
                }else if (n.equals(CommandKey.last_button)) {
                    //watting complited
                }else if (n.equals(CommandKey.next_button)) {
                    //same as upon
                }
            }
        };
        registerReceiver(receiver, filter);
    }

    public void sendTimeBroadcast () {
        Intent intent = new Intent(CommandKey.current_position_filter);
        int[] timeinfo = new int[3];
        timeinfo[0] = player.getCurrentPosition();
        if (player.isPlaying()) {
            timeinfo[1] = player.getDuration();
        } else {
            timeinfo[1] = 0;
        }
        timeinfo[2] = id;
//        Log.v("CurrentPosition", player.getCurrentPosition() + "");
        intent.putExtra(CommandKey.current_position_key, timeinfo);
        sendBroadcast(intent);
    }

    public void sendTime () {
        isSendPositon = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isSendPositon == true) {
                    try {
                        Thread.sleep(500);
                        sendTimeBroadcast();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
