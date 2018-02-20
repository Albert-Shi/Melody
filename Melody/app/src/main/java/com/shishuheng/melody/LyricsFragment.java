package com.shishuheng.melody;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class LyricsFragment extends Fragment {
    MainActivity Parent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);
        Parent = (MainActivity)getActivity();
        FrameLayout lyrics_container = (FrameLayout) view.findViewById(R.id.lyrics_container);
        int height = Parent.pf.album_show.getHeight();
        int width = Parent.pf.album_show.getWidth();
        lyrics_container.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        lyrics_container.setBackgroundColor(Color.argb(121, 166, 166, 166));
        final LyricsPad lyricsPad = new LyricsPad(getContext(), Parent);
        lyrics_container.addView(lyricsPad);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                lyricsPad.invalidate();
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Message message = new Message();
                        handler.sendMessage(message);
                        Thread.sleep(500);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return view;
    }
}
