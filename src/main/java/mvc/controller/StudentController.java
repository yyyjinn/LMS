package mvc.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mvc.model.*;
import mvc.modeldto.BoardDTO;
import mvc.modeldto.QuestionDTO;
import mvc.modeldto.StudentDTO;
import mvc.modeldto.calendarDTO;
import mvc.modeldto.departmentDTO;
import mvc.modeldto.lectureDTO;
import mvc.modeldto.notice_boardDTO;
import mvc.modeldto.ssubjectDTO;

@Controller
@RequestMapping("/student")
public class StudentController{
	static final int LISTCOUNT = 5;
	
	@Autowired
	StudentDAO studentDAO;
	
	@Autowired
	notice_boardDAO noticeDAO;
	
	@Autowired
	QuestionDAO queDAO;
	
	@Autowired
	BoardDAO boardDAO;
	
	@Autowired
	calendarDAO calDAO;
	
	/*메인화면 이동*/
	@RequestMapping("/s_main.so")
	public String stuCon1(Model model, HttpSession session, @RequestParam(defaultValue="1") int CpageNum,
			@RequestParam(defaultValue="1") int pageNum, @RequestParam(required=false) String subjects,
			@RequestParam(defaultValue="1") int nbPageNum) {
		/*DB에서 calendar 어레이스트 가져오기*/
		int Climit = 4;
		int totalofcalendar = calDAO.getListCount();
		ArrayList<calendarDTO> callist = calDAO.getCallist(CpageNum, Climit);
		
		int Ctotalpage;
		if(totalofcalendar==0)
        	Ctotalpage=1;
        else if(totalofcalendar % Climit == 0) {
        	Ctotalpage = totalofcalendar/Climit;
        }
        else {
        	Ctotalpage =totalofcalendar/Climit;
            Ctotalpage =  Ctotalpage + 1;
        } 
		model.addAttribute("CpageNum", CpageNum);
		model.addAttribute("Ctotalpage", Ctotalpage);
		model.addAttribute("callist", callist);
		
		/*내 수강과목 가져오기*/
		int s_id = Integer.parseInt((String) session.getAttribute("s_id"));
		ArrayList<ssubjectDTO> mylist = studentDAO.mySubject(s_id);
		model.addAttribute("mylist", mylist);
		
		/*DB에서 게시글 가져오기*/
		int limit = 10;
		int TotalOfPost = boardDAO.getListCount(subjects);
		ArrayList<BoardDTO> boardlist = boardDAO.getBoardList(pageNum, limit, subjects);
	  
		int total_page;
	  
		if(TotalOfPost==0) {
			total_page=1;
		}
		else if(TotalOfPost % limit == 0) {
			total_page = TotalOfPost/Climit;
		}
		else {
			total_page =TotalOfPost/Climit;
			total_page =  total_page + 1;
		}
		
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("total_page", total_page);
		model.addAttribute("boardlist", boardlist);
		

		/*공지사항*/
		int Nlimit = 4;
		int totalOfNotice = noticeDAO.getListCount();
		ArrayList<notice_boardDTO> nbArr = noticeDAO.getBoardList(nbPageNum, Nlimit);
		int totalNpage;
		
		if (totalOfNotice == 0) {
			totalNpage = 1;
		}
		else if (totalOfNotice % limit == 0) {
			totalNpage = totalOfNotice / Nlimit;
		}
		else {
			totalNpage = totalOfNotice / Nlimit;
			totalNpage = totalNpage + 1;
		}
		
		model.addAttribute("nbPageNum", nbPageNum);
		model.addAttribute("nbtotalpage", totalNpage);
		model.addAttribute("nbArr", nbArr);
		
		
		return "student/s_main";
	}
	
	/*s_info로 이동*/
	@RequestMapping("/s_info.so")
	public String stuCon2(Model model, HttpSession session) {
		int s_id = Integer.parseInt((String) session.getAttribute("s_id"));
		StudentDTO stu_dto = studentDAO.getinfo(s_id);
		
		model.addAttribute("s_id", s_id);
		model.addAttribute("dto", stu_dto);
		
		return "student/s_info";
	}
	
