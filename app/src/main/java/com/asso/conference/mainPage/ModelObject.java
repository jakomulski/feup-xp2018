package com.asso.conference.mainPage;

import com.asso.conference.R;

public enum ModelObject {
    RED(R.string.schedule, R.layout.view_home),
    SCHEDULE(R.string.schedule, R.layout.view_schedule),
    GREEN(R.string.schedule, R.layout.view_home);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }
}
