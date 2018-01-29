package in.thewire.linuxterminal;


import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.lucasr.twowayview.TwoWayView;

/**
 * Created by linux on 4:21 PM.
 */

class FeaturedViewholder extends RecyclerView.ViewHolder {
        /*ImageView imageView;
        TextView textView;

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }*/

    private TwoWayView twowayadapter;

    public FeaturedViewholder(View v) {
        super(v);
        twowayadapter = (TwoWayView) v.findViewById(R.id.lvItems);

    }

    public TwoWayView getTwowayadapter() {
        return twowayadapter;
    }

    public void setTwowayadapter(TwoWayView twowayadapter) {
        this.twowayadapter = twowayadapter;
    }
}
