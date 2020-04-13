import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.*


/**
 * This Action creates a message dialog.
 */
class MessageAction : AnAction() {

    /**
     * Displays a message dialog.
     */
    override fun actionPerformed(e: AnActionEvent) {

        Messages.showMessageDialog(e.project, "Hello!", "Message dialog", Messages.getInformationIcon())

        val file : PsiFile = PsiDocumentManager.getInstance(e.project!!).getPsiFile(e.getData(LangDataKeys.EDITOR)?.document!!)!!

        file.accept(object : JavaRecursiveElementVisitor() {
            override fun visitMethod(method: PsiMethod?) {
                super.visitMethod(method)
                print(method.toString())
            }
        })
    }

    /**
     * Determines the availability of this menu item.
     */
    override fun update(e: AnActionEvent) {
        val project: Project? = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
}