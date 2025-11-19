//package cn.icheer.portal.service.impl;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import cn.icheer.portal.details.JWTUserDetails;
//import cn.icheer.portal.dto.UserChangePasswordDTO;
//import cn.icheer.portal.entity.User;
//import cn.icheer.portal.mapper.UserMapper;
//import cn.icheer.portal.utils.MD5Utils;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//@ExtendWith(MockitoExtension.class)
//public class UserServiceImplTest{
//
//    @Mock
//    private UserMapper userMapper;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    @Mock
//    private Authentication authentication;
//
//    @Mock
//    private SecurityContext securityContext;
//
//    private UserChangePasswordDTO passwordDTO;
//
//    private JWTUserDetails userDetails;
//
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        passwordDTO = new UserChangePasswordDTO();
//        userDetails = new JWTUserDetails();
//        userDetails.setId(1L);
//
//        user = new User();
//        user.setId(1L);
//        user.setPassword("906040021");
//
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//    }
//
//    @Test
//    void changePasswordWhenOldPasswordIncorrectReturns0() {
//        passwordDTO.setOldPassword("wrongOldPassword");
//        passwordDTO.setNewPassword("newPassword");
//        passwordDTO.setRepeatPassword("newPassword");
//
//        when(userMapper.getUserInfoById(1L)).thenReturn(user);
//        try (MockedStatic<MD5Utils> md5Utils = mockStatic(MD5Utils.class)) {
//            md5Utils.when(() -> MD5Utils.getPwd("wrongOldPassword")).thenReturn("wrongHashedPassword");
//
//            int result = userService.changePassword(passwordDTO);
//
//            assertEquals(0, result);
//            verify(userMapper, times(1)).getUserInfoById(1L);
//        }
//    }
//
//    @Test
//    void changePasswordWhenNewPasswordsNotMatchReturns1() {
//        passwordDTO.setOldPassword("oldPassword");
//        passwordDTO.setNewPassword("newPassword");
//        passwordDTO.setRepeatPassword("differentPassword");
//
//        when(userMapper.getUserInfoById(1L)).thenReturn(user);
//        try (MockedStatic<MD5Utils> md5Utils = mockStatic(MD5Utils.class)) {
//            md5Utils.when(() -> MD5Utils.getPwd("oldPassword")).thenReturn("hashedOldPassword");
//
//            int result = userService.changePassword(passwordDTO);
//
//            assertEquals(1, result);
//            verify(userMapper, times(1)).getUserInfoById(1L);
//        }
//    }
//
//    @Test
//    void changePasswordWhenAllConditionsMetReturns2() {
//        passwordDTO.setOldPassword("oldPassword");
//        passwordDTO.setNewPassword("newPassword");
//        passwordDTO.setRepeatPassword("newPassword");
//
//        when(userMapper.getUserInfoById(1L)).thenReturn(user);
//        try (MockedStatic<MD5Utils> md5Utils = mockStatic(MD5Utils.class)) {
//            md5Utils.when(() -> MD5Utils.getPwd("oldPassword")).thenReturn("hashedOldPassword");
//            md5Utils.when(() -> MD5Utils.getPwd("newPassword")).thenReturn("hashedNewPassword");
//
//            int result = userService.changePassword(passwordDTO);
//
//            assertEquals(2, result);
//            verify(userMapper, times(1)).getUserInfoById(1L);
//            verify(userMapper, times(1)).changePassword(passwordDTO, 1L);
//            assertEquals("hashedNewPassword", passwordDTO.getNewPassword());
//        }
//    }
//
//}