package cellText;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.text.TextUtils;
import android.util.SparseArray;


//保存UI最常用的图标，不选择softreference为了一直能抓住资源，不被系统释放。
//所以这个cache不能放太多图片，避免占用资源！

public class UiElementFixedCache {
	private static final String TAG = UiElementFixedCache.class.getSimpleName();
	private static UiElementFixedCache mInstance = null;
	private static Object lock=new Object();

    private SparseArray<SoftReference<ArrayList<TextCell>>> mTextCells;
    private SoftReference<HashMap<MeasuredTextInfo, SoftReference<MeasuredTextResult>>> mMeasuredTextMapRef;
	private UiElementFixedCache() {
		mTextCells = new SparseArray<SoftReference<ArrayList<TextCell>>>();
	}

	public static UiElementFixedCache getInstance() {
	    if (mInstance == null){
            synchronized (lock) {
                if (mInstance == null) {
                    mInstance = new UiElementFixedCache();
                }
            }
        }
        return mInstance;
	}
	
	private HashMap<MeasuredTextInfo, SoftReference<MeasuredTextResult>> getHashMap() {
		HashMap<MeasuredTextInfo, SoftReference<MeasuredTextResult>> ref =
				mMeasuredTextMapRef != null ? mMeasuredTextMapRef.get() : null;
		if (ref == null) {
			ref = new HashMap<MeasuredTextInfo, SoftReference<MeasuredTextResult>>();
			mMeasuredTextMapRef = new SoftReference<HashMap<MeasuredTextInfo, SoftReference<MeasuredTextResult>>>(ref); 
		}
		return ref;
	}
	
	public void putMeasuredCells(int measuredWidth, int textSize, String text, MeasuredTextResult measuredTextResult) {
		MeasuredTextInfo measuredTextInfo = new MeasuredTextInfo(measuredWidth, textSize, text);
		HashMap<MeasuredTextInfo, SoftReference<MeasuredTextResult>> map = getHashMap();
		map.put(measuredTextInfo, new SoftReference<MeasuredTextResult>(measuredTextResult));
	}
	
	public MeasuredTextResult getMeasuredLines(int measuredWidth, int textSize, String text) {
		if (TextUtils.isEmpty(text)) {
			return null;
		}
		
		MeasuredTextInfo measuredTextInfo = new MeasuredTextInfo(measuredWidth, textSize, text);
		HashMap<MeasuredTextInfo, SoftReference<MeasuredTextResult>> map = getHashMap();
		SoftReference<MeasuredTextResult> measuredLineRef = map.get(measuredTextInfo);
		if (measuredLineRef != null) {
			MeasuredTextResult measuredTextResult = measuredLineRef.get();
			if (measuredTextResult != null) {
				return measuredTextResult.copy();
			} else {
				map.remove(measuredTextInfo);
			}
		}
		
		return null;
	}
	
	public void putTextCells(int hashcode, ArrayList<TextCell> cells){
        mTextCells.put(hashcode, new SoftReference<ArrayList<TextCell>>(cells));
    }
    
    public ArrayList<TextCell> getTextCells(int hashcode) {
        SoftReference<ArrayList<TextCell>> reference = mTextCells.get(hashcode);
        if (reference != null) {
            ArrayList<TextCell> cells = reference.get();
            if (cells == null) {
                mTextCells.remove(hashcode);
            }
            return cells;
        }
        return null;
    }
    
    static class MeasuredTextInfo {
    	public int measuredWidth;
    	public int textSize;
    	public String text;
    	
    	public MeasuredTextInfo(int width, int textSize, String text) {
    		this.measuredWidth = width;
    		this.textSize = textSize;
    		this.text = text;
    	}
    	
    	@Override
    	public int hashCode() {
    		return measuredWidth  + textSize + (text != null?text.hashCode():0);
    	}
    	
    	@Override
    	public boolean equals(Object obj) {
    		if (this == obj) {
    			return true;
    		}
    		
    		if (getClass() == obj.getClass()) {
    			MeasuredTextInfo o = (MeasuredTextInfo)obj;
    			return this.measuredWidth == o.measuredWidth
    					&& this.textSize == o.textSize
    					&& this.text.equals(o.text);
    		}
    		
    		return false;
    	}
    }
    
    public static class MeasuredTextResult {
    	public ArrayList<CellTextView.MeasuredLine> measuredLines;
    	public ArrayList<Integer> lineHeights;
    	public int measuredWidth;

    	@SuppressWarnings("unchecked")
		public MeasuredTextResult(ArrayList<CellTextView.MeasuredLine> measuredLines, ArrayList<Integer> lineHeights, int measuredWidth) {
    		this.measuredLines = (ArrayList<CellTextView.MeasuredLine>) measuredLines.clone();
    		this.lineHeights = (ArrayList<Integer>) lineHeights.clone();
    		this.measuredWidth = measuredWidth;
    	}
    	
    	public MeasuredTextResult copy() {
    		return new MeasuredTextResult(measuredLines, lineHeights, measuredWidth);
    	}
    }
}
