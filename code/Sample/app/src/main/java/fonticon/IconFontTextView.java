package fonticon;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;


/**
 * Created by ritterzhou on 2015/8/21.
 */
public class IconFontTextView extends TextView {

    //private static final String FONT_FILE = "fontGqqV2.ttf";
    //private static final String FONT_FILE = "DroidSansFallback.ttf";
    private static final String FONT_FILE = "simfang.ttf";

    private static Typeface sFont = null;

    private int mCurTextColor = 0;
    private int mCurTextSize = 0;
    private int mCurTextChar = 0;

    /** Set By Jni */
    private int mCharBitmap = 0;
    private int mCharBitmapW = 0;
    private int mCharBitmapH = 0;

    private Bitmap mIconBitmap = null;
    private Bitmap mPressedBckBitmap = null;

    private static final String TAG = "IconFontTextView";

    private static int sIndex = 0;

    public IconFontTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public IconFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public IconFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle)
    {
        setTypeFace(context);
        setDefaultBackground();

        String model = Build.MODEL;
        Log.d(TAG, model);

        /** 有机型自己处理字体图标 */
        boolean custom = model.contains("Lenovo S930");
        if (sIndex == 0) custom = true;
        else custom = false;

        custom = true;

        if (custom && loadLibrary()){
        //   initForCustomFontIcon();
        }

        sIndex += 1;
    }

    private void setTypeFace(Context context) {
        if (sFont == null){
            sFont = Typeface.createFromAsset(context.getAssets(), FONT_FILE);
        }
        if (sFont != null){
            setTypeface(sFont);
        } else{
            Log.e(TAG, "字体资源加载失败");
        }
    }

    private void setDefaultBackground(){
        //setBackgroundResource(R.drawable.ui_selector_iconfont_textview_bg);
    }

    private void createIconBmp(int textColor)
    {
        if (mIconBitmap == null || !(mIconBitmap.getWidth() == mCharBitmapW && mIconBitmap.getHeight() == mCharBitmapH)) {
            mIconBitmap = Bitmap.createBitmap(mCharBitmapW, mCharBitmapH, Bitmap.Config.ARGB_8888);
        }

        final int R = Color.red(textColor);
        final int G = Color.green(textColor);
        final int B = Color.blue(textColor);

        for (int i = 0; i < mCharBitmapH; i++)
        {
            for (int j = 0; j < mCharBitmapW; j++)
            {
                final int gray = getCharBitmapPixel(mCharBitmap, j, i);
                final int color = Color.argb(gray, R, G, B);
                mIconBitmap.setPixel(j, i, color);
            }
        }
    }

    private void createPressBckBmp()
    {
        /** 使用R.drawable.ui_selector_iconfont_textview_bg 中指定的背影色 */
        int color = Color.TRANSPARENT;
        if (color != Color.TRANSPARENT) {
            if (mPressedBckBitmap == null || !(mPressedBckBitmap.getWidth() == mCharBitmapW && mPressedBckBitmap.getHeight() == mCharBitmapH)) {
                mPressedBckBitmap = Bitmap.createBitmap(mCharBitmapW, mCharBitmapH, Bitmap.Config.ARGB_8888);
            }
            mPressedBckBitmap.eraseColor(color);
        }
    }

    private int getCurTextChar()
    {
        final String text = this.getText().toString();
        if (text.length() <=0) {
            Log.w(TAG, "text is empty");
            return 0;
        }
        /* &#x4000; */
        char array[] = new char[text.length()];
        text.getChars(0, text.length(), array, 0);
        final int charCode = array[0];
        return charCode;
    }

    private void initForCustomFontIcon() {

        Log.d(TAG, "initForCustomFontIcon");

        int   charCode   = getCurTextChar();
        final float textSize   = getTextSize();
        final int   textColor  = getCurrentTextColor();

        if (charCode == 0){
            Log.w(TAG, "text is null");
            return;
        }

       // charCode = 0x003c;

        Log.d(TAG, String.format("TextSize:%f, TextColor:0x%x, CharCode:0x%x", textSize, textColor, charCode));

        int ret = setAssetManager(this.getResources().getAssets());
        if (ret != 0){
            Log.e(TAG, "setAssetManager failed.");
            return;
        }

        ret = loadCharBitmap(charCode, (int)textSize);
        if (ret != 0) {
            Log.e(TAG, "setProperty failed.");
            return;
        }

        Log.d(TAG, String.format("To Create Bitmap Size:[%d, %d]", mCharBitmapW, mCharBitmapH));

        if (mCharBitmapW <= 0 || mCharBitmapH <= 0){
            Log.e(TAG, "Set Bitmap Size failed.");
            return;
        }

        createIconBmp(textColor);
        createPressBckBmp();

        mCurTextChar = charCode;
        mCurTextSize = (int)textSize;
        mCurTextColor = textColor;

        destroyCharBitmap(mCharBitmap);
        mCharBitmap = 0;

        Log.d(TAG, "initForCustomFontIcon succeeded.");
    }

    void updateCustomFontIcon()
    {
        final int   charCode   = getCurTextChar();
        final float textSize   = getTextSize();
        final int   textColor  = getCurrentTextColor();

        if (       charCode != mCurTextChar
                || (int)textSize != mCurTextSize
                || textColor != mCurTextColor)
        {
           // initForCustomFontIcon();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mIconBitmap != null) {
            setMeasuredDimension(mCharBitmapW, mCharBitmapH);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        Log.d(TAG, "width: " + width);
        Log.d(TAG, "height: " + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mIconBitmap != null)
        {
            if (isPressed() && mPressedBckBitmap != null) {
                canvas.drawBitmap(mPressedBckBitmap, 0, 0, null);
            }

            updateCustomFontIcon();

            if (mIconBitmap != null) {
                canvas.drawBitmap(mIconBitmap, 0, 0, null);
            }

            return;
        }

        super.onDraw(canvas);

        Paint.FontMetrics fontMetrics = this.getPaint().getFontMetrics();
        Log.d(TAG, "ascent: " + fontMetrics.ascent);
        Log.d(TAG, "descent: " + fontMetrics.descent);
        Log.d(TAG, "top: " + fontMetrics.top);
        Log.d(TAG, "bottom: " + fontMetrics.bottom);
        Log.d(TAG, "leading: " + fontMetrics.leading);

        String str = this.getText().toString();
        Rect rect = new Rect();
        this.getPaint().getTextBounds(str, 0, str.length(), rect);
        Log.d(TAG, rect.toString());
    }

    private native int setAssetManager(AssetManager assetManager);
    private native int loadCharBitmap(int textCode, int textSize);
    private native int getCharBitmapPixel(int charBitmap, int x, int y);
    private native int destroyCharBitmap(int charBitmap);

    private static boolean mSuccess = false;

    private boolean loadLibrary()
    {
        try {
            if (!mSuccess){
                System.loadLibrary("fonticon");
                mSuccess = true;
            }
        }
        catch (Error e){
            Log.e(TAG, e.toString());
        }

        return mSuccess;
    }
}
