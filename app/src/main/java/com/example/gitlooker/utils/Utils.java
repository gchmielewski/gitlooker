package com.example.gitlooker.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.example.gitlooker.MainActivity;
import com.example.gitlooker.R;

/**
 * Created by grzesiek on 2015-12-23a
 */
public class Utils {

    public static final int NONE_SEARCH = -1;
    public static final int REPO_SEARCH = 0;
    public static final int USER_SEARCH = 1;
    public static final int ISSUE_SEARCH = 2;
    public static final int OWNER_SEARCH = 3;

    public static String getUserText() {
        return userInput != null ? userInput.getText().toString().trim() : null;
    }

    public static int getSearchType () {
        switch (searchType.getCheckedRadioButtonId()) {
            case R.id.radioRepo:
                return REPO_SEARCH;

            case R.id.radioUser:
                return USER_SEARCH;

            case R.id.radioIssue:
                return ISSUE_SEARCH;

            case R.id.radioOwner:
                return OWNER_SEARCH;

            default:
                return NONE_SEARCH;
        }
    }

    private static AutoCompleteTextView userInput = null;
    private static RadioGroup searchType = null;

    private Utils() {

    }

    public static void showInput(String promptText, Integer typeText, Context context, DialogInterface.OnClickListener OnOkClick) {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts, null);


        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        userInput = (AutoCompleteTextView) promptsView.findViewById(R.id.autoCompleteTextView);
        searchType = (RadioGroup) promptsView.findViewById(R.id.radioGroup);

        ((TextView) promptsView.findViewById(R.id.txtPrompt)).setText("Search word");

        switch (typeText) {
            case REPO_SEARCH:
                searchType.check(R.id.radioRepo);
                break;

            case OWNER_SEARCH:
                searchType.check(R.id.radioOwner);
                break;
        }


        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, SearchList.getInstance());
        userInput.setAdapter(adapter);
        userInput.setThreshold(1);
        userInput.setText(promptText);
        userInput.setSelectAllOnFocus(true);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", OnOkClick).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // create alert dialog
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static boolean haveNetworkConnection(Context con) {
        ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode() {
        if (Utils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog();

            StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder().detectAll().penaltyLog();

            if (Utils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                vmPolicyBuilder.setClassInstanceLimit(MainActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
