package mvc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import mvc.model.PersonalDAO;
import mvc.model.notice_boardDAO;
import mvc.modeldto.PersonalDTO;
import mvc.modeldto.notice_boardDTO;

@Controller
@RequestMapping("/board")
public class notice_boardController{
	
	@Autowired
	notice_boardDAO noticeDAO;
	
	@Autowired
	PersonalDAO personalDAO;
	
	static final int LISTCOUNT = 10;
	
	/*교수 : 게시글 목록 가져오기*/
	@RequestMapping("/p_noticeboardlist.notice")
	public String notice1(Model model, @RequestParam(defaultValue="1") int pageNum, 
			@RequestParam(required=false) String items, @RequestParam(required=false) String text) {
		// 공지사항 게시글 가져오기
		int limit = LISTCOUNT;
		
		int total_record=noticeDAO.getListCount(items, text); // 글 갯수 가져오기
		ArrayList<notice_boardDTO> nblist = noticeDAO.getBoardList(pageNum, limit, items, text); // 전제 글 가져온 list
		
		int total_page; // 게시글로 인해 나와야 하는 총 페이지 수
		
		if (total_record % limit == 0){
			total_page =total_record/limit;
		} else{
			total_page = total_record/limit;
			total_page = total_page + 1;
		}
		
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("total_page", total_page);
		model.addAttribute("nblist", nblist);
		
		return "board/p_noticeboardlist";
	}
	
	/*학생 : 게시글 목록 가져오기*/
	@RequestMapping("/s_noticeboardlist.notice")
	public String notice2(Model model, @RequestParam(defaultValue="1") int pageNum, 
			@RequestParam(required=false) String items, @RequestParam(required=false) String text) {
		// 공지사항 게시글 가져오기
		int limit = LISTCOUNT;
		
		int total_record=noticeDAO.getListCount(items, text); // 글 갯수 가져오기
		ArrayList<notice_boardDTO> nblist = noticeDAO.getBoardList(pageNum, limit, items, text); // 전제 글 가져온 list
		
		int total_page; // 게시글로 인해 나와야 하는 총 페이지 수
		
		if (total_record % limit == 0){
			total_page =total_record/limit;
		} else{
			total_page = total_record/limit;
			total_page = total_page + 1;
		}
		
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("total_page", total_page);
		model.addAttribute("nblist", nblist);
				
		return "board/s_noticeboardlist";
	}
	
	/*새 글 작성 시 */
	@RequestMapping("/p_noticewrite.notice")
	public String notice3(Model model, HttpSession session) {
		// 글쓰기 버튼 클릭 시, 로그인 되어있는 사람의 id에 따라 부서나 과 입력해주기
		String p_id = (String) session.getAttribute("p_id");
		
		PersonalDTO per_dto = personalDAO.getinfo(p_id);
		
		model.addAttribute("p_id", p_id);
		model.addAttribute("dto", per_dto);
		
		return "board/p_noticewrite";
	}
	
