package in.thewire.linuxterminal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by linux on 4:21 PM.
 */

class Twowayadapter extends BaseAdapter {
    private final Context mContext;
    private final List<WireParser.Entry> list;
    /* public Integer[] mThumb = {
             R.drawable.tvf_tripling,R.drawable.sex_chat_with,
             R.drawable.baked,R.drawable.bang_baja,
             R.drawable.pitchers,R.drawable.official_chukyagiri,
             R.drawable.tanlines, R.drawable.mans_world
     };*/
    //ImageList list = new ImageList();


    public Twowayadapter(Context c, List<WireParser.Entry> list) {
        mContext = c;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WireParser.Entry conentry = list.get(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.two_way, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        TextView textView = (TextView) convertView.findViewById(R.id.title);
        textView.setText(conentry.getTitle());
        /*imageView.setImageResource(mThumb[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(100, 100));*/

        //Log.d("poster", name[position]);
        Picasso.with(mContext).load(conentry.getImage())
                .placeholder(R.drawable.women_safety_reuters)
                .resize(270, 70)
                .into(imageView);

        return convertView;
    }
}

