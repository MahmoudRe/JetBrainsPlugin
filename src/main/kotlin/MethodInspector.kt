import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.*
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.refactoring.getLineCount
import org.jetbrains.kotlin.psi.KtNamedFunction


/**
 * This action performs an analysis of the class methods.
 */
class MethodInspector : AnAction() {

    /**
     * Displays a message dialog.
     */
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        // Create a PsiFile from the currently active class.
        val file = e.getData(LangDataKeys.PSI_FILE) ?: return

        // Initialize the StringBuilder
        val builder = StringBuilder("# Method Analyzer Report \n")
        builder.append("## Class: ${file.name} \n-----\n \n")

        // Analyze the file
        val analysis : String
        analysis = when (file.language) {
            is KotlinLanguage -> {
                inspectKotlinFile(builder, file)
            }
            is JavaLanguage -> {
                inspectJavaFile(builder, file)
            }
            else -> {
                Messages.showMessageDialog(project, "The selected file is not compatible. Please select a Kotlin or Java file", "Method Inspector", Messages.getWarningIcon())
                return
            }
        }

        // Write the results to a file on disk.
        val succeeded = writeResult(e, analysis, "report-${file.name.substringBeforeLast(".")}.md")

        if (succeeded) {
            Messages.showMessageDialog(e.project, "Your report has been generated", "Method Inspector", Messages.getInformationIcon())
        } else {
        Messages.showMessageDialog(e.project, "A report for this class already exist", "Warning", Messages.getWarningIcon())
        }

    }

    /**
     * Determines the availability of this menu item.
     */
    override fun update(e: AnActionEvent) {
        val project: Project? = e.project
        e.presentation.isEnabledAndVisible = project != null
    }

    /**
     * Recursively walk the Psi tree to evaluate KtNamedFunctions.
     */
    private fun inspectKotlinFile(builder: StringBuilder, file: PsiFile) : String {
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
        return builder.toString()
    }

    /**
     * Recursively walk the Psi tree to evaluate Java methods.
     */
    private fun inspectJavaFile(builder: StringBuilder, file: PsiFile): String {
        //Iterate over the class methods
        file.accept(object : JavaRecursiveElementVisitor() {
            override fun visitMethod(method: PsiMethod?) {
                if (method != null) {
                    // Save the method name.
                    builder.append("### Method ${method.name}: ")

                    // Check the return type.
                    val returnType = method.returnType
                    if (returnType == null) {
                        builder.append("(constructor) \n")
                    } else {
                        builder.append("\n   Return type: ")
                        builder.append(returnType.getPresentableText(true))
                        builder.append("\n")
                    }

                    // Save the parameter list.
                    builder.append("   Parameter list: ${method.parameterList.text} \n")

                    //Save the thrown exceptions.
                    val list = method.throwsList.text
                    if (list.isNotEmpty()) builder.append("   Throws: $list")

                    //get the number of lines in this method
                    val lines = method.text.split("\n").toTypedArray()
                    val linesCount = if (lines.size > 2) lines.size - 2 else 0
                    builder.append("   number of lines: $linesCount \n")

                    builder.append("\n \n")
                }
            }
        })
        return builder.toString()
    }

    private fun writeResult(e : AnActionEvent, analysis : String, filename : String) : Boolean {
        val factory = PsiFileFactory.getInstance(e.project)
        var result = factory.createFileFromText(filename, PlainTextLanguage.INSTANCE, analysis)
        var succeeded = false

        // Write the generated file to disk. Duplicate reports are prohibited.
        val application : Application = ApplicationManager.getApplication()
        val directory : PsiDirectory = LangDataKeys.IDE_VIEW.getData(e.getDataContext())!!.getOrChooseDirectory()!!
        application.runWriteAction(Runnable {
            try {
                result = directory.add(result) as PsiFile
                succeeded = true
            }
            catch (ex : Exception) {
            }
        })
        return succeeded
    }

}