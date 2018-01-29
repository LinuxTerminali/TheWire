package in.thewire.linuxterminal;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by linux on 4:21 PM.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

    private final Context mContext;
    private final List<WireParser.Entry> entries;
    private String id = "";

    public VideoAdapter(Context mContext, List<WireParser.Entry> entries) {
        this.mContext = mContext;
        this.entries = entries;
    }

    @Override
    public VideoAdapter.VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VideoHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v1 = inflater.inflate(R.layout.videolayout, parent, false);
        viewHolder = new VideoHolder(v1);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {
        WireParser.Entry entry = entries.get(position);
        Matcher matcher = Pattern.compile("<iframe[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>").matcher(entry.getSummary());
        matcher.find();
        String src = matcher.group(1);
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        matcher = compiledPattern.matcher(src);

        if (matcher.find()) {
            id = matcher.group();
        }
        String thubmailurl = "https://img.youtube.com/vi/" + id + "/maxresdefault.jpg";
        TextView textView = holder.title;
        textView.setText(entry.getTitle());
        ImageView button = holder.thumbnail;
        Picasso.with(mContext).load(thubmailurl).placeholder(R.drawable.button_shape).resize(400, 400).into(button);
        holder.videolinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoId = id;
                PackageManager pm = mContext.getPackageManager();
                boolean isInstalled = isPackageInstalled("com.google.android.youtube", pm);
                if (isInstalled) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                    intent.putExtra("VIDEO_ID", videoId);
                    intent.putExtra("force_fullscreen", true);

                    mContext.startActivity(intent);

                } else if (isPackageInstalled("com.android.chrome", pm)) {
                    String urll = "https://www.youtube.com/watch?time_continue=0&v=" + videoId;
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();

                    customTabsIntent.intent.setPackage("com.android.chrome");
                    customTabsIntent.launchUrl(mContext, Uri.parse(urll));

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    private Context getContext() {
        return mContext;
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public class VideoHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView thumbnail;
        public LinearLayout videolinear;

        public VideoHolder(View itemView) {

            super(itemView);

            title = (TextView) itemView.findViewById(R.id.videotitle);
            thumbnail = (ImageView) itemView.findViewById(R.id.videoimage);
            videolinear = (LinearLayout) itemView.findViewById(R.id.videolinear);
        }

        public LinearLayout getVideolinear() {
            return videolinear;
        }

        public void setVideolinear(LinearLayout videolinear) {
            this.videolinear = videolinear;
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
