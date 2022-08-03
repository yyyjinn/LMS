package mvc.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mvc.model.*;
import mvc.modeldto.*;


@Controller
@RequestMapping("/professor")
public class PersonalController{
	static final int LISTCOUNT = 10;
	
	@Autowired
	PersonalDAO personalDAO;
	
	@Autowired
	BoardDAO boardDAO;
	
	@Autowired
	calendarDAO calDAO;
	
	@Autowired
	QuestionDAO queDAO;
	
	@Autowired
	applicationDAO appDAO;
	
	@Autowired
	notice_boardDAO noticeDAO;
	
	/* 메인화면 이동 */
	@RequestMapping("/p_main.per")
	public String perCon1(HttpSession session, Model model, 
			@RequestParam(defaultValue="1") int pageNum, @RequestParam(required=false) String subjects,
			@RequestParam(defaultValue="1") int CpageNum, @RequestParam(defaultValue="1") int nbPageNum) {
		/* 내 과목 DB에서 가져오기 */
		String p_id = (String) session.getAttribute("p_id");
		ArrayList<String> sub_list = boardDAO.getmySubject(p_id);
		
		model.addAttribute("ssubject", sub_list);
		
		/* 게시판*/
		int limit = LISTCOUNT;
		
		int TotalOfPost = boardDAO.getListCount(subjects);
		ArrayList<BoardDTO> boardlist = boardDAO.getBoardList(pageNum, limit, subjects);

		int total_page;

		if (TotalOfPost == 0) {
			total_page = 1;
		} else if (TotalOfPost % limit == 0) {
			total_page = TotalOfPost / limit;
		} else {
			total_page = TotalOfPost / limit;
			total_page = total_page + 1;
		}
		
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("total_page", total_page);
		model.addAttribute("boardlist", boardlist);
		
		/* 캘린더 */
		int Climit = 4;
		
		int totalofcalendar = calDAO.getListCount();
		ArrayList<calendarDTO> callist = calDAO.getCallist(CpageNum, Climit);
		
		int totalpage;
		if (totalofcalendar == 0)
			totalpage = 1;
		else if (totalofcalendar % Climit == 0) {
			totalpage = totalofcalendar / Climit;
		} else {
			totalpage = totalofcalendar / Climit;
			totalpage = totalpage + 1;
		}

		model.addAttribute("CpageNum", CpageNum);
		model.addAttribute("Ctotalpage", totalpage);
		model.addAttribute("callist", callist);
		
		/* 공지사항 */
		int nlimit = 4;
		
		int totalOfNotice = noticeDAO.getListCount(); 
		ArrayList<notice_boardDTO> nbArr = noticeDAO.getBoardList(nbPageNum, nlimit);
		
		int totalNpage;

		if (totalOfNotice == 0) {
			totalNpage = 1;
		}

		else if (totalOfNotice % limit == 0) {
			totalNpage = totalOfNotice / nlimit;
		}

		else {
			totalNpage = totalOfNotice / nlimit;
			totalNpage = totalNpage + 1;
		}
		
		model.addAttribute("nbPageNum", nbPageNum);
		model.addAttribute("nbtotalpage", totalNpage);
		model.addAttribute("nbArr", nbArr);
		
		return "professor/p_main";
	}

	/* p_info : 교직원 정보 */
	@RequestMapping("/p_info.per")
	public String perCon2(HttpSession session, Model model) {
		String p_id = (String) session.getAttribute("p_id");
		
		PersonalDTO per_dto = personalDAO.getinfo(p_id);
		
		model.addAttribute("p_id", p_id);
		model.addAttribute("dto", per_dto);
		
		return "professor/p_info";
	}
	
