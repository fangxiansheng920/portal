package cn.icheer.portal.controller;

import cn.icheer.portal.details.JWTUserDetails;
import cn.icheer.portal.enums.HttpStatusEnum;
import cn.icheer.portal.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
@RestController
public class UploadController {

    /**
     * 测试上传文件
     */
    @RequestMapping("/uploadAvatarImage")
    public ResponseResult uploadAvatarImage(MultipartFile file) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JWTUserDetails user = (JWTUserDetails) authentication.getPrincipal();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss.SSS");
            String timestamp = LocalDateTime.now().format(formatter);
            String s = user.getUsername() + "_" + timestamp + ".jpg";
            File f = new File("C:/Users/28117/Desktop/作业/pictures/avatars/" + s);
            file.transferTo(f);
            log.info("Upload File success.");
            return ResponseResult.success(s);
        } catch (Exception e) {
            log.error("Upload File error.", e);
            return ResponseResult.fail(HttpStatusEnum.UNAUTHORIZED.getCode(), "未登录或token失效");
        }
    }

    @RequestMapping("/uploadWebsiteImage")
    public ResponseResult uploadWebsiteImage(MultipartFile file) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JWTUserDetails user = (JWTUserDetails) authentication.getPrincipal();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss.SSS");
            String timestamp = LocalDateTime.now().format(formatter);
            String s = user.getUsername() + "_" + timestamp + ".jpg";
            File f = new File("C:/Users/28117/Desktop/作业/pictures/website/" + s);
            file.transferTo(f);
            log.info("Upload File success.");
            return ResponseResult.success(s);
        } catch (Exception e) {
            log.error("Upload File error.", e);
            return ResponseResult.fail(HttpStatusEnum.UNAUTHORIZED.getCode(), "未登录或token失效");
        }
    }
}
