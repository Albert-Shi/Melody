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
public class GetMusicFromKuwo {
    public ArrayList<MusicInfo> songInfos = new ArrayList<>();
    private void dealPrimaryJSON(String json) {
        try {
            JSONObject all = new JSONObject(json);
            JSONArray list = all.getJSONArray("abslist");
            for (int i = 0; i < list.length(); i++) {
                JSONObject song = list.getJSONObject(i);
                MusicInfo songinfo = new MusicInfo(song.getString("SONGNAME"), song.getString("ARTIST"), song.getString("ALBUM"), "url");
                songinfo.setSongId(song.getString("MUSICRID"));
                songInfos.add(songinfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public GetMusicFromKuwo (final String name, final String page, final String pagesize) {
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
                        String server = "http://antiserver.kuwo.cn/anti.s?type=convert_url&format=aac|mp3&response=url&rid=" + songInfos.get(i).getSongId();
                        URL geturl = new URL(server);
                        HttpURLConnection urlconn = (HttpURLConnection) geturl.openConnection();
                        urlconn.setDoInput(true);
                        urlconn.setRequestMethod("GET");
                        urlconn.setUseCaches(false);
                        urlconn.setReadTimeout(5000);
                        urlconn.connect();
                        InputStream istream = urlconn.getInputStream();
                        String urlresult = ProjectFunctions.inputStreamToString(istream);
                        songInfos.get(i).setUrl(urlresult);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
