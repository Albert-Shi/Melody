package com.shishuheng.melody;

/**
 * Created by 史书恒 on 2016/8/29.
 */

public class CommandKey {
    public static String play_file = "FILE";
    public static String file_key = "NAME";
    public static String seek = "SEEKTO";
    public static String seek_key = "CURRENTTIME";
    public static String button_control = "BUTTON";
    public static String button_key = "FLAG";
    public static String last_button = "LAST";
    public static String next_button = "NEXT";
    public static String play_resume = "PLAY";
    public static String pause_button = "PAUSE";
    public static String mode_change = "CHANGEMODE";
    public static int LOOP_ALL_MODE = 0;
    public static int LOOP_ONE_MODE = 1;
    public static int SRQUENCE_MODE = 2;
    public static int RANDOM_MODE = 3;
    public static String current_position_filter = "GETTIME";
    public static String current_position_key = "CURRENTTIME";
    static class SongsInfoStructure{
        public static int head = 0;
        public static int song_id = 1;
        public static int song_name = 2;
        public static int artist = 3;
        public static int album_name = 4;
        public static int album_picture_url = 5;
        public static int song_url = 6;
        public static int dj_program_id = 7;
    }
}
