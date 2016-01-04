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
import android.widget.EditText;
import android.widget.TextView;
import com.example.gitlooker.MainActivity;
import com.example.gitlooker.R;

/**
 * Created by grzesiek on 2015-12-23.
 */
public class Utils {

  public static EditText userInput;

  private Utils() {

  }

  public static void showInput(String promptText, Context context, DialogInterface.OnClickListener OnOkClick) {
    // get prompts.xml view
    LayoutInflater li = LayoutInflater.from(context);
    View promptsView = li.inflate(R.layout.prompts, null);

    android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);

    // set prompts.xml to alertdialog builder
    alertDialogBuilder.setView(promptsView);

    userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
    ((TextView)promptsView.findViewById(R.id.txtPrompt)).setText(promptText);

        // set dialog message
            alertDialogBuilder.setCancelable(false)
        .setPositiveButton("OK", OnOkClick)
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
    boolean haveConnectedWifi = false;
    boolean haveConnectedMobile = false;

    ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
    Network[] networks = cm.getAllNetworks();
    for (Network ni : networks) {
      if (cm.getNetworkInfo(ni).getTypeName().equalsIgnoreCase("WIFI"))
        if (cm.getNetworkInfo(ni).isConnected())
          haveConnectedWifi = true;
      if (cm.getNetworkInfo(ni).getTypeName().equalsIgnoreCase("MOBILE"))
        if (cm.getNetworkInfo(ni).isConnected())
          haveConnectedMobile = true;
    }
    return haveConnectedWifi || haveConnectedMobile;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static void enableStrictMode() {
    if (Utils.hasGingerbread()) {
      StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
          new StrictMode.ThreadPolicy.Builder()
              .detectAll()
              .penaltyLog();

      StrictMode.VmPolicy.Builder vmPolicyBuilder =
          new StrictMode.VmPolicy.Builder()
              .detectAll()
              .penaltyLog();

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
