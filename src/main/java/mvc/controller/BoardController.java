package mvc.controller;

import com.oreilly.servlet.*;
import com.oreilly.servlet.multipart.*;
import java.util.*;
import java.io.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mvc.model.BoardDAO;
import mvc.model.PersonalDAO;
import mvc.model.StudentDAO;
import mvc.modeldto.BoardDTO;
import mvc.modeldto.PersonalDTO;
import mvc.modeldto.ssubjectDTO;

@Controller
@RequestMapping("/board")
public class BoardController{

	static final int LISTCOUNT = 10;
	
	@Autowired
	BoardDAO boardDAO;
	
	@Autowired
	StudentDAO studentDAO;
	
	@Autowired
	PersonalDAO personalDAO;
	
	/* 교수 : 게시판 목록 DB에서 가져오기*/
	@RequestMapping("/ListAction1.go")
	public String board1(Model model, HttpSession session,
			@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "sub_all") String subjects) {
		
		//System.out.println(subjects);
		/*교수(나의) 과목 DB에서 가져오기*/
		String p_id = (String)session.getAttribute("p_id");
		ArrayList<String> sub_list = boardDAO.getmySubject(p_id);
		model.addAttribute("ssubject", sub_list);
		
		/*게시판 목록(게시글 Arraylist) DB에서 가져오기*/
		ArrayList<BoardDTO> boardlist = new ArrayList<BoardDTO>();
		
		int limit = LISTCOUNT; //한페이지에 10개 출력
		int TotalOfPost = boardDAO.getListCount(subjects);
		boardlist = boardDAO.getBoardList(pageNum, limit, subjects);
		
		int total_page;
		
		if(TotalOfPost==0) {
			total_page=1;
		}
		else if(TotalOfPost % limit == 0) {
			total_page = TotalOfPost/limit;
		}
		else {
			total_page =TotalOfPost/limit;
			total_page =  total_page + 1;
		}
		
		//int로 model에 addAttribute할 경우 에러가 발생할 수 있으므로, Integer나 String으로 형변환
		String page = String.valueOf(pageNum);
		String total = String.valueOf(total_page);
		
		model.addAttribute("pageNum", page);
		model.addAttribute("total_page", total);
		model.addAttribute("boardlist", boardlist);
		model.addAttribute("search", subjects);

		return "board/p_boardlist";
	}
	
	/*학생 : 게시판 목록 DB에서 가져오기*/
	@RequestMapping("/ListAction2.go")
	public String board2(Model model, HttpSession session,
			@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "sub_all") String subjects) {
		
		/*학생(나의) 과목 DB에서 가져오기*/
		String sid = (String) session.getAttribute("s_id");
		int s_id = Integer.parseInt(sid); 
		ArrayList<ssubjectDTO> mylist = studentDAO.mySubject(s_id);
		model.addAttribute("mylist", mylist);
		
		/*게시판 목록(게시글 Arraylist) DB에서 가져오기*/
		ArrayList<BoardDTO> boardlist = new ArrayList<BoardDTO>();
		
		int limit = LISTCOUNT; //한페이지에 10개 출력
		int TotalOfPost = boardDAO.getListCount(subjects);
		boardlist = boardDAO.getBoardList(pageNum, limit, subjects);
		
		int total_page;
		
		if(TotalOfPost==0) {
			total_page=1;
		}
		else if(TotalOfPost % limit == 0) {
			total_page = TotalOfPost/limit;
		}
		else {
			total_page =TotalOfPost/limit;
			total_page =  total_page + 1;
		}
		
		//int로 model에 addAttribute할 경우 에러가 발생할 수 있으므로, Integer나 String으로 형변환
		String page = String.valueOf(pageNum);
		String total = String.valueOf(total_page);
		
		model.addAttribute("pageNum", page);
		model.addAttribute("total_page", total);
		model.addAttribute("boardlist", boardlist);
		model.addAttribute("search", subjects);
		
		return "board/s_boardlist";
	}
	
	/*교수만 : 글쓰기페이지로 이동*/
	@RequestMapping("/p_write.go")
	public String board3(Model model, HttpSession session) {
		
		/*교수(나의) 과목 DB에서 가져오기*/
		String p_id = (String)session.getAttribute("p_id");
		ArrayList<String> sub_list = boardDAO.getmySubject(p_id);
		model.addAttribute("ssubject", sub_list);
		
		/*교수(나의) 정보 PersonalDAO에서 가져오기*/
		PersonalDTO dto = personalDAO.getinfo(p_id);
		model.addAttribute("p_id", p_id);
		model.addAttribute("dto", dto);
		
		return "board/p_write";
	}
	
	/*교수만 : 작성글 업로드*/
	@RequestMapping("/p_postupload.go")
	public String board4(Model model, HttpServletRequest request) {
		
		/*작성글 DB에 저장하기*/
		String realFolder = request.getSession().getServletContext().getRealPath("resource/upload");
		int maxSize = 10 * 1024 * 1024;
		
		try{ //파일 업로드 안할 수도 있으니까 예외처리
			MultipartRequest multi = new MultipartRequest(request, realFolder, maxSize, "utf-8", new DefaultFileRenamePolicy());
			
			String filename = multi.getFilesystemName("filename");
			String realname = multi.getOriginalFileName("filename");
			String contents = multi.getParameter("summernote");
			contents = contents.replace("\r\n", "<br>");
			
			
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd");
			String write_day = formatter.format(new java.util.Date());

			BoardDTO board = new BoardDTO();
			
			board.setP_id(multi.getParameter("id"));
			board.setSub_name(multi.getParameter("select_subject"));
			board.setPo_subject(multi.getParameter("title"));
			board.setP_oNumber(multi.getParameter("phonenum"));
			board.setP_name(multi.getParameter("p_name"));
			board.setPo_filename(filename);
			board.setN_contents(contents);
			board.setPo_date(write_day);
			board.setPo_realname(realname);
			
			boardDAO.insertBoard(board);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/board/ListAction1.go";
	}
	
	/*교수 : 게시판 글 상세보기*/
	@RequestMapping("/p_post.go")
	public String board5(Model model, @RequestParam int pageNum, @RequestParam int num) {
		
		/*게시글 상세페이지 DB에서 가져오기*/
		BoardDTO board = boardDAO.getBoardByNum(num);
		
		model.addAttribute("num", num);
		model.addAttribute("page", pageNum);
		model.addAttribute("board", board);
		return "board/p_post";
	}
	
	/*학생 : 게시판 글 상세보기*/
	@RequestMapping("/s_post.go")
	public String board6(Model model, @RequestParam int pageNum, @RequestParam int num) {
		
		/*게시글 상세페이지 DB에서 가져오기*/
		BoardDTO board = boardDAO.getBoardByNum(num);
		
		model.addAttribute("num", num);
		model.addAttribute("page", pageNum);
		model.addAttribute("board", board);
		return "board/s_post";
	}
	
	/*교수, 학생 : 파일다운로드*/
	@RequestMapping("/download.go")
	public void board7(@RequestParam(value="po_filename") String filename, 
			@RequestParam(value="po_realname") String realname, HttpServletRequest request, HttpServletResponse response) {
		
		/*첨부된 파일 다운로드 할 수 있게 하기*/
		//파일이 있는 절대경로
		String folder = request.getServletContext().getRealPath("resource/upload");
		
		//파일의 절대경로
		String filePath = folder + "/" + filename;
		
		try {
			//파일 경로를 이용하여 파일 생성, 파일의 크기만큼 바이트 배열 만듦
			File file = new File(filePath);
			byte b[] = new byte[(int) file.length()];
			
			//page의 ContentType등을 동적으로 바꾸기 위해 초기화
			response.reset();
			response.setContentType("application/octet-stream");
			
			//한글 인코딩(utf-8로 할 경우, 파일명이 깨짐)
			String encoding = new String(realname.getBytes("euc-kr"),"8859_1");
			
			//파일 링크를 클릭했을 때 다운로드 저장 화면이 출력되게 처리하는 부분
			response.setHeader("Content-Disposition", "atachement;filename="+ encoding);
			response.setHeader("Content-Length", String.valueOf(file.length()));
			
			//파일이 있을 경우
			if(file.isFile()) {
				FileInputStream fInputSt = new FileInputStream(file);
				ServletOutputStream sOutputSt = response.getOutputStream();
				
				//파일을 읽어서 클라이언트에 저장
				int readNum =0;
				while((readNum = fInputSt.read(b)) != -1) {
					sOutputSt.write(b, 0, readNum);
				}
				sOutputSt.close();
				fInputSt.close();
			}
		}catch(Exception e) {
			System.out.println("file_down Exception : " + e.getMessage());
		}

	}
	
	/*교수 : 글 삭제*/
	@RequestMapping("/DeleteAction.go")
	public String board8(@RequestParam int num) {
		boardDAO.deleteBoard(num);
		return "/ListAction1.go";
	}
	
	/*교수만 : 글 수정화면으로 이동*/
	@RequestMapping("/editWrite.go")
	public String board9(Model model, HttpSession session, @RequestParam int num, @RequestParam int pageNum) {
		
		/*교수(나의) 과목 DB에서 가져오기*/
		String p_id = (String)session.getAttribute("p_id");
		ArrayList<String> sub_list = boardDAO.getmySubject(p_id);
		model.addAttribute("ssubject", sub_list);
		
		/*교수(나의) 정보 PersonalDAO에서 가져오기*/
		PersonalDTO dto = personalDAO.getinfo(p_id);
		model.addAttribute("dto", dto);
		
		/*게시글 상세페이지 DB에서 가져오기*/
		BoardDTO board = boardDAO.getBoardByNum(num);
		model.addAttribute("num", num);
		model.addAttribute("page", pageNum); 
   		model.addAttribute("board", board);
		
		return "board/p_editwrite";
	}
	
	/*교수만 : 수정글 db에 업데이트*/
	@RequestMapping("/p_postupdate.go")
	public String board10(@RequestParam int num, HttpServletRequest request) {
		String realFolder = request.getSession().getServletContext().getRealPath("resource/upload");
		int maxSize = 10 * 1024 * 1024;
		
		try{ //파일 업로드 안할 수도 있으니까 예외처리
			MultipartRequest multi = new MultipartRequest(request, realFolder, maxSize, "utf-8", new DefaultFileRenamePolicy());
			
			String filename = multi.getFilesystemName("filename");
			String realname = multi.getOriginalFileName("filename");
			String contents = multi.getParameter("summernote");
			contents = contents.replace("\r\n", "<br>");
			
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd");
			String write_day = formatter.format(new java.util.Date());

			BoardDTO board = new BoardDTO();
			
			board.setPo_num(num);
			board.setSub_name(multi.getParameter("select_subject"));
			board.setPo_subject(multi.getParameter("title"));
			board.setPo_filename(filename);
			board.setN_contents(contents);
			board.setPo_date(write_day);
			board.setPo_realname(realname);
			
			boardDAO.updateBoard(board);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return "board/p_post";
	}
}
