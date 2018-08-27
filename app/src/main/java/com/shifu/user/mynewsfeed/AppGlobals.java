package com.shifu.user.mynewsfeed;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;

class AppGlobals {

    static Drawable stylish(int resource, Resources resources) {
        Drawable icon = resources.getDrawable(resource);
        icon.setColorFilter(new PorterDuffColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP));
        return icon;
    }

}
