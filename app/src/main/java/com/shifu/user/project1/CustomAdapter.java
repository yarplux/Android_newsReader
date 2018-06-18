package com.shifu.user.project1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    public List<String> cartList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public RelativeLayout viewBackground, viewForeground;

        public ViewHolder(View v) {
            super(v);

            textView = (TextView) v.findViewById(R.id.content);
            viewForeground = v.findViewById(R.id.view_foreground);

            viewForeground.setClickable(true);
            viewForeground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        final View v = view;
                        FragmentActivity activity = (FragmentActivity) v.getContext();
                        Fragment frag2 = new NewFragment();
                        FragmentTransaction fTrans = activity.getSupportFragmentManager().beginTransaction();
                        fTrans.replace(R.id.container, frag2);
                        fTrans.addToBackStack(null);
                        fTrans.commit();
                    }

            });

            viewBackground = v.findViewById(R.id.view_background);

        }

        public TextView getTextView() {
            return textView;
        }
    }

    public CustomAdapter(List<String> dataSet) {
        this.cartList = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_item, viewGroup, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTextView().setText(cartList.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void restoreString(String string, int position) {
        cartList.add(position, string);
        // notify item added by position
        notifyItemInserted(position);
    }

}
