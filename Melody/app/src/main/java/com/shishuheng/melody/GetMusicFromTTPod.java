package com.shishuheng.melody;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 史书恒 on 2016/9/5.
 */
class TTPodBaseInfoStructure {
    public static int song_id = 0;
    public static int song_name = 1;
    public static int singer_id = 2;
    public static int singer_name = 3;
    public static int album_id = 4;
    public static int album_name = 5;
    public static int bitrate32url = 6;
    public static int bitrate128url = 7;
    public static int bitrate320url = 8;
    public static int lyric = 9;
    public static int albumpicurl = 10;
}

public class GetMusicFromTTPod {
    public ArrayList<ArrayList> songInfos = new ArrayList<>();

    private void dealPrimaryJSON (String json) {
        try {
            JSONObject all = new JSONObject(json);
            JSONArray data = all.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject songjs = data.getJSONObject(i);
                ArrayList<String> song = new ArrayList<>();
                song.add(TTPodBaseInfoStructure.album_id, songjs.getString("album_id"));
                song.add(TTPodBaseInfoStructure.album_name, songjs.getString("album_name"));
                song.add(TTPodBaseInfoStructure.singer_id, songjs.getString("singer_id"));
                song.add(TTPodBaseInfoStructure.singer_name, songjs.getString("singer_name"));
                song.add(TTPodBaseInfoStructure.song_id, songjs.getString("song_id"));
                song.add(TTPodBaseInfoStructure.song_name, songjs.getString("song_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GetMusicFromTTPod(final String name, final int page, final int pagesize) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String server = "http://search.dongting.com/song/search/old?q="+ name +"&page=" + page +"&size="+ pagesize;
                    URL url = new URL(server);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setRequestMethod("GET");
                    conn.setUseCaches(false);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    String result = ProjectFunctions.inputStreamToString(is);
                    dealPrimaryJSON(result);
                    for (int i = 0; i < songInfos.size(); i++) {
                        try {
                            String lrcserver = "http://lp.music.ttpod.com/lrc/down?lrcid=&artist="+ songInfos.get(i).get(TTPodBaseInfoStructure.singer_name) +"&title="+ songInfos.get(i).get(TTPodBaseInfoStructure.song_name) +"&song_id=" + songInfos.get(i).get(TTPodBaseInfoStructure.song_id);
                            URL lrcurl = new URL(lrcserver);
                            HttpURLConnection lrcconn = (HttpURLConnection) lrcurl.openConnection();
                            lrcconn.setDoInput(true);
                            lrcconn.setRequestMethod("GET");
                            lrcconn.setUseCaches(false);
                            lrcconn.setReadTimeout(5000);
                            lrcconn.connect();
                            InputStream lrcis = lrcconn.getInputStream();
                            String lrcresult = ProjectFunctions.inputStreamToString(lrcis);
                            JSONObject all = new JSONObject(lrcresult);
                            JSONObject data = all.getJSONObject("data");
                            songInfos.get(i).add(TTPodBaseInfoStructure.lyric, data.getString("lrc"));

                            String picserver = "http://lp.music.ttpod.com/lrc/down?lrcid=&artist="+ songInfos.get(i).get(TTPodBaseInfoStructure.singer_name) +"&title="+ songInfos.get(i).get(TTPodBaseInfoStructure.song_name) +"&song_id=" + songInfos.get(i).get(TTPodBaseInfoStructure.song_id);
                            URL picurl = new URL(picserver);
                            HttpURLConnection picconn = (HttpURLConnection) picurl.openConnection();
                            picconn.setDoInput(true);
                            picconn.setRequestMethod("GET");
                            picconn.setUseCaches(false);
                            picconn.setReadTimeout(5000);
                            picconn.connect();
                            InputStream picis = picconn.getInputStream();
                            String picresult = ProjectFunctions.inputStreamToString(picis);
                            JSONObject pic = new JSONObject(picresult);
                            JSONObject picdata = pic.getJSONObject("data");
                            songInfos.get(i).add(TTPodBaseInfoStructure.albumpicurl, picdata.getString("singerPic"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
