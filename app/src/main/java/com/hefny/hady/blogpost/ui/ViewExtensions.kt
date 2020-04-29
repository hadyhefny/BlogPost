import android.app.Activity
import android.widget.Toast
import com.hefny.hady.blogpost.util.StateMessageCallback

private val TAG: String = "AppDebug"

fun Activity.displayToast(
    message: String,
    stateMessageCallback: StateMessageCallback
) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    stateMessageCallback.removeMessageFromStack()
}

