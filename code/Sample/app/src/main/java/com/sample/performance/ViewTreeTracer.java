package com.sample.performance;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.view.ViewTreeObserver.OnGlobalLayoutListener;

/**
 * 布局深度检测，会打出页面最深布局层数，如果路径超过TOO_DEEP(21)会打出完整的路径.
 */
public class ViewTreeTracer implements Application.ActivityLifecycleCallbacks
{

	public static float getScreenDensity(Context context)
	{
		return context.getResources().getDisplayMetrics().density;
	}

	public static int dip2px(Context context, float dpValue)
	{
		final float scale = getScreenDensity(context);
		return (int) (dpValue * scale + 0.5f);
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	public static final int TOO_DEEP = 20;

	private static final int UPDATE_DELAY_MILLIS = 1000;

	public static Set<List<View>> calMaxPath (View view)
	{

		if (view instanceof ViewGroup && ((ViewGroup) view).getChildCount() > 0)
		{
			ViewGroup viewGroup = (ViewGroup) view;

			Set<List<View>> maxSet = new HashSet<List<View>>();

			int subMax = 0;

			for (int i = 0; i < viewGroup.getChildCount(); i++)
			{
				View child = viewGroup.getChildAt(i);

				Set<List<View>> childMax = calMaxPath(child);

				List<View> max = childMax.iterator().next();

				if (max.size() > subMax)
				{
					maxSet.clear();
					maxSet.addAll(childMax);
					subMax = max.size();
				}
				else if (max.size() == subMax)
				{
					maxSet.addAll(childMax);
				}
			}

			for (List<View> path : maxSet)
			{
				path.add(0, view);
			}

			return maxSet;
		}
		else
		{
			Set<List<View>> maxSet = new HashSet<List<View>>();
			List<View> list = new ArrayList<View>();
			list.add(view);
			maxSet.add(list);
			return maxSet;
		}

	}

	private Map<Activity, OnGlobalLayoutListener> viewTreeObserverMap;

	public ViewTreeTracer()
	{
		viewTreeObserverMap = new HashMap<Activity, OnGlobalLayoutListener>();
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed (Activity activity)
	{
		//不要放到onCreate里哦，会引起调requestWindowFeature的activity crash
		//requestFeature() must be called before adding content
		if (viewTreeObserverMap.get(activity) == null)
		{
			OnGlobalLayoutListener viewTreeObserver = new DepthObserver(activity);
			View decorView = activity.getWindow().getDecorView();
			decorView.getViewTreeObserver().addOnGlobalLayoutListener(viewTreeObserver);
			viewTreeObserverMap.put(activity, viewTreeObserver);
		}
	}

	@Override
	public void onActivityPaused(Activity activity) {

	}

	@Override
	public void onActivityStopped(Activity activity) {

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onActivityDestroyed (Activity activity)
	{
		OnGlobalLayoutListener listener = viewTreeObserverMap.remove(activity);

		if (listener != null)
		{
			View decorView = activity.getWindow().getDecorView();
			decorView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
		}
	}

	public static class DepthObserver implements OnGlobalLayoutListener
	{
		private static Handler mainLoopHandler = new Handler(Looper.getMainLooper());

		private Activity activity;

		private List<List<View>> lastTooDeepPaths;

		public DepthObserver (Activity activity)
		{
			this.activity = activity;
		}

		@Override
		public void onGlobalLayout ()
		{
			mainLoopHandler.removeCallbacks(depthCalTask);
			mainLoopHandler.postDelayed(depthCalTask, UPDATE_DELAY_MILLIS);
		}

		private Runnable depthCalTask = new Runnable()
		{
			@Override
			public void run ()
			{
				if (!activity.isFinishing())
				{
					updateViewDepth();
				}
			}
		};

		private void updateViewDepth ()
		{
			View root = activity.getWindow().getDecorView();

			Set<List<View>> maxPath = calMaxPath(root);

			printViewStack(maxPath);
		}

		private void printViewStack (Collection<List<View>> paths)
		{
			List<List<View>> tooDeepPaths = new ArrayList<List<View>>();

			for (List<View> path : paths)
			{
				if (path.size() > TOO_DEEP)
				{
					tooDeepPaths.add(path);
				}
			}

			if (tooDeepPaths.isEmpty() || tooDeepPaths.equals(lastTooDeepPaths))
				return;

			lastTooDeepPaths = tooDeepPaths;

			markDeepestView(tooDeepPaths);

			printTooDeepPaths(tooDeepPaths);
		}

		private void printTooDeepPaths (List<List<View>> tooDeepPaths)
		{
			String pathsString = pathsString(tooDeepPaths);

			new TooDeepViewStackException(
					activity.getClass().getSimpleName()
							+ String.format("%nDepth Limit(%d):%n", TOO_DEEP)
							+ pathsString
			).printStackTrace();
		}

		private void markDeepestView (List<List<View>> tooDeepPaths)
		{
			for (List<View> tooDeepPath : tooDeepPaths)
			{
				View view = tooDeepPath.get(tooDeepPath.size() - 1);
				int padding = dip2px(activity, 5);

				if (view.getPaddingLeft() < padding
						|| view.getPaddingRight() < padding
						|| view.getPaddingTop() < padding
						|| view.getPaddingBottom() < padding)
				{
					view.setPadding(padding, padding, padding, padding);
				}
				view.setBackgroundColor(Color.parseColor("#ffcc0000"));

				if (view.getVisibility() != View.VISIBLE)
				{
					view.setVisibility(View.VISIBLE);
				}
			}
		}

		private String pathsString (List<List<View>> tooDeepPaths)
		{
			StringBuilder sb = new StringBuilder();

			for (List<View> tooDeepPath : tooDeepPaths)
			{
				for (View view : tooDeepPath)
				{
					sb.append(view.getClass().getSimpleName());
					sb.append('-');
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append(String.format(" (%d)", tooDeepPath.size())).append('\n');
			}
			return sb.toString();
		}

	}

}
