package com.shishuheng.melody;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 史书恒 on 2016/8/27.
 */

public class ProjectFunctions {

    /*
     *此函数参照
     *http://www.mamicode.com/info-detail-959099.html
     */
    public static MediaPlayer Mplayer = new MediaPlayer();
    public static Bitmap blurBitmap(Bitmap bitmap,int bitmap_width, int bitmap_height, float radius, Context context) {
        Bitmap out = Bitmap.createBitmap(bitmap_width, bitmap_height, Bitmap.Config.ARGB_8888);
        RenderScript renderScript = RenderScript.create(context);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        Allocation allIn = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation allOut = Allocation.createFromBitmap(renderScript, out);
        blur.setRadius(radius);
        blur.setInput(allIn);
        blur.forEach(allOut);
        allOut.copyTo(out);
//        bitmap.recycle();
        renderScript.destroy();
        return out;
    }

    /*播放方法*/

    //播放
    public static boolean playMusic(String file, MediaPlayer player) {
        try {
            player.stop();
            player.release();
            player.reset();
            player.setDataSource(file);
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    //暂停
    public static void pauseMusic(MediaPlayer player) {
        player.pause();
    }
    //继续
    public static void resumePlay (MediaPlayer player) {
        player.start();
    }


//    public static void searchLyricsOnCloudMusic (String name, String result) {}
//    /*
    static class SongsAdaptor extends ArrayAdapter {
        private int resid;
        private ArrayList songsInfo = null;
        private Context context = null;
        SongsAdaptor(Context context, int resourceID, int textViewID, ArrayList list){
            super(context, resourceID);
            resid = resourceID;
            this.context = context;
            this.songsInfo = list;
            Log.v("GOUZAO", "RUN");
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.v("GETVIEW", "RUN");
            MusicInfo songinfo = (MusicInfo) getItem(position);
            LayoutInflater inflater = LayoutInflater.from(this.context);
            View view = inflater.inflate(R.layout.online_list, null);
            TextView title = (TextView)view.findViewById(R.id.online_list_title);
            TextView artist_album = (TextView)view.findViewById(R.id.online_list_artist_album);

            title.setText(songinfo.getSongName());
            Log.v("SONG_NAME",songinfo.getSongName());
            artist_album.setText(songinfo.getArtist() + " - " + songinfo.getAlbumName());
            return view;
//            return super.getView(position, convertView, parent);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    @Nullable
    @Override
    public Object getItem(int position) {
        return songsInfo == null ? null : songsInfo.get(position);
//        return super.getItem(position);
    }

    @Override
        public int getCount() {
            return songsInfo == null ? 0 : songsInfo.size();
        }
    }//*/

//    static class MBroadcastReceiver extends BroadcastReceiver{

//    }

    public static void sendPlayBroadcast (String file, Activity activity) {
        Intent intent = new Intent(CommandKey.play_file);
        intent.putExtra(CommandKey.file_key, file);
        activity.sendBroadcast(intent);
    }

    public static void sendButtonControl (int flag, Activity activity) {
        Intent intent = new Intent(CommandKey.button_control);
        if (flag == 0) {
            intent.putExtra(CommandKey.button_key, CommandKey.play_resume);
        }else if (flag == 1) {
            intent.putExtra(CommandKey.button_key, CommandKey.pause_button);
        }else if (flag == 2) {
            intent.putExtra(CommandKey.button_key, CommandKey.last_button);
        }else if (flag == 3) {
            intent.putExtra(CommandKey.button_key, CommandKey.next_button);
        }else if (flag == 4) {
            intent.putExtra(CommandKey.button_key, CommandKey.mode_change);
        }
        activity.sendBroadcast(intent);
    }

    public static void createThread (final MainActivity Parent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap src = null;
//                                    getBitmapFromUrl(songinfo.get(CommandKey.SongsInfoStructure.album_picture_url), src);
                    Thread.sleep(1000);
                    MusicInfo songinfo = Parent.MusicInfos.get(Parent.MusicInfos_Position);
                    URL url = new URL(songinfo.getPicUrl());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setRequestMethod("GET");
                    conn.setUseCaches(false);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    byte[] data = Parent.of.getBytes(is);
                    src = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Message message = Message.obtain();
                    message.obj = src;
                    message.what = Parent.MusicInfos_Position;
                    Parent.of.bghandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Parent.lrc = null;
//                GetMusicFromNetease.getLyrics(Parent.MusicInfos.get(Parent.MusicInfos_Position), Parent.MusicInfos.get(Parent.MusicInfos_Position).getSongId());
                Parent.lrc = Parent.projectFunctions.new Lyrics(Parent.MusicInfos.get(Parent.MusicInfos_Position).getLyricText());
                Log.v("FISTLINE", (String) Parent.lrc.arrayLyrics.get(1).get(0));
            }
        }).start();
    }

    /*InputStream类转String类*/
    public static String inputStreamToString (InputStream is) {
        StringBuffer sb = new StringBuffer();
        byte[] buffer = new byte[1024];
        try {
            for(int i = 0; (i = is.read(buffer)) != -1; i++) {
                sb.append(new String(buffer, 0, i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    class Lyrics {
        ArrayList<ArrayList> arrayLyrics;
        String lrc;
        Lyrics(String lrc) {
           arrayLyrics = new ArrayList<>();
            this.lrc = lrc;
            lyricsToArray();
        }
        public void lyricsToArray () {
            ArrayList<Integer> lyricsTime = new ArrayList<>();
            ArrayList<String> lyricsText = new ArrayList<>();
            String primay[] = lrc.split("\n");
            for (int i = 0; i < primay.length; i++) {
                if ((primay[i].indexOf("][") == -1) && (primay[i].indexOf("] [") == -1)) {
                    String line[] = primay[i].split("]");
                    String timeText = line[0].replace("[", "").replace("]", "");
                    int colon, dot;
                    colon = timeText.indexOf(":");
                    dot = timeText.indexOf(".");
                    if (colon != -1 && dot != -1 && (int)timeText.charAt(1) <= (int)'9') {
                        String minuteText = timeText.substring(0, colon);
                        String secondText = timeText.substring(colon + 1, dot);
                        String millisecondText = timeText.substring(dot + 1);
                        int m = Integer.parseInt(minuteText);
                        int s = Integer.parseInt(secondText);
                        int ms = Integer.parseInt(millisecondText);
                        int time = m*60*1000 + s*1000 + ms;
                        lyricsTime.add(time);
                        if (line.length > 1)
                            lyricsText.add(line[1].replace("]", ""));
                        else
                            lyricsText.add("");
                    }
//                System.gc();
                }
            }
            arrayLyrics.add(lyricsTime);
            arrayLyrics.add(lyricsText);
        }

        public String getCurrentLine(int time, MainActivity mainActivity) {
            for (int i = 0; i < arrayLyrics.get(0).size(); i++) {
//                int nl = (int) arrayLyrics.get(0).get(i+1);
                if (i+1 < arrayLyrics.get(0).size()) {
                    int ct = (int) arrayLyrics.get(0).get(i+1);
                    if ((ct > time)) {//&& nl > time)) {
                        mainActivity.CurrentPosition_Lyrics = i;
                        return (String) arrayLyrics.get(1).get(i);
                    }
                } else {
                    mainActivity.CurrentPosition_Lyrics = arrayLyrics.get(0).size()-1;
                    return (String) arrayLyrics.get(1).get(arrayLyrics.get(0).size()-1);
                }
            }
            return "";
        }
    }

}

