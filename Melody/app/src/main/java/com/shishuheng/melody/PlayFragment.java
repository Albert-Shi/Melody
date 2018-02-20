package com.shishuheng.melody;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PlayFragment extends Fragment {
    public static View view;
    public LinearLayout album_show;
    public LinearLayout time_label;
    public boolean isPlay = false;
    public ImageView play_button;
    public ImageView last_button;
    public ImageView next_button;

    public SeekBar seekBar;
    public TextView bt;//当前播放进度
    public TextView et;//总时长
    public TextView lyric;
    public TextView title;
    public TextView artist;
    public TextView album;

    public int id = 0;

    public static int defaultFontColor = Color.argb(255, 255, 255, 255);

    public boolean isShowLyricsPad = false;

//    public Thread backgroundShow;

    public static MainActivity Parent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_play, container, false);
//        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pink);
//        blurBackground(originBitmap, view);
        Parent = (MainActivity)getActivity();
//        ProjectFunctions.createThread(backgroundShow, Parent);
        album_show = new LinearLayout(getContext());
        seekBar = new SeekBar(getContext());
        bt = new TextView(getContext());
        et = new TextView(getContext());
        lyric = new TextView(getContext());
        title = new TextView(getContext());
        artist = new TextView(getContext());
        album = new TextView(getContext());

        bt.setTextColor(defaultFontColor);
        et.setTextColor(defaultFontColor);
        lyric.setTextColor(defaultFontColor);
        title.setTextColor(defaultFontColor);
        artist.setTextColor(defaultFontColor);
        album.setTextColor(defaultFontColor);

        createLayout(album_show, seekBar, bt, et, lyric, title, artist, album);
