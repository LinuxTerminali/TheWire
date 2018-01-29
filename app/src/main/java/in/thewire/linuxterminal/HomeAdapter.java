package in.thewire.linuxterminal;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by linux on 4:21 PM.
 */

class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int random = 0, featured = 1, topnews = 2, sectiondivide = 3, progressview = 4;
    private CustomPagerAdapter mCustomPagerAdapter;
    private final int delay = 8000; //milliseconds

    private int page = 0;
    private Handler handler;
    private final List<WireParser.Entry> mContacts;
    private final List<WireParser.Entry> sliderEns;
    // Store the context for easy access
    private final Context mContext;
    private boolean triggerd = true;
    public HomeAdapter(Context context, List<WireParser.Entry> contacts, List<WireParser.Entry> sliderEn) {
        mContacts = contacts;
        mContext = context;
        sliderEns = sliderEn;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public int getItemViewType(int position) {
        final WireParser.Entry results = mContacts.get(position);
       /* if (results.getType().equals("belowslider")){
            return featured;
        }else if(results.getType().equals("two_way")){
            return random;
        }else if(results.getType().equals("slider")){
            return topnews;
        }*/

        if (position < sliderEns.size() - 4) {
            return topnews;

        }
        if (position - sliderEns.size() < mContacts.size()) {
            return random;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        switch (viewType) {
            case featured:
                //Log.d("inside","featured");
                View v1 = inflater.inflate(R.layout.twowayintital, parent, false);
                viewHolder = new FeaturedViewholder(v1);
                break;
            case random:
                //Log.d("inside","random");

                View v2 = inflater.inflate(R.layout.randomcontent, parent, false);
                viewHolder = new ViewHolder(v2);
                break;
            case topnews:
                //Log.d("inside","topnews");

                View v3 = inflater.inflate(R.layout.topslider, parent, false);
                viewHolder = new SliderHolder(v3);
                break;
            default:
                //Log.d("inside","default");

                View v = inflater.inflate(R.layout.two_way, parent, false);
                viewHolder = new FeaturedViewholder(v);

        }


        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case featured:
                FeaturedViewholder vh1 = (FeaturedViewholder) holder;
                configureViewholder(vh1, position);
                break;
            case random:
                ViewHolder vh2 = (ViewHolder) holder;
                configureViewholder2(vh2, position);
                break;
            case topnews:
                SliderHolder vh3 = (SliderHolder) holder;
                configureViewholder3(vh3, position);
                break;
            default:
                FeaturedViewholder vh = (FeaturedViewholder) holder;
                configureViewholder(vh, position);

        }


        WireParser.Entry contact = mContacts.get(position);


        /*TextView textView = holder.title;
        textView.setText(contact.getTitle());
        Log.d("entries", contact.getImage()+"dsfsdfsdfsdf");
        ImageView button = holder.thumbnail;
       Picasso.with(mContext).load(contact.getImage()).into(button);*/

    }

    private void configureViewholder(FeaturedViewholder viewholder, int position) {
        WireParser.Entry contact = mContacts.get(position);
        //indicator.setViewPager(mViewPager);
        Twowayadapter twowayadapter = new Twowayadapter(mContext, sliderEns);
        viewholder.getTwowayadapter().setAdapter(twowayadapter);

        //Picasso.with(mContext).load(contact.getImage()).resize(170,170).into(viewholder.getImageView());
        //indicator.setViewPager(mViewPager);

    }

    private void configureViewholder2(ViewHolder viewholder, int position) {
        final WireParser.Entry contact = mContacts.get(position);
        //indicator.setViewPager(mViewPager);
        viewholder.getTitle().setText(contact.getTitle());
        final SimpleDateFormat inFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        Date date = null;
        try {
            date = inFormat.parse(contact.getPubdate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(date);
        SimpleDateFormat outFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String formatted = outFormat.format(date);
        System.out.println(formatted);

        String authorPub = "By " + contact.getAuthor() + " on " + formatted;
        viewholder.getAuthor().setText(authorPub);
        viewholder.getThumbnail().setOnClickListener(new View.OnClickListener() {
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
        viewholder.getTitle().setOnClickListener(new View.OnClickListener() {
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

        Picasso.with(mContext).load(contact.getImage()).resize(170, 170).into(viewholder.getThumbnail());

    }

    private void configureViewholder3(final SliderHolder viewholder, int position) {
        WireParser.Entry contact = mContacts.get(position);
        //indicator.setViewPager(mViewPager);
        if (triggerd) {

            mCustomPagerAdapter = new CustomPagerAdapter(mContext, sliderEns);
            viewholder.getmViewPager().setClipToPadding(true);
            viewholder.getmViewPager().setPadding(16, 10, 16, 0);
            viewholder.getmViewPager().setAdapter(mCustomPagerAdapter);
            viewholder.getIndicator().setViewPager(viewholder.getmViewPager());
            triggerd = false;
            Runnable runnable = new Runnable() {
                public void run() {
                    if (mCustomPagerAdapter.getCount() == page) {
                        page = 0;
                    } else {
                        page++;
                    }
                    viewholder.getmViewPager().setCurrentItem(page, true);
                    handler = new Handler();
                    handler.postDelayed(this, delay);
                }
            };
            runnable.run();

        }


    }

    public void clear() {
        mContacts.clear();
        sliderEns.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView thumbnail;
        public TextView author;

        public ViewHolder(View itemView) {

            super(itemView);
            author = (TextView) itemView.findViewById(R.id.author);
            title = (TextView) itemView.findViewById(R.id.title);
            thumbnail = (ImageView) itemView.findViewById(R.id.image);
        }

        public TextView getAuthor() {
            return author;
        }

        public void setAuthor(TextView author) {
            this.author = author;
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public ImageView getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(ImageView thumbnail) {
            this.thumbnail = thumbnail;
        }
    }
}
