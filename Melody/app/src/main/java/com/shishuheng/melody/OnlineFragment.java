package com.shishuheng.melody;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OnlineFragment extends Fragment {
    public static MainActivity Parent;
    public ImageView control_last;
    public ImageView control_next;
    public ImageView control_play;
    public Handler bghandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_online, container, false);
        Parent = (MainActivity)getActivity();
        control_last = (ImageView)view.findViewById(R.id.control_last);
        control_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Parent.pf.lastButton_action();
            }
        });
        control_next = (ImageView)view.findViewById(R.id.control_next);
        control_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Parent.pf.nextButton_action();
            }
        });
        control_play = (ImageView)view.findViewById(R.id.control_play);
        control_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Parent.pf.playButton_action();
            }
        });
        ImageView search_button = (ImageView) view.findViewById(R.id.search_button);
        final EditText text = (EditText) view.findViewById(R.id.editText);
        final ArrayList<ArrayList> songsinfo = new ArrayList<>();


        bghandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bitmap src = (Bitmap) msg.obj;
                Parent.blurBackground(src);
                Parent.pf.changeBackground(src, Parent.pf.album_show);
                Parent.pf.isPlay = true;
                Parent.pf.play_button.setImageResource(R.mipmap.pause);
                control_play.setImageResource(R.mipmap.pause);
                String name = (String)songsinfo.get(msg.what).get(CommandKey.SongsInfoStructure.song_name);
                String artist = (String)songsinfo.get(msg.what).get(CommandKey.SongsInfoStructure.artist);
                String album = (String)songsinfo.get(msg.what).get(CommandKey.SongsInfoStructure.album_name);
                Parent.pf.title.setText(name);
                Parent.pf.artist.setText(artist);
                Parent.pf.album.setText(album);
                super.handleMessage(msg);
            }
        };

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.v("START_CREATE_ADAPTOR", "RUN");
//                ArrayList<String> name = new ArrayList<>();
                if(songsinfo.size() == 0) {
                    ArrayList<String> f = new ArrayList<>();
                    f.add("");
                    f.add("提示");
                    f.add("抱歉!");
                    f.add("未找到任何相关信息");
                    songsinfo.add(f);
                }
                Parent.MusicInfos = songsinfo;
                ProjectFunctions.SongsAdaptor adaptor = new ProjectFunctions.SongsAdaptor(getContext(), R.layout.online_list, R.id.list_container, songsinfo);
                ListView lv = (ListView)view.findViewById(R.id.online_listview);
                Log.v("START_ADAPT", "OK");
                lv.setAdapter(adaptor);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final ArrayList<String> songinfo = songsinfo.get(i);
                        final String file = songinfo.get(CommandKey.SongsInfoStructure.song_url);
                        ProjectFunctions.sendPlayBroadcast(file, getActivity());
                        Parent.MusicInfos_Position = i;
                        final int p = i;
//                        ArrayList<String> songinfo = songsinfo.get(p);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap src = null;
//                                    getBitmapFromUrl(songinfo.get(CommandKey.SongsInfoStructure.album_picture_url), src);
                                    Thread.sleep(1000);
                                    URL url = new URL(songinfo.get(CommandKey.SongsInfoStructure.album_picture_url));
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setDoInput(true);
                                    conn.setRequestMethod("GET");
                                    conn.setUseCaches(false);
                                    conn.setReadTimeout(5000);
                                    conn.connect();
                                    InputStream is = conn.getInputStream();
                                    byte[] data = getBytes(is);
                                    src = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    Message message = Message.obtain();
                                    message.obj = src;
                                    message.what = p;
                                    bghandler.sendMessage(message);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        /*
                        Message message = new Message();
                        message.what = i;
                        bghandler.sendMessage(message);
                        */
                    }
                });
                super.handleMessage(msg);
            }
        };

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    songsinfo.clear();
//                    ProjectFunctions.searchMusicOnCloudMusic(text.getText().toString(), songsinfo);
                    //
//                    GetMusicFromKuwo gmk = new GetMusicFromKuwo(text.getText().toString(), 1, 10);
//                    Thread.sleep(2000);
                    ArrayList<ArrayList> list = new GetMusicFromKuwo(text.getText().toString(), 1, 10).songInfos;
                    Log.v("SHOW LIST SIZE", list.size()+ "");
                    for(int i = 0; i < list.size(); i++) {
                        Thread.sleep(5);
                        ArrayList model = initModel();
                        model.add(CommandKey.SongsInfoStructure.artist, list.get(i).get(KuWoBaseInfoStructure.ARTIST));
                        model.add(CommandKey.SongsInfoStructure.song_name, list.get(i).get(KuWoBaseInfoStructure.SONGNAME));
                        model.add(CommandKey.SongsInfoStructure.song_url, list.get(i).get(KuWoBaseInfoStructure.URL));
                        songsinfo.add(model);
                    }
                    Thread.sleep(800);
                    int a = 10;
                    int b = 10;
                    //

                    Message message = new Message();
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private ArrayList initModel () {
        ArrayList<String> model = new ArrayList<>();
        for (int j = 0; j < 8; j++) {
            model.add("");
        }
        return model;
    }

    public void getBitmapFromUrl (String path, Bitmap bm) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setReadTimeout(5000);
            conn.connect();
            InputStream is = conn.getInputStream();
            byte[] data = getBytes(is);
            bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            int a = 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; // 用数据装
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            outstream.write(buffer, 0, len);
        }
        outstream.close();
        // 关闭流一定要记得。
        return outstream.toByteArray();
    }
}
