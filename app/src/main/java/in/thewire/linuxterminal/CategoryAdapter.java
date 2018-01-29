package in.thewire.linuxterminal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by linux on 4:21 PM.
 */

class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final private int header = 1;
    final private int cat = 0;
    final private int hindi = 2;
    // --Commented out by Inspection (1/6/18 2:30 PM):List<String> broadCategory;
    private final List<CategoryFrag.CategoryOption> category;
    private final Context mContext;


    public CategoryAdapter(Context context, List<CategoryFrag.CategoryOption> broadCategory) {
        this.mContext = context;
        this.category = broadCategory;
    }

    @Override
    public int getItemViewType(int position) {
        CategoryFrag.CategoryOption categoryOption = category.get(position);
        if (categoryOption.getType().equals("cat")) {
            return cat;
        }
        if (categoryOption.getType().equals("Header")) {
            return header;
        }
        if (categoryOption.getType().equals("hindi")) {
            return hindi;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case cat:
                View v3 = inflater.inflate(R.layout.categoryadapter, parent, false);
                viewHolder = new Categoryholder(v3);
                break;
            case header:
                View v4 = inflater.inflate(R.layout.headerholder, parent, false);
                viewHolder = new HeaderHolder(v4);
                break;
            case hindi:
                View v6 = inflater.inflate(R.layout.hindiholder, parent, false);
                viewHolder = new HeaderHolder(v6);
                break;
            default:
                View v5 = inflater.inflate(R.layout.categoryadapter, parent, false);
                viewHolder = new Categoryholder(v5);
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case cat:
                Categoryholder vh1 = (Categoryholder) holder;
                configureViewholder(vh1, position);
                break;
            case header:
                HeaderHolder vh2 = (HeaderHolder) holder;
                configureViewholder2(vh2, position);
                break;
            case hindi:
                HeaderHolder vh3 = (HeaderHolder) holder;
                configureViewholder3(vh3, position);
                break;
            default:
                Categoryholder vh = (Categoryholder) holder;
                configureViewholder(vh, position);

        }
    }

    private void configureViewholder(final Categoryholder categoryholder, int position) {
        final CategoryFrag.CategoryOption categoryOption = category.get(position);
        categoryholder.getTitle().setText(categoryOption.getCategoryname());
        categoryholder.getTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, IndividualActivity.class);
                String urltext = categoryOption.getCategoryname();

                intent.putExtra("category", urltext);
                mContext.startActivity(intent);
            }
        });
        //categoryholder.getTitle().setBackgroundResource(R.drawable.button_shape);
    }

    private void configureViewholder2(HeaderHolder headerHolder, int position) {
        final CategoryFrag.CategoryOption categoryOption = category.get(position);
        headerHolder.getTitle().setText(categoryOption.getCategoryname());
        headerHolder.getTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, IndividualActivity.class);
                String urltext = categoryOption.getCategoryname();

                intent.putExtra("category", urltext);
                mContext.startActivity(intent);

            }
        });
        //headerHolder.getTitle().setBackgroundResource(R.drawable.square_shape);
    }

    private void configureViewholder3(HeaderHolder headerHolder, int position) {
        final CategoryFrag.CategoryOption categoryOption = category.get(position);
        headerHolder.getTitle().setText(categoryOption.getCategoryname());
        headerHolder.getTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, IndividualActivity.class);
                String urltext = categoryOption.getCategoryname();

                intent.putExtra("category", urltext);
                mContext.startActivity(intent);

            }
        });
        //headerHolder.getTitle().setBackgroundResource(R.drawable.square_shape);
    }

    @Override
    public int getItemCount() {
        return category.size();
    }

    public class Categoryholder extends RecyclerView.ViewHolder {

        public TextView title;


        public Categoryholder(View itemView) {

            super(itemView);
            title = (TextView) itemView.findViewById(R.id.broadcategory);
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {

        public TextView title;


        public HeaderHolder(View itemView) {

            super(itemView);
            title = (TextView) itemView.findViewById(R.id.broadcategory);
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }
    }
}
