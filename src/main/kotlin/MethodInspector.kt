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

        // Initialize the file header.
        val header ="# Method Inspector Report \n" +
                            "## Class: ${file.name} \n-----\n \n"

        // Analyze the file
        val analysis : String
        analysis = when (file.language) {
            is KotlinLanguage -> {
                inspectKotlinFile(header, file)
            }
            is JavaLanguage -> {
                inspectJavaFile(header, file)
            }
            else -> {
                Messages.showMessageDialog(project, "The selected file is not compatible. Please select a Kotlin or Java file", "Method Inspector", Messages.getWarningIcon())
                return
            }
        }

        // Write the results to a file on disk.
        val succeeded = writeResult(e, analysis, "report-${file.name.substringBeforeLast(".")}.md")

        // Return feedback to the user.
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
     * @param header The text that will be at the top of the report.
     * @param file The Kotlin file that will be analyzed.
     * @return The analysis, as a String.
     */
    private fun inspectKotlinFile(header : String, file: PsiFile) : String {
        val builder = StringBuilder(header)
        //Iterate over the class methods
        file.accept(object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {

                if (element is KtNamedFunction) {

                    // Save the method name.
                    builder.append("### Method ${element.name}: \n")

                    // Save the parameters names (when applicable).
                    if (element.valueParameters.size > 0) {
                        builder.append("   Parameters: ${element.valueParameters.map{x -> x.name}}  \n")}

                    // Count the number of lines.
                    builder.append("   Number of lines: ${element.getLineCount()} \n")

                    // Check if the method has a JavDoc description.
                    builder.append("   Has method description: ${element.docComment != null} \n")


                    builder.append("\n \n")
                }

                super.visitElement(element)
            }
        })
        return builder.toString()
    }

    /**
     * Recursively walk the Psi tree to evaluate Java methods.
     * @param header The text that will be at the top of the report.
     * @param file The Java file that will be analyzed.
     * @return The analysis, as a String.
     */
    private fun inspectJavaFile(header : String, file: PsiFile): String {
        val builder = StringBuilder(header)
        //Iterate over the class methods
        file.accept(object : JavaRecursiveElementVisitor() {

            /**
             * Recursively iterates over all children of PsiCodeBlock, 
             * and count the number of Decision Points.
             * The considered DP are if-, for- and while-condition.
             * @param psiCodeBlock the psiCodeBlock to start with
             * @return The number of If-Statements
             */
            fun countDecisionPoints(psiElement: PsiElement): Int {

                //first get the number of if-statements in this code block
                val psiDecisionPoints = psiElement.children
                                                .filter{ it is PsiIfStatement
                                                        || it is PsiWhileStatement
                                                        || it is PsiForStatement }
                var res = psiDecisionPoints.size

                //recursively add the number of Decision Points in each child
                for(psiDP in psiDecisionPoints) {

                    //iterate over all psiBlockStatements, there can be multiple of them
                    val psiBlockStatements = psiDP.children.filterIsInstance<PsiBlockStatement>()
                    for (psiBlockStatement in psiBlockStatements) {
                        
                        //there is only one CodeBlock inside PsiBlockStatement
                        val block = psiBlockStatement.children.filterIsInstance<PsiCodeBlock>()
                        if(block.isNotEmpty())
                            res += countDecisionPoints(block.first())
                    }

                    //check for direct children without block brackets
                    res += countDecisionPoints(psiDP)
                }

                return res;
            }

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
                    builder.append("   Number of lines: $linesCount \n")

                    //calculate the Cyclomatic Complexity
                    val mainPsiCodeBlock = method.children.filterIsInstance<PsiCodeBlock>().first()
                    val cc = countDecisionPoints( mainPsiCodeBlock ) + 1
                    builder.append("   Cyclomatic Complexity: $cc \n")

                    builder.append("\n \n")
                }
            }
        })
        return builder.toString()
    }

    /**
     * Write the generated file to disk. Duplicate reports are prohibited.
     * @param e The ActionEvent of this current Action.
     * @param analysis The result of analyzing the file.
     * @param filename The analysis will be saved under this name.
     * @return Indicates whether the action succeeded.
     */
    private fun writeResult(e : AnActionEvent, analysis : String, filename : String) : Boolean {
        val factory = PsiFileFactory.getInstance(e.project)
        var result = factory.createFileFromText(filename, PlainTextLanguage.INSTANCE, analysis)
        var succeeded = false

        val application : Application = ApplicationManager.getApplication()
        val directory : PsiDirectory = LangDataKeys.IDE_VIEW.getData(e.dataContext)!!.orChooseDirectory!!
        application.runWriteAction(Runnable {
            try {
                result = directory.add(result) as PsiFile
                succeeded = true
            }
            catch (ex : Exception) {
                print("A file with this name already exists. A pop-up warning will now appear to the user.")
            }
        })
        return succeeded
    }

}