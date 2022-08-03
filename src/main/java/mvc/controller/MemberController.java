package mvc.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mvc.model.MemberDAO;

@Controller
@RequestMapping("/member")
public class MemberController{
	
	@Autowired
	MemberDAO memberDAO;
	
	/*주민등록번호로 학번/직번 조회*/
	@RequestMapping("/join_chkprocess.do")
	public String memberCon1(Model model, @RequestParam(value="join_jumin1") String jumin1, 
			@RequestParam(value="join_jumin2") String jumin2, HttpServletResponse response) throws IOException {
		
		String addr = "";
		
		String[] NameId = memberDAO.get_NameId(jumin1, jumin2);
		
		//일치하는 회원정보가 없으면
		if(NameId[0]==null && NameId[1]==null && NameId[2]==null && NameId[3]==null){ 
			NameId[2]="2";
			NameId[3]="2";
		}
		
		model.addAttribute("name", NameId[0]);
		model.addAttribute("id", NameId[1]);
		model.addAttribute("jumin1", jumin1);
		model.addAttribute("jumin2", jumin2);
		model.addAttribute("num", NameId[3]); //일치하는 회원정보가 없으면
		
		//일치하는 정보 & 비밀번호 없으면 회원가입
		if(NameId[2].equals("0")) {
			addr = "member/join";
		}
		
		//비밀번호가 존재(회원정보가 있으면)하면 실행
		if(NameId[2].equals("1")) {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			
			out.println("<script>alert('회원정보가 존재합니다. 로그인페이지로 이동합니다.'); location.href='./login.jsp';</script>");
			out.flush();
		}
		
		//일치하는 회원정보가 없으면 실행
		if(NameId[3].equals("2")) {
			addr = "member/join";
		}
		
		return addr;
	}

	/*회원가입 : 비밀번호 db 업데이트*/
	@RequestMapping("/join_process.do")
	public String memberCon2(@RequestParam(value="join_id") String id, @RequestParam(value="join_pw") String pw) {
		memberDAO.update_pw(id, pw);
		return "redirect:/member/login.jsp?num=1";
	}
	
	/*로그인*/
	@RequestMapping("/login_process.do")
	public String memberCon3(Model model, HttpServletRequest request, 
			@RequestParam(required=false) String id, @RequestParam(value="passwd", required=false) String pw) {
		String addr = "";
		
		String[] IdNum = memberDAO.login(id, pw);
		
		model.addAttribute("login_id", IdNum[0]);
		model.addAttribute("login_name", IdNum[1]);
		
		if (IdNum[2].equals("1")){
			HttpSession session = request.getSession();
            session.setAttribute("p_id", IdNum[0]);
            session.setAttribute("p_name", IdNum[1]);

            addr = "redirect:/professor/p_main.per?num=1";
		}
		
		if (IdNum[2].equals("2")){
			HttpSession session = request.getSession();
            session.setAttribute("s_id", IdNum[0]);
            session.setAttribute("s_name", IdNum[1]);

            addr = "redirect:/student/s_main.so?num=1";
		}
		
		if(IdNum[2].equals("3")){

			addr = "redirect:/member/login.jsp?IdPwNum="+IdNum[2];
		}
		return addr;
	}
	
	/*비밀번호 변경(process)*/
	@RequestMapping("/pwprocess.do")
	public String memberCon4(Model model, @RequestParam String id, HttpSession session,
			@RequestParam(value="pw_before") String pw, @RequestParam String pw_new) {

		String addr = "";
		
		if(memberDAO.checkpw(id, pw)==0) {
			model.addAttribute("pwchknum", "0");
			addr = "member/changepw";
		}
		else {
			memberDAO.changepasswd(id, pw_new);
			session.removeAttribute("p_id");
			session.removeAttribute("s_id");
			addr = "redirect:/member/login.jsp?num=2";
		}
		return addr;
	}
	
	/*로그아웃*/
	@RequestMapping("/logout.do")
	public String memberCon5(HttpSession session) {
		
		session.removeAttribute("p_id");
		session.removeAttribute("s_id");
		
		return "redirect:/member/login.jsp?num=3";
	}

}
