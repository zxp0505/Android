package cellText;

import android.content.Intent;


public class UrlCell extends ColorTextCell {

    private static final long serialVersionUID = -7091133393080460624L;

    private int action;

    public static final int ACTION_NORMAL = 1;

    public static final int ACTION_GOTODETAIL = ACTION_NORMAL + 1;

    public UrlCell(String txt, String url,String post) {
        type = type & ~FLAG_TYPE_MASK | SIGN_URL;
        setUrl(url);
        text = txt;
        this.post = post;
        if (url.contains("enterdetail")) {
            action = ACTION_GOTODETAIL;
        } else {
            action = ACTION_NORMAL;
        }
        // text = CodePointUtils.filter(txt, DefaultCodePointFilter.g());
    }

    @Override
    public Intent getIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // intent.setAction(Intent.ACTION_VIEW);
        // boolean defaultBroswerEnable = MicroblogApp.getSelf().getSettingManager().getDefaultBroswerEnable();
        // if (defaultBroswerEnable) {
        // intent.setData(Uri.parse(Utils.formatInternalBrowserUrlWithParams(url, true, false, false)));
        // } else {
        // intent.setData(Uri.parse(Utils.formatExternalBrowserUrlWithSid(url)));
        // }
        return intent;
    }

    public int getAction() {
        return action;
    }
}

