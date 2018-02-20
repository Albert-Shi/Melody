package com.shishuheng.melody;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.ViewAnimator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public PlayFragment pf;
    public OnlineFragment of;
    public Intent serviceintent;
    public ArrayList<MusicInfo> MusicInfos = null;
    public int MusicInfos_Position = 0;
    public int CurrentTime_Lyrics = 0;
    public int CurrentPosition_Lyrics = 0;

    public ProjectFunctions projectFunctions;
    public ProjectFunctions.Lyrics lrc;

    public void blurBackground(final Bitmap src) {
        final Handler handler  = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                /*将图片调整到原尺寸长宽的0.05倍*/
                Matrix matrix = new Matrix();
                matrix.postScale(0.05f, 0.05f);
                Bitmap bitmap = Bitmap.createBitmap(src,0, 0, src.getWidth(), src.getHeight(), matrix, false);

                /*将图片调整到适合屏幕比例16:9*/
                int X = (bitmap.getWidth() - (bitmap.getHeight()/16*9))/2;
                Bitmap mp = Bitmap.createBitmap(bitmap, X, 0, bitmap.getHeight()/16*9, bitmap.getHeight());

                /*调用ProjectFunctions中的类方法模糊处理过比例后的图片*/
                Bitmap blurBitmap = ProjectFunctions.blurBitmap(mp, mp.getWidth(), mp.getHeight(), 25f, getApplicationContext());
                /*设置背景*/
                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.content_main);
                Drawable drawable = new BitmapDrawable(getResources(), blurBitmap);
                relativeLayout.setBackground(drawable);

//                LinearLayout bg = (LinearLayout)findViewById(R.id.album_pic);
                super.handleMessage(msg);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                handler.sendMessage(message);
            }
        }).start();
    }

    private void viewpage (final ArrayList<Fragment> fragments) {


        PagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };

        ViewPager pager = (ViewPager) findViewById(R.id.viewpager_main);
        pager.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        serviceintent = new Intent(this, MelodyService.class);
        startService(serviceintent);

        projectFunctions = new ProjectFunctions();

        ArrayList<Fragment> fragments = new ArrayList<>();
        pf = new PlayFragment();
        of = new OnlineFragment();
        fragments.add(pf);
        fragments.add(of);
        viewpage(fragments);

        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mass);
        blurBackground(originBitmap);

        final ViewAnimator vm = (ViewAnimator)findViewById(R.id.animator);
        vm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vm.showNext();
            }
        });

        vm.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left));


//        getSupportFragmentManager().beginTransaction().add(R.id.content_main, new PlayFragment()).commit();
        /*
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                getSupportFragmentManager().beginTransaction().add(R.id.content_main, new PlayFragment()).commit();
                super.handleMessage(msg);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                handler.sendMessage(message);
            }
        }).start();
        */
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示");
            builder.setMessage("确定要退出?");
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    stopService(serviceintent);
                    System.exit(1);
                }
            });
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).create().show();
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
