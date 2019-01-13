package hello

import hello.storage.StorageFileNotFoundException
import hello.storage.StorageService
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.Resource
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

@RunWith(SpringRunner::class)
@AutoConfigureMockMvc
@SpringBootTest
class FileUploadTests {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var storageService: StorageService

    @Test
    @Throws(Exception::class)
    fun shouldListAllFiles() {
        given<Stream<Path>>(this.storageService.loadAll())
                .willReturn(Stream.of<Path>(Paths.get("first.txt"), Paths.get("second.txt")))

        this.mvc.perform(get("/")).andExpect(status().isOk)
                .andExpect(model().attribute("files",
                        Matchers.contains("http://localhost/files/first.txt",
                                "http://localhost/files/second.txt")))
    }

    @Test
    @Throws(Exception::class)
    fun shouldSaveUploadedFile() {
        val multipartFile = MockMultipartFile("file", "test.txt",
                "text/plain", "Spring Framework".toByteArray())
        this.mvc.perform(multipart("/").file(multipartFile))
                .andExpect(status().isFound)
                .andExpect(header().string("Location", "/"))

        then<StorageService>(this.storageService).should().store(multipartFile)
    }

    @Test
    @Throws(Exception::class)
    fun should404WhenMissingFile() {
        given<Resource>(this.storageService.loadAsResource("test.txt"))
                .willThrow(StorageFileNotFoundException::class.java)

        this.mvc.perform(get("/files/test.txt")).andExpect(status().isNotFound)
    }

}
