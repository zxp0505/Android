package cellText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Toast;

import com.sample.R;

import cellText.touchbehavior.TouchAnalizer;
import cellText.touchbehavior.TouchBehaviorListener;


import java.util.ArrayList;


public class CellTextView extends View implements TouchBehaviorListener, ReMeasureableLayout {

    private static final String TAG = "CellTextView";

    private boolean isFakeFeed = false;
	private int fontHeight;
	private int mw;
	private int linePos = 0;
	private int verPos = 0;
	private int lineSpace = 0;
	private int linebreakSeq = TextCell.FLAG_LINEBREAK_SEQ;
	
	private int maxLine = -1;
	private float mMaxWidth = -1;
	
	protected Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	private ArrayList<TextCell> text;
	private boolean isLongClicked = false;
	
	private ArrayList<MeasuredLine> measuredLines = new ArrayList<MeasuredLine>();	
	private ArrayList<Integer> lineHeights = new ArrayList<Integer>();	
	private boolean contentChanged = true;
	private boolean forceRequestLayout = false;
	private boolean measuredTextCacheEnabled = false; // 已替换自绘组件，该缓存默认关闭
	private int measuredWidth = 0;
	private int measuredHeight;
	
	private int textColor = 0xff000000;
	private int textColorLink = 0xff00ff00;
	private int textColorLinkBackground = 0xffd5d5d5;
	private ColorStateList textColorList = null;
	private ColorStateList textColorLinkList = null;

	private RectF highLightFrame = new RectF();
	
	private TextCell touchTarget;
	//private ReasignablePair<Integer, Integer> touchedPos;
	
	private ArrayList<TextCell> touchTargetEx = new ArrayList<TextCell>();	// for lineBreak;
	private float downX = -1, downY = -1;
	private boolean clickable = false, longclickable = false;
	private boolean longClickTrig = false;
	private final static long LONG_CLICK_TRIG_TIME = 600;
	private OnCellClickListener onCellClickListener;	
	private boolean cellClickable = true;
	private boolean isLineBreakNeeded = true;
	private boolean lineBreakInContent = true;
	private boolean isShowMore = false;
	private boolean hasMore = false;
	
	private static final int maxCharCount = 30;
	private float[] charWidths = new float[maxCharCount];
	//private ReasignablePair<Integer, Float> pairForMeasure = new ReasignablePair<Integer, Float>(-1, -1f);
	//private ReasignablePair<Integer, Integer> pairForClick = new ReasignablePair<Integer, Integer>(-1, -1);
	
	private String ellipsisStr = "...";
	private TextCell ellipsisCell = new TextCell(TextCell.SIGN_NORMAL, ellipsisStr);	
	private ColorTextCell showMoreCell;
	
	private float mDensity;
	
	private OnTextOperater textOperator = new OnTextOperater(){

		@Override
		public void onCopy() {
			copyText(getCopiedText());
			isLongClicked = false;
			postInvalidate();
		}

		@Override
		public void onCancle() {
			isLongClicked = false;
			postInvalidate();
		}
	}; 
	
	public void setOnTextOperateListener(OnTextOperater operateListener){
		this.textOperator = operateListener;
	}
	
	public interface OnTextOperater{
		void onCopy();
		void onCancle();
	}
	
	public static void setCanCopy(ArrayList<TextCell> arrayList, boolean canCopy) {
		if (arrayList == null)
			return;
		
		for (TextCell cell : arrayList) {
			cell.setCanCopy(canCopy);
		}
	}
	
	public void setShowMore(boolean isShowMore) {
		this.isShowMore = isShowMore;
	}
	
	public void setCellClickable(boolean cellClickable) {
		this.cellClickable = cellClickable;
	}
	
	public void setLineBreakNeeded(boolean isLineBreakNeeded) {
		this.isLineBreakNeeded = isLineBreakNeeded;
	}
	
