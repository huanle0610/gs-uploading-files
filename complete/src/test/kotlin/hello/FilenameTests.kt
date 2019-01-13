package hello

import org.junit.Assert
import org.junit.Test
import org.springframework.util.StringUtils

class FilenameTests {
    @Test
    fun fileName() {
        val originalFilename = """C:\Users\huanl\Desktop\SpotlightImages\daa67d0a899bd2d728161992cdc3ece270abf3ff21a2fbca81b68242aec22561.jpg"""
        val name = StringUtils.getFilename(originalFilename)
        val name2 = StringUtils.getFilename(originalFilename.replace("\\", "/"))
        Assert.assertEquals(originalFilename, name)
        Assert.assertEquals("daa67d0a899bd2d728161992cdc3ece270abf3ff21a2fbca81b68242aec22561.jpg", name2)
    }
}
