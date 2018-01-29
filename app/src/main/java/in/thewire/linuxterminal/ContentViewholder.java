package in.thewire.linuxterminal;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by linux on 4:21 PM.
 */

class ContentViewholder extends RecyclerView.ViewHolder {
    private ViewPager mViewPager;

    public ContentViewholder(View v) {
        super(v);
        mViewPager = (ViewPager) v.findViewById(R.id.pager);

    }

    public ViewPager getmViewPager() {
        return mViewPager;
    }

    public void setmViewPager(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }
}
