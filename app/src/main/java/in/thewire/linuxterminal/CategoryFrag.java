package in.thewire.linuxterminal;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CategoryFrag extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    // --Commented out by Inspection (1/6/18 2:31 PM):LinearLayout layout;
    private final String list = "Politics , Government , Communalism , Rights , Law " +
            " - Economy , Agriculture , Digital , Energy , Labour" +
            " - Science , Environment , Health , Space , Tech " +
            "- External Affairs, Indian Diplomacy , South Asia , World " +
            "- Society, Gender , Books , Cities & Architecture , Culture , Media , Religion  ";
    private RecyclerView recyclerView;
    private List<CategoryOption> options;

    public CategoryFrag() {
    }

    public static CategoryFrag newInstance() {
        CategoryFrag fragment = new CategoryFrag();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, 0);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //List<String> items = Arrays.asList(list.split("\\s*,\\s*"));
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean pref = sharedPrefs.getBoolean("summaryPref", false);

        //addbutons();
        if (pref) {
            hindisplit();
        } else {
            Splitadd();

        }
        return view;
    }


    private void Splitadd() {
        CategoryOption option;
        options = new ArrayList<>();
        String[] items = list.split("\\s*-\\s*");
        for (String it : items) {
            String[] finalSplit = it.split("\\s*,\\s");
            boolean count = true;
            for (String t : finalSplit) {
                if (count) {
                    option = new CategoryOption(t, "Header");
                    options.add(option);
                    //Log.d("Values",option.getCategoryname());
                    count = false;
                } else {
                    option = new CategoryOption(t, "cat");
                    options.add(option);
                    //Log.d("catvalues",option.getCategoryname());
                }

            }


        }
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), options);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
               // linearLayoutManager.getOrientation());
        //recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(categoryAdapter);

    }


    private void hindisplit() {
        CategoryOption option;
        options = new ArrayList<>();

        String hindiList = "भारत , राजनीति , समाज , विज्ञान , दुनिया";
        String[] finalSplit = hindiList.split("\\s*,\\s");
        //boolean count = true;
        for (String t : finalSplit) {

            option = new CategoryOption(t, "hindi");
            options.add(option);
            //Log.d("catvalues",option.getCategoryname());


        }
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), options);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
               // linearLayoutManager.getOrientation());
        //recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(categoryAdapter);

    }


    public class CategoryOption {

        String categoryname;
        String type;

        public CategoryOption(String categoryname, String type) {
            this.categoryname = categoryname;
            this.type = type;
        }

        public String getCategoryname() {
            return categoryname;
        }

        public void setCategoryname(String categoryname) {
            this.categoryname = categoryname;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
