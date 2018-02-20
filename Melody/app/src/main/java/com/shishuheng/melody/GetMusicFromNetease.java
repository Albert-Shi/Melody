package com.shishuheng.melody;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 史书恒 on 2016/9/9.
 */

public class GetMusicFromNetease {
    public ArrayList<MusicInfo> songInfos;

    /*JSON转换*/
    public void dealJSON(String json) {
        try {
            JSONObject p = new JSONObject(json);
            JSONObject result = p.getJSONObject("result");
            JSONArray songs = result.getJSONArray("songs");
            for(int i = 0; i < songs.length(); i++) {
                MusicInfo songInfo = new MusicInfo();
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
//                String djprogramid = song.getString("djProgramId");
/*
                songInfo.add(songsid);
                songInfo.add(songsname);
                songInfo.add(songsartist);
                songInfo.add(albumname);
                songInfo.add(albumpic);
                songInfo.add(audio);
                songInfo.add(djprogramid);
*/
                songInfo.setSongId(songsid);
                songInfo.setSongName(songsname);
                songInfo.setArtist(songsartist);
                songInfo.setAlbumName(albumname);
                songInfo.setPicUrl(albumpic);
                songInfo.setUrl(audio);

//                getLyrics(songInfo, songsid);

                songInfos.add(songInfo);
            }
        }catch (Exception e) {
//            String failed = "抱歉，未找到任何歌曲";

            e.printStackTrace();
        }
    }

    /*联网搜索*/
    public GetMusicFromNetease (final String name, final String pagesize) {
        songInfos = new ArrayList<>();
        String json;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int responseCode;
                try {
                    String server = "http://s.music.163.com/search/get/?src=lofter&type=1&limit="+pagesize+"&offset=0&s=";
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
                    dealJSON(result);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void getLyrics(final MusicInfo musicInfo, final String songid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String server = "http://music.163.com/api/song/lyric?lv=1&kv=1&tv=-1&id=" + songid;
                    URL url = new URL(server);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
//                    conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
                    conn.setRequestMethod("GET");
                    conn.setUseCaches(false);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    String result = inputStreamToString(is);
                    JSONObject all = new JSONObject(result);
                    JSONObject lrc = all.getJSONObject("lrc");
                    musicInfo.setLyricText(lrc.getString("lyric"));
                    JSONObject tlrc = all.getJSONObject("tlyric");
                    musicInfo.setLyricText_trans(tlrc.getString("lyric"));
                }catch (Exception e) {
                    e.printStackTrace();
                }
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
}
