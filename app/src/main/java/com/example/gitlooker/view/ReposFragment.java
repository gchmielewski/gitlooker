package com.example.gitlooker.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.gitlooker.R;
import com.example.gitlooker.service.RepoContent;
import com.example.gitlooker.service.RepositoryRecyclerViewAdapter;
import com.example.gitlooker.model.Repo;
import com.example.gitlooker.service.GitHubService;
import com.example.gitlooker.service.GitLookerDataModule;
import com.example.gitlooker.utils.Authorization;
import com.example.gitlooker.utils.Utils;
import java.io.InputStream;
import java.util.List;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * A fragment representing a list of Items.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ReposFragment extends BaseFragment {

  private static final String ARG_COLUMN_COUNT = "column-count";


  private ImageView mAwatar = null;
  private RecyclerView mRepoList = null;
  private ProgressBar mProgress = null;
  private TextView mUserName = null;
  private TextView mSessionInfo = null;
  private String mLastUser = null;

  private int mColumnCount = 1;
  private OnListFragmentInteractionListener mListener;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public ReposFragment() {
  }

  // TODO: Customize parameter initialization
  @SuppressWarnings("unused") public static ReposFragment newInstance(int columnCount) {
    ReposFragment fragment = new ReposFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_COLUMN_COUNT, columnCount);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_repos_list, container, false);

    mAwatar = (ImageView)view.findViewById(R.id.icon);
    mRepoList = (RecyclerView)view.findViewById(R.id.list);
    mProgress = (ProgressBar)view.findViewById(R.id.marker_progress);
    mUserName = (TextView)view.findViewById(R.id.firstLine);
    mSessionInfo = (TextView)view.findViewById(R.id.session_info);

    if (mRepoList != null) {
      Context context = view.getContext();

      if (mColumnCount <= 1) {
        mRepoList.setLayoutManager(new LinearLayoutManager(context));
      }
      else {
        mRepoList.setLayoutManager(new GridLayoutManager(context, mColumnCount));
      }

      RepoContent.clearRepos();
      mRepoList.setAdapter(new RepositoryRecyclerViewAdapter(RepoContent.ITEMS, mListener));

      mUserName.setText(Authorization.getInstance().getLoginName());
      // get repository list
      getData(Authorization.getInstance().getLoginName());
    }

    return view;
  }

  private void getData(String repoOwner) {
    mLastUser = repoOwner;

    RepoContent.clearRepos();

    mProgress.setVisibility(View.VISIBLE);
    mSessionInfo.setVisibility(View.INVISIBLE);
    mUserName.setText(repoOwner);

    if (Utils.haveNetworkConnection(getActivity())) {
      getDataFromGitHub(); // online
    }
    else {
      getDataFromSQLLite(); //offline - database
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.repos_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_refresh) {
      getData(mLastUser);
      return true;
    }

    if (id == R.id.action_search) {
      search();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void search() {
    Utils.showInput("Type repo's owner", getActivity(), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {

        getData(Utils.userInput.getText().toString());
      }
    });

  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    if (context instanceof OnListFragmentInteractionListener) {
      mListener = (OnListFragmentInteractionListener) context;
    }
    else {
      throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  /**
   * This interface must be implemented by activities that contain this fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that activity.
   * See the Android Training lesson <a href="http://developer.android.com/training/basics/fragments/communicating.html">
   * Communicating with Other Fragments</a> for more information.
   */
  public interface OnListFragmentInteractionListener {

    void onListFragmentInteraction(Repo item);
  }

  private void getDataFromGitHub() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(Authorization.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    final GitHubService service = retrofit.create(GitHubService.class);

    Call<List<Repo>> repos = service.listRepos(mLastUser);
    repos.enqueue(new Callback<List<Repo>>() {

      @Override
      public void onResponse(retrofit.Response<List<Repo>> response, Retrofit retrofit) {

        int i = 1;

        for (Repo r : response.body()) {
          RepoContent.addRepo(r);
          if (i++ == 1) {
            new DownloadImageTask(mAwatar).execute(r.owner.avatar_url); // load awatar
          }
        }

        GitLookerDataModule dm = new GitLookerDataModule(getActivity());
        dm.saveRepos(RepoContent.ITEMS, mLastUser);

        mRepoList.getAdapter().notifyDataSetChanged();
        mProgress.setVisibility(View.GONE);
      }

      @Override
      public void onFailure(Throwable t) {
        Toast.makeText(getActivity(), "Failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
      }
    });

    /*Interceptor interceptor = new Interceptor() {
      @Override
      public Response intercept(Chain chain) throws IOException {

        Request newRequest = chain.request().newBuilder().addHeader("User-Agent", "Retrofit-Sample-App").build();
        return chain.proceed(newRequest);
      }
    };*/

  }

  private void getDataFromSQLLite() {

    GitLookerDataModule dm = new GitLookerDataModule(getActivity());

    RepoContent.addRepoList(dm.getRepos());
    mSessionInfo.setText(dm.getSessionInfo(mLastUser));

    mProgress.setVisibility(View.GONE);
    mSessionInfo.setVisibility(View.VISIBLE);
    mRepoList.getAdapter().notifyDataSetChanged();
  }

  private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView mImage;

    public DownloadImageTask(ImageView bmImage) {
      this.mImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
      String urldisplay = urls[0];
      Bitmap bit = null;

      try {
        InputStream in = new java.net.URL(urldisplay).openStream();
        bit = BitmapFactory.decodeStream(in);
      }
      catch (Exception e) {
        Log.e("Error", e.getMessage());
        e.printStackTrace();
      }
      return bit;
    }

    protected void onPostExecute(Bitmap result) {
      mImage.setImageBitmap(result);
    }
  }

}
