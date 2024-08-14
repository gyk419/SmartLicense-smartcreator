package SmartLicense.smartlicense.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${fronturl}")
    private String fronturl;

    /*******************
     * 날짜 : 2024.07.10
     * 이름 : 김준식
     * 내용 : CORS 허용
     * *****************/
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(fronturl)
//                .allowedOrigins("*")
                .allowedMethods("GET", "POST")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
//        interceptorRegistry.addInterceptor(new AuthenticationInterceptor())
//                .addPathPatterns("/**")
//                .excludePathPatterns("/memberLogin/verify","/memberLogin/logout");
//    }


}
