package com.example.educoder_backend.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "文件大小超出限制，请上传小于 100MB 的文件！");
        return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE); // 413 状态码
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleFileNotFoundException(FileNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "文件保存失败：路径不可用，请联系管理员！错误信息：" + ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500 状态码
    }
}