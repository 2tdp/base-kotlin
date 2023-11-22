package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.viewcustom.realtimeblur;

import android.content.Context;
import android.graphics.Bitmap;

/* loaded from: classes3.dex */
interface BlurImpl {
    void blur(Bitmap bitmap, Bitmap bitmap2);

    boolean prepare(Context context, Bitmap bitmap, float f);

    void release();
}
