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

            /**
             * Get the number of all If-Statements inside a PsiCodeBlock,
             * including the nested if-statements and else-if cases.
             * This method iterates over all children inside the given
             * psiCodeBlock and go to sub-children recursively looking for psiIfStatements
             * @param PsiCodeBlock the psiCodeBlock to iterate inside
             * @return The number of If-Statements
             */
            fun getNumIfStatments(psiCodeBlock: PsiCodeBlock): Int {

                //first get the number of if-statements in this code block
                val psiIfStatements = psiCodeBlock.children.filterIsInstance<PsiIfStatement>()
                var res = psiIfStatements.size

                //check inside each if-block if there is any nested if-statements or else-if
                for(psiIfStatement in psiIfStatements) {
                    val ifBlocks = psiIfStatement.children.filterIsInstance<PsiBlockStatement>()
                    for (ifBlock in ifBlocks) {
                        val myCodeBlock = ifBlock.children.filterIsInstance<PsiCodeBlock>()
                        if(myCodeBlock.isNotEmpty())
                            res += getNumIfStatments(myCodeBlock.first())
                    }

                    //check for else if case
                    val elseIfBlocks = psiIfStatement.children.filterIsInstance<PsiIfStatement>()
                    res += elseIfBlocks.size;
                    for (elseIfBlock in elseIfBlocks) {
                        val myCodeBlock = elseIfBlock.children.filterIsInstance<PsiCodeBlock>()
                        if(myCodeBlock.isNotEmpty())
                            res += getNumIfStatments(myCodeBlock.first())
                    }
                }

                return res;
            }

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

                    //get the number of lines in this method
                    val lines = method.text.split("\n").toTypedArray()
                    val linesCount = if (lines.size > 2) lines.size - 2 else 0
                    builder.append("   number of lines: $linesCount \n")

                    builder.append("   NumCodeBlock: ${method.children.filterIsInstance<PsiCodeBlock>().size} \n")

                    val mainPsiCodeBlock = method.children.filterIsInstance<PsiCodeBlock>().first()
                    val cc = getNumIfStatments( mainPsiCodeBlock ) + 1
                    builder.append("   Cyclomatic Complexity: $cc \n")

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