package com.iory.zhixun.view;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import com.iory.zhixun.R;

import java.lang.reflect.Method;


public class OverScroller {
	
	private static final String TAG = OverScroller.class.getName();
	
	private float mScrollDistanceSinceBoundary = 0;
	private Rect mPaddingRectangle = new Rect();
	private GestureDetector mListViewGestureDetector;
	private boolean mInterruptFade = false;
	private EdgeGlow mEdgeGlowTop;
	private EdgeGlow mEdgeGlowBottom;

	private ViewGroup mList;

	private float mDistanceTraveled;

	public OverScroller(ViewGroup list) {
		this.mList = list;
		initGlowEdge();
	}

	public void dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			mInterruptFade = true;
		}
		if (mListViewGestureDetector != null) {
			mListViewGestureDetector.onTouchEvent(ev);
		}
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			mInterruptFade = false;
			mScrollDistanceSinceBoundary = 0;
			if (mEdgeGlowTop != null && !mEdgeGlowTop.isFinished()) {
				mEdgeGlowTop.onAbsorb((int) mDistanceTraveled);
				mEdgeGlowTop.finish();
			}

			if (mEdgeGlowBottom != null && !mEdgeGlowBottom.isFinished()) {
				mEdgeGlowBottom.onAbsorb((int) mDistanceTraveled);
				mEdgeGlowTop.finish();
			}

		}
	}

	// 去掉2.3中发光效果
	private void initGlowEdge() {
		mListViewGestureDetector = new GestureDetector(new ListViewGestureDetector());
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK);
			if (version > 9) {
				Method method = this.getClass().getMethod("setOverScrollMode", int.class);
				if (method != null) {
					method.invoke(this, 2);
				}
			}
		} catch (Exception e) {

		}

		try {
			if (mEdgeGlowTop == null) {
				final Resources res = mList.getContext().getResources();
				final Drawable edge = res.getDrawable(R.drawable.overscroll_edge);
				final Drawable glow = res.getDrawable(R.drawable.overscroll_glow);
				mEdgeGlowTop = new EdgeGlow(edge, glow);
				mEdgeGlowBottom = new EdgeGlow(edge, glow);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private class ListViewGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent downMotionEvent, MotionEvent currentMotionEvent, float distanceX, float distanceY) {
			mDistanceTraveled = downMotionEvent.getY() - currentMotionEvent.getY();
			if (listIsAtTop() && mDistanceTraveled < 0) {
				mScrollDistanceSinceBoundary -= distanceY;
				if (distanceY < 0 && mEdgeGlowTop != null) {
					mEdgeGlowTop.onPull((float) distanceY / mList.getHeight());
					if (mEdgeGlowBottom != null && !mEdgeGlowBottom.isFinished()) {
						mEdgeGlowBottom.onRelease();
					}
				}
				mList.invalidate();
			} else if (listIsAtTop() && mDistanceTraveled > 0 && mScrollDistanceSinceBoundary > 0) {
				mScrollDistanceSinceBoundary -= distanceY;
			} else if (listIsAtBottom() && mDistanceTraveled > 0) {
				mScrollDistanceSinceBoundary += distanceY;
				if (mDistanceTraveled > 0 && mEdgeGlowBottom != null) {
					mEdgeGlowBottom.onPull((float) -distanceY / mList.getWidth());
					if (mEdgeGlowTop != null && !mEdgeGlowTop.isFinished()) {
						mEdgeGlowTop.onRelease();
					}
				}
				mList.invalidate();
			} else if (listIsAtBottom() && mDistanceTraveled < 0 && mScrollDistanceSinceBoundary > 0) {
				mScrollDistanceSinceBoundary += distanceY;
			} else if (mScrollDistanceSinceBoundary != 0) {
				mInterruptFade = false;
				mScrollDistanceSinceBoundary = 0;
			}
			return false;
		}

		private boolean listIsAtTop() {
			if (mList != null) {
				View view = mList.getChildAt(0);
				if (view == null) {
					return false;
				} else {
					return view.getTop() - mPaddingRectangle.top == 0;
				}
			} else {
				return false;
			}
		}

		private boolean listIsAtBottom() {
			if (mList != null) {
				View view = mList.getChildAt(mList.getChildCount() - 1);
				if (view == null) {
					return false;
				} else {
					if (mList instanceof ScrollView) {
						int lastY = mList.getScrollY();
						if (lastY == view.getHeight() - mList.getHeight()) {
							return true;
						}
						return false;
					} else {
						return view.getBottom() + mPaddingRectangle.bottom == mList.getHeight();
					}
				}
			} else {
				return false;
			}
		}
	}

	public void draw(Canvas canvas) {
		if (mEdgeGlowBottom != null && mEdgeGlowTop != null && mList != null) {
			if (!mEdgeGlowTop.isFinished()) {
				int restoreCount = canvas.save();
				int width = mList.getWidth();

				canvas.translate(-width / 2, Math.min(0, 3));
				mEdgeGlowTop.setSize(width * 2, mList.getHeight());

				if (mEdgeGlowTop.draw(canvas)) {
					mList.invalidate();
				}
				canvas.restoreToCount(restoreCount);
			}
			if (!mEdgeGlowBottom.isFinished()) {
				int restoreCount = canvas.save();
				int width = mList.getWidth();
				int height = mList.getHeight();
				if (mList instanceof ScrollView) {
					View v = mList.getChildAt(0);
					if (v != null) {
						height = v.getHeight();
					}
				}
				canvas.translate(-width / 2, Math.max(height, 10));
				canvas.rotate(180, width, 0);
				mEdgeGlowBottom.setSize(width * 2, height);
				if (mEdgeGlowBottom.draw(canvas)) {
					mList.invalidate();
				}
				canvas.restoreToCount(restoreCount);
			}
		}
	}

	public void destroy() {
		mListViewGestureDetector = null;
		mEdgeGlowTop = null;
		mEdgeGlowBottom = null;
		mList = null;

	}

}
