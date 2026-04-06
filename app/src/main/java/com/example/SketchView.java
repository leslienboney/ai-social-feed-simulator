package com.example.a433assn4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SketchView extends View {

    private Paint paint;
    private Path path;

    public SketchView(Context context) {
        super(context);
        init();
    }

    public SketchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SketchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFF000000);

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
                invalidate();
                return true;

            default:
                return false;
        }
    }

    public void clear() {
        path.reset();
        invalidate();
    }

    public Path getPath() {
        return path;
    }

    // Export the user's sketch as a Bitmap
    public Bitmap exportBitmap() {
        Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        draw(canvas);   // draw current sketch into the bitmap
        return bmp;
    }
}