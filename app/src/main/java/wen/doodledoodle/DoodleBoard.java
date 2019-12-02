package wen.doodledoodle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DoodleBoard extends View {

    private int width = 0;
    private int height = 0;
    private float preX = 0;
    private float preY = 0;
    private Path path;

    public static Paint paint = null;
    public static Bitmap bitmap = null;
    private static Canvas canvas = null;

    public DoodleBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 获取宽高并打印
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        System.out.println("图像宽高：" + width + " / " + height);

        // 创建缓存区
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        path = new Path();
        Bitmap tempBitmap = getBitmapByColor(width, height, Color.WHITE);

        //true表示该bitmap对象是可变的；false则反之
        bitmap = tempBitmap.copy(tempBitmap.getConfig(), true);
        canvas.setBitmap(bitmap);

        // 设定画笔默认风格
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(MainActivity.strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setDither(true);


    }

    /**
     * 得到相应背景色的位图
     *
     * @param width  位图的宽度
     * @param height 位图的高度
     * @param color  位图的背景色
     * @return 该颜色的位图
     */
    public Bitmap getBitmapByColor(int width, int height, int color) {
        Bitmap newBitmap;
        //新建像素点数组，数组元素个数是位图的宽乘以高
        int[] colors = new int[width * height];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = color;//将颜色赋值给每一个像素点
        }
        newBitmap = Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888);
        return newBitmap;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(0xFFFFFFFF);
        Paint drawPaint = new Paint();
        canvas.drawBitmap(bitmap, 0, 0, drawPaint);
        canvas.drawPath(path, paint);
        canvas.save();
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                preX = x;
                preY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - preX);
                float dy = Math.abs(y - preY);
                if (dx >= 5 || dy >= 5) {
                    path.quadTo(preX, preY, (x + preX) / 2, (y + preY) / 2);
                    preX = x;
                    preY = y;
                }
                break;

            case MotionEvent.ACTION_UP:
                canvas.drawPath(path, paint);
                path.reset();
                break;
        }

        invalidate();
        return true;
    }

    public static void eraser() {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(MainActivity.eraserWidth);
    }

    public static void clear() {
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

}
