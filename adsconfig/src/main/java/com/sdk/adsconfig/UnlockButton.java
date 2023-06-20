package com.sdk.adsconfig;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class UnlockButton {

    public interface UnlockButtonListener {
        void unlock();
    }

    public static void unlockSet(Context context, UnlockButtonListener unlockButtonListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Unlock Button");
        alertDialog.setMessage("If you use this app feature, Watch ads...");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Watch Ads", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unlockButtonListener.unlock();
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
