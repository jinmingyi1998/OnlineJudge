package cn.edu.zjnu.acm.controller;

import cn.edu.zjnu.acm.entity.ImageLog;
import cn.edu.zjnu.acm.entity.User;
import cn.edu.zjnu.acm.exception.NotFoundException;
import cn.edu.zjnu.acm.repo.logs.ImageLogRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/media")
public class MediaController {
    private final HttpSession session;
    private final ImageLogRepository imageLogRepository;
    private final String saveDir = "/onlinejudge/media/";

    public MediaController(HttpSession session, ImageLogRepository imageLogRepository) {
        this.session = session;
        this.imageLogRepository = imageLogRepository;
    }

    @PostConstruct
    public void init() {
        initCreating("/onlinejudge/media");
    }

    private void initCreating(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            log.info(filename + " Existed");
        } else {
            if (file.mkdirs())
                log.info(filename + " not existed, created");
            else
                log.error(filename + " not existed, created fail");
        }

    }

    @PostMapping("/upload")
    public ImageUploadResponse uploadImage(@RequestParam("editormd-image-file") MultipartFile multipartFile, HttpServletRequest request) {
        ImageUploadResponse imgResponse = new ImageUploadResponse();
        imgResponse.setSuccess(0);
        if (multipartFile.isEmpty() || StringUtils.isEmpty(multipartFile.getOriginalFilename())) {
            imgResponse.setMessage("image is empty");
            return imgResponse;
        }
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            imgResponse.setMessage("Need to login");
            return imgResponse;
        }
        String filePath;
        String md5;
        try {
            md5 = DigestUtils.md5DigestAsHex(multipartFile.getInputStream());
            filePath = saveDir + md5;
            File saveFile = new File(filePath);
            if (!saveFile.exists()) {
                multipartFile.transferTo(saveFile);//save file
            }
        } catch (IOException e) {
            e.printStackTrace();
            imgResponse.setMessage("Internal error");
            return imgResponse;
        }
        ImageLog imageLog = new ImageLog(user, request.getRemoteAddr(), filePath, multipartFile.getSize(), "/api/media/" + md5, Instant.now());
        imageLog.saveLog(imageLogRepository);

        imgResponse.setUrl("/api/media/" + md5);
        imgResponse.setSuccess(1);
        imgResponse.setMessage("success");
        return imgResponse;
    }

    @GetMapping("/{filename}")
    public void downloadImage(@PathVariable(value = "filename") String filename, HttpServletResponse response) {
        File img = new File(saveDir + filename);
        if (img.exists()) {
            byte[] buffer = new byte[1024];
            try {
                BufferedInputStream stream = new BufferedInputStream(new FileInputStream(img));
                OutputStream os = response.getOutputStream();
                int i = stream.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = stream.read(buffer);
                }
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new NotFoundException();
            }
        } else {
            throw new NotFoundException();
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class ImageUploadResponse {
        Integer success = 0;
        String url = null;
        String message = null;

        public ImageUploadResponse() {
        }
    }
}
