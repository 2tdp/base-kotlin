package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom.realtimeblur;

import android.content.Context;
import android.graphics.Bitmap;

/* loaded from: classes3.dex */
public class EmptyBlurImpl implements BlurImpl {
    @Override
    public void blur(Bitmap bitmap, Bitmap bitmap2) {
    }

    @Override
    public boolean prepare(Context context, Bitmap bitmap, float f) {
        return false;
    }

    @Override
    public void release() {
    }
}
