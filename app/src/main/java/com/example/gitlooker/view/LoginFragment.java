package com.example.gitlooker.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.gitlooker.R;
import com.example.gitlooker.view.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment {

  public LoginFragment() {
    // Required empty public constructor
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_login, container, false);
  }
}
