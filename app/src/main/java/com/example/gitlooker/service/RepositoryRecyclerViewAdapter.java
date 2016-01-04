package com.example.gitlooker.service;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gitlooker.R;
import com.example.gitlooker.view.ReposFragment.OnListFragmentInteractionListener;

import com.example.gitlooker.model.Repo;
import java.util.List;

public class RepositoryRecyclerViewAdapter extends RecyclerView.Adapter<RepositoryRecyclerViewAdapter.ViewHolder> {

  private final List<Repo> mValues;
  private final OnListFragmentInteractionListener mListener;

  public RepositoryRecyclerViewAdapter(List<Repo> items, OnListFragmentInteractionListener listener) {
    mValues = items;
    mListener = listener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_repos, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.mItem = mValues.get(position);
    holder.mIdView.setText(mValues.get(position).name);
    holder.mContentView.setText(mValues.get(position).description);
    holder.mWatchView.setText("Stars: " + String.valueOf(mValues.get(position).stargazers_count) + " Watchers: " + String.valueOf(mValues.get(position).watchers_count));

    holder.mView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != mListener) {
          // Notify the active callbacks interface (the activity, if the
          // fragment is attached to one) that an item has been selected.
          mListener.onListFragmentInteraction(holder.mItem);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mValues.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;
    public final TextView mWatchView;
    public Repo mItem;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mIdView = (TextView) view.findViewById(R.id.name);
      mContentView = (TextView) view.findViewById(R.id.description);
      mWatchView = (TextView) view.findViewById(R.id.watch);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }
  }
}
