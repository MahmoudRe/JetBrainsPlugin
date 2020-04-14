import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.*
import org.jetbrains.kotlin.idea.refactoring.getLineCount
import org.jetbrains.kotlin.psi.KtNamedFunction


/**
 * This action performs an analysis of the class methods in Kotlin.
 */
class MethodInspectorK : AnAction() {

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
        file.accept(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {

                if (element is KtNamedFunction) {

                    // Save the method name.
                    builder.append("### Method ${element.name}: \n")

                    // Save the parameters names (when applicable).
                    if (element.valueParameters.size > 0) {
                        builder.append("Parameters: ${element.valueParameters.map{x -> x.name}}  \n")}

                    // Count the number of lines.
                    builder.append("Number of lines: ${element.getLineCount()} \n")

                    // Check if the method has a JavDoc description.
                    builder.append("Has method description: ${element.docComment != null} \n")


                    builder.append("\n \n")
                }

                super.visitElement(element)
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