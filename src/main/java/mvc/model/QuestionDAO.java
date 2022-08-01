package mvc.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import mvc.modeldto.QuestionDTO;
import mvc.modeldto.ssubjectDTO;

public class QuestionDAO{
	
	private JdbcTemplate jT;
	
	public QuestionDAO(DataSource dataSource) {
		this.jT = new JdbcTemplate(dataSource);
	}

	/* DB에서 시험 문제 가져오기 / 교수, 학생 */
	public ArrayList<QuestionDTO> getExam(String sub_name){
		
		String sql = "select * from exam1 where sub_name=?";
		RowMapper<QuestionDTO> mapper = new RowMapper<QuestionDTO>() {

			@Override
			public QuestionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				QuestionDTO queDTO = new QuestionDTO();
				queDTO.setSub_name(rs.getString("sub_name"));
				queDTO.setEx_num(rs.getInt("ex_num"));
				String contents = rs.getString("ex_contents");
				contents = contents.replace("<br>", "\r\n");
				queDTO.setEx_contents(contents);
				queDTO.setEx_ans(rs.getInt("ex_ans"));
				
				return queDTO;
			}
		};
		
		ArrayList<QuestionDTO> queDTO_list = (ArrayList<QuestionDTO>) jT.query(sql, mapper, sub_name);
		return queDTO_list;
	}
	
	/* 문제 정답 업데이트 / 교수 */
	public void updateAnswer(String ans, String subject_name){
		char num;
		String ans_num;
		
		String sql = "update exam1 set ex_ans=? where sub_name=? and ex_num=?";
		for(int i=0; i<ans.length(); i++) {
			num = ans.charAt(i);
			ans_num = Character.toString(num);
			
			jT.update(sql, ans_num, subject_name, i+1);
		}
	}
	
	/* 정답리스트 업데이트 / 교수 */
	public void insertAnswer(String ans, String subject_name){
		//System.out.println(ans+"-------QuestionDAO : insertAnswer");
		//System.out.println(subject_name+"-------QuestionDAO : insertAnswer");
		String sql1 = "select * from exam2 where sub_name=?";
		String anslist = null;
		
		RowMapper<String> mapper = new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				String answer = rs.getString("ex_ans2");
				return answer;
			}
			
		};
		
		List<String> list = jT.query(sql1, mapper, subject_name);
		if(list.size()!=0) {
			anslist = list.get(0);
		}
		
		String sql2 = "insert into exam2(sub_name, ex_ans2) values(?,?)";
		String sql3 = "update exam2 set ex_ans2=? where sub_name=?";
		if (!(ans.equals("")) && anslist == null) {
			jT.update(sql2, subject_name, ans);
		}else {
			jT.update(sql3, ans, subject_name);
		}
	}
	
	/* 제출 답안 리스트 업데이트 / 학생 */
	public void stu_insertAnswer(String ans, String subject_name, int s_id){
		String sql = "update answer set ans_answer=? where sub_name=? and s_id=?";
		jT.update(sql, ans, subject_name, s_id);
	}
	
	/* 수강신청 시 공백 제출 답안 리스트 생성 / 학생 */
	public void insertStuDabjiList(String s_name, int s_id){
		
		String sql = "insert into answer(ans_answer, sub_name, s_id) values(?,?,?)";
		jT.update(sql, "", s_name, s_id);
	}
	
	/* 수강취소 시 공백 제출 답안 리스트 삭제 / 학생 */
	public void deleteStuDabjiList(String s_name, int s_id){
		
		String sql = "delete from answer where sub_name=? and s_id=?";
		jT.update(sql, s_name, s_id);
	}
	
	/* 제출 답안이 있는지 확인 / 학생 */
	public QuestionDTO stu_getAnswer(String subject_name, int s_id){
		QuestionDTO queDTO = null;
		
		String sql = "select * from answer where sub_name=? and s_id=?";
		RowMapper<QuestionDTO> mapper = new RowMapper<QuestionDTO>() {

			@Override
			public QuestionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				QuestionDTO queDTO = new QuestionDTO();
				queDTO.setAnslist(rs.getString("ans_answer"));
				queDTO.setSub_name(rs.getString("sub_name"));
				queDTO.setS_id(rs.getInt("s_id"));
				return queDTO;
			}
		};
		List<QuestionDTO> queList = jT.query(sql, mapper, subject_name, s_id);

		if(queList.size()!=0) {
			queDTO = queList.get(0);
		}
		return queDTO;
	}
	
	/* 시험응시 가능한(문제가 출제되어있는지) 상태인지 확인 ==>jsp와 함수내용다름*/
	public ArrayList<QuestionDTO> isTest(ArrayList<ssubjectDTO> mySubList){
		
		String sql = "select * from exam2";
		
		RowMapper<QuestionDTO> mapper = new RowMapper<QuestionDTO>() {

			@Override
			public QuestionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				QuestionDTO queDTO = new QuestionDTO();
				queDTO.setAnslist(rs.getString("ex_ans2"));
				queDTO.setSub_name(rs.getString("sub_name"));
				return queDTO;
			}
		};
		
		ArrayList<QuestionDTO> examlist = (ArrayList<QuestionDTO>) jT.query(sql, mapper);
		
		Map<String, QuestionDTO> map = new HashMap<String, QuestionDTO>();
		
		//전체과목이랑 나의 과목이랑 비교해서 있는 것만 map담음
		for(int i=0; i<mySubList.size(); i++) {
			String subject = mySubList.get(i).getSub_name();
			for(int j=0; j<examlist.size(); j++) {
				QuestionDTO dto = examlist.get(j);
				String exam = dto.getSub_name();
				if(subject.equals(exam)) {
					map.put(subject, dto);
				}
			}
		}
		
		//나의 과목이 map에 존재하는지 확인
		for(int i=0; i<mySubList.size(); i++) {
			String subject = mySubList.get(i).getSub_name();
			if(!map.containsKey(subject)) {
				QuestionDTO dto = new QuestionDTO();
				dto.setAnslist("");
				dto.setSub_name(subject);
				map.put(subject, dto);
			}
		}
		
		ArrayList<QuestionDTO> queDTO_List = new ArrayList<QuestionDTO>();
		
		Iterator<String> keys = map.keySet().iterator(); //컬렉션에 저장된 요소를 읽어옴
		while(keys.hasNext()) {
			String key = keys.next();
			queDTO_List.add(map.get(key));
		}
		return queDTO_List;
	}
	
	/*성적산출페이지 : 답안지 들고오기*/
	public String AnswerSheet(String sub_name, String s_id) {
		
		String Answer="";		
		int sid = Integer.valueOf(s_id);
		
		String sql = "select ans_answer from answer where s_id =? and sub_name=?";
		
		RowMapper<String> mapper = new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				String ans = rs.getString("ans_answer");
				return ans;
			}
		};
		
		List<String> result = jT.query(sql, mapper, sid, sub_name);
		
		if(result.size()!=0) {
			Answer = result.get(0);
		}
		return Answer;
	}
	
	/*성적산출페이지 : 정답 들고오기*/
	public String CorrectSheet(String sub_name) {
		String correct="";
		String sql = "select ex_ans2 from exam2 where sub_name=?";
		
		RowMapper<String> mapper = new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				String cor = rs.getString("ex_ans2");
				return cor;
			}
		};
		
		List<String> result = jT.query(sql, mapper, sub_name);
		
		if(result.size()!=0) {
			correct = result.get(0);
		}

		return correct;
	}
}
