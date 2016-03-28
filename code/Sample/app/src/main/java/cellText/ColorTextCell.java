package cellText;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;

/**
 * 用来自定义颜色文字
 * @author alairzhang
 *
 */
public class ColorTextCell extends TextCell {    
    /**
	 * 
	 */
	private static final long serialVersionUID = -7171491901412815564L;
	private boolean clickable = true;


	public ColorTextCell() {
    	super(SIGN_NORMAL);
	}
    
    public ColorTextCell(int type) {
    	super(type);
	}
    
    public ColorTextCell(int type, String text) {
    	super(type, text);
	}

	private int textColor;
	private boolean bold;

	
	public void setTextColor(int textColor) {
		this.textColor = textColor;		
	}
	
	@Override
	public boolean clickable() {
		boolean clickable = super.clickable();
		return this.clickable && clickable;
	}
	
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}

	public void setTextBold(boolean b)
	{
		this.bold=b;
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint, int lineHeight, Rect rect, int textColor, int textColorLink) {
		if ((type & ColorTextCell.FLAG_TYPE_MASK) == ColorTextCell.SIGN_NORMAL
				|| type == TextCell.SIGN_USER
				|| type == TextCell.SIGN_URL
				|| type == TextCell.SIGN_GOTO_DETAIL
				|| type == TextCell.SIGN_FEED_OWNER) {
			paint.setColor(this.textColor);
		} else {
			paint.setColor(textColor);
		}
		boolean boldRestore = paint.isFakeBoldText();
		if (bold) { // 仅当加粗时修改字体，用后还原
			paint.setFakeBoldText(bold);
		}
		int drawY = rect.top + (int) ((lineHeight - paint.descent() - paint.ascent()) / 2);
		
		String drawText = text;
		if (text.endsWith("\r\n")) {
			drawText = text.substring(0, text.length() - 2);
		} else if (text.endsWith("\n")) {
			drawText = text.substring(0, text.length() - 1);
		}

		if(!TextUtils.isEmpty(drawText)) {

			Log.d("ColorTextCell", "left" + rect.left);
			Log.d("ColorTextCell", "drawY" + drawY);

			canvas.drawText(drawText, rect.left, drawY, paint);			
		}
		
		if (bold) {
			paint.setFakeBoldText(boldRestore);
		}
	}

}
