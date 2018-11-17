package com.teambrella.android.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * Akkurat Regular Edit Text
 */
public class AkkuratRegularEditText extends AppCompatEditText {
  public AkkuratRegularEditText(Context context) {
    super(context);
    init();
  }

  public AkkuratRegularEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public AkkuratRegularEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/AkkuratPro-Regular.otf"));
  }
}
