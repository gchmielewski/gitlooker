package com.example.gitlooker.utils;

import android.util.Base64;
import com.example.gitlooker.service.GitHubService;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by grzesiek on 2015-12-30.
 */
public class Authorization {

  private String mLoginName;
  private String mPassword;
  public static final String BASE_URL = "https://api.github.com";

  public Authorization(String loginName, String password) {
    this.mLoginName = loginName;
    this.mPassword = password;
  }

  public String getLoginName() {
    return mLoginName;
  }

  public String getPassword() {
    return mPassword;
  }

  public void setPassword(String password) {
    this.mPassword = password;
  }

  private static Authorization ourInstance = new Authorization();

  public static Authorization getInstance() {
    return ourInstance;
  }

  private Authorization() {
  }

  public void setAuthorization(String login, String password) {
    this.mLoginName = login;
    this.mPassword = password;
  }

  public GitHubService getGitService() {
    String credentials = getLoginName() + ":" + getPassword();
    final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

    OkHttpClient httpClient = new OkHttpClient();
    httpClient.interceptors().clear();
    httpClient.interceptors().add(new Interceptor() {
      @Override
      public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
            .header("Authorization", basic)
            .header("Accept", "application/json")
            .method(original.method(), original.body());

        Request request = requestBuilder.build();
        return chain.proceed(request);
      }
    });

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(Authorization.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build();

    return retrofit.create(GitHubService.class);
  }
}
