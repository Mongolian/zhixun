package com.iory.zhixun.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import com.iory.zhixun.ani.Rotate3dAnimation;
import com.iory.zhixun.app.DLApp;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class Tools {

	private static final String TAG = Tools.class.getName();

	private Tools() {

	}

	public static void showFolder(File file) {
		if (file.isFile()) {
		//	TLog.s("Test", file.getName() + "Size:" + file.length());
		} else {
		//	TLog.s("Test", file.getName() + "Dir");
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					showFolder(files[i]);
				}
			}
		}
	}

	/**
	 * 获取文字高度
	 * 
	 * @param fontSize
	 * @return
	 */
	public static int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.top) + 2;
	}

	/**
	 * 振动
	 * 
	 * @param context
	 */
	public static void vibrate(long vibrateTime) {
		Vibrator vibrator = (Vibrator) DLApp.getContext().getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(vibrateTime); // 震动200s
	}

	/**
	 * 从90翻转到平面度
	 * 
	 * @param aniView
	 * @param animationListener
	 * @return
	 */
	public static Animation getTurninAnimation(View aniView, AnimationListener animationListener, int start, int end, boolean revert) {
		final float centerX = (aniView.getWidth()) / 2.0f;
		final float centerY = (aniView.getHeight()) / 2.0f;
		Rotate3dAnimation ra = new Rotate3dAnimation(start, end, centerX, centerY, 310, revert);
		ra.setDuration(500);
		ra.setFillAfter(false);
		ra.setAnimationListener(animationListener);
		return ra;
	}

	/**
	 * 像素转DIP,
	 * 
	 * @param aDipValue
	 * @param context
	 * @return
	 */
	public static final int getPixFromDip(int aDipValue, final Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wMgr.getDefaultDisplay().getMetrics(dm);
		int pix = (int) (aDipValue * dm.density);
		return pix;
	}
	
	public static final float getPixFromDip(float aDipValue, final Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wMgr.getDefaultDisplay().getMetrics(dm);
		float pix = (float) (aDipValue * dm.density);
		return pix;
	}

	/**
	 * 根据像素的值获得DIP
	 * 
	 * @return
	 */
	public static final int getDipFromPix(int aPixValue, final Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wMgr.getDefaultDisplay().getMetrics(dm);
		return (int) (aPixValue / dm.density);
	}

	// public static boolean testNetworkIsAvailable() {
	// // 模拟器一直有网络
	// String imei = TContext.getImei();
	// if (imei == null || imei.length() == 0 || "000000000000000".equals(imei)) {
	// return true;
	// }
	// ConnectivityManager cm = (ConnectivityManager) DLApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	// NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
	// int netState = MainLogicController.getInstance().getNetworkStateFlag();
	// if (activeNetInfo != null && netState != NetworkMonitor.CONNECT_TYPE_NO_NETWORK) {
	// return true;
	// } else {
	// return false;
	// }
	// }

	// public static boolean isAutoBrightness(ContentResolver aContentResolver)
	// {
	//
	// boolean automicBrightness = false;
	// try {
	// automicBrightness = Settings.System.getInt(aContentResolver,
	// Settings.System.SCREEN_BRIGHTNESS_MODE) ==
	// Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
	// } catch (SettingNotFoundException e) {
	// e.printStackTrace();
	// }
	// return automicBrightness;
	// }

	/** * 设置亮度 */

	public static void setBrightness(Activity activity, int brightness) {

		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();

		lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);

		activity.getWindow().setAttributes(lp);
	}

	public static class ImgTool {

		/**
		 * 在图片上画边线，并有圆角效果
		 */
		public static Bitmap getSidelineBitmap(Bitmap bitmap, float r, int lineColor) {
			if (bitmap == null) {
				return bitmap;
			}
			Bitmap dstbmp = null;
			try {
				dstbmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			} catch (Throwable e) {
//				TLog.v(Tools.TAG, "out of memory");
				e.printStackTrace();
				return null;
			}
			Canvas canvas = new Canvas(dstbmp);

			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			paint.setAntiAlias(true);

			// 清除颜色
			canvas.drawARGB(0, 0, 0, 0);

			// 画笔颜色
			paint.setColor(lineColor);

			// 画罩子
			canvas.drawRoundRect(rectF, r, r, paint);

			// 设置Xfermode, 让图片区域在罩子内
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

			// 画图
			canvas.drawBitmap(bitmap, rect, rect, paint);

			paint.setXfermode(null);

			// 画空心圆角矩形
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(1);
			canvas.drawRoundRect(rectF, r, r, paint);
			return dstbmp;
		}

		/**
		 * 获取背景平铺的的drawable对象
		 * 
		 * @param res
		 * @param resId
		 * @return
		 */
		public static Drawable createDrawableReapter(Resources res, int resId) {
			try {
				Bitmap bitmap = BitmapFactory.decodeResource(res, resId);
				BitmapDrawable drawable = new BitmapDrawable(bitmap);
				drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
				drawable.setDither(true);
				return drawable;
			} catch (Throwable e) {
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * 画渐变背景
		 * 
		 * @param w
		 * @param h
		 * @param bgcolor
		 * @return
		 */
		public static Bitmap createGradBitmap(int w, int h) {

			try {
				Bitmap bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
				Canvas canvas = new Canvas();
				canvas.setBitmap(bitmap);

				// 画背景
				Paint p = new Paint();
				// p.setColor(bgcolor);
				// p.setAlpha(alpha);
				// canvas.drawRect(new Rect(0, 0, w, h), p);

				// p.reset();

				// 以下代码为渐变
				int[] gradientColors = new int[2];
				gradientColors[0] = 0xff5ea8dc;
				gradientColors[1] = 0xff9ad0f9;
				// gradientColors[2] = 0x00ffffff;

				float[] gradientPositions = new float[2];
				gradientPositions[0] = 0f;
				gradientPositions[1] = 1f;
				// gradientPositions[2] = 1f;

				LinearGradient radialGradientShader = new LinearGradient(0, 0, 0, h, gradientColors, gradientPositions, TileMode.CLAMP);

				p.setShader(radialGradientShader);
				canvas.drawRect(new Rect(0, 0, w, h), p);

				return bitmap;
			} catch (Throwable e) {
//				TLog.v(Tools.TAG, "out of memory");
				e.printStackTrace();
				return null;
			}
		}

		public static Bitmap getIconBitmap(Drawable drawable, Activity act) {
			Rect restorerect = drawable.getBounds();
			Rect rect = Utils.getIconBounds(act);
			Bitmap bitmap = null;
			try {
				bitmap = Bitmap.createBitmap(rect.width(), rect.height(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565);
			} catch (Throwable e) {
//				TLog.v(Tools.TAG, "out of memory");
				e.printStackTrace();
			}
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(rect);
			drawable.draw(canvas);
			drawable.setBounds(restorerect);
			return bitmap;
		}

		/*public static Bitmap getSidelineBitmap(String field, int[] args, int width, int height) {
			Bitmap bg = null;
			try {
				int transId = 0;
				int left = args[0];
				int top = args[1];
				// 可升级页面需要2个提示
				int transId2 = 0;
				int left2 = 0;
				int top2 = 0;
				if (args.length == 8) {
					left2 = args[4];
					top2 = args[5];
				}
				if (field.equals("isFirstHome")) {
					transId = R.drawable.newbee_guide_home_fling;
				} else if (field.equals("isFirstDownload")) {
					// transId = R.drawable.newbee_guide_manage_trans;
				} else if (field.equals("isFirstFavorite")) {
					// transId = R.drawable.newbee_guide_favorite_trans;
				} else if (field.equals("isFirstUpdatable")) {
					// transId = R.drawable.newbee_guide_ignore_trans;
					// transId2 = R.drawable.newbee_guide_newfeature_trans;
				} else if (field.equals("isFirstMorePageDownload")) {
					transId = R.drawable.newbee_guide_more_page_download_trans;
				} else if (field.equals("isFirstSoftwareDetail")) {
					// transId = R.drawable.newbee_guide_software_share_trans;
					// transId2 = R.drawable.newbee_guide_software_favorit_trans;
				}
				Rect frame = new Rect();
				if (TContext.mCurActivity != null) {
					TContext.mCurActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
				}
				int statusBarHeight = frame.top;
				height = height - statusBarHeight;
				bg = ImgTool.readBitMap(DLApp.getContext(), R.drawable.newbee_guide_bg);
				Matrix m = new Matrix();
				m.setScale(width / (float) bg.getWidth(), height / (float) bg.getHeight());

				bg = Bitmap.createBitmap(bg, 0, 0, bg.getWidth(), bg.getHeight(), m, false);

				BitmapDrawable d1 = (BitmapDrawable) DLApp.getContext().getResources().getDrawable(transId);
				Bitmap subBitmap = d1.getBitmap();
				Bitmap subBitmap2 = null;
				// Bitmap dstbmp = Bitmap.createBitmap(width, height,
				// Config.RGB_565);
				Canvas canvas = new Canvas(bg);
				final Paint paint = new Paint();
				// final Rect rect = new Rect(0, 0, width, height);
				// final RectF rectf = new RectF(rect);
				paint.setAntiAlias(true);

				// // 清除颜色
				// canvas.drawARGB(0, 0, 0, 0);
				// canvas.drawRoundRect(rectf, 0f, 0f, paint);
				// 设置Xfermode, 让图片区域在罩子内
				// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				// Matrix m = new Matrix();
				// m.setScale(width / (float) bg.getWidth(), height / (float)
				// bg.getHeight());
				// 画图
				// canvas.drawBitmap(bg, rect, rect, paint);
				// canvas.drawBitmap(bg, m, paint);
				paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
				// 画图
				// top = top - statusBarHeight;
				Rect dstRect = null;
				if (field.equals("isFirstHome")) {
					top = (int) ((height - subBitmap.getHeight())) / 2;
					left = (int) ((width - subBitmap.getWidth())) / 2;
					top = top + 10;
				} else if (field.equals("isFirstDownload")) {
					left = args[0];
				} else if (field.equals("isFirstUpdatable")) {
					if (transId2 != 0) {
						left2 = 0;
						BitmapDrawable d2 = (BitmapDrawable) DLApp.getContext().getResources().getDrawable(transId2);
						subBitmap2 = d2.getBitmap();
						top2 -= 10;
					}
					left = 0;
					top = top + args[3] - subBitmap.getHeight() + 10;
				} else if (field.equals("isFirstMorePageDownload")) {
					top += 10;
				} else if (field.equals("isFirstSoftwareDetail")) {
					top = top + args[3] - subBitmap.getHeight();
					BitmapDrawable d2 = (BitmapDrawable) DLApp.getContext().getResources().getDrawable(transId2);
					subBitmap2 = d2.getBitmap();
					top2 = top2 + args[7] - subBitmap2.getHeight();
					left2 = left2 + args[6] - subBitmap2.getWidth();
					left -= 10;
					top -= 15;
					top2 -= 25;
					left2 += 10;
				}
				top = top - 10;
				if (field.equals("isFirstUpdatable")) {
					dstRect = new Rect(left, top, width, subBitmap.getHeight() + top);
				} else {
					dstRect = new Rect(left, top, subBitmap.getWidth() + left, subBitmap.getHeight() + top);

				}
				canvas.drawRect(dstRect, paint);
				// paint.setXfermode(new PorterDuffXfermode(Mode.SCREEN));
				// canvas.drawBitmap(subBitmap, new Rect(0, 0,
				// subBitmap.getWidth(),
				// subBitmap.getHeight()),
				// dstRect, paint);
				BitmapDrawable draw = new BitmapDrawable(subBitmap);
				draw.setBounds(dstRect);
				draw.draw(canvas);
				if (args.length == 8) {
					canvas.save();

					Rect dstRect2 = null;
					if (field.equals("isFirstSoftwareDetail")) {
						dstRect2 = new Rect(left2, top2, subBitmap2.getWidth() + left2, subBitmap2.getHeight() + top2);
					} else if (field.equals("isFirstUpdatable")) {
						dstRect2 = new Rect(left2, top2, width, subBitmap2.getHeight() + top2);
					}
					canvas.drawRect(dstRect2, paint);
					BitmapDrawable draw2 = new BitmapDrawable(subBitmap2);
					draw2.setBounds(dstRect2);
					draw2.draw(canvas);
					canvas.restore();
				}
			} catch (Throwable e) {
//				TLog.v(Tools.TAG, "getSidelineBitmap out of memory");
				e.printStackTrace();
			}
			return bg;
		}*/

		public static Drawable generateBg(int width, Bitmap src, Bitmap toDraw) {
			Bitmap bitmap = ImgTool.createRepeater(width, src);
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(toDraw, bitmap.getWidth() - toDraw.getWidth(), 0, null);
			return new BitmapDrawable(bitmap);
		}

		/**
		 * 图片圆角处理
		 */
		public static Bitmap getRoundCornedBitmap(Bitmap bitmap, float roundPX) {
			if (bitmap == null) {
				return null;
			}
			Bitmap dstbmp = null;
			try {
				dstbmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			
				Canvas canvas = new Canvas(dstbmp);

				final int color = 0xff424242;
				final Paint paint = new Paint();
				final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
				final RectF rectF = new RectF(rect);
				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(color);
				canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				canvas.drawBitmap(bitmap, rect, rect, paint);
			} catch (Throwable e) {
				e.printStackTrace();
//				TLog.v(Tools.TAG, "out of memory");
				e.printStackTrace();
				return null;
			}
			
			return dstbmp;
		}

		/**
		 * 最小内存加载图片
		 * 
		 * @param context
		 * @param resId
		 * @return
		 */
		public static Bitmap readBitMap(Context context, int resId) {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
//			TLog.v(Tools.TAG, "density:" + opt.inDensity + "targetDensity:" + opt.inTargetDensity);
			// 获取资源图片
			InputStream is = context.getResources().openRawResource(resId);
			return BitmapFactory.decodeStream(is, null, opt);
		}

		public static Bitmap readBitMap(Context context, int resId, int scaleWidth, int scaleHeight) {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.ARGB_4444;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			opt.inJustDecodeBounds = false;
			opt.outWidth = scaleWidth;
			opt.outHeight = scaleHeight;
			// 获取资源图片
			InputStream is = context.getResources().openRawResource(resId);
			return BitmapFactory.decodeStream(is, null, opt);
		}

		/**
		 * 处理图标及其后面的阴影
		 * 
		 * @param icon
		 * @param iconSize
		 * @return
		 */
		public static Bitmap createIconBitmap(Bitmap icon, int iconSize) {
			try {
				Bitmap frame = Bitmap.createBitmap(iconSize + 2, iconSize + 3, Config.ARGB_8888);
				Canvas canvas = new Canvas();

				canvas.setBitmap(frame);
				Paint p = new Paint();
				p.reset();
				p.setColor(0xff000000);
				p.setAlpha(30);
				p.setAntiAlias(true);
				// canvas.drawRoundRect(new RectF(0,0,iconSize+2,iconSize+3),
				// AdStyle.ICON_SHADE_CORNER, AdStyle.ICON_SHADE_CORNER, p);

				p.reset();
				p.setColor(0xff000000);
				p.setAlpha(20);
				p.setAntiAlias(true);
				// canvas.drawRoundRect(new RectF(1,1,iconSize,iconSize+1),
				// AdStyle.ICON_ROUND_CORNER, AdStyle.ICON_ROUND_CORNER, p);

				p.reset();

				// Bitmap iconR =
				// getRoundCornedBitmap(icon,AdStyle.ICON_ROUND_CORNER);
				// p.setAlpha(200);
				// canvas.drawBitmap(iconR, new
				// Rect(0,0,icon.getWidth(),icon.getHeight()), new
				// Rect(1,1,iconSize,iconSize),null);
				// iconR.recycle();
				return frame;
			} catch (Throwable e) {
//				TLog.v(Tools.TAG, "out of memory");
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * 画渐变背景
		 * 
		 * @param w
		 * @param h
		 * @param bgcolor
		 * @return
		 */
		public static Bitmap createGradBitmap(int w, int h, int bgcolor, int alpha) {
			Bitmap bitmap = null;
			try {
				bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			} catch (Throwable e) {
//				TLog.v(Tools.TAG, "out of memory");
				e.printStackTrace();
				return null;
			}
			Canvas canvas = new Canvas();
			canvas.setBitmap(bitmap);

			// 画背景
			Paint p = new Paint();
			p.setColor(bgcolor);
			p.setAlpha(alpha);
			canvas.drawRect(new Rect(0, 0, w, h), p);

			p.reset();

			// 以下代码为渐变
			int[] gradientColors = new int[3];
			gradientColors[0] = 0x55ffffff;
			gradientColors[1] = 0x33ffffff;
			gradientColors[2] = 0x00ffffff;

			float[] gradientPositions = new float[3];
			gradientPositions[0] = 0f;
			gradientPositions[1] = 0.3f;
			gradientPositions[2] = 1f;

			LinearGradient radialGradientShader = new LinearGradient(0, 0, 0, h, gradientColors, gradientPositions, TileMode.CLAMP);

			p.setShader(radialGradientShader);
			canvas.drawRect(new Rect(0, 0, w, h), p);

			return bitmap;

		}

		/**
		 * 平铺图片
		 * 
		 * @param width
		 * @param src
		 * @return
		 */
		public static Bitmap createRepeater(int width, Bitmap src) {

			int count = (width + src.getWidth() - 1) / src.getWidth();
			Bitmap bitmap = null;
			try {
				bitmap = Bitmap.createBitmap(width, src.getHeight(), Config.ARGB_8888);
			} catch (Throwable e) {
//				TLog.v(Tools.TAG, "out of memory");
				e.printStackTrace();
			}
			Canvas canvas = new Canvas(bitmap);
			for (int idx = 0; idx < count; ++idx) {
				canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
			}
			return bitmap;

		}

		public static Bitmap drawableToBitmap(Drawable drawable) {
			if (drawable instanceof BitmapDrawable) {
				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				return bitmap;
			}

			Rect restorerect = drawable.getBounds();
			Rect rect = new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			Bitmap bitmap = null;
			try {
				bitmap = Bitmap.createBitmap(rect.width(), rect.height(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565);
			} catch (Throwable e) {
//				TLog.v(Tools.TAG, "out of memory");
				e.printStackTrace();
			}
			Canvas canvas = new Canvas(bitmap);
			// canvas.setBitmap(bitmap);
			drawable.setBounds(rect);
			drawable.draw(canvas);
			drawable.setBounds(restorerect);
			return bitmap;
		}

		public static BitmapDrawable bitmap2BitmapDrawable(Bitmap bitmap) {
			if (bitmap == null) {
				return null;
			}
			return new BitmapDrawable(bitmap);
		}

		public static Bitmap bitmapDrawable2Bitmap(BitmapDrawable drawable) {
			if (drawable == null) {
				return null;
			}
			return drawable.getBitmap();
		}

		public static BitmapDrawable toBitmapDrawable(byte[] data) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			if (bitmap != null) {
				return new BitmapDrawable(bitmap);
			}
			return null;
		}

		public static byte[] toBytes(BitmapDrawable bitmapDrawable) {
			ByteArrayOutputStream baops = new ByteArrayOutputStream();
			Bitmap bitmap = bitmapDrawable.getBitmap();
			if (bitmap != null) {
				bitmap.compress(CompressFormat.PNG, 0, baops);
				return baops.toByteArray();
			}
			return null;
		}
	}

	public static class BaseTool {

		private static final DecimalFormat mDf = new DecimalFormat("##.##");
		private static final DecimalFormat mDf2 = new DecimalFormat("##.00");
		private static final String[] UNITS = new String[] { "GB", "MB", "KB", "B" };
		private static final long[] DIVIDERS = new long[] { 1024 * 1024 * 1024, 1024 * 1024, 1024, 1 };

		/**
		 * 从网络字节流中读取4个字节拼装int
		 * 
		 * @param data
		 * @param index
		 * @return
		 */
		public static int byte2Int(byte[] data, int index) {
			return (data[index] & 0xff) << 24 | (data[index + 1] & 0xff) << 16 | (data[index + 2] & 0xff) << 8 | (data[index + 3] & 0xff);
		}

		public static String bytesToHexString(byte[] src) {
			StringBuilder stringBuilder = new StringBuilder("");
			if (src == null || src.length <= 0) {
				return null;
			}
			for (int i = 0; i < src.length; i++) {
				int v = src[i] & 0xFF;
				String hv = Integer.toHexString(v);
				if (hv.length() < 2) {
					stringBuilder.append(0);
				}
				stringBuilder.append(hv);
			}
			return stringBuilder.toString();
		}

		/**
		 * 小数点最后面出现0，不显示
		 * 
		 * @param value
		 * @return
		 */
		public static String byteToString(final long value) {
			if (value < 1)
				return "0B";
			String result = null;
			for (int i = 0; i < DIVIDERS.length; i++) {
				final long divider = DIVIDERS[i];
				if (value >= divider) {
					result = format(value, divider, UNITS[i]);
					break;
				}
			}
			return result;
		}

		/**
		 * 小数点最后面出现0，显示
		 * 
		 * @param value
		 * @return
		 */
		public static String byteToString2(final long value) {
			if (value < 1)
				return "0B";
			String result = null;
			for (int i = 0; i < DIVIDERS.length; i++) {
				final long divider = DIVIDERS[i];
				if (value >= divider) {
					result = format2(value, divider, UNITS[i]);
					break;
				}
			}
			return result;
		}

		/**
		 * 解密
		 * 
		 * @param psw
		 * @return
		 */
		/*public static String decodePSW(byte[] psw) {
			if (psw == null || psw.length <= 0)
				return "";

			String pwst = "";
			psw = new com.tencent.qphone.base.util.Cryptor().decrypt(psw, TContext.data);
			try {
				pwst = new String(psw, "ISO8859_1");
			} catch (UnsupportedEncodingException e) {
				pwst = new String(psw);
			} catch (Exception e) {
				pwst = "";
			}
			return pwst;
		}

		*//**
		 * 加密
		 * 
		 * @param psw
		 * @return
		 *//*
		public static byte[] encodePSW(String psw) {
			byte[] pwbt = null;
			try {
				pwbt = psw.getBytes("ISO8859_1");
			} catch (UnsupportedEncodingException e) {
				pwbt = psw.getBytes();
			}
			return new com.tencent.qphone.base.util.Cryptor().encrypt(pwbt, TContext.data);
		}*/

		private static String format(final long value, final long divider, final String unit) {
			final double result = divider > 1 ? (double) value / (double) divider : (double) value;
			return mDf.format(result) + " " + unit;
		}

		private static String format2(final long value, final long divider, final String unit) {
			final double result = divider > 1 ? (double) value / (double) divider : (double) value;
			return mDf2.format(result) + " " + unit;
		}

		/**
		 * 对传入进来的byte数组进行gzip压缩，并输出压缩后的byte数组
		 * 
		 * @param bytes
		 * @return
		 */
		public static byte[] compressBytes(byte[] bytes) throws Exception {
			byte[] rs = bytes;
			// int olength=bytes.length;
			// long st=System.currentTimeMillis();
			// 建立字节数组输出流
			ByteArrayOutputStream o = null;

			// 建立gzip压缩输出流
			GZIPOutputStream gzout = null;
			try {

				o = new ByteArrayOutputStream();
				gzout = new GZIPOutputStream(o);
				gzout.write(bytes, 0, bytes.length);
			} catch (Exception ex) {
				throw ex;
			} finally {
				if (gzout != null) {
					try {
						gzout.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (o != null) {
					rs = o.toByteArray();
					try {
						o.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			// System.out.println("before compress length:"+olength+",after compress length:"+rs.length+",cost time:"+(System.currentTimeMillis()-st)+"ms");
			return rs;
		}

		/**
		 * 对传入进来的byte数组进行gzip解压缩，并输出解压缩后的byte数组
		 * 
		 * @param bytes
		 * @return
		 */
		public static byte[] unCompressBytes(final byte[] bytes) throws Exception {
			byte[] readBuffer = null;
			byte[] result = null;
			ByteArrayOutputStream baos = null;
			ByteArrayInputStream inputStream = null;
			GZIPInputStream gzipInputStream = null;
			try {
				inputStream = new ByteArrayInputStream(bytes);
				gzipInputStream = new GZIPInputStream(inputStream);
				readBuffer = new byte[4096]; // 每次读取4K
				baos = new ByteArrayOutputStream(bytes.length * 2);
				int len = 0;
				while ((len = gzipInputStream.read(readBuffer)) != -1) {
					baos.write(readBuffer, 0, len);
				}
				result = baos.toByteArray();
				baos.close();
				baos = null;
			} catch (Exception ex) {
				throw ex;
			} finally {
				if (baos != null) {
					baos.close();
				}
				if (gzipInputStream != null) {
					try {
						gzipInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return result;
		}

		// public static String getSubUrl(String url) {
		// String sub = "";
		// if (url != null && url.length() > 0) {
		// int index = url.indexOf("/", "http://".length());
		// if (index > -1) {
		// sub = url.substring(index, url.length());
		// TLog.v(TAG, "getSubUrl--key:" + sub);
		// }
		// }
		// return sub;
		// }
	}

	public static class TimeTool {

		private static final long timeMin = 60;
		private static final long timeHalfHour = 60 * 30;
		private static final long timeHour = 60 * 60;
		private static final long timeDay = 60 * 60 * 24;
		private static final long time2Day = 60 * 60 * 24 * 2;
		private static final SimpleDateFormat FormatterHM = new SimpleDateFormat("HH:mm");
		private static final SimpleDateFormat FormatterMDHM = new SimpleDateFormat("M月d日 HH:mm");
		private static final SimpleDateFormat FormatterMD = new SimpleDateFormat("M月d日");
		private static final SimpleDateFormat FormatterYMD = new SimpleDateFormat("yyyy年M月d日");
		private static final SimpleDateFormat FormatterYMDHM = new SimpleDateFormat("yyyy年M月d日 HH:mm");
		private static final SimpleDateFormat FormatterYYMMDD = new SimpleDateFormat("yyyy年MM月dd日");

		private static Date mLocalDate = new Date();
		private static Date mDownloadDate = new Date();

		public static boolean compareTimeSameMonth(long time, long toCompareTime) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(time);
			Calendar toCompare = Calendar.getInstance();
			toCompare.setTimeInMillis(toCompareTime);
			return c.get(Calendar.MONTH) == toCompare.get(Calendar.MONTH);
		}

		public static String formatData(long Millis) {
			java.util.Date date = new java.util.Date(Millis);
			return FormatterYYMMDD.format(date);
		}

		// 1分钟内： 刚刚
		// 半小时内： XX分钟前
		// 今天： 今天 **：**（时：分）
		// 昨天： 昨天**：**（时：分）
		// 前天： 前天**：**（时：分）
		// 早于前天： mm-dd **：**（时：分）
		// 早于今年： yyyy-mm-dd **：**（时：分）
		public static String getDisplayTime(long displayTime, boolean isShowHM,boolean isShowCN) {
			String showTime = "";
			long localTime = System.currentTimeMillis();
			mLocalDate.setTime(localTime);
			mDownloadDate.setTime(displayTime);
			if(!isShowCN){
				if (mLocalDate.getYear() == mDownloadDate.getYear()) {
					if (isShowHM) {
						showTime = FormatterMDHM.format(mDownloadDate);
					} else {
						showTime = FormatterMD.format(mDownloadDate);
					}
				} else {
					if (isShowHM) {
						showTime = FormatterYMDHM.format(mDownloadDate);
					} else {
						showTime = FormatterYMD.format(mDownloadDate);
					}
				}
			} else {
				long time = (localTime - mDownloadDate.getTime()) / 1000;
				if (time < (-5) * timeMin) {
					showTime = FormatterYMDHM.format(mDownloadDate);
				} else {
					if (time < timeMin) {
						showTime = "刚刚";
					} else if (time < timeHalfHour) {
						showTime = (time / 60) + "分钟前";
					} else {
						int h = mLocalDate.getHours();
						int m = mLocalDate.getMinutes();
						int s = mLocalDate.getSeconds();
						long todayPassTime = h * timeHour + m * timeMin + s;
						if (time < todayPassTime) {
							if (isShowHM) {
								showTime = "今天" + FormatterHM.format(mDownloadDate);
							} else {
								showTime = "今天";
							}
						} else if (time < timeDay + todayPassTime) {
							if (isShowHM) {
								showTime = "昨天" + FormatterHM.format(mDownloadDate);
							} else {
								showTime = "昨天";
							}
						} else if (time < time2Day + todayPassTime) {
							if (isShowHM) {
								showTime = "前天" + FormatterHM.format(mDownloadDate);
							} else {
								showTime = "前天";
							}
						} else {
							if (mLocalDate.getYear() == mDownloadDate.getYear()) {
								if (isShowHM) {
									showTime = FormatterMDHM.format(mDownloadDate);
								} else {
									showTime = FormatterMD.format(mDownloadDate);
								}

							} else {
								if (isShowHM) {
									showTime = FormatterYMDHM.format(mDownloadDate);
								} else {
									showTime = FormatterYMD.format(mDownloadDate);
								}

							}
						}
					}
				}
			}

			// showTime.replaceAll("-0", "-");
			// showTime.replaceAll(" 0", " ");

			return showTime;
		}

		public static String getTimeSortStr(long sortTime) {
			String showTime = "";
			long localTime = System.currentTimeMillis();
			Calendar c = Calendar.getInstance();
			mLocalDate.setTime(localTime);
			mDownloadDate.setTime(sortTime);
			long time = (localTime - mDownloadDate.getTime()) / 1000;
			int h = mLocalDate.getHours();
			int m = mLocalDate.getMinutes();
			int s = mLocalDate.getSeconds();
			int day = c.get(Calendar.DAY_OF_WEEK);
			if (day == Calendar.SUNDAY) {
				day = 7;
			} else {
				day = day - 1;
			}
			long todayPassTime = h * timeHour + m * timeMin + s;
			if (time < todayPassTime) {
				showTime = "今天";
			} else if (time < 6 * timeDay + todayPassTime) {
				showTime = "一周内";
			} else {
				showTime = "早些时候";
			}

			return showTime;
		}

		/**
		 * 解析字符串,格式是2011-06-01 12:14:23
		 */
		public static long parseDate(final String dateString) {
			try {
				String[] dandt = dateString.split(" ");
				String[] dateS = dandt[0].split("-");
				Calendar cal = Calendar.getInstance();
				if (dandt.length == 1) {
					cal.set(Integer.parseInt(dateS[0]), Integer.parseInt(dateS[1]) - 1, Integer.parseInt(dateS[2])/*-1*/);
				} else {
					String[] timeS = dandt[1].split(":");
					cal.set(Integer.parseInt(dateS[0]), Integer.parseInt(dateS[1]) - 1, Integer.parseInt(dateS[2])/*-1*/, Integer.parseInt(timeS[0]),
							Integer.parseInt(timeS[1]), Integer.parseInt(timeS[2]));
				}
				// long t = cal.getTimeInMillis();
				// Date d = new Date();
				// d.setTime(t);
				// String s = d.toString();
				// Log.v("ttt", "");
				return cal.getTimeInMillis();
			} catch (Exception e) {
				return 0;
			}

		}

	}

	/**
	 * 获取手机联网的IP地址
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			// TLog.e(TAG, ex.toString());
		}
		return "";
	}

	/**
	 * 判断自身是否系统程序（即内置于ROM的程序）
	 * 
	 * @param context Android Application Context
	 * @return true: 系统程序 false: 第三方程序
	 */
	public static boolean isSystemApp(Context context) {
		PackageManager packageManager = context.getPackageManager();
		try {
			ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
			if (applicationInfo != null) {
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					return true;
				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
