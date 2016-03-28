package cellText;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

public class EmoObjectPool {
    
	private static EmoObjectPool mInstance;
	private static Object lock = new Object();
	private SparseArray<Drawable> defaultEmoList;
	
	private ConcurrentHashMap<String, SparseArray<SoftReference<EmoCell>>> mEmoPool;
	
	/**
     * EmoObjectPool 单件
     * @return
     */
    public static EmoObjectPool getInstance() {
        if (mInstance == null){
            synchronized (lock) {
                if (mInstance == null) {
                    mInstance = new EmoObjectPool();
                }
            }
        }
        return mInstance;
    }
    
    private EmoObjectPool() {
    	mEmoPool = new ConcurrentHashMap<String, SparseArray<SoftReference<EmoCell>>>();
    	defaultEmoList = new SparseArray<Drawable>();
    }
    
    public void refreshEmoDrawable(EmoCell cell, ReMeasureableLayout parent) {
//    	if (cell == null) {
//    		return;
//    	}
//
//    	int bound = (int)cell.getWidth(null);
//    	Drawable defaultDrawable = defaultEmoList.get(bound);
//    	if (defaultDrawable == null) {
//    		defaultDrawable = BaseApplication.getContext().getResources().getDrawable(R.drawable.qzone_icon_default_emoji);
//    		defaultDrawable.setBounds(0, 0, bound, bound);
//    		defaultEmoList.put(bound, defaultDrawable);
//    	}
//
//    	if (cell.emoDrawable == defaultDrawable) {
//    		loadEmoAsync(cell, cell.emoCode, bound, parent);
//    	}
    }
    

    private Handler mainHandler = new Handler(Looper.getMainLooper());
	
}
