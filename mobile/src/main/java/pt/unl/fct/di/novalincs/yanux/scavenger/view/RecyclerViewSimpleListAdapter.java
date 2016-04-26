package pt.unl.fct.di.novalincs.yanux.scavenger.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pt.unl.fct.di.novalincs.yanux.scavenger.R;

public class RecyclerViewSimpleListAdapter<E> extends RecyclerView.Adapter<RecyclerViewSimpleListAdapter.ViewHolder> {
    private List<E> dataSet;

    // Provide a suitable constructor (depends on the kind of dataSet)
    public RecyclerViewSimpleListAdapter(List<E> dataset) {
        this.dataSet = dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewSimpleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_simple_text, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataSet at this position
        // - replace the contents of the view with that element
        holder.textView.setText(dataSet.get(position).toString());

    }

    // Return the size of your dataSet (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public List<E> getDataSet() {
        return dataSet;
    }

    public void setDataSet(List<E> dataSet) {
        this.dataSet = dataSet;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.recycler_view_simple_text_view);
        }
    }
}