package com.jsports.dialogs


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.jsports.R
import com.jsports.api.models.requests.EventRequest
import com.jsports.api.models.responses.CyclicResult


class AddEventDialog(
    private val sportsDiscipline: String,
    private val addEvent: (request: EventRequest) -> Unit
) : DialogFragment() {

    private lateinit var etDistance: EditText
    private lateinit var etTime: EditText
    private lateinit var etComment: EditText

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_event, null)
        etDistance = view.findViewById(R.id.et_distance)
        etTime = view.findViewById(R.id.et_dialog_time)
        etComment = view.findViewById(R.id.et_comment)
        builder.setView(view)
            .setPositiveButton(
                R.string.add
            ) { _, _ ->
            }
            .setNegativeButton(
                R.string.cancel
            ) { _, _ -> }
        val created = builder.create()
        created.setOnShowListener {dialog ->
            val button: Button =
                (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                if (validateEventRequest()) {
                    var comment: String? = null
                    if (etComment.text.isNotEmpty()) {
                        comment = etComment.text.toString()
                    }
                    val request = EventRequest(
                        sportsDiscipline,
                        comment,
                        CyclicResult(
                            etDistance.text.toString().toFloat(),
                            etTime.text.toString().toFloat()
                        )
                    )
                    addEvent(request)
                }
            }
        }
        return created
    }

    private fun validateEventRequest(): Boolean {

        when {
            etDistance.text.isEmpty() -> {
                etDistance.error = getString(R.string.distance_required)
                return false
            }

            etTime.text.isEmpty() -> {
                etTime.error = getString(R.string.time_required)
                return false
            }
        }

        return true
    }
}