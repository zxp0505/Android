package cellText;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

public class EmoCell extends TextCell {
	private static final long serialVersionUID = -3999833992135197771L;
	
	public Drawable emoDrawable;
	String emoCode;
	
	public EmoCell() {
		type = SIGN_NORMAL;
	}
	
	public EmoCell(String emoCode, Drawable drawable) {
		super(SIGN_NORMAL);
		this.emoCode = emoCode;
		this.emoDrawable = drawable;
	}
	public EmoCell(String text) {
		this.text = text;
	}
	
	@Override
	public boolean canBreak() {
		return false;
	}
	
	@Override
	public boolean isEmo() {
		return true;
	}
	
	@Override
	public String getText() {
        if (TextUtils.isEmpty(this.text)) {
            text = emoCode;
        }
        return text;
	}
	
	@Override
	public int getLength() {
		return 1;
	}

	public Drawable getEmoDrawable() {
		return emoDrawable;
	}
	
	@Override
	public float getWidth(Paint p) {
		Drawable d = getEmoDrawable();
		return d == null ? 0 : d.getBounds().width();
	}
	
	@Override
	public int getWidths(Paint p, int start, int maxCount, float[] widths) {
		Drawable d = getEmoDrawable();
		widths[0] = d == null ? 0 : d.getBounds().width();
		return 1;
	}
	
	@Override
	public int getHeight(Paint paint) {
		Drawable d = getEmoDrawable();
		return d == null ? 0 : d.getBounds().height();
	}
		
	@Override
	public void draw(Canvas canvas, Paint paint, int lineHeight, Rect rect, int textColor, int textColorLink) {
		Drawable d = getEmoDrawable();
		if (d != null) {
			int x = rect.left;
			int y = rect.top-1 + (lineHeight - d.getBounds().height()) / 2;
			canvas.translate(x, y);
			d.draw(canvas);
			canvas.translate(-x, -y);
		}
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint, int lineHeight, int textColor, int textColorLink, int breakTail) {
		Drawable d = getEmoDrawable();
		if (d != null) {
			int x = rect.left;
			int y = rect.top-1 + (lineHeight - d.getBounds().height()) / 2;
			canvas.translate(x, y);
			d.draw(canvas);
			canvas.translate(-x, -y);
		}
		
	}
	
	@Override
	public TextCell copy() {
		return this;
	}
	
}
