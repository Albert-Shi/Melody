package com.shishuheng.melody;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    public TextView control_title;
    public TextView control_artist_album;
    public Handler bghandler;
    public static int music_library = 0; //0: 网易云音乐 1: 酷我音乐
    public static String PAGE = "1";
    public static String PAGESIZE = "100";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
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
        control_title = (TextView)view.findViewById(R.id.control_title);
        control_artist_album = (TextView)view.findViewById(R.id.control_artist_album);

        ImageView search_button = (ImageView) view.findViewById(R.id.search_button);
        final EditText text = (EditText) view.findViewById(R.id.editText);


        bghandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bitmap src = (Bitmap) msg.obj;
                Parent.blurBackground(src);
                Parent.pf.changeBackground(src, Parent.pf.album_show);
                Parent.pf.isPlay = true;
                Parent.pf.play_button.setImageResource(R.mipmap.pause);
                control_play.setImageResource(R.mipmap.pause);
                super.handleMessage(msg);
            }
        };

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.v("START_CREATE_ADAPTOR", "RUN");
//                ArrayList<String> name = new ArrayList<>();
                final ArrayList<MusicInfo> list = (ArrayList<MusicInfo>) msg.obj;
                if(list.size() == 0) {
                    MusicInfo f = new MusicInfo();
                    f.setSongName("提示");
                    f.setArtist("抱歉!");
                    f.setAlbumName("未找到任何相关信息");
                    list.add(f);
                }
                Parent.MusicInfos = list;
                ProjectFunctions.SongsAdaptor adaptor = new ProjectFunctions.SongsAdaptor(getContext(), R.layout.online_list, R.id.list_container, list);
                ListView lv = (ListView)view.findViewById(R.id.online_listview);
                Log.v("START_ADAPT", "OK");
                lv.setAdapter(adaptor);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        final MusicInfo songinfo = list.get(i);
                        final String file = songinfo.getUrl();
                        ProjectFunctions.sendPlayBroadcast(file, getActivity());
                        Log.v("SENDURL", file);
                        Parent.MusicInfos_Position = i;
                        final int p = i;
//                        ArrayList<String> songinfo = songsinfo.get(p);
                        String name = songinfo.getSongName();
                        String artist = songinfo.getArtist();
                        String album = songinfo.getAlbumName();
                        Parent.pf.title.setText(name);
                        Parent.pf.artist.setText(artist);
                        Parent.pf.album.setText(album);
                        control_title.setText(name);
                        control_artist_album.setText(artist + "-" + album);

                        Parent.pf.play_button.setImageResource(R.mipmap.pause);
                        control_play.setImageResource(R.mipmap.pause);
                        Parent.pf.isPlay = true;

                        Parent.lrc = null;
                        Parent.pf.lyric.setText("此处显示歌词");
                        GetMusicFromNetease.getLyrics(songinfo, songinfo.getSongId());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap src = null;
//                                    getBitmapFromUrl(songinfo.get(CommandKey.SongsInfoStructure.album_picture_url), src);
                                    Thread.sleep(1000);
                                    URL url = new URL(songinfo.getPicUrl());
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
                                    Parent.lrc = Parent.projectFunctions.new Lyrics(songinfo.getLyricText());
                                    Log.v("LYRICS_ALREADY","SURE");
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
                    ArrayList<MusicInfo> list = null;
                    String searchContent = text.getText().toString().replace(" ", "%20");
                    if (music_library == 1) {
                        list = new GetMusicFromKuwo(searchContent, PAGE, PAGESIZE).songInfos;
                    } else if (music_library == 0){
                        list = new GetMusicFromNetease(searchContent, PAGESIZE).songInfos;
                    }
                    Log.v("SHOW LIST SIZE", list.size()+ "");
                    Thread.sleep(1000);

                    Message message = Message.obtain();
                    message.obj = list;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        search_button.setLongClickable(true);
        search_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LinearLayout searchSelect = new LinearLayout(getContext());
                int size = Parent.getWindowManager().getDefaultDisplay().getWidth()/4*3;
                searchSelect.setLayoutParams(new ViewGroup.LayoutParams(size, size));
                final RadioButton netease = new RadioButton(getContext());
                final RadioButton kuwo = new RadioButton(getContext());
                netease.setText("网易云音乐");
                netease.setActivated(true);
                netease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        music_library = 0;
                    }
                });
                kuwo.setText("酷我");
                kuwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        music_library = 1;
                    }
                });
                RadioGroup radioGroup = new RadioGroup(getContext());
                radioGroup.addView(netease);
                radioGroup.addView(kuwo);
                radioGroup.setLayoutParams(new ViewGroup.LayoutParams(size, size/5));
                searchSelect.setOrientation(LinearLayout.VERTICAL);
                searchSelect.setGravity(Gravity.CENTER);
//                searchSelect.addView(netease);
//                searchSelect.addView(kuwo);
                LinearLayout setLine = new LinearLayout(getContext());
                setLine.setOrientation(LinearLayout.HORIZONTAL);
                setLine.setGravity(Gravity.CENTER);
                setLine.setLayoutParams(new ViewGroup.LayoutParams(size, size/5));
                final EditText page = new EditText(getContext());
                final EditText pagesize = new EditText(getContext());
                page.setHint("搜索页数");
                pagesize.setHint("搜索数量");
                page.setHintTextColor(Color.argb(80, 80, 80, 80));
                pagesize.setHintTextColor(Color.argb(80, 80, 80, 80));
                setLine.addView(page);
                setLine.addView(pagesize);
                searchSelect.addView(radioGroup);
                searchSelect.addView(setLine);
                AlertDialog.Builder builder = new AlertDialog.Builder(Parent);
                builder.setTitle("请选择在线搜索的曲库");
                builder.setView(searchSelect);
                builder.setPositiveButton("保存设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!page.getText().toString().equals("")) {
                            PAGE = page.getText().toString();
                        }
                        if (!pagesize.getText().toString().equals("")) {
                            PAGESIZE = pagesize.getText().toString();
                        }
                    }
                });
                builder.create().show();
                return false;
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

    /*获取URL上的bitmap*/
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
