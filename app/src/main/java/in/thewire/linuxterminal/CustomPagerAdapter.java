package in.thewire.linuxterminal;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by linux on 4:21 PM.
 */

class CustomPagerAdapter extends PagerAdapter {
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final List<WireParser.Entry> content;


    public CustomPagerAdapter(Context context, List<WireParser.Entry> content) {
        mContext = context;
        this.content = content;

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final WireParser.Entry contact = content.get(position);
        View itemView = mLayoutInflater.inflate(R.layout.sliderlayout, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
        TextView textView = (TextView) itemView.findViewById(R.id.title);
        textView.setText(contact.getTitle());
        Picasso.with(mContext).load(contact.getImage()).into(imageView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailNews.class);
                intent.putExtra("content", contact.getSummary());
                intent.putExtra("title", contact.getTitle());
                intent.putExtra("pubdate", contact.getPubdate());
                intent.putExtra("author", contact.getAuthor());
                intent.putExtra("link", contact.getLink());

                mContext.startActivity(intent);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailNews.class);
                intent.putExtra("content", contact.getSummary());
                intent.putExtra("title", contact.getTitle());
                intent.putExtra("pubdate", contact.getPubdate());
                intent.putExtra("author", contact.getAuthor());
                intent.putExtra("link", contact.getLink());

                mContext.startActivity(intent);
            }
        });
        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext,Series_detail.class);
                Bundle extras = new Bundle();
                extras.putString("playlist",playid.get(position));
                extras.putString("name",title.get(position));
                extras.putString("trailer",trailer.get(position));
                extras.putString("second_id",id2.get(position));
                i.putExtras(extras);
                //Toast.makeText(getActivity().this,"Clicked at"+type,Toast.LENGTH_SHORT).show();
                mContext.startActivity(i);

            }
        });*/

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
