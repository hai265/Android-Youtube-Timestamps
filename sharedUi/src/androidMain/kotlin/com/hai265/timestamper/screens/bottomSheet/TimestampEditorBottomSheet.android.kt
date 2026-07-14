package com.hai265.timestamper.screens.bottomSheet

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hai265.timestamper.screens.R
import com.hai265.timestamper.theme.AppTheme

@Composable
actual fun BottomSheet(
    onDismiss: () -> Unit,
    content: @Composable (hideSheet: () -> Unit) -> Unit
) {
    val activity = LocalActivity.current as androidx.fragment.app.FragmentActivity
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                context
            )
        }

        else -> null
    }

    LaunchedEffect(Unit) {
        val bottomSheet = ModalBottomSheet.newInstance(onDismiss, content, colorScheme)

        bottomSheet.show(
            activity.supportFragmentManager,
            ModalBottomSheet.TAG
        )
    }
}

class ModalBottomSheet : BottomSheetDialogFragment() {

    private var onDismiss: (() -> Unit)? = null
    private var content: (@Composable (hideSheet: () -> Unit) -> Unit)? = null
    private var colorScheme: ColorScheme? = null

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val isRotating = activity?.isChangingConfigurations == true

        if (!isRotating) {
            onDismiss?.invoke()
            this.onDismiss = null
            this.content = null
            this.colorScheme = null
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        val view = View.inflate(requireContext(), R.layout.modal_bottom_sheet, null)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        dialog.window?.setSoftInputMode(
            //https://drive.google.com/file/d/143l-DGaEOnQXLEY1iTv2Rp9Cwkh08M1K/view?usp=sharing
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            //https://drive.google.com/file/d/1clBQJt_VuhglEFJFlWASKb6p7u31v_Gd/view?usp=sharing
//            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )

        dialog.setContentView(view)
        val behavior = BottomSheetBehavior.from(view.parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme(customColor = colorScheme) {
                    androidx.compose.material3.Surface {
                        Column {
                            content?.invoke { dismiss() }
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        return dialog
    }

    companion object {
        const val TAG = "ModalBottomSheet"

        fun newInstance(
            onDismiss: () -> Unit,
            content: @Composable (hideSheet: () -> Unit) -> Unit,
            colorScheme: ColorScheme?
        ): ModalBottomSheet = ModalBottomSheet().apply {
            this.onDismiss = onDismiss
            this.content = content
            this.colorScheme = colorScheme
        }
    }
}