	/*글 저장*/
	@RequestMapping("/p_noticewrite_update.notice")
	public String notice4(Model model, HttpServletRequest request) {
		String realFolder = request.getSession().getServletContext().getRealPath("resource/upload");
		int maxSize = 10 * 1024 * 1024;
		
		try{	
			MultipartRequest multi = new MultipartRequest(request, realFolder, maxSize, "utf-8", new DefaultFileRenamePolicy());
			
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd");
			String regist_day = formatter.format(new java.util.Date());
			
			String contents = multi.getParameter("summernote");
			contents = contents.replace("\r\n", "<br>");
			
			String filename = multi.getFilesystemName("filename");
			String realname = multi.getOriginalFileName("filename");
			
			notice_boardDTO nbDTO = new notice_boardDTO();
			nbDTO.setN_subject(multi.getParameter("title"));
			nbDTO.setN_date(regist_day);
			nbDTO.setP_department(multi.getParameter("p_department"));
			nbDTO.setP_oNumber(multi.getParameter("p_oNumber"));
			nbDTO.setN_contents(contents);
			nbDTO.setP_name(multi.getParameter("p_name"));
			nbDTO.setP_id(multi.getParameter("p_id"));
			nbDTO.setN_filename(filename);
			nbDTO.setN_realname(realname);
			
			noticeDAO.insertBoard(nbDTO);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "redirect:/board/p_noticeboardlist.notice";
	}
	
	/*교수 : 글 상세보기*/
	@RequestMapping("/p_notice.notice")
	public String notice5(Model model, @RequestParam int n_num, @RequestParam int pageNum) {
		//제목 클릭 시 상세보기 페이지
		notice_boardDTO nbDTO = noticeDAO.getClick_title(n_num);
		
		model.addAttribute("n_num", n_num);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("nbDTO", nbDTO);
		return "board/p_notice";
	}
	
	/*학생 : 글 상세보기*/
	@RequestMapping("/s_notice.notice")
	public String notice6(Model model, @RequestParam int n_num, @RequestParam int pageNum) {
		//제목 클릭 시 상세보기 페이지
		notice_boardDTO nbDTO = noticeDAO.getClick_title(n_num);
		
		model.addAttribute("n_num", n_num);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("nbDTO", nbDTO);
		return "board/s_notice";
	}
	
	/*교수, 학생 : 파일다운로드*/
	@RequestMapping("/download.notice")
	public void notice7(@RequestParam(value="n_filename") String filename,
			@RequestParam(value="n_realname") String realname, HttpServletRequest request, HttpServletResponse response) {
		//파일이 있는 절대경로
				String folder = request.getServletContext().getRealPath("resource/upload");
				
				//파일의 절대경로
				String filePath = folder + "/" + filename;
				
				try{
					//파일 경로를 이용하여 파일 생성, 파일의 크기만큼 바이트 배열 만듦
					File file = new File(filePath);
					byte b[] = new byte[(int) file.length()];
					
					//page의 ContentType등을 동적으로 바꾸기 위해 초기화
					response.reset();
					response.setContentType("application/octet-stream");
					
					//한글 인코딩
					String encoding = new String(realname.getBytes("euc-kr"),"8859_1");
					
					//파일 링크를 클릭했을 때 다운로드 저장 화면이 출력되게 처리하는 부분
					response.setHeader("Content-Disposition", "atachement;filename="+ encoding);
					response.setHeader("Content-Length", String.valueOf(file.length()));
					
					//파일이 있을 경우
					if(file.isFile()){
						FileInputStream fInputSt = new FileInputStream(file);
						ServletOutputStream sOutputSt = response.getOutputStream();
						
						//파일을 읽어서 클라이언트에 저장
						int readNum =0;
						
						while((readNum = fInputSt.read(b)) != -1){
							sOutputSt.write(b, 0, readNum);
						}
						sOutputSt.close();
						fInputSt.close();
					}
				}catch (Exception e){
					System.out.println("file_down Exception : " + e.getMessage());
				}
	}
	
	/*수정하기*/
	@RequestMapping("/p_editnotice.notice")
	public String notice8(Model model, @RequestParam int n_num, @RequestParam int pageNum) {
		//글 수정 페이지로 이동
		notice_boardDTO nbDTO = noticeDAO.editform(n_num);
		
		model.addAttribute("n_num", n_num);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("nbDTO", nbDTO);
		
		return "board/p_editnoticewrite";
	}
	
	/*수정한 내용 업로드*/
	@RequestMapping("/p_editnoticewrite_update.notice")
	public String notice9(Model model, @RequestParam int n_num, @RequestParam int pageNum, HttpServletRequest request) {
		//수정 버튼 눌렀을 때
		String realFolder = request.getSession().getServletContext().getRealPath("resource/upload");
		int maxSize = 10 * 1024 * 1024;
		
		int[] num = {n_num, pageNum};
		
		try{
			MultipartRequest multi = new MultipartRequest(request, realFolder, maxSize, "utf-8", new DefaultFileRenamePolicy());

			String title = multi.getParameter("title");
			String contents = multi.getParameter("summernote");
			contents = contents.replace("\r\n", "<br>");
			
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy/MM/dd");
			String write_day = formatter.format(new java.util.Date());
			
			noticeDAO.update_editnotice(n_num, title, contents, write_day);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return "/board/p_notice.notice?n_num=" + num[0] + "&pageNum=" + num[1];
	}
	
	/*삭제하기*/
	@RequestMapping("/p_deletenotice.notice")
	public String notice10(@RequestParam int n_num, @RequestParam int pageNum) {
		noticeDAO.deleteDAO(n_num);
		
		return "/board/p_noticeboardlist.notice?pageNum=" + pageNum;
	}

}