	/*s_info : 정보 업데이트*/
	@RequestMapping("/modify_process.so")
	public String stuCon3(Model model, HttpSession session, @RequestParam Map<String, String> map) {
		model.addAttribute("check", "1");
		
		studentDAO.update(map.get("s_address"), map.get("s_phone"), map.get("s_email"),
				map.get("s_account1"), map.get("s_account2"), map.get("s_account3"), (String) session.getAttribute("s_id"));
		
		return "redirect:/student/s_info.so";
	}
	
	/*s_subject(수강신청)로 이동*/
	@RequestMapping("/s_subject.so")
	public String stuCon4(Model model, HttpSession session, @RequestParam(defaultValue="1") int pageNum,
			@RequestParam(value="sel_sub", defaultValue="major_all") String major,
			@RequestParam(value="code", required=false) String namecode) {
		/*모든 학과 들고오기*/
		ArrayList<departmentDTO> majorlist = studentDAO.getMajor();
		model.addAttribute("majorlist", majorlist);
		
		/*수강신청 과목정보 가져오기*/
		int limit = LISTCOUNT;
		int TotalOfSubject;
		
		ArrayList<ssubjectDTO> sublist = new ArrayList<ssubjectDTO>();
		
		if(namecode!=null && !namecode.equals("")) {
			TotalOfSubject = studentDAO.s_searchListCount(namecode);
			sublist = studentDAO.searchSubjectList(pageNum, limit, namecode);
		}
		else {
			TotalOfSubject = studentDAO.s_getListCount(major);
			sublist = studentDAO.s_getSubjectList(pageNum, limit, major);
		}
		
		int total_page;
		
		if(TotalOfSubject==0) {
			total_page=1;
		}
		else if(TotalOfSubject % limit == 0) {
			total_page = TotalOfSubject/limit;
		}
		else {
			total_page =TotalOfSubject/limit;
			total_page =  total_page + 1;
		}

		model.addAttribute("pageNum", pageNum);
		model.addAttribute("total_page", total_page); 
		model.addAttribute("sublist", sublist);
		model.addAttribute("searchmajor", major);
		
		/*내 수강과목 가져오기*/
		int s_id = Integer.parseInt((String) session.getAttribute("s_id"));
		ArrayList<ssubjectDTO> mylist = studentDAO.mySubject(s_id);
		model.addAttribute("mylist", mylist);
		
		return "student/s_subject";
	}
	
	/*수강신청 과목담기*/
	@RequestMapping("/subjectProcess.so")
	public String stuCon5(HttpSession session, @RequestParam(value="subject") String sub_code) {
		
		/*수강신청 처리*/
		int number=5;
		int s_id = Integer.parseInt((String) session.getAttribute("s_id"));
		
		ssubjectDTO subjectDTO = studentDAO.getssubjectDTO(sub_code);
		StudentDTO s_DTO = studentDAO.getinfo(s_id);
		
		String day; //요일
		String[] classtime; //수강신청한 과목들의 수업시간
		String[] subjecttime = subjectDTO.getSub_classtime().split(","); //선택과목 수업시간 분리
		ArrayList<String[]> time = new ArrayList<String[]>(); 
		ArrayList<ssubjectDTO> list = null; //수강신청한 과목(DTO)들의 arraylist
		
		int count = studentDAO.countmySubject(s_id); //수강신청한 과목 갯수세는 함수
		
		//1. 과목 최대수강인원 비교
		int numOfstudent = studentDAO.numberOfstudent(sub_code);
		numOfstudent = numOfstudent+1;
		
		if(numOfstudent>subjectDTO.getSub_max()) {
			number = 3;
		}

		//* 수강신청이 되어있지않은 경우
		if(count==0) {
			number = 5;
		}

		list = studentDAO.mySubject(s_id); //시작시간 순서대로
		
		//2. 내 최대학점과 비교
		int sub_hakjum = subjectDTO.getSub_hakjum();
		int s_hakjum = s_DTO.getS_max();
		int hakjum = sub_hakjum + s_hakjum;
		
		if(hakjum>18) {
			number = 1;
		}

		//4. 요일비교
		for(int i=0; i<list.size(); i++) {
			day = list.get(i).getSub_day();
			if(day.equals(subjectDTO.getSub_day())) {
				classtime = list.get(i).getSub_classtime().split(",");
				time.add(classtime);
			}
		}

		if(time.size()==0) {
			number = 5;
		}
		
		//5. 시간비교
		for(int i=0; i<time.size(); i++) {
			for(int j=0; j<time.get(i).length; j++) {
				String compare = (time.get(i))[j];
				if(compare.equals(subjecttime[0])) {
					number = 2;
				}
				else if(compare.equals(subjecttime[1])) {
					number = 2;
				}
				else if(compare.equals(subjecttime[2])){
					number = 2;
				}
			}
		}
		if(number==5) {
			ssubjectDTO subjectDTO2 = studentDAO.getssubjectDTO(sub_code);
			String s_name = subjectDTO2.getSub_name();
			StudentDTO s_DTO2 = studentDAO.getinfo(s_id);
			
			int sub_hakjum2 = subjectDTO2.getSub_hakjum();
			int s_hakjum2 = s_DTO2.getS_max();
			int hakjum2 = sub_hakjum2 + s_hakjum2;
			
			studentDAO.plusSubject(sub_code, s_id, s_name);
			studentDAO.updatehakjum(hakjum2, s_id);
			queDAO.insertStuDabjiList(s_name, s_id);
		}
		return "redirect:/student/s_subject.so?type="+number;
	}
	
