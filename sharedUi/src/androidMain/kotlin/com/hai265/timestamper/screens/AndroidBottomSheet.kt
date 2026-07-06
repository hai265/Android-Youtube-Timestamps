package com.hai265.timestamper.screens

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@Composable
fun BottomSheet(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    val activity = LocalActivity.current as androidx.fragment.app.FragmentActivity

    LaunchedEffect(Unit) {
        val bottomSheet = ModalBottomSheet(onDismiss, content)

        bottomSheet.show(
            activity.supportFragmentManager,
            ModalBottomSheet.TAG
        )
    }
}

class ModalBottomSheet(
    private val onDismiss: () -> Unit,
    private val content: @Composable (() -> Unit)
) :
    BottomSheetDialogFragment() {

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.modal_bottom_sheet, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        dialog?.window?.setSoftInputMode(
            //https://drive.google.com/file/d/143l-DGaEOnQXLEY1iTv2Rp9Cwkh08M1K/view?usp=sharing
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            //https://drive.google.com/file/d/1clBQJt_VuhglEFJFlWASKb6p7u31v_Gd/view?usp=sharing
//            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                content()
            }
        }
        return view
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}