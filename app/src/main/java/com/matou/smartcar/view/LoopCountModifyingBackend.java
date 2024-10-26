package com.matou.smartcar.view;


import androidx.annotation.Nullable;

import com.facebook.fresco.animation.backend.AnimationBackend;
import com.facebook.fresco.animation.backend.AnimationBackendDelegate;

public class LoopCountModifyingBackend extends AnimationBackendDelegate {
    private int mLoopCount;
    public LoopCountModifyingBackend(@Nullable AnimationBackend animationBackend, int loopCount) {
        super(animationBackend);
        mLoopCount = loopCount;
    }

    @Override
    public int getLoopCount() {
        return mLoopCount;
    }
}