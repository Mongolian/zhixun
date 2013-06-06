package com.iory.zhixun.view;

import com.iory.zhixun.app.TLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


/**
 * 类似桌面的workspace控件
 * 
 * @author albertzhong
 */
public class HorizonScrollLayout extends ViewGroup {

	private static final String TAG = "ScrollLayout";
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private int mCurScreen;
	private int mDefaultScreen = 0;
	private int mChildScreenWidth = 0;
	private int mScreenWidth = 0;

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private static final int TOUCH_STATE_LOCK = 2;

	private static final int SNAP_VELOCITY = 600;

	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop = 24;
	private float mAngleSlop = 0.577f; // tan(a) = x/y , 滑动的角度30
	private float mLastMotionX;
	private float mLastMotionY;

	private float mMinScrollX = 0;
	private float mMaxScrollX = 0;

	private float mPreScrollX = 0;

	// 屏幕状态
	private int mScrollState = OnTouchScrollListener.SCROLL_STATE_IDLE;

	// 是否禁止滚动标记
	private boolean mEnableScroll = true;

	// 状态回调
	private OnTouchScrollListener mTouchScrollListener = null;


	/**
	 * 屏幕滚动回调
	 * 
	 * @author albertzhong
	 * 
	 */
	public interface OnTouchScrollListener {
		// scroll state
		public static int SCROLL_STATE_IDLE = 0;
		public static int SCROLL_STATE_TOUCH_SCROLL = 1;
		public static int SCROLL_STATE_FLING = 2;


		/**
		 * 当前显示的屏幕改变
		 * 
		 * @param displayScreem
		 *            :显示的屏幕
		 */
		public void onScreenChange(int displayScreem);


		/**
		 * 屏幕当前滚动的位置
		 * 
		 * @param leftX
		 *            :当前屏左边的位置
		 * @param screemWidth
		 *            :屏幕的总宽度
		 */
		public void onScroll(View view, float leftX, float screemWidth);


		/**
		 * 滚动状态
		 * 
		 * @param scrollState
		 * @param currentScreem
		 */
		public void onScrollStateChanged(int scrollState, int currentScreem);
	}


