package com.example.sortlistview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SideBar extends View {
	// 触摸事件
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	// 26个字母
	public static String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "#" };
	private int choose = -1;// 选中
	private Paint paint = new Paint();

	private TextView mTextDialog;

	private int mTextSizePressed;

	private int mWidth, mHeight, mItemHeight;

	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}


	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SideBar(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context)
	{
		//初始化默认属性
		Resources resources = context.getResources();
		mTextSizePressed = resources.getDimensionPixelSize(R.dimen.ib_text_size_normal_default);
	}

	/**
	 * 重写这个方法
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 获取焦点改变背景颜色.
		int height = getHeight();// 获取对应高度
		int width = getWidth(); // 获取对应宽度
		int singleHeight = height / b.length;// 获取每一个字母的高度

		for (int i = 0; i < b.length; i++) {
			paint.setColor(Color.rgb(33, 65, 98));
			// paint.setColor(Color.WHITE);
			paint.setTypeface(Typeface.DEFAULT);
			paint.setAntiAlias(true);
			paint.setTextSize(mTextSizePressed);
			// 选中的状态
			if (i == choose) {
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}
			// x坐标等于中间-字符串宽度的一半.
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(b[i], xPos, yPos, paint);
			paint.reset();// 重置画笔
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
		if (b != null && b.length > 0)
			mItemHeight = (mHeight - getPaddingTop() - getPaddingBottom()) / b.length;
		//如果没有指定具体的宽度，修改宽度为Item高度+paddingLeft+paddingRight
		if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY)
			mWidth = mItemHeight + getPaddingLeft() + getPaddingRight();
		setMeasuredDimension(mWidth, mHeight);
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();// 点击y坐标
		Log.d("liyangzi","event.getY()=="+y);
		Log.d("liyangzi","getHeight()=="+getHeight());
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * b.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.
		Log.d("liyangzi","int c=="+c);

		switch (action) {
		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			choose = -1;//
			invalidate();
			if (mTextDialog != null) {
				mTextDialog.setVisibility(View.INVISIBLE);
			}
			break;

		default:
			setBackgroundResource(R.drawable.sidebar_background);
			if (oldChoose != c) {
				if (c >= 0 && c < b.length) {
					if (listener != null) {
						listener.onTouchingLetterChanged(b[c]);
					}
					if (mTextDialog != null) {
						mTextDialog.setText(b[c]);
//						mTextDialog.setTranslationY(y+getHeight());
                        mTextDialog.setY(calPositionY(b[c],c));
                        Log.d("liyangzi","mTextDialog.getTop()=="+mTextDialog.getTop());
						Log.d("liyangzi","mTextDialog.getBottom()=="+mTextDialog.getBottom());
                        Log.d("liyangzi","mTextDialog.getHeight()=="+mTextDialog.getBottom());
						mTextDialog.setVisibility(View.VISIBLE);
					}
					
					choose = c;
					invalidate();
				}
			}

			break;
		}
		return true;
	}


	private float calPositionY(String str ,int index)
	{
		Rect rect = new Rect();
		paint.getTextBounds(String.valueOf(str), 0, str.length(), rect);
		float y = mItemHeight * index + (mItemHeight + rect.height()) / 2 + getPaddingTop();
		return y;
	}



	/*//计算位置
	private Pair<Float, Float> calPosition(CharSequence str, Paint paint, int index)
	{
		// x坐标等于中间-字符串宽度的一半.
		float x = (mWidth - paint.measureText(String.valueOf(str))) / 2;
		Rect rect = new Rect();
		paint.getTextBounds(String.valueOf(str), 0, str.length(), rect);
		float y = mItemHeight * index + (mItemHeight + rect.height()) / 2 + getPaddingTop();
		return new Pair<>(x, y);
	}*/

	/**
	 * 向外公开的方法
	 * 
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	/**
	 * 接口
	 * 
	 * @author coder
	 * 
	 */
	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}