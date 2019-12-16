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
            ) { dialog, _ ->
                positive?.let { it() }
                dialog.dismiss()
            }
            .setNegativeButton(
                R.string.cancel
            ) { dialog, _ ->
                negative?.let { it() }
                dialog.dismiss()
            }
        return builder.create()
    }
}