import com.hai265.timestamper.data.exporter.ExportUtils
import com.hai265.timestamper.ui.fakes.fakeTimestamp1
import com.hai265.timestamper.ui.fakes.fakeTimestamp2
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class ExportUtilsTest {

    private lateinit var subject: ExportUtils

    @BeforeTest
    fun setup() {
        subject = ExportUtils()
    }

    @Test
    fun testTimestampsToYamlStringString() {
        val timestamp = fakeTimestamp1
        val expected = """
            info: 
              lastUpdated: 0
              title: title
              videoId: 0
            tags: 
              - description: Sample Description
                id: 1
                seconds: 0.0
        """.trimIndent()

        val serializedString = subject.timestampsToYamlString(listOf(timestamp))
        assertEquals(expected, serializedString)
    }

    @Test
    fun testExportTwoTimestampToYamlString() {
        val expected = """
            info: 
              lastUpdated: 0
              title: title
              videoId: 0
            tags: 
              - description: Sample Description
                id: 1
                seconds: 0.0
              - description: Sample Description
                id: 2
                seconds: 10000.0
        """.trimIndent()

        val serializedString =
            subject.timestampsToYamlString(listOf(fakeTimestamp1, fakeTimestamp2))
        assertEquals(expected, serializedString)
    }
}