//        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mass);
//        changeBackground(originBitmap, album_show);

        getTime();

        play_button = (ImageView) view.findViewById(R.id.play_play);
        last_button = (ImageView) view.findViewById(R.id.last_play);
        next_button = (ImageView) view.findViewById(R.id.next_play);
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButton_action();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    Intent intent = new Intent(CommandKey.seek);
                    intent.putExtra(CommandKey.seek_key, i);
                    getActivity().sendBroadcast(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        lyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LyricsFragment lyricsFragment = new LyricsFragment();
                if (isShowLyricsPad == false) {
                    isShowLyricsPad = true;
                    getFragmentManager().beginTransaction().replace(R.id.play_layout, lyricsFragment).addToBackStack(null).commit();
                }else {
                    Parent.getSupportFragmentManager().popBackStack();
                    isShowLyricsPad = false;
                }
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextButton_action();
            }
        });
        last_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastButton_action();
            }
        });

        return view;
    }

    private void createLayout (LinearLayout album_show, SeekBar seekBar, TextView bt, TextView et, TextView lyric, TextView title, TextView artist, TextView album) {
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.album_parent);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float scale = dm.density;
        album_show.setLayoutParams(new ViewGroup.LayoutParams(dm.widthPixels, dm.widthPixels));
        layout.addView(album_show);
        time_label = new LinearLayout(getContext());
        time_label.setOrientation(LinearLayout.HORIZONTAL);
        bt.setText("0:00");
        et.setText("0.00");
        int h = (int)(3*scale);
        LinearLayout begin = new LinearLayout(getContext());
        LinearLayout end = new LinearLayout(getContext());
        lyric.setText("此处显示歌词");
        begin.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        end.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        begin.setLayoutParams(new ViewGroup.LayoutParams(dm.widthPixels/8, 16*h));
        end.setLayoutParams(new ViewGroup.LayoutParams(dm.widthPixels/8, 16*h));
        lyric.setLayoutParams(new ViewGroup.LayoutParams(dm.widthPixels/4*3, 16*h));
        lyric.setGravity(Gravity.CENTER_HORIZONTAL);
        time_label.addView(begin);
        time_label.addView(lyric);
        time_label.addView(end);
        begin.addView(bt);
        end.addView(et);
        seekBar.setMax(0);

        layout.addView(time_label);
        layout.addView(seekBar);

        title.setGravity(Gravity.CENTER);
        artist.setGravity(Gravity.CENTER);
        album.setGravity(Gravity.CENTER);
        title.setTextSize(22f);
        artist.setTextSize(16f);
        album.setTextSize(16f);

        TextView fill = new TextView(getContext());
        fill.setTextSize(15);
        layout.addView(fill);
        layout.addView(title);
        layout.addView(artist);
        layout.addView(album);
        layout.setGravity(Gravity.CENTER);

        title.setText("歌名");
        artist.setText("歌手");
        album.setText("专辑");
    }

    public void changeBackground (Bitmap bk, LinearLayout album_show) {

        /*
        int scale = (int)dm.density;
        int w = dm.widthPixels;
        int wdp = w/scale;
//        int lh = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int s =400/wdp;
        int h = bk.getWidth()*s;
        int y = (bk.getHeight()-h)/2;
        */

//        Bitmap bitmap = Bitmap.createBitmap(bk, 0, y, bk.getWidth(), h);
        album_show.setBackground(new BitmapDrawable(bk));
    }

    public void getTime() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int[] time = intent.getExtras().getIntArray(CommandKey.current_position_key);
                Log.v("GOTTA_TIME", "" + time[0]);
                if (time[0] < 1000) {
                    seekBar.setMax(time[1]);
                    int m = time[1] / 60000;
                    int s = (time[1] / 1000) - (m*60);
                    if (s < 10) {
                        et.setText(m + ":0" + s);
                    } else {
                        et.setText(m + ":" + s);
                    }
                }
                if ((time[1] - time[0] < 500) && (time[0] > 500) && time[1] != 0)
                    Parent.pf.nextButton_action();
                int cm = time[0] / 60000;
                int cs = (time[0] / 1000) - (cm*60);
                id = time[2];
                if (cs < 10) {
                    bt.setText(cm + ":0" + cs);
                } else {
                    bt.setText(cm + ":" + cs);
                }
                Parent.CurrentTime_Lyrics = time[0];
                seekBar.setProgress(time[0]);
                if (Parent.lrc != null) {
                    String thisLine = Parent.lrc.getCurrentLine(time[0], Parent);
                    if (isShowLyricsPad == false) {
                        lyric.setText(thisLine);
                    } else {
                        lyric.setText("关闭歌词面板");
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(CommandKey.current_position_filter);
        getActivity().registerReceiver(receiver, filter);
    }

    public void lastButton_action() {
        if (Parent.MusicInfos != null && Parent.MusicInfos.size() != 0) {
            //暂时使用
            Parent.MusicInfos_Position--;
            if (Parent.MusicInfos_Position < 0)
                Parent.MusicInfos_Position = 0;

            String file = (String) Parent.MusicInfos.get(Parent.MusicInfos_Position).getUrl();
            String ttitle = (String) Parent.MusicInfos.get(Parent.MusicInfos_Position).getSongName();
            String tartist = (String) Parent.MusicInfos.get(Parent.MusicInfos_Position).getArtist();
            String talbum = (String) Parent.MusicInfos.get(Parent.MusicInfos_Position).getAlbumName();
            ProjectFunctions.sendPlayBroadcast(file, getActivity());
            title.setText(ttitle);
            artist.setText(tartist);
            album.setText(talbum);
            Parent.of.control_title.setText(ttitle);
            Parent.of.control_artist_album.setText(tartist + "-" + talbum);

            play_button.setImageResource(R.mipmap.pause);
            Parent.of.control_play.setImageResource(R.mipmap.pause);
            isPlay = true;

            Parent.pf.lyric.setText("此处显示歌词");
            GetMusicFromNetease.getLyrics(Parent.MusicInfos.get(Parent.MusicInfos_Position), Parent.MusicInfos.get(Parent.MusicInfos_Position).getSongId());
            ProjectFunctions.createThread(Parent);
        }
    }

    public void nextButton_action () {
        if (Parent.MusicInfos != null && Parent.MusicInfos.size() != 0) {
            //暂时使用
            Parent.MusicInfos_Position++;
            if (Parent.MusicInfos_Position == Parent.MusicInfos.size())
                Parent.MusicInfos_Position--;

            String file = (String) Parent.MusicInfos.get(Parent.MusicInfos_Position).getUrl();
            String ttitle = (String) Parent.MusicInfos.get(Parent.MusicInfos_Position).getSongName();
            String tartist = (String) Parent.MusicInfos.get(Parent.MusicInfos_Position).getArtist();
            String talbum = (String) Parent.MusicInfos.get(Parent.MusicInfos_Position).getAlbumName();
            ProjectFunctions.sendPlayBroadcast(file, getActivity());
            title.setText(ttitle);
            artist.setText(tartist);
            album.setText(talbum);
            Parent.of.control_title.setText(ttitle);
            Parent.of.control_artist_album.setText(tartist + "-" + talbum);

            play_button.setImageResource(R.mipmap.pause);
            Parent.of.control_play.setImageResource(R.mipmap.pause);
            isPlay = true;

            Parent.pf.lyric.setText("此处显示歌词");
            GetMusicFromNetease.getLyrics(Parent.MusicInfos.get(Parent.MusicInfos_Position), Parent.MusicInfos.get(Parent.MusicInfos_Position).getSongId());
            ProjectFunctions.createThread(Parent);
        }
    }

    public void playButton_action() {
        if (Parent.MusicInfos != null && Parent.MusicInfos.size() != 0) {
            if (isPlay == false) {
                ProjectFunctions.sendButtonControl(0, getActivity());
                play_button.setImageResource(R.mipmap.pause);
                Parent.of.control_play.setImageResource(R.mipmap.pause);
                isPlay = true;
            } else if(isPlay == true) {
                ProjectFunctions.sendButtonControl(1, getActivity());
                play_button.setImageResource(R.mipmap.play);
                Parent.of.control_play.setImageResource(R.mipmap.play);
                isPlay = false;
            }
        }
    }
}
