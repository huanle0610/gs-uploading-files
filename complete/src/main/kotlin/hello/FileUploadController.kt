package hello

import hello.storage.StorageFileNotFoundException
import hello.storage.StorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.io.IOException
import kotlin.streams.toList

@Controller
class FileUploadController @Autowired
constructor(private val storageService: StorageService) {

    @GetMapping("/")
    @Throws(IOException::class)
    fun listUploadedFiles(model: Model): String {

        model.addAttribute("files", storageService
                .loadAll()
                .map { path ->
                    MvcUriComponentsBuilder
                            .fromMethodName(FileUploadController::class.java,
                                    "serveFile", path.fileName.toString())
                            .build().toString()
                }.toList()
        )

        return "uploadForm"
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    fun serveFile(@PathVariable filename: String): ResponseEntity<Resource> {

        val file = storageService.loadAsResource(filename)
        val ok = ResponseEntity.ok()
        return ok.headers(chineseFileDownloadHeaders(filename))
                .body(file)
    }

    @PostMapping("/")
    fun handleFileUpload(@RequestParam("file") file: MultipartFile,
                         redirectAttributes: RedirectAttributes): String {

        storageService.store(file)
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + getFileName(file.originalFilename!!) + "!")

        return "redirect:/"
    }

    @ExceptionHandler(StorageFileNotFoundException::class)
    fun handleStorageFileNotFound(exc: StorageFileNotFoundException): ResponseEntity<*> {
        return ResponseEntity.notFound().build<Any>()
    }

}
