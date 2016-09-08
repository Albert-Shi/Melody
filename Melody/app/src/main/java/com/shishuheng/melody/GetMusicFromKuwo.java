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
class KuWoBaseInfoStructure {
    public static int MUSICID = 0;
    public static int SONGNAME = 1;
    public static int ARTIST = 2;
    public static int ARTISTID = 3;
    public static int URL = 4;
}

public class GetMusicFromKuwo {
    public ArrayList<ArrayList> songInfos = new ArrayList<>();
    private void dealPrimaryJSON(String json) {
        try {
            JSONObject all = new JSONObject(json);
            JSONArray list = all.getJSONArray("abslist");
            for (int i = 0; i < list.length(); i++) {
                ArrayList<String> songinfo = new ArrayList<>();
                JSONObject song = list.getJSONObject(i);
                songinfo.add(KuWoBaseInfoStructure.MUSICID, song.getString("MUSICRID"));
                songinfo.add(KuWoBaseInfoStructure.SONGNAME, song.getString("SONGNAME"));
                songinfo.add(KuWoBaseInfoStructure.ARTIST, song.getString("ARTIST"));
                songinfo.add(KuWoBaseInfoStructure.ARTISTID, song.getString("ARTISTID"));
                songInfos.add(songinfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public GetMusicFromKuwo (final String name, final int page, final int pagesize) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String query = "http://search.kuwo.cn/r.s?all=" + name + "&ft=music&itemset=web_2013&client=kt&pn=" + page + "&rn=" + pagesize + "&rformat=json&encoding=utf8";
                    URL url = new URL(query);
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
                        String server = "http://antiserver.kuwo.cn/anti.s?type=convert_url&format=aac|mp3&response=url&rid=" + songInfos.get(i).get(KuWoBaseInfoStructure.MUSICID);
                        URL geturl = new URL(server);
                        HttpURLConnection urlconn = (HttpURLConnection) geturl.openConnection();
                        urlconn.setDoInput(true);
                        urlconn.setRequestMethod("GET");
                        urlconn.setUseCaches(false);
                        urlconn.setReadTimeout(5000);
                        urlconn.connect();
                        InputStream istream = urlconn.getInputStream();
                        String urlresult = ProjectFunctions.inputStreamToString(istream);
                        songInfos.get(i).add(KuWoBaseInfoStructure.URL, urlresult);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
