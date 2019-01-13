/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hello.storage

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import java.util.*

/**
 * @author Dave Syer
 */
class FileSystemStorageServiceTests {

    private val properties = StorageProperties()
    private lateinit var service: FileSystemStorageService

    @Before
    fun init() {
        properties.location = "target/files/" + Math.abs(Random().nextLong())
        service = FileSystemStorageService(properties)
        service.init()
    }

    @Test
    fun loadNonExistent() {
        assertThat(service.load("foo.txt")).doesNotExist()
    }

    @Test
    fun saveAndLoad() {
        service.store(MockMultipartFile("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello World".toByteArray()))
        assertThat(service.load("foo.txt")).exists()
    }

    @Test
    fun savePermitted() {
        service.store(MockMultipartFile("foo", "bar/../foo.txt",
                MediaType.TEXT_PLAIN_VALUE, "Hello World".toByteArray()))
    }

}
