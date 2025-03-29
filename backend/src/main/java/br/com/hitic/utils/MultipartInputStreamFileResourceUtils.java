package br.com.hitic.utils;

import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;

public class MultipartInputStreamFileResourceUtils extends InputStreamResource {

    private final String filename;

    public MultipartInputStreamFileResourceUtils(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public long contentLength() {
        return -1;
    }
}
