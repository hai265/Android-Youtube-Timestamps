import androidx.compose.ui.window.ComposeUIViewController
import com.hai265.timestamper.App
import deps.IosFileController
import deps.IosInsetsController
import deps.IosOrientationController

fun MainViewController() = ComposeUIViewController {
    App(
        insetsController = IosInsetsController(),
        orientationController = IosOrientationController(),
        fileController = IosFileController(),
        customTheme = null,
    )
}