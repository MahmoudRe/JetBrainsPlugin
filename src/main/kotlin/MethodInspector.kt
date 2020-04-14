import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.*


/**
 * This action performs an analysis of the class methods.
 */
class MethodInspector : AnAction() {

    /**
     * Displays a message dialog.
     */
    override fun actionPerformed(e: AnActionEvent) {

        Messages.showMessageDialog(e.project, "Your report is being generated", "Method Inspector", Messages.getInformationIcon())

        // Create a PsiFile from the currently active class.
        val file : PsiFile = PsiDocumentManager.getInstance(e.project!!).getPsiFile(e.getData(LangDataKeys.EDITOR)?.document!!)!!

        // Initialize the StringBuilder
        var builder : StringBuilder = StringBuilder("# Method Analyzer Report \n")
        builder.append("## Class: ${file.name} \n-----\n \n")

        //Iterate over the class methods
        file.accept(object : JavaRecursiveElementVisitor() {
            override fun visitMethod(method: PsiMethod?) {
                if (method != null) {
                    // Save the method name.
                    builder.append("### Method ${method.name}: ")

                    // Check the return type.
                    val returnType = method.returnType
                    if (returnType == null){
                        builder.append("(constructor) \n")
                    } else {
                        builder.append("\n   Return type: ")
                        builder.append(returnType.getPresentableText(true))
                        builder.append("\n") }

                    // Save the parameter list.
                    builder.append("   Parameter list: ${method.parameterList.text} \n")

                    //Save the thrown exceptions.
                    val list = method.throwsList.text
                    if (list.isNotEmpty()) builder.append("   Throws: $list")

                    builder.append("\n \n")
                }
            }
        })

        // Write the results to a file.
        val factory = PsiFileFactory.getInstance(e.project)
        var result = factory.createFileFromText("report-${file.name}.md", PlainTextLanguage.INSTANCE, builder.toString())

        // Write the generated file to disk. Duplicate reports are prohibited.
        val application : Application = ApplicationManager.getApplication()
        val directory : PsiDirectory = LangDataKeys.IDE_VIEW.getData(e.getDataContext())!!.getOrChooseDirectory()!!
        application.runWriteAction(Runnable {
            try {
                result = directory.add(result) as PsiFile?}
            catch (ex : Exception) {
                Messages.showMessageDialog(e.project, "A report for this class already exist", "Warning", Messages.getWarningIcon())
        }})
    }

    /**
     * Determines the availability of this menu item.
     */
    override fun update(e: AnActionEvent) {
        val project: Project? = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
}