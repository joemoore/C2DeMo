package com.pivot13.test.support;

import com.pivot13.util.CurrentTime;

public class FakeCurrentTime extends CurrentTime {
    private long fakeTime;

    @Override
    public long currentTimeMillis() {
        return fakeTime;
    }

    public void setCurrentTime(long time) {
        fakeTime = time;
    }
}
