package com.example.gitlooker;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "ImageGridActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    //if (BuildConfig.DEBUG) {
    //  Utils.enableStrictMode();
    //}

    super.onCreate(savedInstanceState);

    setContentView(R.layout.main_activity);

    //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
    //setSupportActionBar(myToolbar);

    if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {

      final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.fragment_container, new LoginFragment(), TAG);
      ft.commit();
    }

  }



  public void onClick(View v) {

    final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.fragment_container, new RepositoryListFragment());
    ft.commit();
  }
}