	public void setLineBreakInContent(boolean lineBreakInContent) {
		this.lineBreakInContent = lineBreakInContent;
	}
	
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}

	public void setLongclickable(boolean longclickable) {
		this.longclickable = longclickable;
		
		if (this.longclickable) {
			setOnLongClickListener(new  OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					isLongClicked = true;
					postInvalidate();
					boolean handle = false;
					if(onCellClickListener!=null){
						handle = onCellClickListener.onLongClick(CellTextView.this,textOperator);
					}
					if(!handle){
						//TextCopyDialog.showDialog(mContext,textOperator);
//						DialogUtils.showCopyAlert(mContext, textOperator);
					}
					return true;
				}
			});
		}
	}

	private Handler longClickHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			longClickTrig = true;
			onInvoke(TouchAnalizer.BehaviorType.LONG_CLICK, msg.arg1, msg.arg2, 0);
			
			TextCell temp = touchTarget; // 此处不清空touchTarget，因为昵称需要单独复制！！！
			clearTouchTarget();
			touchTarget = temp;
		}
		
	};

	public void setOnCellClickListener(OnCellClickListener onCellClickListener) {
		this.onCellClickListener=onCellClickListener;
	}
	public CellTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	public CellTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public CellTextView(Context context) {
		super(context);
		init(context, null, 0);
	}


    public static final int getPixFromDip(float aDipValue, final Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wMgr.getDefaultDisplay().getMetrics(dm);
        int pix = (int) (aDipValue * dm.density);
        return pix;
    }

	private Context mContext = null;
	protected void init(Context context, AttributeSet attrs, int defStyle) {
        TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.CellTextView, defStyle, 0);
        int n = tArray.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = tArray.getIndex(i);
            switch (attr) {
                case R.styleable.CellTextView_android_textSize:
                    int textSize = tArray.getDimensionPixelSize(attr, (int) getPixFromDip(13, getContext()));
                    setTextSize(textSize);
                    break;
                case R.styleable.CellTextView_android_textColor:
                    this.textColor = tArray.getColor(attr, this.textColor);
                    break;
                case R.styleable.CellTextView_android_singleLine:
                    boolean singleLine = tArray.getBoolean(attr, false);
                    if (singleLine) {
                        setMaxLine(1);
                    }
                    break;
                case R.styleable.CellTextView_android_maxWidth:
                    mMaxWidth = tArray.getDimensionPixelSize(attr, -1);
                    break;
                case R.styleable.CellTextView_android_maxLines:
                    this.maxLine = tArray.getInt(attr, -1);
                    break;
                case R.styleable.CellTextView_android_text:
                    setText(tArray.getString(attr));
                    break;
            }
        }
        tArray.recycle();
		mContext = context;
		mDensity = getResources().getDisplayMetrics().density;
	}

	@SuppressWarnings("deprecation")
	public void copyText(String content) {
		try {
			ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
			clipboardManager.setText(content);
			Toast.makeText(getContext(), "复制成功", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getContext(), "复制失败", Toast.LENGTH_SHORT).show();
		}
	}

	
	public void setMaxLine(int maxLine) {
		this.maxLine = maxLine;
	}

	public void setText(int resid) {
		setText(getResources().getString(resid));
	}
	
	public void setText(String str) {
		text = new ArrayList<TextCell>();
		text.add(new TextCell(TextCell.SIGN_NORMAL, str));
		requestLayout();
		invalidate();
	}
	
	public void setText(ArrayList<TextCell> text) {
		this.text = text;
		requestLayout();
		invalidate();
	}

	public void setText(TextCell text) {
		this.text=new ArrayList<TextCell>();
		this.text.add(text);
		requestLayout();
		invalidate();
	}
	
	public void setTextBold(boolean b) {
		paint.setFakeBoldText(b);
	}
	
	public String getText() {
		if (text == null)
			return "";
		
		StringBuilder sb = new StringBuilder();
		for (TextCell cell : text) {
			String temp = cell.getText();
			if (temp != null)
				sb.append(temp);
		}
		return sb.toString();
	}
	
	private String getCopiedText() {
		if (text == null)
			return "";

		StringBuilder sb = new StringBuilder();		
		int lineNum = measuredLines.size();
		for (int i = 0; i < lineNum; i++) {
			MeasuredLine line = measuredLines.get(i);
			int size = line.getSize();
			for (int j = 0; j < size; j++) {
				TextCell cell = line.getCell(j);
                if(cell!=null)
                {
				if (touchTarget != null  // 评论中的昵称和评论内容需要分开复制！！！
					&& !touchTarget.canCopy()
					&&  touchTarget.type == TextCell.SIGN_USER) {
					if (cell.linebreakSeq == touchTarget.linebreakSeq) {
						sb.append(cell.getText());
					} else {
						continue;
					}
				} else if (cell.canCopy()) {
					sb.append(cell.getText());
				}
                }
			}
		}
		return sb.toString();
	}
	
	public String getDisplayedText() {
		if (text == null)
			return null;

		StringBuilder sb = new StringBuilder();		
		int lineNum = measuredLines.size();
		for (int i = 0; i < lineNum; i++) {
			MeasuredLine line = measuredLines.get(i);
			int size = line.getSize();
			for (int j = 0; j < size; j++) {
				TextCell cell = line.getCell(j);
                if(cell!=null)
                {
				 sb.append(cell.getText());
                }
			}
		}
		return sb.toString();
	}
	
	public void setTextSize(float textSize) {
		paint.setTextSize(textSize);
		setFontHeight(textSize);
		requestLayout();
		invalidate();
	}
	
	public float getTextSize() {
		return paint.getTextSize();
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
		invalidate();
	}

	public void setLineSpace(int lineSpace){
		this.lineSpace=lineSpace;
		invalidate();
	}
	public void setTextColorLink(int textColorLink) {
		this.textColorLink = textColorLink;
		invalidate();
	}
	
	public void setTextColor(ColorStateList c) {
		textColorList = c;
		drawableStateChanged();
		invalidate();
	}
	
	public void setTextColorLink(ColorStateList c) {
		this.textColorLinkList = c;
		drawableStateChanged();
		invalidate();
	}
	
	public void setTextColorLinkBackground(int parseColor) {
	    textColorLinkBackground = parseColor;
	}
	
	private String getModeStr(int mode){  
        String modeStr = null;  
        switch (mode) {  
        case MeasureSpec.UNSPECIFIED:  
            modeStr = "UNSPECIFIED";  
            break;  
        case MeasureSpec.AT_MOST:  
            modeStr = "AT_MOST";  
            break;  
        case MeasureSpec.EXACTLY:  
            modeStr = "EXACTLY";  
            break;
        default:
        	modeStr = "";
        }  
        return modeStr;  
    } 
	
	@Override
	public void requestLayout() {
		contentChanged = true;
		super.requestLayout();
	} 
	
	public void forceRequestLayout() {
		forceRequestLayout = true;
		requestLayout();
	}
	
	public void setMeasuredTextCacheEnabled(boolean measuredTextCacheEnabled) {
		this.measuredTextCacheEnabled = measuredTextCacheEnabled;
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {	
		int wMode = MeasureSpec.getMode(widthMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		width = width - getPaddingLeft() - getPaddingRight();

		if (width == 0) {
			width = getResources().getDisplayMetrics().widthPixels;
		}
		
		if (mMaxWidth > 0 && mMaxWidth < width) {
			width = (int)mMaxWidth;
		}
		
		if (!contentChanged && mw == width) { // 系统改变大小时，才会重新计算
			setMeasuredDimension(measuredWidth, measuredHeight);
			return;
		}
		contentChanged = false;
		mw = width;
		
		if (measuredTextCacheEnabled) {
			UiElementFixedCache.MeasuredTextResult measuredTextResult = UiElementFixedCache.getInstance().getMeasuredLines(width, (int)paint.getTextSize(), getText());
			if (measuredTextResult != null && !forceRequestLayout){
				lineHeights = measuredTextResult.lineHeights;
				measuredLines = measuredTextResult.measuredLines;
				measuredWidth = measuredTextResult.measuredWidth;
			} else {
				measureText(width);
				UiElementFixedCache.getInstance().putMeasuredCells(width,
						(int)paint.getTextSize(),
						getText(),
						new UiElementFixedCache.MeasuredTextResult(measuredLines, lineHeights, measuredWidth));
				forceRequestLayout = false;
			}
		} else {
			measureText(width);			
		}
		
		if (wMode == MeasureSpec.EXACTLY) {
			measuredWidth = mw;
		} // 其它情况直接使用给的大小
		
		int height = lineSpace;

        Log.d(TAG, "h:" + height);

		for (int c = lineHeights.size() - 1; c >= 0; c--) {
			height += lineHeights.get(c) + lineSpace;

            Log.d(TAG, "h1:" + height);

        }

        height = 126;
		//int mh = getPaddingTop() + getPaddingBottom() + height + lineSpace;
		//居中不能再加lineSpace
		int mh = getPaddingTop() + getPaddingBottom() + height;

        Log.d(TAG, "h2:" + mh);

        measuredWidth = measuredWidth + getPaddingRight() + getPaddingLeft();
		measuredHeight = mh;
		mw = measuredWidth;
		setMeasuredDimension(measuredWidth, measuredHeight);
		
		setContentDescription(getDisplayedText());

        Log.d(TAG, "width: " + measuredWidth);
        Log.d(TAG, "height: " + measuredHeight);

		Paint.FontMetrics fontMetrics = paint.getFontMetrics();
		Log.d(TAG, "ascent: " + fontMetrics.ascent);
		Log.d(TAG, "descent: " + fontMetrics.descent);
		Log.d(TAG, "top: " + fontMetrics.top);
		Log.d(TAG, "bottom: " + fontMetrics.bottom);
		Log.d(TAG, "leading: " + fontMetrics.leading);
	}
	
	/**
	 * 根据传入的pos，把前后关联的cell都设置成点击态（measure过程中截断的cell需要当成一个cell）
	 * 找出所有关联的cell后存起来，方便后续使用
	 */

    /*
	private void pressTouchTarget(ReasignablePair<Integer, Integer> pos) {
		if (touchTarget == null) {
			return;
		}
		
		int lineCount = measuredLines.size();
		// 点击的cell不在第一行时，向前找到起始cell
		for (int i = pos.first; i >= 0; i--) {
			MeasuredLine line = measuredLines.get(i);
			int size = line.getSize();
			for (int j = size - 1; j >= 0; j--) {
				TextCell cell = line.getCell(j);
                if(cell!=null)
                {
				if (touchTarget.linebreakSeq == cell.linebreakSeq) {
					cell.isPresseding = true;
					touchTargetEx.add(cell);
				} else {
					break;
				}
                }
			}
		}
		
		// 把该cell后面关联的cell置点击态.此处为方便使用，所以把位于pos的cell在touchTargetEx里面添加了2个
		for (int i = pos.first; i < lineCount; i++) {
			MeasuredLine line = measuredLines.get(i);
			int size = line.getSize();
			for (int j = (i == pos.first) ? pos.second : 0; j < size; j++) {
				TextCell cell = line.getCell(j);
                if(cell!=null)
                {
				if (touchTarget.linebreakSeq == cell.linebreakSeq) {
					cell.isPresseding = true;
					touchTargetEx.add(cell);
				} else {
					break;
				}
                }
			}
		}
		invalidate();
	}
	*/
	
	private void clearTouchTarget() {
		if (touchTarget == null) {
			return;			
		}
		
		for (TextCell c : touchTargetEx) {
			c.isPresseding = false;
		}
		
		touchTargetEx.clear();
		touchTarget = null;
		downX = -1;
		downY = -1;
		invalidate();
	}
	
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (textColorLinkList != null) {
        	textColorLink = textColorLinkList.getColorForState(getDrawableState(), textColorLink);
        	invalidate();
        }
        if (textColorList != null) {
        	textColor = textColorList.getColorForState(getDrawableState(), textColor);
        	invalidate();
        }
    }
    
    private boolean hasAttached = false;
    private void attachStateChangeToParent() {
    	if (hasAttached) {
    		return;
    	}
    	
    	hasAttached = true;
    	ViewParent vp = getParent();
    	
    	while (vp instanceof ViewGroup) {
    		ViewGroup v = (ViewGroup)vp;
    		v.setAddStatesFromChildren(true);
    		vp = v.getParent();
    	}
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

        /*
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			clearTouchTarget();
			downX = event.getX();
			downY = event.getY();
			if (longclickable) {
				longClickTrig = false;
				longClickHandler.sendMessageDelayed(longClickHandler.obtainMessage(0, (int)event.getX(), (int)event.getY()), LONG_CLICK_TRIG_TIME);
			}
			if (cellClickable) {
				ReasignablePair<Integer, Integer> pos = findCellIndex(event.getX(), event.getY());
                if(pos!=null)
                {
				if (pos.first != -1) {
					touchedPos = pos;
					TextCell cell = measuredLines.get(pos.first).getCell(pos.second);
                    if(cell!=null)
                    {
					if (cell.clickable()) {
						touchedPos = pos;
						touchTarget = cell;
						pressTouchTarget(touchedPos);
						return true;
					}
                    }
				}
               }
			}
			if (clickable || longclickable) {
				if (!clickable) {
					attachStateChangeToParent();
				}
				setPressed(true);
				return true;
			}			
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (longClickTrig) {
				return true;
			}
			boolean outside = checkClickOutside(event.getX(), event.getY());
			if (touchTarget != null) {
				boolean isIn = isInRect(event.getX(), event.getY(),
						measuredLines.get(touchedPos.first).getRect(touchedPos.second));
				if (isIn) {
					return true;
				} else {
					clearTouchTarget();
					longClickHandler.removeMessages(0);
					longClickTrig = false;
					return false;
				}
			}
			if (!outside && (clickable || (longclickable && !longClickTrig))) {				
				return true;
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			setPressed(false);	
			if (longClickTrig) {
				return true;
			}
			longClickHandler.removeMessages(0);
			longClickTrig = false;
			if (touchTarget != null) {
				boolean isIn = isInRect(event.getX(), event.getY(),
						measuredLines.get(touchedPos.first).getRect(touchedPos.second));
				if (isIn) {
					performCellClick(touchTarget);
					clearTouchTarget();
					return true;
				} else {
					onInvoke(TouchAnalizer.BehaviorType.SINGLE_CLICK, downX, downY, 0);
					clearTouchTarget();
					return false;
				}
			}
			onInvoke(TouchAnalizer.BehaviorType.SINGLE_CLICK, downX, downY, 0);
			clearTouchTarget();
			if (!clickable) { // 激活长按事件会屏蔽click事件，所以此处把点击事件直接交给上层处理
				performCellClick(new TextCell(TextCell.SIGN_GOTO_DETAIL));
			}
		} else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			setPressed(false);	
			longClickHandler.removeMessages(0);
			longClickTrig = false;
			clearTouchTarget();
		}
		boolean ret = super.onTouchEvent(event);
		if (!ret) {
			longClickHandler.removeMessages(0);
			longClickTrig = false;
		}
		return ret;
		*/
        return false;
	}

	private boolean checkClickOutside(float x, float y) {
		if (downX < 0 || downY < 0) {
			return true;
		} else {
			boolean ret = (downX - x) * (downX - x) + (downY - y) * (downY - y) > TouchAnalizer.CLICK_AREA;
			if (ret) {
				downX = downY = -1;
			}
			return ret;
		}
	}

	private void performCellClick(TextCell cell) {
		if (onCellClickListener!=null) {
			onCellClickListener.onClick(cell, this);
		}
	}
	
	/**
	 * 找出点击的cell，再向前查找到被截断cell的起始cell
	 * 返回起始cell的位置
	 */

    /*
	private ReasignablePair<Integer, Integer> findCellIndex(float x, float y) {
		// 根据坐标查找被点击的cell
		int lineCount = measuredLines.size();
		for (int i = 0; i < lineCount; i++) {
			MeasuredLine line = measuredLines.get(i);
			int size = line.getSize();
			for (int j = 0; j < size; j++) {
				if (isInRect(x, y, line.getRect(j))) {
					return pairForClick.reasign(i, j);
				}
			}
		}

		return pairForClick.reasign(-1, -1);
	}
*/
	private boolean isInRect(float x, float y, Rect rect) {
		if (rect == null) {
			return false;
		}
		float density = 4 * mDensity;
		return x >= rect.left - density
				&& x <= rect.right + density
				&& y >= rect.top - density
				&& y <= rect.bottom + density;
	}
	
	private static int defaultMaxMeasureCount = 100;
	private int breakText(TextCell cell, int maxWidth) {
		if (!cell.canBreak()) { // 表情直接截断
			return 1;
		}
		int codePointIndex;
		if (cell.text.length() >= defaultMaxMeasureCount) {
			codePointIndex = paint.breakText(cell.text.substring(0, defaultMaxMeasureCount), true, maxWidth, null);
		} else {
			codePointIndex = paint.breakText(cell.text, true, maxWidth, null);
		}
		
		if (codePointIndex == 0) { // 系统的breakText有bug，可能返回的是0，而实际是有值的，用这个方法修正一下
			cell.text = replaceUnsportedChars(cell.text);
			return breakTextSafe(cell, maxWidth);
		}
		
		int breakAt = getIndexByCodePoint(cell.text, codePointIndex);
		return breakAt;
	}

    public static int getIndexByCodePoint(String text, int codePointPos) {
        int size = text.length();
        int index = 0;
        for (int i = 0; i < size;) {
            i += Character.charCount(text.codePointAt(i));
            index++;
            if (index >= codePointPos) {
                return i;
            }
        }
        return size;
    }

    public static String replaceUnsportedChars(String text) {
        if (TextUtils.isEmpty(text)) {
            return text;
        }

        StringBuilder sb = new StringBuilder(text);
        int size = text.length();
        for (int i = 0; i < size;) {
            char high = text.charAt(i);
            char low = (i == size - 1) ? 0 : text.charAt(i + 1);
            boolean isSupport = Character.isSurrogatePair(high, low);
            if (!isSupport && Character.isHighSurrogate(high)) { // 字符只有一半,干掉它!!!
                sb.setCharAt(i, '*');
            }

            i += isSupport ? 2 : 1;
        }

        return sb.toString();
    }
	

	/**
	 * @param cell 需计算宽度的cell
	 * @param maxWidth 最大宽度
	 * @return 返回cell内部截断的位置
	 */
	private int breakTextSafe(TextCell cell, int maxWidth) {
		if (!cell.canBreak()) {
			return 1;
		} else {
			if (TextUtils.isEmpty(cell.text)) {
				return 0;
			}
			float width = 0;
			int size = cell.text.length();
			for (int i = 0; i < size;) {
				float w;
				int charCount = Character.charCount(cell.text.codePointAt(i));
				w = paint.measureText(cell.text, i, i + charCount); // 一次只计算一个字符,防止出现其它问题(emoji两个字符只算一个宽度)
				width += w;
				if (width > maxWidth) {
					return i;
				}
				i += charCount;
			}
			
			return size;
		}
	}
	
	/**
	 * 把准备截断的cell添加到一行里面，返回截断后剩下的cell
	 */
	private TextCell addCellToLine(TextCell measuredCell, int breakAt,  MeasuredLine line, int maxWidth) {
		if (breakAt == 0 && linePos != 0) {
			return measuredCell;
		}
        if(measuredCell==null)
            return null;

		
		// 会修改内部的数据，所以需要copy一份
		TextCell breakPrev = measuredCell.copy();
		if (measuredCell.canBreak()) {
			int len = measuredCell.getLength();
			breakAt = breakAt > len ? len : breakAt;
			breakPrev.text = measuredCell.getText().substring(0, breakAt);
		}
		
		float width = getCellWidth(breakPrev);
		if (breakAt == 0 && linePos == 0) { // 最大宽度太小（放不下一个字符），避免死循环，直接放在当前行
			addCellToLineInternal(line, breakPrev, width);
			return null;
		}
		
		// 不能截断时，如果放不到当前行，整体放到下一行
		if (!measuredCell.canBreak() && width > maxWidth && linePos != 0) { // 不在当前行，且放不下时，放到下一行
			return measuredCell;
		}

		addCellToLineInternal(line, breakPrev, width);
		
		if (breakAt >= measuredCell.getLength()) {
			return null;
		}

		// 有截断时返回剩下的部分继续循环添加到一行中
		TextCell breakNext = measuredCell.copy();
        if(breakNext!=null) {
            breakNext.text = measuredCell.text.substring(breakAt);
        }
        return breakNext;
	}

	private void addCellToLineInternal(MeasuredLine line, TextCell cell, float width) {
		int height = getCellHeight(cell);

		Log.d(TAG, "addCellToLineInternal, height:" + height);

		int h = lineHeights.get(measuredLines.size() - 1);
		height = height > h ? height : h;

        Log.d(TAG, "addCellToLineInternal, height1:" + height);

        lineHeights.set(measuredLines.size() - 1, height);
		
		Rect rect = new Rect(linePos, verPos,
				(int)(linePos + width), verPos + height);
		
		linePos += width;
		measuredWidth = linePos > measuredWidth ? linePos : measuredWidth;
		line.add(cell, rect);
	}

	
	/**
	 * 排版核心方法，内部将cell按行放置
	 * @param maxMeasuredWidth
	 * @return
	 */
	private boolean measureCell(int maxMeasuredWidth) {		
		linebreakSeq = 0;
		measuredWidth = 0;
		measuredLines.clear();
		lineHeights.clear();
		MeasuredLine line = new MeasuredLine();
		measuredLines.add(line);
		
		for (TextCell textCell : text) {
			TextCell measuredCell = textCell;
			//TODO 处理emoji表情
			//measuredCell.text = EmotcationUtils.replaceEmojiText(measuredCell.text);
			int height = getCellHeight(measuredCell);
			if (lineHeights.isEmpty()) {
				lineHeights.add(0);
			}
			if (measuredCell.isEmo()) { // 下载组件会在快速滑动时取消下载，所以此处需要重新load，以后修正
				EmoObjectPool.getInstance().refreshEmoDrawable((EmoCell)measuredCell, this);
			}
			linebreakSeq += TextCell.FLAG_LINEBREAK_SEQ;
			while (measuredCell != null && !TextUtils.isEmpty(measuredCell.getText())) {
				if (measuredCell.canBreak()) {
					measuredCell.linebreakSeq = linebreakSeq;
				}
				if (maxLine > 0 && measuredLines.size() > maxLine) {
					int last = lineHeights.size() - 1;
					int lastHeight = lineHeights.get(last);
					lineHeights.set(last, lastHeight - lineSpace);
					return false;
				}
				int maxWidth = maxMeasuredWidth - linePos;
				int breakAt = breakText(measuredCell, maxWidth);
				//int breakAt = cellInfo.first;  // 截断位置 注意需要是真实下标
				
				int lineBreakIndex = getLineBreakIndex(measuredCell, breakAt);
				if (lineBreakIndex != -1) { // 有换行符
					breakAt = lineBreakIndex + 1;
					measuredCell = addCellToLine(measuredCell, breakAt, line, maxWidth);
	
					line = addNewLine(height);
				} else {
					int newBreakAt = reMeasureForEnglishWords(measuredCell.getText(), breakAt);
					if (linePos != 0 && newBreakAt == 0) // 本行已有数据的情况下，该cell显示不下，需要整体换行
					{
						breakAt = 0;
					}
					
					breakAt = newBreakAt != 0 ? newBreakAt : breakAt;
					measuredCell = addCellToLine(measuredCell, breakAt, line, maxWidth);
					
					if (measuredCell != null && !TextUtils.isEmpty(measuredCell.getText())) {
						line = addNewLine(height);
					} else {
						break;
					}
				}
			}
		}
		
//		for (MeasuredLine measuredLine : measuredLines) {
//			measuredLine.showContent();
//		}
		
		return true;
	}

	private MeasuredLine addNewLine(int height) {

        Log.d(TAG, "addNewLine:" + height);

		MeasuredLine line;
		linePos = 0;
		int lineNum = measuredLines.size();
		int h = lineNum > 0 ? lineHeights.get(lineNum - 1) : height;
		verPos += lineSpace + h;
		line = new MeasuredLine();
		measuredLines.add(line);
		lineHeights.add(0);
		return line;
	}

	private void measureText(int maxMeasuredWidth) {
		lineHeights.clear();
		linePos = 0;
		verPos = lineSpace;
		if (text == null) {
			return;
		}
		
		boolean isFinished = measureCell(maxMeasuredWidth);
		
		if (hasMore && isFinished) { // 所有字符排版完成，但后台没有发完，这时一定会显示查看全文
			addNewLine(0);
		}
		
		if (!isFinished || hasMore) {
			addEllipsis();
		}
		
		if ((isShowMore && !isFinished) || hasMore) {
			if (showMoreCell == null) {
				showMoreCell = new ColorTextCell(TextCell.SIGN_GOTO_DETAIL, "查看全文");
				showMoreCell.setCanCopy(false);
			}
			linebreakSeq += TextCell.FLAG_LINEBREAK_SEQ;
			showMoreCell.linebreakSeq = linebreakSeq;
			if (cellClickable) {
				if (!isFakeFeed) {
//				showMoreCell.setTextColor(getContext().getResources()
//						.getColor(R.color.qzone_feed_username));
				showMoreCell.setClickable(true);
				} else {
//					showMoreCell.setTextColor(getContext().getResources()
//							.getColor(R.color.qzone_color_zhonghui));
					showMoreCell.setClickable(false);
				}
			}
			MeasuredLine line = measuredLines.get(measuredLines.size() - 1);
			float width = getCellWidth(showMoreCell);
			linePos = 0;
			verPos += lineSpace;
			addCellToLineInternal(line, showMoreCell, width);
		}
	}

	/**
	 * 添加截断后的结束符。
	 * 算法描述：预留结束符的位置后，重新计算最后一行的排版
	 */
	private void addEllipsis() {
		if (measuredLines.size() < 2) { // 防止越界，一般情况下不会进入
			addNewLine(0);
		}
		MeasuredLine line = measuredLines.get(measuredLines.size() - 2);
		float ellipsisWidth = getCellWidth(ellipsisCell);
		
		linePos = 0;
		boolean isAdded = false;
		int size = line.getSize();
		for (int i = 0; i < size; i++) {
			TextCell cell = line.getCell(i);
			int maxWidth = (int)(mw - ellipsisWidth - linePos);
			if (maxWidth < 0) {
				return;
			}
			int breakAt = breakText(cell, maxWidth);
			
			float width = cell.getWidth(paint);
			if (width <= maxWidth) {
				linePos += width;
			} else {
				if (cell.canBreak()) {
					if (getCharSafe(cell.text, breakAt - 1) == '\n') {
						breakAt--;
					}
					cell.text = cell.text.substring(0, breakAt) + ellipsisStr;
					line.removeToEnd(i + 1); // 将之前排版多余的部分去掉
					isAdded = true;
				} else {
					line.removeToEnd(i); // 不能被截断，需要单独添加一个cell
				}
				break;
			}
		}
		
		// 更新当前所占用宽度
		linePos +=  (int)ellipsisWidth;
		measuredWidth = linePos > measuredWidth ? linePos : measuredWidth;
		
		if (isAdded) {
			return;
		}
		
		Rect rect = new Rect(line.getRect(line.getSize() - 1));
		TextCell lastCell = line.getCell(line.getSize() - 1);
        if(lastCell!=null)
        {
		if (lastCell.canBreak() && lastCell.text.endsWith("\n")) { // 去掉最后一个cell的换行符
			lastCell.text = lastCell.text.substring(0, lastCell.text.length() - 1);
		}
        }
		rect.left = rect.right;
		rect.right = linePos;
		line.add(ellipsisCell, rect);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.translate(getPaddingLeft(), getPaddingTop());
		
		int lineNum = measuredLines.size();
		for (int i = 0; i < lineNum; i++) {
			MeasuredLine line = measuredLines.get(i);
			int size = line.getSize();
			for (int j = 0; j < size; j++) {
				TextCell cell = line.getCell(j);
				Rect rect = line.getRect(j);

                if(cell!=null&&rect!=null)
                {
				if (cell.isEmo() && !cell.isDrawableLoaded) { // 表情没加载出来时，需要重新请求
					EmoObjectPool.getInstance().refreshEmoDrawable((EmoCell) cell, this);
				}
				
				if (isLongClicked // 评论中的昵称和评论内容需要分开复制！！！所以此处需要分开高亮！！！
						&& touchTarget != null
						&& !touchTarget.canCopy()
						&&  touchTarget.type == TextCell.SIGN_USER) {
					if (cell.linebreakSeq == touchTarget.linebreakSeq) {
						paint.setColor(textColorLinkBackground);
						highLightFrame.set(rect.left - mDensity,
								rect.top - mDensity,
								rect.right + mDensity,
								lineHeights.get(i) + rect.top + mDensity);
						canvas.drawRect(highLightFrame, paint);
					}
					cell.draw(canvas, paint, lineHeights.get(i), rect, textColor, textColorLink);
					continue;
				}
				
				if ((cell.isPresseding && cellClickable) || (isLongClicked && cell.canCopy())) {
					paint.setColor(textColorLinkBackground);
					highLightFrame.set(rect.left - mDensity,
							rect.top - mDensity,
							rect.right + mDensity,
							lineHeights.get(i) + rect.top + mDensity);
					canvas.drawRect(highLightFrame, paint);
				}
					Log.d(TAG, cell.toString());
				cell.draw(canvas, paint, lineHeights.get(i), rect, textColor, textColorLink);
			   }
            }
		}

		canvas.translate(-getPaddingLeft(), -getPaddingTop());
	}
	
	private boolean isEnglishChar(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
				|| (c >= '0' && c <= '9')
				|| c == '\'' || c == ';' || c == ',' || c =='.'
				|| c == '?' || c == '!' || c == '\"' || c == '_'
				|| c == '-' || c == ':' || c == '@';
	}
	
	private int findWordStartPos(String text, int breakIndex) {
		for (int i = breakIndex; i >= 0; i--) {
			char c = text.charAt(i);
			if (c == ' ' || !isEnglishChar(c)) {
				for (i--; i >= 0; i--) { // find first space
					if (text.charAt(i) != ' ') break;
				}
				return i + 2; // return the first char of the word
			}
		}
		return 0; // 如果整行都没有空格，不折断
	}
	
	private char getCharSafe(String text, int index) {
		if (index >= 0 && index < text.length()) {
			return text.charAt(index);
		}
		return '\0'; // 返回特殊字符防止越界.
	}
	
	private int reMeasureForEnglishWords(String text, int breakPos) {
		int breakIndex = breakPos - 1; // 转换成index
		if (isEnglishChar(getCharSafe(text, breakIndex))) {
			if (isEnglishChar(getCharSafe(text, breakIndex + 1))) { // 当前字符和下一个都是英文
				int wordStart = findWordStartPos(text, breakIndex);
				return wordStart;
			}
		}
		
		if (getCharSafe(text, breakIndex + 1) == ' ' || getCharSafe(text, breakIndex + 1) == '\n') {
			return breakPos + 1; // 把空格放到行末
		}

		return breakPos;
	}
	
	private int getLineBreakIndex(TextCell cell, int maxSearchCount) {
		int length = cell.getLength();
		length = length > maxSearchCount ? maxSearchCount : length;
		for (int i = 0; i < length; i++) {
			if (getCharSafe(cell.getText(), i) == '\n') {
				return i;
			}
		}
		return -1;
	}	

	protected int getCellHeight(TextCell cell) {
		return cell.getHeight(paint);
	}
	
	protected float getCellWidth(TextCell cell) {
        if(cell!=null)
        {
		return cell.getWidth(paint);
        }
        else
        {
            return 0.0f;
        }
	}

	public Paint getPaint() {
		return paint;
	}

	public boolean onInvoke(int behaviorType, float x, float y, int state) {
		try {
			if (TouchAnalizer.BehaviorType.SINGLE_CLICK == behaviorType) {
				return performClick();
			} else if (TouchAnalizer.BehaviorType.LONG_CLICK == behaviorType) {
				return performLongClick();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
/*
	public ArrayList<TextCell> setRichText(String text,int nickNameColorRes ) {
        ArrayList<TextCell> cells = new ArrayList<TextCell>();
        ArrayList<RichTextElement> textElements = RichTextParser.getRichTextElements(text);
        if (textElements != null) {
            int bound = getFontHeight(getTextSize());
            bound = Math.max(bound, 20);
            for (int i = 0; i < textElements.size(); i++) {
                RichTextElement element = textElements.get(i);
                if (element == null)
                    continue;
                switch (element.getType()) {
                    case RichTextElement.RICH_TEXT_ELEMENT_NICK_NAME:
                        if (!(element instanceof NickNameElement))
                            continue;
                        UserNameCell cell = new UserNameCell(TextCell.SIGN_USER, ((NickNameElement) element).nickName);
                        if (cell != null) {
                            cell.setTextColor(nickNameColorRes);
                            cell.setUin(((NickNameElement) element).uin);
                            cells.add(cell);
                        }
                        break;
                    case RichTextElement.RICH_TEXT_ELEMENT_CONTENT: {
                        cells.add(new TextCell(TextCell.SIGN_NORMAL, text.subSequence(element.startPosition, element.endPosition).toString()));
                        break;
                    }
                    case RichTextElement.RICH_TEXT_ELEMENT_LOCAL_SMILEY: {
                        if (!(element instanceof SmileyElement))
                            continue;
                        EmoCell emo = new EmoCell();
                        Drawable d = getLocalEmoDrawable(((SmileyElement) element).smileyIndex);
                        if (d != null) {
                            emo.text = ((SmileyElement) element).smileyCode;
                            emo.emoDrawable = d;
                            d.setBounds(0, 0, bound, bound);// 设置表情图片的显示大小
                            cells.add(emo);
                        }
                        break;
                    }
                }
            }
        }
		if (this.text == null) {
            this.text = new ArrayList<TextCell>();
        }
        this.text.clear();
		this.text.addAll(cells);
		requestLayout();
		invalidate();
		return cells;
    }
*/

    /*
    private Drawable getLocalEmoDrawable(int smileyIndex) {
        int resId = 0;
        try {
            resId = SmileyGrid.getImageIds()[smileyIndex];
            Drawable d = DLApp.getContext().getResources().getDrawable(resId);
            return d;
        } catch (Exception e) {
        }
        return null;
    }
    */

//	public ArrayList<TextCell> parseContent(CharSequence text){
//        int bound = getFontHeight(getTextSize());
//        return TextCellHelper.parseContent(this, text, bound, cellClickable);
//	}
	
	private int getFontHeight(float fontSize) {	
		   if (fontHeight==0) {
			   setFontHeight(fontSize);
		   }
	       return fontHeight;
	}
	
	private void setFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		fontHeight= (int) Math.ceil(fm.descent - fm.top);
	}

	public void setFakeFeed(boolean isFakeFeed) {
		this.isFakeFeed = isFakeFeed;		
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;		
	}	
	
	public interface OnCellClickListener {
	    public void onClick(TextCell textCell, View view);
	    public boolean onLongClick(View view, OnTextOperater operator);
	}
	
	public static class MeasuredLine {
		private ArrayList<TextCell> measuredText;
		private ArrayList<Rect> measuredRectList;
		
		private void initList() {
			if(measuredText == null) {
				measuredText = new ArrayList<TextCell>(1);				
			}
			if(measuredRectList == null) {
				measuredRectList = new ArrayList<Rect>(1);
			}
		}
		public void add(TextCell cell, Rect rect) {
			initList();
			measuredText.add(cell);
			measuredRectList.add(rect);
		}
		
		public TextCell getCell(int index) {
			return measuredText != null ? measuredText.get(index) : null;
		}
		
		public Rect getRect(int index) {
			return measuredRectList != null ? measuredRectList.get(index) : null;
		}
		
		/**
		 * @param index 删除index之后的所有元素
		 * 算法描述：每次删除最后一个元素
		 */
		public void removeToEnd(int index) {
			if(measuredText == null) return;
			int size = measuredText.size();

			if (index >= size) {
				return;
			}
			
			for (int i = 0; i < size - index; i++) {
				measuredText.remove(size - i - 1);
				measuredRectList.remove(size - i - 1);
			}
		}
		
		public int getSize() {
			if(measuredText == null) return 0;
			
			int size = measuredText.size();
			int rectSize = measuredRectList.size();
			
			return size < rectSize ? size : rectSize;
		}
		
//		public void showContent() {
//			StringBuilder sb = new StringBuilder();
//			for (TextCell cell : measuredText) {
//				sb.append(cell.getText());
//			}
//			QZLog.d("MeasuredLine", "Line: " + sb.toString());
//			
//			sb.setLength(0);
//			for (Rect rect : measuredRectList) {
//				sb.append(rect.toShortString() + " | ");
//			}
//			QZLog.d("MeasuredLine", "Rect: " + sb.toString());
//		}
	}

	/**
	 * 设置最大宽度
	 */
	public void setMaxWidth(int maxpixels) {
		mMaxWidth = maxpixels;
	}

	@Override
	public void postRequestLayout() {
		requestLayout();		
	}
}
