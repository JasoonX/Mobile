package com.jsports.dialogs


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.jsports.R


class SimpleDialog(
    private val mCtx: Context,
    private val message: String,
    private val positive: (() -> Unit)? = null,
    private val negative: (() -> Unit)? = null
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(mCtx)
        builder.setMessage(message)
            .setPositiveButton(
                getString(R.string.ok)
            ) { _, _ ->
                positive?.let { it() }
            }
            .setNegativeButton(
                R.string.cancel
            ) { _, _ ->
                negative?.let { it() }
            }
        return builder.create()
    }
}