	public HorizonScrollLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}


	public HorizonScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new Scroller(context);

		mCurScreen = mDefaultScreen;
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		TLog.v(TAG, "plan1, onLayout, changed = " + changed);
		int childLeft = 0;
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {
				final int childWidth = childView.getMeasuredWidth();

				// 如果屏幕大小末发生变化, 只对当前屏幕重新布局, 否则全部布局
				if (changed == false) {
					if (i == mCurScreen) {
						childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
						childView.postInvalidate();
						break;
					}
				} else {
					childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
					childView.postInvalidate();
				}
				childLeft += childWidth;
			}
		}

		// 限制滑动范围
		mChildScreenWidth = getWidth(); // 子屏幕宽度
		mScreenWidth = mChildScreenWidth * childCount; // 屏幕总宽度
		mMinScrollX = -(mChildScreenWidth >> 2); // 向左移动范围
		mMaxScrollX = mScreenWidth - mChildScreenWidth - mMinScrollX; // 向右移动范围

		// 区域改变后,重新移动
		if (changed == true) {
			// 解决横竖屏切换位置不正确问题
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			scrollTo(mCurScreen * getWidth(), 0);
		}
	}


	public void layoutChild(int child) {
		final View childView = getChildAt(child);
		if (childView != null) {
			childView.requestLayout();
		}
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		TLog.v(TAG, "plan1, onMeasure");
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			// throw new
			// IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			// throw new
			// IllegalStateException("ScrollLayout only can run at EXACTLY mode!");
		}

		// The children are given the same width and height as the scrollLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);

		}
	}


	/**
	 * According to the position of current layout scroll to the destination
	 * page.
	 */
	public void snapToDestination() {
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen, true);
	}


	public void snapToScreen(int whichScreen, boolean isCallback) {
		// get the valid layout page
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			if (isCallback == true && mTouchScrollListener != null && mCurScreen != whichScreen) {
				mTouchScrollListener.onScreenChange(whichScreen);
			}

			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);

			mCurScreen = whichScreen;

			layoutChild(mCurScreen);
			invalidate(); // Redraw the layout
		}
	}


	// 无过度动画
	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;
		scrollTo(whichScreen * getWidth(), 0);
		if (mTouchScrollListener != null) {
			mTouchScrollListener.onScreenChange(whichScreen);
		}
		layoutChild(mCurScreen);
		invalidate(); // Redraw the layout
	}


	public int getCurScreen() {
		return mCurScreen;
	}


	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();

			// 状态回调
			if (mTouchScrollListener != null) {
				// 开始
				if (mScroller.isFinished() == false) {
					TLog.v(TAG, "computeScroll, computeScrollOffset=" + true + ", finish=" + false);
					if (mScrollState != OnTouchScrollListener.SCROLL_STATE_FLING) {
						mTouchScrollListener.onScrollStateChanged(OnTouchScrollListener.SCROLL_STATE_FLING, mCurScreen);
						mScrollState = OnTouchScrollListener.SCROLL_STATE_FLING;
					}
				} else {
					// 结束
					TLog.v(TAG, "computeScroll, computeScrollOffset=" + true + ", finish=" + true);
					if (mScrollState != OnTouchScrollListener.SCROLL_STATE_IDLE) {
						mTouchScrollListener.onScrollStateChanged(OnTouchScrollListener.SCROLL_STATE_IDLE, mCurScreen);
						mScrollState = OnTouchScrollListener.SCROLL_STATE_IDLE;
					}
				}
			}
		} else {
			if (mTouchScrollListener != null) {
				TLog.v(TAG, "computeScroll, computeScrollOffset=" + false + ", finish=" + mScroller.isFinished() + ", touch=" + mTouchState);
				// 因为自由滚动结束后,还会调用一次computeScroll, 所以需要加入mTouchState的判断
				if (mScrollState != OnTouchScrollListener.SCROLL_STATE_TOUCH_SCROLL && mTouchState == TOUCH_STATE_SCROLLING) {
					mTouchScrollListener.onScrollStateChanged(OnTouchScrollListener.SCROLL_STATE_TOUCH_SCROLL, mCurScreen);
					mScrollState = OnTouchScrollListener.SCROLL_STATE_TOUCH_SCROLL;
				}
			}
		}

		// 滚动回调, 类似 listView的onScroll
		if (mPreScrollX != getScrollX()) {
			mPreScrollX = getScrollX();
			if (mTouchScrollListener != null) {
				mTouchScrollListener.onScroll(this, getScrollX(), mScreenWidth);
			}

		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mEnableScroll) {
			return true;
		}

		TLog.v(TAG, "onTouchEvent");
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		final int action = event.getAction();
		final float x = event.getRawX();
		final float y = event.getRawY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			TLog.v(TAG, "event down!");
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;

		case MotionEvent.ACTION_MOVE:
			// 锁定,当手势向下滑动后,就不进行左右滑动
			if (mTouchState == TOUCH_STATE_LOCK) {
				break;
			}
			int deltaX = (int) (mLastMotionX - x);
			mLastMotionX = x;
			int beScrollTo = getScrollX() + deltaX;
			if (beScrollTo > mMinScrollX && beScrollTo < mMaxScrollX) {
				scrollBy(deltaX, 0);
				// if( mTouchScrollListener != null){
				// Log.v(TAG, "touch, cenX=" + (getScrollX() +
				// (mChildScreenWidth >>1)));
				// mTouchScrollListener.onTouchScrolling(getScrollX(),
				// mScreenWidth);
				// }
			}
			break;

		case MotionEvent.ACTION_UP:
			TLog.v(TAG, "event : up");
			// if (mTouchState == TOUCH_STATE_SCROLLING) {
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();

			TLog.v(TAG, "velocityX:" + velocityX);

			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				TLog.v(TAG, "snap left");
				snapToScreen(mCurScreen - 1, true);
			} else if (velocityX < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1) {
				// Fling enough to move right
				TLog.v(TAG, "snap right");
				snapToScreen(mCurScreen + 1, true);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			// }
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return true;
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		TLog.v(TAG, "onInterceptTouchEvent-slop:" + mTouchSlop);
		if (!mEnableScroll) {
			return false;
		}

		final int action = ev.getAction();

		// 移动过程中,锁定
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState == TOUCH_STATE_SCROLLING)) {
			TLog.v(TAG, "onInterceptTouchEvent lock return true:");
			return true;
		}

		final float x = ev.getRawX();
		final float y = ev.getRawY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			if (mTouchState == TOUCH_STATE_LOCK) {
				break;
			}
			final float xDiff = Math.abs(mLastMotionX - x);
			final float yDiff = Math.abs(mLastMotionY - y);
			TLog.v(TAG, "ACTION_MOVE xDiff:" + xDiff + ", yDiff:" + yDiff);

			if (xDiff > mTouchSlop) {
				float tan = yDiff / xDiff;
				TLog.v(TAG, "onInterceptTouchEvent-AngleSlop:" + tan);
				if (tan < mAngleSlop) {
					mTouchState = TOUCH_STATE_SCROLLING;
				} else {
					mTouchState = TOUCH_STATE_LOCK;
				}
			}
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return mTouchState == TOUCH_STATE_SCROLLING;
	}


	public void setDisplayedChild(int i) {
		// 有动画
		snapToScreen(i, false);
		// 无动画
		// setToScreen(i);
	}


	public int getDisplayedChild() {
		return mCurScreen;
	}


	// 如要绘制中间过程特效,在dispathchDraw中添加代码
	// @Override
	// protected void dispatchDraw(Canvas canvas) {
	// boolean restore = false;
	// int restoreCount = 0;
	//
	// // ViewGroup.dispatchDraw() supports many features we don't need:
	// // clip to padding, layout animation, animation listener, disappearing
	// // children, etc. The following implementation attempts to fast-track
	// // the drawing dispatch by drawing only what we know needs to be drawn.
	//
	// // boolean fastDraw = mTouchState != TOUCH_STATE_SCROLLING && mNextScreen
	// == INVALID_SCREEN;
	// // If we are not scrolling or flinging, draw only the current screen
	// // if (fastDraw) {
	// // drawChild(canvas, getChildAt(mCurScreen), getDrawingTime());
	// // } else {
	// // final long drawingTime = getDrawingTime();
	// // final float scrollPos = (float) mScrollX / getWidth();
	// // final int leftScreen = (int) scrollPos;
	// // final int rightScreen = leftScreen + 1;
	// // if (leftScreen >= 0) {
	// // drawChild(canvas, getChildAt(leftScreen), drawingTime);
	// // }
	// // if (scrollPos != leftScreen && rightScreen < getChildCount()) {
	// // drawChild(canvas, getChildAt(rightScreen), drawingTime);
	// // }
	// // }
	//
	// if (restore) {
	// canvas.restoreToCount(restoreCount);
	// }
	// }

	public void setOnTouchScrollListener(OnTouchScrollListener listener) {
		mTouchScrollListener = listener;
	}


	public OnTouchScrollListener getOnTouchScrollListener() {
		return mTouchScrollListener;
	}


	public void setTouchScrollEnable(boolean enable) {
		mEnableScroll = enable;
	}


	public boolean getEnableScroll() {
		return mEnableScroll;
	}


	public void setDefaultScreem(int screem) {
		mDefaultScreen = screem;
		mCurScreen = mDefaultScreen;
	}


	public int getDefaultScreem() {
		return mDefaultScreen;
	}
	
	public void destroy() {
		mScroller = null;
		mVelocityTracker = null;
		mTouchScrollListener = null;
	}
}
