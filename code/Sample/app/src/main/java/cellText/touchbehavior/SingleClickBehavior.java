package cellText.touchbehavior;

import android.view.MotionEvent;


public class SingleClickBehavior extends TouchBehavior {

	private  static final int CLICK_AREA = TouchAnalizer.CLICK_AREA;
	private float lastPosX = -1, lastPosY = -1;
	
	public SingleClickBehavior(TouchAnalizer manager) {
		super(manager);
		behaviorType = TouchAnalizer.BehaviorType.SINGLE_CLICK;
	}
	
	private boolean checkOutside(MotionEvent curr) {
		if (curr == null || lastPosX == -1 || lastPosY == -1) {
			return true;
		}
		float dx = lastPosX - curr.getX();
		float dy = lastPosY - curr.getY();
		boolean ret = dx * dx + dy * dy > CLICK_AREA;
		return ret;
	}
	
	@Override
	public int analizeTouchEvent(MotionEvent event) {
		int ret = RET_CONTINUE;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:	
			lastPosX = event.getX();
			lastPosY = event.getY();
			if (judger != null) {
				ret = judger.judgeEvent(behaviorType, lastPosX, lastPosY, 0);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (checkOutside(event)) {
				ret = RET_FAILED;
			} else {
				ret = RET_CONTINUE;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (checkOutside(event)) {
				ret = RET_FAILED;
			} else {
				ret = RET_MATCHED;
			}
			break;
		default:
			ret = RET_FAILED;
			break;
		}
		if (ret == RET_FAILED || ret == RET_MATCHED) {
			lastPosX = lastPosY = -1;
		}
		return ret;
	}

}
