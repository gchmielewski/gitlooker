package com.example.gitlooker;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  public void onClick(View v) {

    final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(R.id.fragment_container, new RepositoryListFragment());
    ft.commit();
  }
}
