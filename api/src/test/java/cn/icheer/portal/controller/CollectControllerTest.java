//package cn.icheer.portal.controller;
//
//import cn.icheer.portal.dto.CollectPortalDTO;
//import cn.icheer.portal.entity.User;
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
// * @since 2025/7/8 16:49
// */
//@SpringBootTest
//class CollectControllerTest {
//    @Autowired
//    private CollectController collectController;
//
//    @Test
//    public void collectPortalTest() {
//        CollectPortalDTO collectPortalDTO = new CollectPortalDTO();
//        collectPortalDTO.setPortalId(1L);
//        collectPortalDTO.setDoCollect((short) 1);
//        User user = new User();
//        user.setId(1L);
//        HttpSession session = new MockHttpSession();
//        session.setAttribute("user", user);
//        System.out.println(collectController.collectPortal(collectPortalDTO));
//    }
//}