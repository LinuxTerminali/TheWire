package in.thewire.linuxterminal;

import android.content.Context;
import android.content.Intent;
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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchHolder> {

    private final Context mContext;
    private final List<WireParser.Entry> entries;
    String id = "";

    public SearchAdapter(Context mContext, List<WireParser.Entry> entries) {
        this.mContext = mContext;
        this.entries = entries;
    }

    @Override
    public SearchAdapter.SearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SearchHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v1 = inflater.inflate(R.layout.searchlayout, parent, false);
        viewHolder = new SearchHolder(v1);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchHolder holder, int position) {
        final WireParser.Entry contact = entries.get(position);
        //indicator.setViewPager(mViewPager);
        holder.getTitle().setText(contact.getTitle());
        SimpleDateFormat inFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
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
        holder.getAuthor().setText(authorPub);
        holder.getThumbnail().setOnClickListener(new View.OnClickListener() {
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
        holder.setIsRecyclable(false);
        Picasso.with(mContext).load(contact.getImage())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .resize(270, 170)
                .into(holder.getThumbnail());

    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    private Context getContext() {
        return mContext;
    }

    public class SearchHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView thumbnail;
        public TextView author;

        public SearchHolder(View itemView) {

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