	/*수강신청 삭제*/
	@RequestMapping("/deleteProcess.so")
	public String stuCon6(HttpSession session, @RequestParam(value="subject") String sub_code) {
		int s_id = Integer.parseInt((String) session.getAttribute("s_id"));
		
		ssubjectDTO subjectDTO = studentDAO.getssubjectDTO(sub_code);
		StudentDTO s_DTO = studentDAO.getinfo(s_id); //학생정보 DTO
		String sub_name = subjectDTO.getSub_name(); //선택과목 이름
		
		int sub_hakjum = subjectDTO.getSub_hakjum();
		int s_hakjum = s_DTO.getS_max();
		int hakjum = s_hakjum-sub_hakjum;
		
		studentDAO.deleteSubject(sub_code, s_id);
		studentDAO.updatehakjum(hakjum, s_id);
		queDAO.deleteStuDabjiList(sub_name, s_id);
		//시험 성적 처리에서 삭제
		studentDAO.deletelecture(s_id, sub_name);
		
		return "redirect:/student/s_subject.so?type=4";
	}
	
	/*s_schedule로 이동*/
	@RequestMapping("/s_schedule.so")
	public String stuCon7(Model model, HttpSession session) {
		/*나의 수강과목 가져오기*/
		int s_id = Integer.parseInt((String) session.getAttribute("s_id"));
		ArrayList<ssubjectDTO> mylist = studentDAO.mySubject(s_id);
		
		model.addAttribute("mylist", mylist);
		
		/*시간표 출력 전처리*/
		Map<String, ssubjectDTO> tmap = new HashMap<>();
		
		for(int i=0; i<mylist.size(); i++) {
			ssubjectDTO dto = mylist.get(i);
			String day = dto.getSub_day();
			int hakjum = dto.getSub_hakjum();
			int start = dto.getSub_time();
			
			for(int j=start; j<start+hakjum; j++) {
				tmap.put(day+j, dto);
			}
		}
		
		model.addAttribute("tmap", tmap);
		
		return "student/s_schedule";
	}
	
