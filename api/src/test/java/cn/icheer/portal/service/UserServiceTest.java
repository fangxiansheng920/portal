//package cn.icheer.portal.service;
//
//import cn.icheer.portal.dto.*;
//import cn.icheer.portal.entity.User;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockHttpSession;
//
//import javax.servlet.http.HttpSession;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//
///**
// * @author fangweihao
// * @since 2025/7/3 14:10
// */
//
//@SpringBootTest
//class UserServiceTest {
//    @Autowired
//    private UserService userService;
//
//    @Test
//    public void loginByPasswordTest() {
//        // 数据模拟
//        String phoneNumber = "15606045638";
//        String password = "906040021";
//        // 模拟HttpSession
//        HttpSession session = new MockHttpSession();
//
//        // 初始化DTO
//        UserLoginByPasswordDTO userLoginByPasswordDTO = new UserLoginByPasswordDTO();
//        userLoginByPasswordDTO.setPhoneNumber(phoneNumber);
//        userLoginByPasswordDTO.setPassword(password);
//
//        UserLoginByPasswordResultDTO userLoginByPasswordResultDTO = userService.loginByPassword(userLoginByPasswordDTO);
//        Integer status = userLoginByPasswordResultDTO.getStatus();
//        String token = userLoginByPasswordResultDTO.getToken();
//        if (status == 0) {
//            System.out.println("无此账号");
//        }
//        else if (status == 1) {
//            System.out.println("未设置密码");
//        } else if (status == 2) {
//            System.out.println("密码错误");
//        } else {
//            System.out.println("登录成功");
//        }
//        System.out.println(token);
//        System.out.println(session.getAttribute("token"));
//        System.out.println(session.getAttribute("user"));
//    }
//
//    @Test
//    public void quickLoginByEpAuthTest() {
//        String code = "77aa12e6b4f8d768c83869de20b2eb90cdcacd6de794b48c6647681e1a3b48cebd42f6e8c7a79b9a82f823b686263416403318e73d26dabab12a85183c4b5ad6d40d06667156a67c6381fd6bc552265010f865b0c7e8e6b9d33183ae5d6b825a5e5500b295de039b2996610151d4ce0bff1feb05c6b35c65d436a8e9983fd5304d63e46b31df77895c4d36812db5f383ad238e866ac7bea8ea6221357f256e7373bfe96ded25740edf00c2bfcd4e514a50aa45bf1433fc5f670e9f45e4845f176a3796f92f2748597d4a454e7fea5279c8df921da6c45e31808d1a9925d1da03b511848c337a06cde6b8734f480e8881f4848970bab788b2eb003f3751596289b60ad0dda12d5619e8c9fca2a73bf755564ae15563fc932cd286615dad555ba7f932dedcc40e14feb624c65f6db5ee35";
//        HttpSession session = new MockHttpSession();
//        UserQuickLoginByEpAuthDTO userQuickLoginByEpAuthDTO = new UserQuickLoginByEpAuthDTO();
//        userQuickLoginByEpAuthDTO.setCode(code);
//        System.out.println(userService.quickLoginByEpAuth(userQuickLoginByEpAuthDTO));
//        System.out.println(session.getAttribute("token"));
//        System.out.println(session.getAttribute("user"));
//    }
//
//    @Test
//    public void registerTest() {
//        String phoneNumber = "1";
//        String password = "1";
//        String repeatPassword = "1";
//        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(phoneNumber, password, repeatPassword);
//        System.out.println(userService.register(userRegisterDTO));
//    }
//
//    @Test
//    public void getUserInfoByIdTest() {
//        Long id = 1L;
//        User user = userService.getUserInfoById(id);
//        System.out.println(user);
//    }
//
//    @Test
//    public void changePasswordTest() {
//        // 登录
//
//        String oldPassword = "906040021";
//        String newPassword = "123456";
//        String repeatPassword = "123456";
//        UserChangePasswordDTO userChangePasswordDTO = new UserChangePasswordDTO(oldPassword, newPassword, repeatPassword);
//        Integer result = userService.changePassword(userChangePasswordDTO);
//        System.out.println("Result: " + result);
//    }
//}