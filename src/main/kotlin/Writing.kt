import java.io.File

class Writing {

    fun write(str: String) {
        File("report.txt").writeText(str)
    }
}