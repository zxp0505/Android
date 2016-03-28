package cellText;

import java.io.Serializable;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;


public class TextCell implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5342730555874002105L;
	public static final int FLAG_TYPE_MASK = 0xff;
    public static final int SIGN_NORMAL = 0x00;
    public static final int SIGN_TOPIC = 0x01;
    public static final int SIGN_WALL = 0x02;
    public static final int SIGN_USER = 0x03;
    public static final int SIGN_URL = 0x04;
    public static final int SIGN_GOTO_DETAIL = 0x05;
    public static final int SIGN_FEED_OWNER = 0x06; // feeds主人，推广feeds需求新增该类型用于特殊逻辑处理
        
	public static final int SIGN_COMMENT = 0x07;   
    public static final int SIGN_REPLY = 0x08;
 	public static final int SIGN_COLOR = 0x09;
 	public static final int SIGN_LINEBREAK = 0x10;
 
    public static final int FLAG_EMO = 0x100;
    
    public static final int FLAG_EMO_SEQ = 0x1000;
    public static final int FLAG_EMO_MASK = 0xff000;
    
    public static final int FLAG_LINEBREAK_SEQ = 0x100000;
    public static final int FLAG_LINEBREAK_MASK = 0xff00000;
    public boolean needHighLight = false;
	private int textColor;
    private Long uin;
    public TextCell() {
    	type = SIGN_NORMAL;
	}
    
    public TextCell(int type) {
		this.type = type;
	}
    
    public TextCell(int type, String text) {
		this.type = type;
		this.text = text;//CodePointUtils.filter(text, DefaultCodePointFilter.getInstance());
	}

	public int linebreakSeq = 0;
    public boolean isPresseding = false;
    public transient Rect rect;
	public int type = 0;
	public String text;
	private String url;
	public String post;
	private boolean canCopy = true;
	public boolean isDrawableLoaded = true;
	public int linebreak = 0;
	
	public void setCanCopy(boolean canCopy) {
		this.canCopy = canCopy;
	}
	
	public boolean canCopy() {
		return canCopy;
	}
	
	public boolean isEmo() {
		return false;
	}

	public boolean clickable() {
		int t = (type & TextCell.FLAG_TYPE_MASK);
		return t != TextCell.SIGN_NORMAL;
	}
	
	public boolean canBreak() {
		return true;
	}
	
	public float getWidth(Paint p) {
		float w = p.measureText(text);
		return w > 0 ? w : getWidthSafe(p);
	}
	
	/**
	 * @param p 需计算宽度的cell
	 */
	private float getWidthSafe(Paint p) { // 系统计算宽度有问题时,用这个方法修正
			if (TextUtils.isEmpty(text)) {
				return 0;
			}
			float width = 0;
			int size = text.length();
			for (int i = 0; i < size;) {
				int charCount = Character.charCount(text.codePointAt(i));
				float w = p.measureText(text, i, i + charCount); // 一次只计算一个字符,防止出现其它问题(emoji两个字符只算一个宽度)
				width += w;
				i += charCount;
			}
			
			return width;
	}
	
	public int getLength() {
		if (text != null)
			return text.length();
		else 
			return 0;
	}
	
	public int getWidths(Paint p, int start, int maxCount, float[] widths) {
		if (TextUtils.isEmpty(text)) {
			return 0;
		}
		
		int end = start + maxCount;
		if (end > text.length()) {
			end = text.length();
		}
		if (start >= end) {
			return 0;
		}
		return p.getTextWidths(text, start, end, widths);
	}
	
	public void setUin(Long uin) {
		this.uin=uin;
	}
	
	public void setUrl(String url) {
		this.url=url;
	}
	
	public void setPost(String post) {
		this.post=post;
	}
	
	public String getPost() {
		if (!TextUtils.isEmpty(post)) {			
			return post;
		}else {
			return "";
		}
	}
	
	public Long getUin() {
		return uin;
	}
	public int getHeight(Paint paint) {
		return (int) (paint.descent() - paint.ascent());
	}

	public String getUrl(){
		return this.url;
	}
	
	public Intent getIntent() {
		return null;
	}
	
	public String getText() {
		if (text == null) {
			return "";
		} else {
			return this.text;
		}
	}
	
	public ClickableSpan getSpan() {
		return new ClickableSpan() {
			
		    @Override
		    public void updateDrawState(TextPaint ds) {
		        ds.setColor(ds.linkColor);
		        ds.setUnderlineText(false);
		    }
		    
		    @Override
		    public void onClick(View widget) {
		        if(clickable()){
		            final Intent intent = getIntent();
		            if (intent != null) {
		                widget.getContext().startActivity(intent);
		            }
		        }
		    }

		};
	}
	
	public void draw(Canvas canvas, Paint paint, int lineHeight, Rect rect, int textColor, int textColorLink) {
		if ((type & TextCell.FLAG_TYPE_MASK) == TextCell.SIGN_NORMAL) {
			paint.setColor(textColor);
		} else {
			paint.setColor(textColorLink);
		}
		int drawY = rect.top + (int) ((lineHeight - paint.descent() - paint.ascent()) / 2);

		Log.d("ColorTextCell", "left" + rect.left);
		Log.d("ColorTextCell", "drawY" + drawY);

		Rect temrect = new Rect();
		paint.getTextBounds(text, 0, text.length(), temrect);
		Log.d("ColorTextCell", temrect.toString());

		String drawText = text;
		if (text.endsWith("\r\n")) {
			drawText = text.substring(0, text.length() - 2);
		} else if (text.endsWith("\n")) {
			drawText = text.substring(0, text.length() - 1);
		}

		drawY = Math.abs(temrect.top);

		canvas.drawText(drawText, rect.left, drawY, paint);
	}

	public void draw(Canvas canvas, Paint paint, int lineHeight, int textColor, int textColorLink, int breakAt) {
		if (isEmo()) {
			// child class implement.
		} else {
		    // transient Rect rect; transient特殊变量，在序列化之后可能导致null，这里添加判断，防止crash by chaseli
		    if (null == rect) return;

			int masktype = type & TextCell.FLAG_TYPE_MASK;
			if (masktype == TextCell.SIGN_NORMAL || masktype == TextCell.SIGN_COMMENT || masktype == TextCell.SIGN_REPLY) {
				paint.setColor(textColor);
			} else {
				paint.setColor(textColorLink);
			}


			int drawY = rect.top + (int) ((lineHeight - paint.descent() - paint.ascent()) / 2);

			Log.d("ColorTextCell", "left" + rect.left);
			Log.d("ColorTextCell", "drawY" + drawY);

			if (breakAt > 0 && breakAt < rect.right) {
				String fixText = text.substring(0, paint.breakText(text, true, breakAt - rect.left - paint.measureText("..."), null));
				canvas.drawText(fixText + "...", rect.left, drawY, paint);
			} else {
				canvas.drawText(text, rect.left, drawY, paint);
			}
		}		
	}
	
	public TextCell copy() {
		try {
			return (TextCell) clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}
}
