package hello

import org.springframework.http.HttpHeaders
import org.springframework.util.StringUtils
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun chineseFileDownloadHeaders(filename: String): HttpHeaders {
    val responseHeaders = HttpHeaders()
    val realFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
    responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$realFileName\"; filename*=utf-8''$realFileName")

    return responseHeaders
}

fun getFileName(originalFilename: String) = StringUtils.getFilename(StringUtils.cleanPath(originalFilename))!!
