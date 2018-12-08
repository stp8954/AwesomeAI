package com.awesome.app.awesomeapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.awesome.app.awesomeapp.R;
import com.awesome.app.awesomeapp.util.Data;
import com.awesome.app.awesomeapp.util.EventRecognitionService;
import com.awesome.app.awesomeapp.util.EventSelection;
import com.awesome.app.awesomeapp.util.EventStore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class EventSelectorFragment extends Fragment {

    @BindView(R.id.app_selections_recycler_view)
    RecyclerView recycler_view;
    @BindView(R.id.app_list_loading_text_view)
    TextView mLoadingView;

    List<String> registeredEvents ;
    List<String> selectedEvents;
    private EventStore mStore;
    //private EventS

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View v = inflater.inflate(R.layout.event_selector, container, false);
        recycler_view = (RecyclerView) v.findViewById(R.id.app_selections_recycler_view);

        SimpleLabelAdapter adapter = new SimpleLabelAdapter();
        recycler_view.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(layoutManager);

        return v ;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStore = EventStore.get(getActivity());
        registeredEvents = mStore.getRegisteredEvents();
        selectedEvents = mStore.getSelectedEvents();

        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Event Selector");
    }


    /**
     * A Simple Adapter for the RecyclerView
     */
    public class SimpleLabelAdapter extends RecyclerView.Adapter<SimpleViewHolder> {
        public SimpleLabelAdapter(){
            //dataSource = dataArgs;
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_select_list_item , parent, false);
            SimpleViewHolder viewHolder = new SimpleViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            ((SimpleViewHolder) holder).bindView(position);
        }

        @Override
        public int getItemCount() {
            return registeredEvents.size();
        }
    }

    /**
     * A Simple ViewHolder for the RecyclerView
     */
    public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textView;
        public CheckBox chBox;
        private int position;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.labelNameTextBox);
            chBox = (CheckBox) itemView.findViewById(R.id.labelelectCheckBox);
            chBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    EventSelection sel = new EventSelection(Data.labelMap.get(position), isChecked);
                    mStore.updateEventSelection(sel);
                    EventRecognitionService.onEventSelectionUpdated(getActivity());
                }
            });
            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {


        }

        public void bindView(int position)
        {
            this.position = position;
            textView.setText( Data.labelMap.get(position));
            if(selectedEvents.contains(registeredEvents.get(position)))
            {
                chBox.setChecked(true);
            }
        }
    }

}
