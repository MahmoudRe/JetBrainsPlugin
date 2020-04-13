import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

/**
 * This Action creates a message dialog.
 */
class MessageAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        Messages.showMessageDialog(e.project, "Hello!", "Message dialog", Messages.getInformationIcon())
    }
}