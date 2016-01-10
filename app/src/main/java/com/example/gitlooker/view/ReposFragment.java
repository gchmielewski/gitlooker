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
import com.example.gitlooker.model.SearchRepo;
import com.example.gitlooker.service.RepoContent;
import com.example.gitlooker.service.RepositoryRecyclerViewAdapter;
import com.example.gitlooker.model.Repo;
import com.example.gitlooker.service.GitHubService;
import com.example.gitlooker.service.GitLookerDataModule;
import com.example.gitlooker.utils.Authorization;
import com.example.gitlooker.utils.SearchList;
import com.example.gitlooker.utils.Utils;
import java.io.InputStream;
import java.util.List;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
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

    private String mLastSearchText = null;
    private Integer mLastSearchType = null;

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    public ReposFragment() {
    }

    @SuppressWarnings("unused")
    public static ReposFragment newInstance(int columnCount) {
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

        mAwatar = (ImageView) view.findViewById(R.id.icon);
        mRepoList = (RecyclerView) view.findViewById(R.id.list);
        mProgress = (ProgressBar) view.findViewById(R.id.marker_progress);
        mUserName = (TextView) view.findViewById(R.id.firstLine);
        mSessionInfo = (TextView) view.findViewById(R.id.session_info);

        if (mRepoList != null) {
            Context context = view.getContext();

            if (mColumnCount <= 1) {
                mRepoList.setLayoutManager(new LinearLayoutManager(context));
            }
            else {
                mRepoList.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            mRepoList.setAdapter(new RepositoryRecyclerViewAdapter(RepoContent.ITEMS, mListener));
        }

        // get repository list
        getOwnerRepositories(Authorization.getInstance().getLoginName());

        return view;
    }

    private void getOwnerRepositories(final String repoOwner) {
        mLastSearchText = repoOwner;
        mLastSearchType = Utils.OWNER_SEARCH;

        RepoContent.clearRepos();

        mUserName.setText("User's repositories : " + repoOwner);

        mProgress.setVisibility(View.VISIBLE);
        mSessionInfo.setVisibility(View.INVISIBLE);

        final GitLookerDataModule dm = new GitLookerDataModule(getActivity());

        if (Utils.haveNetworkConnection(getActivity())) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(Authorization.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

            final GitHubService service = retrofit.create(GitHubService.class);

            Call<List<Repo>> repos = service.listRepos(repoOwner);
            repos.enqueue(new Callback<List<Repo>>() {

                @Override
                public void onResponse(retrofit.Response<List<Repo>> response, Retrofit retrofit) {

                    int i = 1;

                    if (response.body() != null) {
                        for (Repo r : response.body()) {
                            RepoContent.addRepo(r);
                            if (i++ == 1) {
                                new DownloadImageTask(mAwatar).execute(r.owner.avatar_url); // load awatar
                            }
                        }

                        mRepoList.getAdapter().notifyDataSetChanged();
                        mProgress.setVisibility(View.GONE);

                        String link = response.headers().get("Link");
                        if (isNextLink(link)) {
                            getOwnerRepositoriesNext(link, dm, repoOwner);
                        }
                        else {
                            dm.saveRepos(RepoContent.ITEMS, Utils.OWNER_SEARCH, repoOwner);
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getActivity(), "Failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            getDataFromSQLLite(Utils.OWNER_SEARCH, repoOwner); //offline - database
        }
    }

    private void getOwnerRepositoriesNext(String link, final GitLookerDataModule dm, final String searchText) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Authorization.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        final GitHubService service = retrofit.create(GitHubService.class);
        int index = link.indexOf("rel=\"next\"");
        String nextLink;

        if (index != -1) {
            nextLink = link.substring(index + 13);
            nextLink = nextLink.substring(0, nextLink.indexOf(">"));
        }
        else {
            return;
        }

        Call<List<Repo>> nextRepos = service.listReposPaginate(nextLink);
        nextRepos.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Response<List<Repo>> response, Retrofit retrofit) {
                for (Repo r : response.body()) {
                    RepoContent.addRepo(r);
                }

                mRepoList.getAdapter().notifyDataSetChanged();

                String link = response.headers().get("Link");
                if (isNextLink(link)) {
                    getOwnerRepositoriesNext(link, dm, searchText);
                }
                else {
                    dm.saveRepos(RepoContent.ITEMS, Utils.OWNER_SEARCH, searchText);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    private void getSearchedRepositories(final String searchWord) {
        RepoContent.clearRepos();
        mUserName.setText("Repositories : " + searchWord);
        mAwatar.setImageResource(android.R.drawable.ic_dialog_info);
        mLastSearchText = searchWord;
        mLastSearchType = Utils.REPO_SEARCH;

        mProgress.setVisibility(View.VISIBLE);
        mSessionInfo.setVisibility(View.INVISIBLE);

        final GitLookerDataModule dm = new GitLookerDataModule(getActivity());

        if (Utils.haveNetworkConnection(getActivity())) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(Authorization.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

            final GitHubService service = retrofit.create(GitHubService.class);

            Call<SearchRepo> repos = service.listSearchRepo(searchWord);
            repos.enqueue(new Callback<SearchRepo>() {

                @Override
                public void onResponse(retrofit.Response<SearchRepo> response, Retrofit retrofit) {

                    int i = 1;

                    if (response.body() != null) {
                        for (Repo r : response.body().items) {
                            r.name = r.full_name;
                            RepoContent.addRepo(r);
                        }

                        mRepoList.getAdapter().notifyDataSetChanged();
                        mProgress.setVisibility(View.GONE);

                        String link = response.headers().get("Link");
                        if (isNextLink(link)) {
                            getSearchedRepositoriesNext(link, dm, searchWord);
                        }
                        else {
                            dm.saveRepos(RepoContent.ITEMS, Utils.REPO_SEARCH, searchWord);
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getActivity(), "Failed " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            getDataFromSQLLite(Utils.REPO_SEARCH, searchWord); //offline - database
        }

    }

    private void getSearchedRepositoriesNext(String link, final GitLookerDataModule dm, final String searchWord) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Authorization.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        final GitHubService service = retrofit.create(GitHubService.class);
        int index = link.indexOf("rel=\"next\"");
        String nextLink;

        if (index != -1) {
            nextLink = link.substring(index + 13);
            nextLink = nextLink.substring(0, nextLink.indexOf(">"));
        }
        else {
            return;
        }

        Call<SearchRepo> nextRepos = service.listSearchRepoNext(nextLink);
        nextRepos.enqueue(new Callback<SearchRepo>() {
            @Override
            public void onResponse(Response<SearchRepo> response, Retrofit retrofit) {
                for (Repo r : response.body().items) {
                    r.name = r.full_name;
                    RepoContent.addRepo(r);
                }

                mRepoList.getAdapter().notifyDataSetChanged();

                String link = response.headers().get("Link");
                if (isNextLink(link)) {
                    getSearchedRepositoriesNext(link, dm, searchWord);
                }
                else {
                    dm.saveRepos(RepoContent.ITEMS, Utils.REPO_SEARCH, searchWord);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });

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
            switch (mLastSearchType) {
                case Utils.REPO_SEARCH:
                    getSearchedRepositories(mLastSearchText);
                    break;

                case Utils.OWNER_SEARCH:
                    getOwnerRepositories(mLastSearchText);
                    break;
            }

            return true;
        }

        if (id == R.id.action_search) {
            search();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void search() {
        Utils.showInput(mLastSearchText, mLastSearchType, getActivity(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String searchString = Utils.getUserText();

                RepoContent.clearRepos();

                switch (Utils.getSearchType()) {
                    case Utils.REPO_SEARCH:
                        getSearchedRepositories(searchString);
                        break;

                    case Utils.OWNER_SEARCH:
                        getOwnerRepositories(searchString);
                        break;
                }

                SearchList.getInstance().add(searchString);
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

    private void getDataFromSQLLite(int type, String searchText) {

        GitLookerDataModule dm = new GitLookerDataModule(getActivity());

        RepoContent.addRepoList(dm.getRepos(type, searchText));

        if (RepoContent.ITEMS.size() > 0) {
            mSessionInfo.setText(dm.getSessionInfo(RepoContent.ITEMS.get(0).search_id));
        }
        else {
            mSessionInfo.setText("No session");
        }

        mProgress.setVisibility(View.GONE);
        mSessionInfo.setVisibility(View.VISIBLE);
        mRepoList.getAdapter().notifyDataSetChanged();
    }

    private boolean isNextLink(String link) {
        return link != null && link.contains("rel=\"next\"");
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
            } catch (Exception e) {
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
