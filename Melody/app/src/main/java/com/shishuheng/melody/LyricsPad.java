package com.shishuheng.melody;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by 史书恒 on 2016/9/26.
 */

public class LyricsPad extends View {
    private MainActivity Parent;
    private DisplayMetrics dm;
    private Paint paint, subPaint;

    public LyricsPad(Context context, MainActivity parent) {
        super(context);
        this.Parent = parent;
        dm = new DisplayMetrics();
        parent.getWindowManager().getDefaultDisplay().getMetrics(dm);
        paint = new Paint();
        paint.setTextSize(26);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(255, 248, 70, 131));

        subPaint = new Paint();
        subPaint.setTextSize(24);
        subPaint.setTextAlign(Paint.Align.CENTER);
        subPaint.setAntiAlias(true);
//        subPaint.setColor(Color.argb(200, 255, 255, 255));
        subPaint.setColor(Color.argb(255, 255, 255, 255));
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Parent.lrc.getCurrentLine(Parent.CurrentTime_Lyrics, Parent.CurrentPosition_Lyrics);
        if (Parent.lrc.arrayLyrics.get(1).get(Parent.CurrentPosition_Lyrics) != null) {
            final int width_center = getWidth()/2;
            final int height_center = getHeight()/2;
            final int interval = height_center/4;
            if (Parent.CurrentPosition_Lyrics - 3 >= 0) {
                canvas.drawText((String) Parent.lrc.arrayLyrics.get(1).get(Parent.CurrentPosition_Lyrics - 3), width_center, height_center - (3 * interval), subPaint);
            }else {
                canvas.drawText("", width_center, height_center - (3 * interval), subPaint);
            }
            if (Parent.CurrentPosition_Lyrics - 2 >= 0) {
                canvas.drawText((String) Parent.lrc.arrayLyrics.get(1).get(Parent.CurrentPosition_Lyrics - 2), width_center, height_center - (2 * interval), subPaint);
            }else {
                canvas.drawText("", width_center, height_center - (2 * interval), subPaint);
            }
            if (Parent.CurrentPosition_Lyrics - 1 >= 0) {
                canvas.drawText((String) Parent.lrc.arrayLyrics.get(1).get(Parent.CurrentPosition_Lyrics - 1), width_center, height_center - interval, subPaint);
            }else {
                canvas.drawText("", width_center, height_center - interval, subPaint);
            }

            canvas.drawText((String) Parent.lrc.arrayLyrics.get(1).get(Parent.CurrentPosition_Lyrics), width_center, height_center, paint);

            if (Parent.CurrentPosition_Lyrics + 1 < Parent.lrc.arrayLyrics.get(0).size()) {
                canvas.drawText((String) Parent.lrc.arrayLyrics.get(1).get(Parent.CurrentPosition_Lyrics + 1), width_center, height_center + interval, subPaint);
            }else {
                canvas.drawText("", width_center, height_center + interval, subPaint);
            }
            if (Parent.CurrentPosition_Lyrics + 2 < Parent.lrc.arrayLyrics.get(0).size()) {
                canvas.drawText((String) Parent.lrc.arrayLyrics.get(1).get(Parent.CurrentPosition_Lyrics + 2), width_center, height_center + (2 * interval), subPaint);
            }else {
                canvas.drawText("", width_center, height_center + (2 * interval), subPaint);
            }
            if (Parent.CurrentPosition_Lyrics + 3 < Parent.lrc.arrayLyrics.get(0).size()) {
                canvas.drawText((String) Parent.lrc.arrayLyrics.get(1).get(Parent.CurrentPosition_Lyrics + 3), width_center, height_center + (3 * interval), subPaint);
            }else {
                canvas.drawText("", width_center, height_center + (3 * interval), subPaint);
            }
        }
    }
}
