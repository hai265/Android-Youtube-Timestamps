import com.hai265.timestamper.data.getYouTubeId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetYoutubeIdTest {
    @Test
    fun youtube_com_watchv_returnsVideoId() {
        val url = "https://www.youtube.com/watch?v=tQDO-uVCl40"

        assertEquals("tQDO-uVCl40", getYouTubeId(url))
    }

    @Test
    fun emptyString_returnsNull() {
        val url = ""

        assertNull(getYouTubeId(url))
    }

    @Test
    fun testShorts_returnsVideoID() {
        val url = "https://www.youtube.com/shorts/VN_lRj7x_dA"
        assertEquals("VN_lRj7x_dA", getYouTubeId(url))
    }

    @Test
    fun testLive_returnVideoId() {
        val url = "https://www.youtube.com/watch?v=VxDtIgKbWsA"
        assertEquals("VxDtIgKbWsA", getYouTubeId(url))
    }

    @Test
    fun testShorts_returnVideoId() {
        val url = "https://www.youtube.com/shorts/VN_lRj7x_dA"
        assertEquals("VN_lRj7x_dA", getYouTubeId(url))
    }

    @Test
    fun testTimestamp_returnVideoId() {
        val url =
            "https://www.youtube.com/wathttps://www.youtube.com/watch?v=tQDO-uVCl40&t=1275sch?v=tQDO-uVCl40&t=1275s"
        assertEquals("tQDO-uVCl40", getYouTubeId(url))
    }

    @Test
    fun randomString_returnNull() {
        val url = "notaurl"
        assertNull(getYouTubeId(url))
    }

    @Test
    fun nonYoutubeUrl_returnNull() {
        val url = "https://www.twitch.tv/videos/12345"
        assertNull(getYouTubeId(url))
    }

}