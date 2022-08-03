package mvc.DI;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//WebMvcConfigurer : spring mvc의 기능을 가지고 있음
@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer{

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
		//dispatcher를 사용하게 해줌
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp("/", ".jsp");
		//viewResolver설정
	}

	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resource/**").addResourceLocations("/resource/");
		/*/resource/**라고 경로를 설정했을 때 실제 정적리소스 경로를 설정해줌*/
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		/*BoardController - 교수 : 게시판 목록 - 화면*/
		registry.addViewController("/board/p_boardlist.go").setViewName("board/p_boardlist");
		
		/*BoardController - 학생 : 게시판 목록 - 화면*/
		registry.addViewController("/board/s_boardlist.go").setViewName("board/s_boardlist");
		
		/*MemberController - 회원가입*/
		registry.addViewController("/member/join.do").setViewName("member/join");
		
		/*MemberController - 비밀번호 변경(info > 변경페이지로 이동)*/
		registry.addViewController("/member/changepw.do").setViewName("member/changepw");
	
		/*PersoncalController - 게시판 글쓰기로 이동 */
		registry.addViewController("/professor/p_write.per").setViewName("board/p_wirte");
	
		/*StudentController - s_post로 이동*/
		registry.addViewController("/student/s_post.so").setViewName("student/s_post");
	}
	
	
	
}
