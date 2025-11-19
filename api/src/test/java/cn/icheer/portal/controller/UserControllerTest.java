//package cn.icheer.portal.controller;
//
//import cn.icheer.portal.dto.UserLoginByPasswordDTO;
//import cn.icheer.portal.dto.UserLoginByPasswordResultDTO;
//import cn.icheer.portal.entity.User;
//import cn.icheer.portal.result.ResponseResult;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockHttpSession;
//
//import javax.servlet.http.HttpSession;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * @author fangweihao
// * @since 2025/7/4 9:53
// */
//@SpringBootTest
//class UserControllerTest {
//    @Autowired
//    private UserController userController;
//
//    @Test
//    public void loginByPasswordTest() {
////        // 数据模拟
////        String phoneNumber = "15606045638";
////        String password = "906040021";
////        // 模拟HttpSession
////        HttpSession session = new MockHttpSession();
////
////        // 初始化DTO
////        UserLoginByPasswordDTO userLoginByPasswordDTO = new UserLoginByPasswordDTO();
////        userLoginByPasswordDTO.setPhoneNumber(phoneNumber);
////        userLoginByPasswordDTO.setPassword(password);
////
////        ResponseResult userLoginByPasswordResultDTO = userController.loginByPassword(userLoginByPasswordDTO);
////        Boolean status = userLoginByPasswordResultDTO.getStatus();
////        String token = userLoginByPasswordResultDTO.getToken();
////        if (status == 0) {
////            System.out.println("无此账号");
////        }
////        else if (status == 1) {
////            System.out.println("未设置密码");
////        } else if (status == 2) {
////            System.out.println("密码错误");
////        } else {
////            System.out.println("登录成功");
////        }
////        System.out.println(token);
////        System.out.println(session.getAttribute("token"));
////        System.out.println(session.getAttribute("user"));
//    }
//
//    @Test
//    public void getUserInfoByIdTest() {
////        HttpSession session = new MockHttpSession();
////        User user = new User();
////        user.setId(1L);
////        session.setAttribute("user", user);
////        user = userController.getUserInfoById();
////        System.out.println(user);
//    }
//}
