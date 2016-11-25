package com.deng.mychat.view;

import com.deng.mychat.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class MyRoundImageRing extends View {
	
	private int layoutWidth=0;
	private int layoutHeight=0;
	private int rimWidth=3;
	
	private int paddingTop = 5;
	private int paddingBottom = 5;
	private int paddingLeft = 5;
	private int paddingRight = 5;
	
	private int rimColor = 0xEE000000;
	private int bgColor=0x99FF33;

	
	private Paint rimPaint = new Paint();
	private Paint bgPaint = new Paint();
	private Paint innerPaint=new Paint();
	
	private RectF rectBounds = new RectF();
	private RectF innerBounds= new RectF();
	
	
	
	private Context mContext;
	
	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			invalidate();
		}
	};
	
	public MyRoundImageRing(Context context) {
		super(context);
		mContext=context;
	}

	public MyRoundImageRing(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.MyRoundImageRing));
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		
		layoutWidth = this.getLayoutParams().width;
		layoutHeight =  this.getLayoutParams().height;
		
		setupBounds();
		setupPaints();
		invalidate();
	}

	private void setupPaints()
	{
		rimPaint.setColor(rimColor);
		rimPaint.setAntiAlias(true);
		rimPaint.setStyle(Style.STROKE);
		rimPaint.setStrokeWidth(rimWidth);
		
		bgPaint.setColor(bgColor);
		bgPaint.setAntiAlias(true);
		bgPaint.setStyle(Style.FILL);
		
	}
	
	
	private void setupBounds()
	{
		int minValue = Math.min(layoutWidth, layoutHeight);

		int xOffset = layoutWidth - minValue;
		int yOffset = layoutHeight - minValue;

		paddingTop = this.getPaddingTop() + (yOffset / 2);
		paddingBottom = this.getPaddingBottom() + (yOffset / 2);
		paddingLeft = this.getPaddingLeft() + (xOffset / 2);
		paddingRight = this.getPaddingRight() + (xOffset / 2);

		rectBounds = new RectF(paddingLeft, paddingTop,
				this.getLayoutParams().width - paddingRight, this.getLayoutParams().height
						- paddingBottom);
		
		innerBounds = new RectF(paddingLeft+rimWidth/3, paddingTop+rimWidth/3,
				this.getLayoutParams().width - paddingRight-rimWidth/3, this.getLayoutParams().height
						- paddingBottom-rimWidth/3);
	}
	

	private void parseAttributes(TypedArray a) {
	
		
		rimWidth = (int) a.getDimension(R.styleable.MyRoundImageRing_rimWidth, rimWidth);
		rimColor = (int) a.getColor(R.styleable.MyRoundImageRing_rimColor, rimColor);
        bgColor=(int)a.getColor(R.styleable.MyRoundImageRing_bgColor, bgColor);
		a.recycle();
	}
	
	
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		canvas.drawArc(rectBounds,0, 360, false, rimPaint);
	}
	


	public int getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}

	public int getPaddingBottom() {
		return paddingBottom;
	}

	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	public int getPaddingRight() {
		return paddingRight;
	}

	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
	}
	
	
	public int getRimColor() {
		return rimColor;
	}

	public void setRimColor(int rimColor) {
		this.rimColor = rimColor;
	}

	public Shader getRimShader() {
		return rimPaint.getShader();
	}

	public void setRimShader(Shader shader) {
		this.rimPaint.setShader(shader);
	}
	
	public int getRimWidth() {
		return rimWidth;
	}

	public void setRimWidth(int rimWidth) {
		this.rimWidth = rimWidth;
	}

	
}
