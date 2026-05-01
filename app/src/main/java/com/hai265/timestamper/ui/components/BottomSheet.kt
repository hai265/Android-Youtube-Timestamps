package com.hai265.timestamper.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.compose.AndroidFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hai265.timestamper.R

@Composable
fun BottomSheet() {
    val activity = LocalActivity.current as androidx.fragment.app.FragmentActivity

    LaunchedEffect(Unit) {
        val bottomSheet = ModalBottomSheet()

        bottomSheet.show(
            activity.supportFragmentManager,
            ModalBottomSheet.TAG
        )
    }
}

class ModalBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.modal_bottom_sheet_content, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        val editText = view.findViewById<EditText>(R.id.dialog_edittext)
        editText.requestFocus()
        dialog?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                MaterialTheme {
                    Text("Hello Compose!")
                }
            }
        }
        return view
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}