package cellText;

public interface ReMeasureableLayout {
	public void requestLayout();
	public void invalidate();
	public void setPressed(boolean pressed);
    public boolean isPressed();
    public void postInvalidate();    
    public void postRequestLayout();
}