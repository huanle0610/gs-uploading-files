package hello

import hello.MockitoHelper.any
import hello.storage.StorageService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileUploadIntegrationTests {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockBean
    private lateinit var storageService: StorageService

    @LocalServerPort
    private val port: Int = 0

    @Test
    @Throws(Exception::class)
    fun shouldUploadFile() {
        val resource = ClassPathResource("testupload.txt", javaClass)

        val map = LinkedMultiValueMap<String, Any>()
        map.add("file", resource)
        val response = this.restTemplate.postForEntity("/", map,
                String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.FOUND)
        assertThat(response.headers.location!!.toString())
                .startsWith("http://localhost:" + this.port + "/")
        then(storageService)
                .should()
                .store(any())
    }

    @Test
    @Throws(Exception::class)
    fun shouldDownloadFile() {
        mapOf("testupload.txt" to "Spring Framework", "托尔斯泰.txt" to "精进").forEach { filename, content ->
            val resource = ClassPathResource(filename, javaClass)
            given<Resource>(this.storageService.loadAsResource(filename)).willReturn(resource)

            val response = this.restTemplate
                    .getForEntity("/files/{filename}", String::class.java, filename)

            assertThat(response.statusCodeValue).isEqualTo(200)
            assertThat(response.headers.getFirst(HttpHeaders.CONTENT_DISPOSITION))
                    .isEqualTo(chineseFileDownloadHeaders(filename).getFirst(HttpHeaders.CONTENT_DISPOSITION))
            assertThat(response.body).isEqualTo(content)
        }
    }

}
