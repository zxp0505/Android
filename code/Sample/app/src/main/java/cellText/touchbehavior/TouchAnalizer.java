package cellText.touchbehavior;



import android.view.MotionEvent;


public class TouchAnalizer {
	
	public static class BehaviorType {
		public static final int SINGLE_CLICK = 0; // 必须按照顺序
		public static final int DOUBLE_CLICK = 1;
		public static final int DRAG = 2;
		public static final int SINGLE_DRAG = 3; // for API level 3-
		public static final int SLASH = 4;
		public static final int MULTI_SLASH = 5;
		public static final int LONG_CLICK = 6;
		public static final int ROTATE = 7;
		
		public static final int MAX_TYPE_COUNT = 8; // 记得修改最大值
	};

//	public static enum BehaviorType {
//		SINGLE_CLICK,
//		DOUBLE_CLICK,
//		DRAG,
//		SINGLE_DRAG,	// for API level 3-
//		SLASH,
//		MULTI_SLASH,
//		LONG_CLICK,
//		//TRIPLE_CLICK,
//		PINCH,
//		ROTATE,
//	};
	
	public static int CLICK_AREA = 400;
	public static int CLICK_AREA_SEC = 900;
	
	private TouchBehaviorListener[] listeners = new TouchBehaviorListener[BehaviorType.MAX_TYPE_COUNT];
	private TouchBehavior[] analizers = new TouchBehavior[BehaviorType.MAX_TYPE_COUNT];
	
	
	public void setListener(int behavior, TouchBehaviorListener l) {
		setListener(behavior, l, null);
	}
	public void setListener(int behaviorType, TouchBehaviorListener l, TouchBehavior.TouchBehaviorEventJudger judger) {
		int seq = behaviorType;
		listeners[seq] = l;
		if (l == null) {
			analizers[seq] = null;
		} else {
			if (analizers[seq] == null) {
				analizers[seq] = createTouchBehavior(behaviorType);
			}
		}
		if (analizers[seq] != null && judger != null) {
			analizers[seq].setJudger(judger);
		}
	}
	
	private TouchBehavior createTouchBehavior(int behavior) {
		switch (behavior) {
		case BehaviorType.SINGLE_CLICK:
			return new SingleClickBehavior(this);
//		case DOUBLE_CLICK:
//			return new DoubleClickBehavior(this);
//		case DRAG:
//			return new DragBehavior(this);
//		case SINGLE_DRAG:
//			return new DragBehaviorSinglePoint(this);
//		case SLASH:
//			return new SlashBehavior(this);
//		case MULTI_SLASH:
//			return new MultiSlashBehavior(this);
//		case LONG_CLICK:
//			return new LongClickBehavior(this);
//		case PINCH:
//			return new PinchBehavior(this);
//		case ROTATE:
//			return new RotateBehavior(this);
		}
		return null;
	}

	public boolean inputTouchEvent(MotionEvent event) {
		boolean ret = false;
		for (TouchBehavior analizer : analizers) {
			if (analizer != null) {
				if (analizer.onTouchEvent(event)) {
					ret = true;
				}
			}
		}
		return ret;
	}
	
	public void pauseBehavior(int behaviorType) {
		if (analizers[behaviorType] != null) {
			analizers[behaviorType].pause();
		}
	}

	public boolean onBehavior(int behaviorType, float x, float y) {
		return onBehavior(behaviorType, x, y, -1);
	}
	
	public boolean onBehavior(int behaviorType, float x, float y, int state) {
		if (listeners[behaviorType] != null) {
			return listeners[behaviorType].onInvoke(behaviorType, x, y, state);
		} else {
			return false;
		}
	}
	
	
}
