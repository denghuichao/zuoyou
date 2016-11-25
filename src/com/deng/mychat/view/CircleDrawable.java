package com.deng.mychat.view;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Created With Android Studio
 * User @47
 * Date 2014-07-28
 * Time 0:32
 */
public  class CircleDrawable extends Drawable {
    public static final String TAG = "CircleDrawable";

    protected final Paint paint;

    protected final int margin;
    protected final BitmapShader bitmapShader;
    protected float radius;
    protected Bitmap oBitmap;//ԭͼ
    public CircleDrawable(Bitmap bitmap){
        this(bitmap,0);
    }

    public CircleDrawable(Bitmap bitmap, int margin) {
        this.margin = margin;
        this.oBitmap = bitmap;
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        computeBitmapShaderSize();
        computeRadius();

    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();//��һ��ԲȦ
        canvas.drawCircle(bounds.width() / 2F,bounds.height() / 2F,radius,paint);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }


    /**
     * ����Bitmap shader ��С
     */
    public void computeBitmapShaderSize(){
        Rect bounds = getBounds();
        if(bounds == null) return;
        //ѡ�����űȽ϶�����ţ�����ͼƬ�Ͳ�����ͼƬ����ʧ��
        Matrix matrix = new Matrix();
        float scaleX = bounds.width() / (float)oBitmap.getWidth();
        float scaleY = bounds.height() / (float)oBitmap.getHeight();
        float scale = scaleX > scaleY ? scaleX : scaleY;
        matrix.postScale(scale,scale);
        bitmapShader.setLocalMatrix(matrix);
    }

    /**
     * ����뾶�Ĵ�С
     */
    public void computeRadius(){
        Rect bounds = getBounds();
        radius = bounds.width() < bounds.height() ?
                bounds.width() /2F - margin:
                bounds.height() / 2F - margin;
    }
}
