package com.example.gitlooker.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Created by grzesiek on 2015-12-28.
 */
public class BaseFragment extends Fragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    super.onCreateOptionsMenu(menu, inflater);


  }
}
