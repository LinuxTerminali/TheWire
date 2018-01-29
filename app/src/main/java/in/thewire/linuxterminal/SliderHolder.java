package in.thewire.linuxterminal;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by linux on 4:21 PM.
 */

class SliderHolder extends RecyclerView.ViewHolder {

    private ViewPager mViewPager;
    private CircleIndicator indicator;

    public SliderHolder(View itemView) {
        super(itemView);
        mViewPager = (ViewPager) itemView.findViewById(R.id.pager);
        indicator = (CircleIndicator) itemView.findViewById(R.id.indicator);
    }

    public CircleIndicator getIndicator() {
        return indicator;
    }

    public void setIndicator(CircleIndicator indicator) {
        this.indicator = indicator;
    }

    public ViewPager getmViewPager() {
        return mViewPager;
    }

    public void setmViewPager(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
    }
}