	/* p_info : 개인정보 수정 */
	@RequestMapping("/modify_process.per")
	public String perCon3(HttpSession session, Model model, @RequestParam Map<String, String> map,
			RedirectAttributes redirect) {
		//model.addAttribute("check", "1");
		redirect.addAttribute("check", "1");
		personalDAO.update(map.get("p_adress"), map.get("p_phone"), map.get("p_email"), (String) session.getAttribute("p_id"));
		return "redirect:/professor/p_info.per";
	}
	
	/* p_lecture */
	@RequestMapping("/p_lecture.per")
	public String perCon4(HttpSession session, Model model) {
		/*나의 과목 DB에서 가져오기*/
		String p_id = (String) session.getAttribute("p_id");
		ArrayList<String> sub_list = boardDAO.getmySubject(p_id);
		
		model.addAttribute("ssubject", sub_list);
		
		/* 수강신청현황 페이지 : 과목별 학생 수 가져옴 */
		Map<String, Integer> cmap = new HashMap<>();	
		for (int i = 0; i < sub_list.size(); i++) {
			cmap.put(sub_list.get(i), appDAO.getCount(sub_list.get(i)));
		}
		
		model.addAttribute("cmap", cmap);
		
		return "professor/p_lecture";
	}
	
	/* p_popup_lecture*/
	@RequestMapping("/p_popup_lecture.per")
	public String perCon5(@RequestParam String subject_name, Model model) {
		ArrayList<StudentDTO> stuArr = appDAO.get_Stu_list(subject_name);
		
		model.addAttribute("subject_name", subject_name);
		model.addAttribute("stuArr", stuArr);
		
		return "professor/p_popup_lecture";
	}
	
	/* p_exam */
	@RequestMapping("/p_exam.per")
	public String perCon6(HttpSession session, Model model, HttpServletRequest request,
			@RequestParam(required=false) String subject_name) {
		/* 나의 과목 DB에서 가져오기 */
		String p_id = (String) session.getAttribute("p_id");
		ArrayList<String> sub_list = boardDAO.getmySubject(p_id);
		
		model.addAttribute("ssubject", sub_list);
		
		//시험출제 한 후 
		Enumeration<String> enu = request.getParameterNames();
		ArrayList<String> arr = new ArrayList<String>();
		while (enu.hasMoreElements()) {
			String ans = (String) enu.nextElement();
			arr.add(ans);
		}
		String ans = "";
		for (int i = 0; i < arr.size() - 1; i++) {
			ans = ans + request.getParameter(arr.get(i));
		}
		//System.out.println(ans+"-------PersonalContorller : p_exam : ans");
		queDAO.updateAnswer(ans, subject_name);
		queDAO.insertAnswer(ans, subject_name);
		
		
		return "professor/p_exam";
	}
	
	/* p_popup_exam으로 이동 */
	@RequestMapping("/p_popup_exam.per")
	public String perCon7(Model model, @RequestParam(value="subject") String sub_name) {
		ArrayList<QuestionDTO> queDTO_list = queDAO.getExam(sub_name);
		
		model.addAttribute("sub_name", sub_name);
		model.addAttribute("queDTO_list", queDTO_list);
		
		return "professor/p_popup_exam";
	}
	
	//확인해보기
	/* p_score로 이동 */
	@RequestMapping("/p_score.per")
	public ModelAndView perCon8(Model model, HttpSession session, @RequestParam Map<String, Object> map) {

		/* 나의 과목 DB에서 가져오기 */
		String p_id = (String) session.getAttribute("p_id");
		ArrayList<String> sub_list = boardDAO.getmySubject(p_id);
		
		model.addAttribute("ssubject", sub_list);
		
		String listfirst = (String) model.getAttribute("subject");
		if (listfirst == null) {		
			listfirst = (String) map.get("subject");
		}

		if(listfirst==null) {
			 listfirst = sub_list.get(0);
		}
		
		// *성적산출 페이지 : 과목별 학생 수강신청테이블에서 가져오기, 수업일수가져오기*/
		ArrayList<StudentDTO> st_list = appDAO.getStudentList(listfirst);
		String suballday = personalDAO.getstudyday(listfirst);
		model.addAttribute("st_list", st_list);
		model.addAttribute("suballday", suballday);
		
		/* 성적산출 페이지 : 강의테이블에서 강의정보 들고오기 */
		String s_id = (String) map.get("s_id");
		if(s_id==null) {
			s_id = (String) model.getAttribute("student");
		}

		lectureDTO lecdto = personalDAO.getlec(listfirst, s_id);
		model.addAttribute("lecdto", lecdto);
		
		if(model.getAttribute("msg")!=null) {
			model.addAttribute("msg", "1");
		}
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("subject", listfirst);
		mav.addObject("student", s_id);
		mav.setViewName("professor/p_score");
		
		return mav;
	}
	
