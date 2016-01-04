package com.example.gitlooker.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.gitlooker.R;
import com.example.gitlooker.model.Repo;
import com.example.gitlooker.service.GitHubService;
import com.example.gitlooker.utils.Authorization;
import com.example.gitlooker.view.BaseFragment;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class RepoFragment extends BaseFragment {

    private Repo mRepo;
    private ImageButton mStarButton;
    private ImageButton mWatcherButton;

    public RepoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_repo, container, false);
        ((TextView) v.findViewById(R.id.repo_name)).setText(mRepo.name);
        ((TextView) v.findViewById(R.id.repo_description)).setText(mRepo.description);
        ((TextView) v.findViewById(R.id.repo_created)).setText("Created : " + mRepo.created_at);
        ((TextView) v.findViewById(R.id.repo_updated)).setText("Updated : " + mRepo.updated_at);

        mStarButton = (ImageButton) v.findViewById(R.id.ibStar);
        mStarButton.setTag(mRepo);

        mWatcherButton = (ImageButton) v.findViewById(R.id.ibWatchers);
        mWatcherButton.setTag(mRepo);

        setButtons();
        return v;
    }

    private void setButtons() {
        setStarred();
        setSubscribed();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.repo_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_close) {
            getFragmentManager().popBackStackImmediate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setRepo(Repo repo) {
        mRepo = repo;
    }

    private boolean setStarred() {
        GitHubService service = Authorization.getInstance().getGitService();
        retrofit.Call call = service.isStarred(mRepo.owner.login, mRepo.name);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(retrofit.Response response, Retrofit retrofit) {
                Log.i("Info is star", String.valueOf(response.code()));
                switch (response.code()) {
                    case 401: // no authorization
                        mStarButton.setEnabled(false);
                        break;

                    case 204: // repository is starred by you
                        mStarButton.setImageResource(android.R.drawable.star_on);
                        mRepo.Starred = true;
                        break;

                    default: // repository is not starred by you
                        mStarButton.setImageResource(android.R.drawable.star_off);
                        mRepo.Starred = false;
                        break;
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        return true;
    }

    private boolean setSubscribed() {

        GitHubService service = Authorization.getInstance().getGitService();
        Call call = service.isSubscribed(mRepo.owner.login, mRepo.name);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(retrofit.Response response, Retrofit retrofit) {
                Log.i("Info is watch", String.valueOf(response.code()));
                switch (response.code()) {
                    case 401: // no authorization
                        mWatcherButton.setEnabled(false);
                        break;

                    case 204: // repository is subscribed by you
                        mWatcherButton.setImageResource(android.R.drawable.checkbox_on_background);
                        mRepo.Watched = true;
                        break;

                    default: // repository is not subscribed by you
                        mWatcherButton.setImageResource(android.R.drawable.checkbox_off_background);
                        mRepo.Watched = false;
                        break;
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        return true;
    }
}
