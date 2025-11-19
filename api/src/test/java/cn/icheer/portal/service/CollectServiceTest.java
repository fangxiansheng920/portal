//package cn.icheer.portal.service;
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
///**
// * @author fangweihao
// * @since 2025/7/8 9:17
// */
//@SpringBootTest
//public class CollectServiceTest {
//    @Autowired
//    private CollectService collectService;
//
//    @Test
//    public void getPortalDoCollectTest() {
//        HttpSession session = new MockHttpSession();
//        User user = new User();
//        user.setId(1L);
//        session.setAttribute("user", user);
//        System.out.println(collectService.getPortalDoCollect(1L));
//    }
//
//    @Test
//    public void collectPortalTest() {
//        Long portalId = 15L;
//        Long userId = 1L;
//        Short doCollect = 1;
//        CollectPortalDTO collectPortalDTO = new CollectPortalDTO();
//        collectPortalDTO.setPortalId(portalId);
//        collectPortalDTO.setDoCollect(doCollect);
//        System.out.println(collectService.collectPortal(collectPortalDTO, userId));
//    }
//}