	/*s_exam로 이동*/
	@RequestMapping("/s_exam.so")
	public String stuCon8(Model model, HttpSession session, HttpServletRequest request,
			@RequestParam(required=false) String subject_name) {
		/*나의 수강과목 가져오기*/
		int s_id = Integer.parseInt((String) session.getAttribute("s_id"));
		ArrayList<ssubjectDTO> mylist = studentDAO.mySubject(s_id);
		
		model.addAttribute("mylist", mylist);
		
		/* 시험이 출제되었는지 확인 */
		ArrayList<QuestionDTO> isTest_List = queDAO.isTest(mylist);
		
		model.addAttribute("isTest_List", isTest_List);
		
		//
		Enumeration<String> enu = request.getParameterNames();
		ArrayList<String> arr = new ArrayList<String>();
		while(enu.hasMoreElements()){
			String ans = (String)enu.nextElement();
			arr.add(ans);
		}
		String ans = "";
		for(int i = 0; i < arr.size()-1; i++){
			ans = ans + request.getParameter(arr.get(i));
		}
		
		/*제출 답안 저장*/
		queDAO.stu_insertAnswer(ans, subject_name, s_id);
		
		/*이미 답안을 제출했는지 확인*/
		ArrayList<QuestionDTO> queDTO_List = new ArrayList<QuestionDTO>();
		
		for(int i=0; i < mylist.size(); i++){
			ssubjectDTO subDTO = mylist.get(i);
			QuestionDTO queDTO = queDAO.stu_getAnswer(subDTO.getSub_name(), s_id);
			queDTO_List.add(queDTO);
		}
		
		model.addAttribute("queDTO_List", queDTO_List);
		
		return "student/s_exam";
	}
	
	/*s_popup_exam로 이동*/
	@RequestMapping("/s_popup_exam.so")
	public String stuCon9(Model model, @RequestParam(value="subject") String sub_name) {
		/*해당 과목 문제 가져오기*/
		ArrayList<QuestionDTO> queDTO_list = queDAO.getExam(sub_name);
		model.addAttribute("sub_name", sub_name);
		model.addAttribute("queDTO_list", queDTO_list);
		
		return "student/s_popup_exam";
	}
	
	/*s_inquiry로 이동*/
	@RequestMapping("/s_inquiry.so")
	public String stuCon10(Model model, HttpSession session) {
		/*성적조회 페이지 - 내 성적 들고오기*/
		int s_id = Integer.parseInt((String) session.getAttribute("s_id"));
		ArrayList<lectureDTO> scorelist = studentDAO.inquirylist(s_id);
		
		model.addAttribute("scorelist", scorelist);
		
		/*성적조회 페이지 - 이수구분 들고오기*/
		ArrayList<ssubjectDTO> isuhakjum = new ArrayList<ssubjectDTO>();
		
		for(int i=0; i<scorelist.size(); i++) {
			String subject = scorelist.get(i).getSub_name();
			ssubjectDTO dto = studentDAO.isuhakjumlist(subject);
			isuhakjum.add(dto);
		}
		
		model.addAttribute("isuhakjum", isuhakjum);
		
		/*성적조회 페이지 - 학점, 평점계산*/
		int totalhakjum=0; //총 신청학점
		int gethakjum=0; //총 취득학점
		float finalscore=0; //총 평점
		String average="0.0";
		
		//총 신청학점
		for(int i=0; i<isuhakjum.size(); i++) {
			totalhakjum += isuhakjum.get(i).getSub_hakjum();
		}
		
		//총 취득학점
		for(int i=0; i<scorelist.size(); i++) {
			if(!scorelist.get(i).getLec_score().equals("F")) {
				if(scorelist.get(i).getSub_name().equals(isuhakjum.get(i).getSub_name())) {
					gethakjum += isuhakjum.get(i).getSub_hakjum();
				}
			}
		}
		
		int count=0;
		//평균평점
		for(int i=0; i<scorelist.size(); i++) {
			String score = scorelist.get(i).getLec_score();
			if(score.equals("A+")) {
				finalscore += 4.5;
			}else if(score.equals("A")) {
				finalscore += 4.0;
			}else if(score.equals("B+")) {
				finalscore += 3.5;
			}else if(score.equals("B")) {
				finalscore += 3.0;
			}else if(score.equals("C+")) {
				finalscore += 2.5;
			}else if(score.equals("C")) {
				finalscore += 2.0;
			}else if(score.equals("D+")) {
				finalscore += 1.5;
			}else if(score.equals("D+")) {
				finalscore += 1.0;
			}else if(score.equals("F")) {
				count++;
			}
		}
		
		// 총학점/(과목수-F학점과목수)
		average = String.valueOf((float) finalscore/(scorelist.size()-count));
		if (average.equals("NaN")) {
			average = "0.0";
		}
		
		model.addAttribute("totalhakjum", totalhakjum);
		model.addAttribute("gethakjum", gethakjum);
		model.addAttribute("average", average);
		
		return "student/s_inquiry";
	}

}
