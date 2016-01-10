package com.example.gitlooker.utils;

import java.util.ArrayList;

/**
 * Created by grzesiek on 2016-01-09a
 */
public class SearchList extends ArrayList<String> {

    private static SearchList ourInstance = new SearchList();

    public static SearchList getInstance() {
        return ourInstance;
    }

    private SearchList() {
    }
}
