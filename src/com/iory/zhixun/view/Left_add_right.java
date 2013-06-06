package com.iory.zhixun.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 有加入左右view的方法，移动的调用方法，主要被toplayout调用
 * @author yangjie
 *
 */
public class Left_add_right extends ViewGroup{

	Context ctx;
	LinearLayout leftLayout;
	LinearLayout centerLayout;
	LinearLayout rightLayout;
	
	public boolean isLeftExtends;
	boolean isRightExtends;
	boolean isInited = false;
	
	private Scroller mScroller;
	
	//定义左边宽度
	private int leftWidth = 20;
	
	public Left_add_right(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		ctx = context;
		init();
	}
	
	private void init(){
		
        mScroller = new Scroller(ctx);
		
		leftLayout = new LinearLayout(ctx);
		addView(leftLayout);
		
		centerLayout = new LinearLayout(ctx);
		
		centerLayout.setOrientation(LinearLayout.VERTICAL);
		addView(centerLayout);
		
		rightLayout = new LinearLayout(ctx);
		addView(rightLayout);
	}

	public void setLeftWidth(int width){
		leftWidth = width;
	}
	
	/**
	 * 展示的宽度
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		int childLeft = 0;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth,
						child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}
	  //view.View的子函数,需要重写
    //执行初始的判断测量，滑到初始位置，其他时候不产生作用
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//有三种模式，一种是atmost，unspecial,exectal,
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		//MeasureSpec是全局静态变量View的成员变量，封装传递从父到子的布局要求。,获取模式
		if (widthMode != MeasureSpec.EXACTLY) {//如果父母没有为子窗体提供合适的宽度，则抛出异常
			throw new IllegalStateException("error mode.");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			//如果父母没有为子窗体提供合适的高度，则抛出异常
			throw new IllegalStateException("error mode.");
		}
		int measureSpec = MeasureSpec.makeMeasureSpec(Getdensity.dip2px(ctx, leftWidth), MeasureSpec.EXACTLY);
		leftLayout.measure(measureSpec, heightMeasureSpec);
		centerLayout.measure(widthMeasureSpec, heightMeasureSpec);
		rightLayout.measure(measureSpec, heightMeasureSpec);
		if (isInited == false)
		{
			scrollTo(leftLayout.getMeasuredWidth(), 0);
			isInited = true;
		}
		
		
	}

	/**
	 * 当我们执行ontouch或invalidate(）或postInvalidate()都会导致这个方法的执行 
	 *当startScroll执行过程中即在duration时间内，computeScrollOffset  方法会一直返回false，但当动画执行完成后会返回返加true. 
	 */
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if(mScroller.computeScrollOffset()){
			int x = mScroller.getCurrX();
			scrollTo(x, 0);
			postInvalidate();
		}
		super.computeScroll();
	}

	/**
	 * 加入左view
	 * @param view
	 */
	public void addViewleft(View view){
		if(leftLayout.getChildCount()>0){
			leftLayout.removeAllViews();
		}
		leftLayout.addView(view, LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		
	}
	/**
	 * 加入中view
	 * @param view
	 */
	public void addViewCenter(View view)
	{
		if (centerLayout.getChildCount() > 0)
		{
			centerLayout.removeAllViews();
		}
		centerLayout.addView(view, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
	}
	/**
	 * 加入右view
	 * @param view
	 */
	public void addViewRight(View view) {
		if (rightLayout.getChildCount() > 0)
		{
			rightLayout.removeAllViews();
		}
		rightLayout.addView(view, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
	}
	
	/**
	 * 获取是否左扩展
	 * @return
	 */
	public boolean isLeftExtended()
	{
		return isLeftExtends;
	}
	/**
	 * 获取是否右扩展
	 * @return
	 */
	public boolean isRightExtended()
	{
		return isRightExtends;
	}
	/**
	 * 展示左边的view,如果没有这个方法，将无法展示左view
	 */
	public void showLeft(){
		
		if(isLeftExtends == false && isRightExtends == false && leftLayout.getChildCount() > 0){
			int delta = 0 - leftLayout.getWidth();
			
			mScroller.startScroll(getScrollX(), 0, delta, 0, 500);
			invalidate();
			isLeftExtends = true;
		}
	}
	/**
	 * 向右移动，没有这个方法，只会一直向左移动
	 */
	public void showRight(){
		if(isLeftExtends == false && isRightExtends == false && leftLayout.getChildCount() > 0 ){
			int delat = rightLayout.getWidth();
			mScroller.startScroll(getScrollX(),0,delat, 0, 500);
			invalidate();
			isRightExtends = true;
		}
	}
	
	/**
	 * 中间展示状态，没有这个将一直处于中间太
	 * @return
	 */
	public boolean showCenter(){
		if (!mScroller.isFinished())
			return true;
		
		if(isLeftExtends){
			int delta = leftLayout.getWidth();
			mScroller.startScroll(getScrollX(), 0, delta, 0, 500);
		}else if (isRightExtends){
			int delta2 = rightLayout.getWidth();
			mScroller.startScroll(getScrollX(), 0, -delta2, 0, 500);
		}

		
	
		
		invalidate();
		isLeftExtends = false;
		isRightExtends = false;
		
		
		return true;
	}
}
