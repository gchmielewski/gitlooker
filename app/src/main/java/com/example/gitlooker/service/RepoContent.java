package com.example.gitlooker.service;

import com.example.gitlooker.model.Repo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by grzesiek on 2016-01-01.
 */
public class RepoContent {

  public static final List<Repo> ITEMS = new ArrayList<Repo>();
  public static final Map<Integer, Repo> ITEM_MAP = new HashMap<Integer, Repo>();

  private static final int COUNT = 25;

  static {
    // Add some sample items.
    for (int i = 1; i <= COUNT; i++) {
      addItem(createDummyItem(i));
    }
  }

  private static void addItem(Repo item) {
    ITEMS.add(item);
    ITEM_MAP.put(item.id, item);
  }

  private static Repo createDummyItem(int position) {
    return new Repo(position, "Repo " + position, makeDetails(position));
  }

  private static String makeDetails(int position) {
    StringBuilder builder = new StringBuilder();
    builder.append("Details about Item: ").append(position);
    for (int i = 0; i < position; i++) {
      builder.append("\nMore details information here.");
    }
    return builder.toString();
  }

  public static void clearRepos() {
    ITEMS.clear();
    ITEM_MAP.clear();
  }

  public static void addRepo(Repo r) {
    ITEMS.add(r);
    ITEM_MAP.put(r.id, r);
  }

  public static void addRepoList(ArrayList<Repo> repos) {
    clearRepos();

    for (Repo r: repos) {
      addRepo(r);
    }
  }
}
