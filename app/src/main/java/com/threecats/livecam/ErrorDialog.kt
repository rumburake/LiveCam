/*
 * Copyright (c) 2020 rumburake@gmail.com
 */
package com.threecats.livecam

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ErrorDialog : DialogFragment() {
    private var argMessage: String? = null
    private var argAck: String? = null

    interface AckListener {
        fun ack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        argMessage = arguments!!.getString(ARG_MESSAGE)
        argAck = arguments!!.getString(ARG_ACK)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(activity!!)
        builder.setMessage(argMessage)
                .setPositiveButton(argAck) { dialog, id -> (activity as AckListener?)!!.ack() }
        return builder.create()
    }

    companion object {
        const val ARG_MESSAGE = "arg_message"
        const val ARG_ACK = "arg_ack"
        fun newInstance(messageText: String?, ackText: String?): ErrorDialog {
            val args = Bundle()
            args.putString(ARG_MESSAGE, messageText)
            args.putString(ARG_ACK, ackText)
            val errorDialog = ErrorDialog()
            errorDialog.arguments = args
            return errorDialog
        }
    }
}