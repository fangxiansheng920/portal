//package cn.icheer.portal.service;
//
//import cn.icheer.portal.controller.PortalController;
//import cn.icheer.portal.dto.PortalCreateDTO;
//import cn.icheer.portal.dto.PortalSearchByKeywordDTO;
//import cn.icheer.portal.dto.PortalUpdateDTO;
//import cn.icheer.portal.entity.User;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockHttpSession;
//
//import javax.servlet.http.HttpSession;
//
///**
// * @author fangweihao
// * @since 2025/7/8 17:23
// */
//@SpringBootTest
//public class PortalServiceTest {
//    @Autowired
//    private PortalService portalService;
//
//    @Test
//    public void createPortalTest() {
////        PortalCreateDTO portalCreateDTO = new PortalCreateDTO(null, "测试网站", "iiiiiiiiiiiiiiiiiiiiiiiiiii", (short) 1, null, (short) 1, "测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试", "PC", "测试", null, null, null);
////        HttpSession session = new MockHttpSession();
////        User user = new User();
////        user.setId(1L);
////        user.setUserName("小方");
////        session.setAttribute("user", user);
////        for (int i = 0; i < 10; i++) {
////            System.out.println(portalService.createPortal(portalCreateDTO));
////        }
//    }
//
//    @Test
//    public void deletePortalByIdTest() {
//        HttpSession session = new MockHttpSession();
//        User user = new User();
//        user.setId(1L);
//        session.setAttribute("user", user);
//        System.out.println(portalService.deletePortalById(1L));
//    }
//
//
//    @Test
//    public void updatePortalInfoTest() {
//        PortalUpdateDTO portalUpdateDTO = new PortalUpdateDTO(2L, "111", "111", (short) 1, "111", (short) 1,"111", "PC", "111");
//        System.out.println(portalService.updatePortalInfo(portalUpdateDTO));
//    }
//
//    @Test
//    public void searchPortalByKeywordTest() {
//        HttpSession session = new MockHttpSession();
//        User user = new User();
//        user.setId(1L);
//        session.setAttribute("user", user);
//        System.out.println(portalService.searchPortalByKeyword(new PortalSearchByKeywordDTO("aaa", 1,1)));
//        System.out.println(portalService.searchPortalByKeyword(new PortalSearchByKeywordDTO("aaa", 1, 2)));
//        System.out.println(portalService.searchPortalByKeyword(new PortalSearchByKeywordDTO("aaa", 1, 3)));
//        System.out.println(portalService.searchPortalByKeyword(new PortalSearchByKeywordDTO("aaa", 1, 4)));
//    }
//
//    @Test
//    public void getPortalInfoById() {
//        HttpSession session = new MockHttpSession();
//        User user = new User();
//        user.setId(1L);
//        session.setAttribute("user", user);
//        portalService.getPortalInfoById(15L);
//    }
//}
