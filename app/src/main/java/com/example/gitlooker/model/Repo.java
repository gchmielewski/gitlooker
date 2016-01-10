
package com.example.gitlooker.model;

import java.util.HashMap;
import java.util.Map;

public class Repo {

    public int id;
    public int search_id;
    public Owner owner;
    public String name;
    public String full_name;
    public String description;
    public boolean _private;
    public boolean fork;
    public String url;
    public String html_url;
    public String homepage;
    public Object language;
    public int forks_count;
    public int stargazers_count;
    public int watchers_count;
    public int size;
    public String defaultBranch;
    public int openIssuesCount;
    public boolean has_issues;
    public boolean has_wiki;
    public boolean has_pages;
    public boolean has_downloads;
    public String pushed_at;
    public String created_at;
    public String updated_at;
    public Permissions permissions;
    public boolean Starred;
    public boolean Watched;

    public Repo(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.stargazers_count = 1;
        this.watchers_count = 2;
    }

    public Repo(int id, String name, String avatarUrl, String login, int _private, String description, int stargazersCount, int watchersCount, int searchId) {
        this.id = id;
        this.search_id = searchId;
        this.name = name;
        this.description = description;
        this._private = _private == 1;
        this.stargazers_count = stargazersCount;
        this.watchers_count = watchersCount;

        this.owner = new Owner();
        this.owner.login = login;
        this.owner.avatar_url = avatarUrl;
    }
}
