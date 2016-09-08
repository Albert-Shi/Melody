package com.shishuheng.melody;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsic;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    public static void playMusic(String file, MediaPlayer player) {
        try {
            player.stop();
            player.reset();
            player.setDataSource(file);
            player.prepare();
            player.start();
        } catch (Exception e) {
            try {
                player.stop();
                player.reset();
                player.setDataSource(file);
                player.prepare();
                player.start();
            }catch (Exception es) {
                try {
                    player.stop();
                    player.reset();
                    player.setDataSource(file);
                    player.prepare();
                    player.start();
                }catch (Exception ees) {
                    try {
                        player.stop();
                        player.reset();
                        player.setDataSource(file);
                        player.prepare();
                        player.start();
                    }catch (Exception eees) {
                        eees.printStackTrace();
                    }
                    ees.printStackTrace();
                }
                es.printStackTrace();
            }
            e.printStackTrace();
        }
    }
    //暂停
    public static void pauseMusic(MediaPlayer player) {
        player.pause();
    }
    //继续
    public static void resumePlay (MediaPlayer player) {
        player.start();
    }

    /*JSON转换*/
    public static void dealJSON(String json, ArrayList<ArrayList> songsInfo) {
        try {
            JSONObject p = new JSONObject(json);
            JSONObject result = p.getJSONObject("result");
            JSONArray songs = result.getJSONArray("songs");
            for(int i = 0; i < songs.length(); i++) {
                ArrayList<String> songInfo = new ArrayList<>();
                JSONObject song = songs.getJSONObject(i);
                String songsid = song.getInt("id") + "";
                String songsname = song.getString("name");
                String songsartist = "";
                JSONArray artists = song.getJSONArray("artists");
                for (int j = 0; j < artists.length(); j++) {
                    JSONObject artsit = artists.getJSONObject(j);
                    songsartist += artsit.getString("name") + ",";
                }
                songsartist = songsartist.subSequence(0,songsartist.length()-1).toString();
                JSONObject album = song.getJSONObject("album");
                String albumname = album.getString("name");
                String albumpic = album.getString("picUrl");
                String audio = song.getString("audio");
                String djprogramid = song.getString("djProgramId");
                songInfo.add(songsid);
                songInfo.add(songsname);
                songInfo.add(songsartist);
                songInfo.add(albumname);
                songInfo.add(albumpic);
                songInfo.add(audio);
                songInfo.add(djprogramid);

                songsInfo.add(songInfo);
            }
        }catch (Exception e) {
//            String failed = "抱歉，未找到任何歌曲";

            e.printStackTrace();
        }
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
    /*联网搜索*/
    public static void searchMusicOnCloudMusic (final String name, final ArrayList<ArrayList> sinfo) {
        String json;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int responseCode;
                try {
                    String server = "http://s.music.163.com/search/get/?src=lofter&type=1&limit=200&offset=0&s=";
                    URL url = new URL(server + name);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
//                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
                    conn.setRequestMethod("GET");
                    conn.setUseCaches(false);
                    conn.setReadTimeout(5000);
                    conn.connect();
//                    conn.setConnectTimeout(5000);
//                    conn.setDoInput(true);
//                   responseCode = conn.getResponseCode();
//                    if(responseCode == 200) {
                        InputStream is = conn.getInputStream();
                        String result = inputStreamToString(is);
//                    }
                    dealJSON(result, sinfo);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

//    public static void searchLyricsOnCloudMusic (String name, String result) {}
//    /*
    static class SongsAdaptor extends ArrayAdapter<ArrayList> {
        private int resid;
        private ArrayList<ArrayList> songsInfo = null;
        private Context context = null;
        SongsAdaptor(Context context, int resourceID, int textViewID, ArrayList<ArrayList> list){
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
            ArrayList<String> songinfo = getItem(position);
            LayoutInflater inflater = LayoutInflater.from(this.context);
            View view = inflater.inflate(R.layout.online_list, null);
            TextView title = (TextView)view.findViewById(R.id.online_list_title);
            TextView artist_album = (TextView)view.findViewById(R.id.online_list_artist_album);

            title.setText(songinfo.get(1));
            Log.v("SONG_NAME",songinfo.get(1));
            artist_album.setText(songinfo.get(2) + " - " + songinfo.get(3));
            return view;
//            return super.getView(position, convertView, parent);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Nullable
        @Override
        public ArrayList getItem(int position) {
            return songsInfo == null ? null : songsInfo.get(position);
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
                    ArrayList<String> songinfo = Parent.MusicInfos.get(Parent.MusicInfos_Position);
                    URL url = new URL(songinfo.get(CommandKey.SongsInfoStructure.album_picture_url));
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
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

