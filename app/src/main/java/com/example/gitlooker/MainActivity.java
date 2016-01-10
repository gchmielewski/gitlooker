package com.example.gitlooker;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.gitlooker.model.Repo;
import com.example.gitlooker.service.GitHubService;
import com.example.gitlooker.utils.Authorization;
import com.example.gitlooker.view.LoginFragment;
import com.example.gitlooker.view.RepoFragment;
import com.example.gitlooker.view.ReposFragment;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity implements ReposFragment.OnListFragmentInteractionListener {

    private static final String LOGIN_TAG = "LoginFragment";
    private static final String REPO_LIST = "ReposFragment";
    private static final String REPO_DETAIL = "RepoFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //if (BuildConfig.DEBUG) {
        //  Utils.enableStrictMode();
        //}

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);

        if (savedInstanceState == null) {
            if (getSupportFragmentManager().findFragmentByTag(LOGIN_TAG) == null) {

                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, new LoginFragment(), LOGIN_TAG);
                ft.commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void loginClick(View v) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(LOGIN_TAG);
        if (currentFragment != null && currentFragment instanceof LoginFragment) {

            EditText edtLogin = (EditText) findViewById(R.id.edtLogin);
            EditText edtPassword = (EditText) findViewById(R.id.edtPassword);

            if (edtLogin != null && edtPassword != null) {
                final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Checking", true, false);

                Authorization.getInstance().setAuthorization(edtLogin.getText().toString(), edtPassword.getText().toString());
                GitHubService service = Authorization.getInstance().getGitService();
                Call c = service.isStarred("gchmielewski", "gitlooker");
                c.enqueue(new Callback() {

                    @Override public void onResponse(Response response, Retrofit retrofit) {
                        progressDialog.dismiss();

                        if (response.code() != 401) {
                            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.fragment_container, new ReposFragment(), REPO_LIST);
                            ft.commit();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Login error", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override public void onFailure(Throwable t) {
                        progressDialog.dismiss();
                    }
                });
            }
            else {
                Toast.makeText(this, "Login and password can't be empty", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void starClick(View v) {
        final Repo r = (Repo)v.getTag();
        final View view = v;
        Call c;

        GitHubService service = Authorization.getInstance().getGitService();
        c = r.Starred? service.unstarRepo(r.owner.login, r.name): service.starRepo(r.owner.login, r.name);

        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", r.Starred? "Unstaring": "Staring", true, false);

        c.enqueue(new Callback() {

            @Override public void onResponse(Response response, Retrofit retrofit) {
                progressDialog.dismiss();
                Log.i("Info star", response.message());

                if (response.code() == 401) {
                    Toast.makeText(MainActivity.this, "Authorization error", Toast.LENGTH_LONG).show();
                }
                else {
                    r.Starred = !r.Starred;
                    ((ImageButton)view).setImageResource(r.Starred? android.R.drawable.star_on: android.R.drawable.star_off);
                }
            }

            @Override public void onFailure(Throwable t) {
                progressDialog.dismiss();
            }


        });

    }

    public void watcherClick(View v) {
        final Repo r = (Repo)v.getTag();
        final View view = v;
        Call c;

        GitHubService service = Authorization.getInstance().getGitService();
        c = r.Watched? service.unwatchRepo(r.owner.login, r.name): service.watchRepo(r.owner.login, r.name);

        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", r.Watched? "Unwatching": "Watching", true, false);

        c.enqueue(new Callback() {

            @Override public void onResponse(Response response, Retrofit retrofit) {
                progressDialog.dismiss();

                Log.i("Info watch", response.message());
                if (response.code() == 401) {
                    Toast.makeText(MainActivity.this, "Authorization error", Toast.LENGTH_LONG).show();
                }
                else {
                    r.Watched = !r.Watched;
                    ((ImageButton)view).setImageResource(r.Watched? android.R.drawable.checkbox_on_background: android.R.drawable.checkbox_off_background);
                }
            }

            @Override public void onFailure(Throwable t) {
                progressDialog.dismiss();
            }
        });


    }

/*  private class CheckLogin extends AsyncTask<Void, Void, Boolean> {
    private ProgressDialog progressDialog;
    private String mUserName;
    private String mPassword;

    public CheckLogin(String userName, String password) {
      mUserName = userName;
      mPassword = password;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Checking", true, false);
    }

    @Override
    protected void onPostExecute(Boolean b) {
      super.onPostExecute(b);
      progressDialog.dismiss();

      final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
      ft.replace(R.id.fragment_container, new ReposFragment(), REPO_LIST);
      ft.commit();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
      try {
        return Authorization.getInstance().setAuthorization(mUserName, mPassword);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      return false;
    }
  }*/

    @Override
    public void onListFragmentInteraction(Repo item) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(getSupportFragmentManager().getFragments().get(0));
        RepoFragment rf = new RepoFragment();
        rf.setRepo(item);
        ft.add(R.id.fragment_container, rf, REPO_DETAIL);
        //replace(R.id.fragment_container, new RepoFragment());
        ft.addToBackStack(null);
        ft.commit();
    }
}
