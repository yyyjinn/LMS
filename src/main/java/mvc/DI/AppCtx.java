package mvc.DI;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mvc.controller.BoardController;
import mvc.controller.MemberController;
import mvc.controller.PersonalController;
import mvc.controller.StudentController;
import mvc.controller.notice_boardController;
import mvc.model.BoardDAO;
import mvc.model.MemberDAO;
import mvc.model.PersonalDAO;
import mvc.model.QuestionDAO;
import mvc.model.StudentDAO;
import mvc.model.applicationDAO;
import mvc.model.calendarDAO;
import mvc.model.notice_boardDAO;

@Configuration
public class AppCtx {
	
	@Bean(destroyMethod = "close")
	public DataSource dataSource() {
		DataSource ds = new DataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost:3306/pro5_lms?characterEncoding=utf8&useSSL=false");
		ds.setUsername("root");
		ds.setPassword("1234");
		return ds;
	}
	
	@Bean
	public BoardController boardController() {
		return new BoardController();
	}
	
	@Bean
	public MemberController memberController() {
		return new MemberController();
	}
	
	@Bean
	public notice_boardController noticeController() {
		return new notice_boardController();
	}
	
	@Bean
	public PersonalController personalController() {
		return new PersonalController();
	}
	
	@Bean
	public StudentController studentController() {
		return new StudentController();
	}
	
	@Bean
	public applicationDAO appDAO() { //appdao
		return new applicationDAO(dataSource());
	}
	
	@Bean
	public BoardDAO boardDAO() {
		return new BoardDAO(dataSource());
	}
	
	@Bean
	public calendarDAO calDAO() {
		return new calendarDAO(dataSource());
	}
	
	@Bean 
	public MemberDAO memberDAO() {
		return new MemberDAO(dataSource());
	}
	
	@Bean
	public notice_boardDAO noticeDAO() {
		return new notice_boardDAO(dataSource());
	}
	
	@Bean
	public PersonalDAO personalDAO() {
		return new PersonalDAO(dataSource());
	}
	
	@Bean
	public QuestionDAO queDAO() {
		return new QuestionDAO(dataSource());
	}
	
	@Bean
	public StudentDAO studentDAO() {
		return new StudentDAO(dataSource());
	}

}
