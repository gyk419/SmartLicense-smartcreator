package SmartLicense.smartlicense.SignInUp.Controller;

import SmartLicense.smartlicense.SignInUp.Service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
/*******************
 * 날짜 : 2024.07.10
 * 이름 : 김준식
 * 내용 : login Controller
 * *****************/
@RestController
@RequestMapping("/memberLogin")
public class LoginController {

    @Autowired
    LoginService loginService;

    /*******************
     * 날짜 : 2024.07.10
     * 이름 : 김준식
     * 내용 : 로그인
     * *****************/
    @PostMapping("/verify")
    public ResponseEntity<HashMap<String, Object>> login(@RequestBody HashMap<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("login Controller 실행");
        return ResponseEntity.ok(loginService.smartLicenselogin(params, request, response));
    }

    /*******************
     * 날짜 : 2024.07.28
     * 이름 : 김준식
     * 내용 : 게스트로그인
     * *****************/
    @PostMapping("/guestLogin")
    public ResponseEntity<HashMap<String, Object>> guestLogin() {
        System.out.println("guestLogin 실행");
        return ResponseEntity.ok(loginService.guestLogin());
    }

    /*******************
     * 날짜 : 2024.07.28
     * 이름 : 김준식
     * 내용 : 게스트로그인
     * *****************/
}