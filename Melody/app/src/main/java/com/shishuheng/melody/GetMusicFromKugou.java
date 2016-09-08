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

class KuGouBasicInfoStructure {
    public static int filename = 0;
    public static int songname = 1;
    public static int m4afilesize = 2;
    public static int hash320 = 3;
    public static int mvhash = 4;
    public static int privilege = 5;
    public static int filesize = 6;
    public static int source = 7;
    public static int bitrate = 8;
    public static int ownercount = 9;
    public static int topic = 10;
    public static int filesize320 = 11;
    public static int isnew = 12;
    public static int duration = 13;
    public static int album_id = 14;
    public static int Accompany = 15;
    public static int singername = 16;
    public static int extname = 17;
    public static int privilege320 = 18;
    public static int sourceid = 19;
    public static int srctype = 20;
    public static int feettype = 21;
    public static int sqfilesize = 22;
    public static int hash = 23;
    public static int sqprivilege = 24;
    public static int sqhash = 25;
    public static int othername = 26;
    public static int songurl = 27;
}

public class GetMusicFromKugou {
//    String json;
    ArrayList<ArrayList> primarySongInfos = new ArrayList<>();
    private void dealPrimaryJSON (String json) {
        try {
            JSONObject all = new JSONObject(json);
            JSONObject data = all.getJSONObject("data");
            JSONArray info = data.getJSONArray("info");
            for (int i = 0; i < info.length(); i++) {
                JSONObject songinfo = info.getJSONObject(i);
                ArrayList<String> primarySongInfo = new ArrayList<>();
//                primarySongInfo.add(KuGouBasicInfoStructure.Accompany, songinfo.getString("Accompany"));
//                primarySongInfo.add(KuGouBasicInfoStructure.album_id, songinfo.getString("album_id"));
//                primarySongInfo.add(KuGouBasicInfoStructure.bitrate, songinfo.getString("bitrate"));
                primarySongInfo.add(KuGouBasicInfoStructure.duration, songinfo.getString("duration"));
                primarySongInfo.add(KuGouBasicInfoStructure.extname, songinfo.getString("extname"));
//                primarySongInfo.add(KuGouBasicInfoStructure.feettype, songinfo.getString("feettype"));
                primarySongInfo.add(KuGouBasicInfoStructure.filename, songinfo.getString("filename"));
//                primarySongInfo.add(KuGouBasicInfoStructure.filesize, songinfo.getString("filesize"));
//                primarySongInfo.add(KuGouBasicInfoStructure.filesize320, songinfo.getString("320filesize"));
                primarySongInfo.add(KuGouBasicInfoStructure.hash, songinfo.getString("hash"));
                primarySongInfo.add(KuGouBasicInfoStructure.hash320, songinfo.getString("320hash"));
//                primarySongInfo.add(KuGouBasicInfoStructure.isnew, songinfo.getString("isnew"));
                primarySongInfo.add(KuGouBasicInfoStructure.m4afilesize, songinfo.getString("m4afilesize"));
                primarySongInfo.add(KuGouBasicInfoStructure.mvhash, songinfo.getString("mvhash"));
//                primarySongInfo.add(KuGouBasicInfoStructure.othername, songinfo.getString("othername"));
//                primarySongInfo.add(KuGouBasicInfoStructure.ownercount, songinfo.getString("ownercount"));
//                primarySongInfo.add(KuGouBasicInfoStructure.privilege, songinfo.getString("privilege"));
//                primarySongInfo.add(KuGouBasicInfoStructure.privilege320, songinfo.getString("320privilege"));
                primarySongInfo.add(KuGouBasicInfoStructure.singername, songinfo.getString("singername"));
                primarySongInfo.add(KuGouBasicInfoStructure.songname, songinfo.getString("songname"));
//                primarySongInfo.add(KuGouBasicInfoStructure.source, songinfo.getString("source"));
//                primarySongInfo.add(KuGouBasicInfoStructure.sourceid, songinfo.getString("sourceid"));
                primarySongInfo.add(KuGouBasicInfoStructure.sqfilesize, songinfo.getString("sqfilesize"));
                primarySongInfo.add(KuGouBasicInfoStructure.sqhash, songinfo.getString("sqhash"));
//                primarySongInfo.add(KuGouBasicInfoStructure.sqprivilege, songinfo.getString("sqprivilege"));
//                primarySongInfo.add(KuGouBasicInfoStructure.srctype, songinfo.getString("srctype"));
//                primarySongInfo.add(KuGouBasicInfoStructure.topic, songinfo.getString("topic"));
                primarySongInfos.add(primarySongInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealFinalJSON (ArrayList<String> songinfo, String json) {
        try {
            JSONObject all = new JSONObject(json);
            songinfo.add(KuGouBasicInfoStructure.songurl, all.getString("url"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GetMusicFromKugou(final String name, final int page, final int pagesize) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String server = "http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword=" + name + "&page=" + page + "&pagesize=" + pagesize;
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
                    for (int i = 0; i < primarySongInfos.size(); i++) {
                        getUrl(primarySongInfos.get(i));
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getUrl(final ArrayList<String> songinfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int responseCode;
                try {
                    String server = "http://m.kugou.com/app/i/getSongInfo.php?cmd=playInfo&hash=";
                    URL url = new URL(server + songinfo.get(KuGouBasicInfoStructure.hash));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setRequestMethod("GET");
                    conn.setUseCaches(false);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    String result = ProjectFunctions.inputStreamToString(is);
                    dealFinalJSON(songinfo, result);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}