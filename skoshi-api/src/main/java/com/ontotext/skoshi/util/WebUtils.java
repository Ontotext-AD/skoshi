package com.ontotext.skoshi.util;

import com.google.common.io.Files;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WebUtils {

    private final static Logger log = LoggerFactory.getLogger(WebUtils.class);

    private WebUtils() {}

    public static File getFileFromParam(MultipartFile fileParam) throws IOException {
        File tempDir = Files.createTempDir();
        File uploadedFile = new File(tempDir, fileParam.getOriginalFilename());
        fileParam.transferTo(uploadedFile);
        return uploadedFile;
    }

    public static List<File> getFilesFromArchiveParam(MultipartFile fileParam) throws IOException {

        List<File> files;
        File tempDir = Files.createTempDir();
        File uploadedFile = new File(tempDir, fileParam.getOriginalFilename());
        fileParam.transferTo(uploadedFile);

        if (isArchive(uploadedFile)) {
            try {
                files = extractArchive(uploadedFile);
            } catch (ZipException ze) {
                log.error("Failed to extract archive: " + uploadedFile.getName(), ze);
                throw new IllegalArgumentException("Failed to extract archive: " + uploadedFile.getName(), ze);
            } finally {
                FileUtils.deleteQuietly(uploadedFile);
            }
        } else {
            throw new IllegalArgumentException("File " + fileParam.getOriginalFilename() + " is not an archive!");
        }

        return files;
    }

    public static boolean isArchive(File file) {
        String fileType;
        try {
            Tika tika = new Tika();
            fileType = tika.detect(file);
            log.trace("Detected file type: {}", fileType);
        } catch (IOException e) {
            log.error("Error occurred upon trying to detect the input file media type!", e);
            throw new IllegalArgumentException("Error detecting input file type!", e);
        }
        assert fileType != null;
        MediaType mediaType = MediaType.parse(fileType);
        return MediaType.APPLICATION_ZIP.equals(mediaType);
    }

    public static List<File> extractArchive(File archiveFile) throws ZipException {
        List<File> extractedFiles = new ArrayList<>();
        File unzipDir = Files.createTempDir();
        ZipFile zipFile = new ZipFile(archiveFile);
        zipFile.extractAll(unzipDir.getAbsolutePath());
        extractedFiles.addAll(FileUtils.listFiles(unzipDir, TrueFileFilter.INSTANCE, null));
        log.trace("Extracted {} files from archive to: {}", extractedFiles.size(), unzipDir.getAbsolutePath());
        return extractedFiles;
    }

    public static void appendFileToResponse(String fileName, String contentType, String content, HttpServletResponse response) {
        try {
            byte[] bytes = content.getBytes("UTF-8");
            response.setContentType(contentType);
            response.setContentLength(bytes.length);
            response.setHeader("Content-Disposition", "attachment; filename="+fileName);
            InputStream is = new ByteArrayInputStream(bytes);
            // copy it to response's OutputStream
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write file to output stream", ex);
        }
    }

}
