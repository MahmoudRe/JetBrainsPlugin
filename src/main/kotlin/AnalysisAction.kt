import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.kdoc.KDocTemplate
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid


class AnalysisAction : AnAction(){

    /**
     * Displays a message dialog.
     */
    override fun actionPerformed(e: AnActionEvent) {
        val file : PsiFile = e.getData(LangDataKeys.PSI_FILE)!!

        val prov = file.viewProvider

        val tr = prov.getPsi(KotlinLanguage.INSTANCE)


//        val file : PsiFile = PsiFileFactory.getInstance(getEventProject(e)).createFileFromText("example", "result")
////
//        VirtualFileManager.getInstance().findFileByUrl("file://" + localPath);

//        val documentManager = getEventProject(e)?.let { PsiDocumentManager.getInstance(it) }
//        val directory : PsiDirectory = PsiManager.getInstance(getEventProject(e)).


//        val inputStream: InputStream = File("result.txt").inputStream()
//
//        val inputString = inputStream.bufferedReader().use { it.readText() }
//        println(inputString)
//        val wr : Writing = Writing()
//        wr.write("result")

        Messages.showMessageDialog(e.project, "hello", "Message dialog", Messages.getInformationIcon())
    }

    /**
     * Determines the availability of this menu item.
     */
    override fun update(e: AnActionEvent) {
        val project: Project? = e.project
        e.presentation.isEnabledAndVisible = project != null
    }
}