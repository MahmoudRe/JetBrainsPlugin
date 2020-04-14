import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.*
import java.io.File


/**
 * This Action creates a message dialog.
 */
class MethodInspector : AnAction() {

    /**
     * Displays a message dialog.
     */
    override fun actionPerformed(e: AnActionEvent) {

        print("Reading file \n \n")

        val file : PsiFile = PsiDocumentManager.getInstance(e.project!!).getPsiFile(e.getData(LangDataKeys.EDITOR)?.document!!)!!

        file.accept(object : JavaRecursiveElementVisitor() {
            override fun visitMethod(method: PsiMethod?) {
                //super.visitMethod(method)
                if (method != null) {
                    File("report.txt").writeText(method.name + "/n")
                    print("Method " + method.name + ": ")
                    val returnType = method.returnType
                    if (returnType == null) print ("(constructor) \n") else {
                        print("\n   Return type: " + returnType.getPresentableText(true) + "\n") }
                    print("   Parameter list: " + method.parameterList.text + "\n")
                    val list = method.throwsList.text
                    if (list.isNotEmpty()) print("   Throws: $list")
                    print("\n \n")
                }
            }
        })

        Messages.showMessageDialog(e.project, "Your report is being generated!", "Method Inspector", Messages.getInformationIcon())

    }

    /**
     * Determines the availability of this menu item.
     */
    override fun update(e: AnActionEvent) {
        val project: Project? = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
}