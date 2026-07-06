package com.hai265.timestamper.screens.bottomSheet

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val bottomSheet = ModalBottomSheet(onDismiss, content, colorScheme)

        bottomSheet.show(
            activity.supportFragmentManager,
            ModalBottomSheet.TAG
        )
    }
}

class ModalBottomSheet(
    private val onDismiss: () -> Unit,
    private val content: @Composable (hideSheet: () -> Unit) -> Unit,
    private val colorScheme: ColorScheme?
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
                AppTheme(customColor = colorScheme) {
                    androidx.compose.material3.Surface {
                        Column {
                            content { dismiss() }
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
        return view
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}