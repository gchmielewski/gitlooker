package com.example.gitlooker.service;

import com.example.gitlooker.model.Repo;
import com.example.gitlooker.model.SearchRepo;
import java.util.List;
import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Url;

/**
 * Created by grzesiek on 29.12.2015a
 */
public interface GitHubService {

    // repos
    @GET("/users/{user}/repos")
    Call<List<Repo>> listRepos(@Path("user") String user);

    // starring
    @GET("/user/starred/{user}/{repo}")
    Call<Void> isStarred(@Path("user") String user, @Path("repo") String repo);

    @POST("/user/starred/{owner}/{repo}")
    Call<Void> starRepo(@Path("owner") String owner, @Path("repo") String repo);

    @DELETE("/user/starred/{owner}/{repo}")
    Call<Void> unstarRepo(@Path("owner") String owner, @Path("repo") String repo);

    // watching
    @GET("/user/subscription/{owner}/{repo}")
    Call<Void> isSubscribed(@Path("owner") String owner, @Path("repo") String repo);

    @POST("/user/subscription/{owner}/{repo}")
    Call<Void> watchRepo(@Path("owner") String owner, @Path("repo") String repo);

    @DELETE("/user/subscription/{owner}/{repo}")
    Call<Void> unwatchRepo(@Path("owner") String owner, @Path("repo") String repo);

    // next user repositories
    @GET
    Call<List<Repo>> listReposPaginate(@Url String url);

    // search
    @GET("/search/repositories")
    Call<SearchRepo> listSearchRepo(@Query("q") String query);

    @GET
    Call<SearchRepo> listSearchRepoNext(@Url String url);

}