	/* p_score : 성적처리정보 불러오기 */
	@RequestMapping("/getScore.per")
	public String perCon9(Model model, @RequestParam(value="subject", required=false) String sub_name, 
			@RequestParam(value="student", required=false) String s_id, RedirectAttributes redirect) {
		
		if(sub_name==null && s_id==null) {
			sub_name = (String) model.getAttribute("subject");
			s_id = (String) model.getAttribute("student");
		}
		//System.out.println("-------------2성적처리페이지");
		String Answer = queDAO.AnswerSheet(sub_name, s_id);
		String correct = queDAO.CorrectSheet(sub_name);
		String[] compare = new String[correct.length()];
		int point = 0;
		if (!Answer.equals("")) {
			for (int i = 0; i < correct.length(); i++) {
				if (correct.charAt(i) == Answer.charAt(i)) {
					compare[i] = "1";
					point += 5;
				} else {
					compare[i] = "2";
				}
			}
			scoreDTO scDTO = new scoreDTO();
			scDTO.setAnswer(Answer);
			scDTO.setCompare(compare);
			scDTO.setCorrect(correct);
			scDTO.setPoint(point);
			
			if(model.getAttribute("msg")!=null) {
				redirect.addFlashAttribute("msg", "1");
			}

			redirect.addFlashAttribute("scDTO", scDTO);
			redirect.addFlashAttribute("subject", sub_name);
			redirect.addFlashAttribute("student", s_id);

		}

		return "redirect:/professor/p_score.per";
	}
	
	/* p_score : 학점계산 */
	@RequestMapping("/calculator.per")
	public String perCon10(Model model, @RequestParam(value="subject") String sub_name, @RequestParam(value="student") String s_id,
			@RequestParam(value="lec_score") int score, @RequestParam int absence, @RequestParam(value="point") String lec_point,
			RedirectAttributes redirect) {
		double total;
		
		if (!lec_point.equals("F") || absence < 4) {
			total = (score * 0.8) + (20 - absence * 5);

			if (total >= 95) {
				lec_point = "A+";
			} else if (total >= 90) {
				lec_point = "A";
			} else if (total >= 85) {
				lec_point = "B+";
			} else if (total >= 80) {
				lec_point = "B";
			} else if (total >= 75) {
				lec_point = "C+";
			} else if (total >= 70) {
				lec_point = "C";
			} else if (total >= 65) {
				lec_point = "D+";
			} else if (total >= 60) {
				lec_point = "D";
			} else {
				lec_point = "F";
			}
		}

		lectureDTO lec_dto = personalDAO.getlec(sub_name, s_id);
		if (lec_dto.getLec_score() == null) {
			personalDAO.putScore(sub_name, s_id, lec_point, absence);
		} else {
			personalDAO.updateScore(sub_name, s_id, lec_point, absence);
		}
		

		//model.addAttribute("msg", "1");
		redirect.addFlashAttribute("msg", "1");
		redirect.addFlashAttribute("subject", sub_name);
		redirect.addFlashAttribute("student", s_id);
		//System.out.println("-------------학점계산 페이지 1");
		return "redirect:/professor/getScore.per";
	}
	
}