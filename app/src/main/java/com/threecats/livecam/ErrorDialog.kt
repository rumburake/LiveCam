/*
 * Copyright (c) 2020 rumburake@gmail.com
 */

package com.threecats.livecam;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

public class ErrorDialog extends DialogFragment {

    public static final String ARG_MESSAGE = "arg_message";
    public static final String ARG_ACK = "arg_ack";

    private String argMessage;
    private String argAck;

    public interface AckListener {
        void ack();
    }

    public static ErrorDialog newInstance(String messageText, String ackText) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, messageText);
        args.putString(ARG_ACK, ackText);
        ErrorDialog errorDialog = new ErrorDialog();
        errorDialog.setArguments(args);
        return errorDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        argMessage = getArguments().getString(ARG_MESSAGE);
        argAck = getArguments().getString(ARG_ACK);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(argMessage)
                .setPositiveButton(argAck, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((AckListener)getActivity()).ack();
                    }
                });
        return builder.create();
    }
}