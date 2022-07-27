package mvc.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import mvc.modeldto.StudentDTO;
import mvc.modeldto.departmentDTO;
import mvc.modeldto.lectureDTO;
import mvc.modeldto.ssubjectDTO;

public class StudentDAO {
	
	private JdbcTemplate jT;
	
	public StudentDAO(DataSource dataSource) {
		this.jT = new JdbcTemplate(dataSource);
	}
	
	/* info 페이지에서 정보 가져오기 */
	public StudentDTO getinfo(int s_id) {
		
		String sql = "select * from student where s_id=?";
		RowMapper<StudentDTO> mapper = new RowMapper<StudentDTO>() {

			@Override
			public StudentDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				StudentDTO dto = new StudentDTO();
				dto.setS_id(rs.getInt("s_id"));
				dto.setS_name(rs.getString("s_name"));
				dto.setS_college(rs.getString("s_college"));
				dto.setS_grade(rs.getInt("s_grade"));
				dto.setD_name(rs.getString("d_name"));
				dto.setS_email(rs.getString("s_email"));
				dto.setS_state(rs.getString("s_state"));
				dto.setS_address(rs.getString("s_address"));
				dto.setS_pNumber(rs.getString("s_pNumber"));
				dto.setS_account1(rs.getString("s_account1"));
				dto.setS_account2(rs.getString("s_account2"));
				dto.setS_account3(rs.getString("s_account3"));
				dto.setS_max(rs.getInt("s_max"));
				return dto;
			}
		};
		StudentDTO dto = jT.queryForObject(sql, mapper, s_id);
		return dto;
	}

	/* info페이지에서 수정:정보 업데이트 */
	public void update(String s_address, String s_pNumber, String s_email, String s_account1, String s_account2,
			String s_account3, String s_id) {
		
		String sql = "update student set s_address=?, s_pNumber=?, s_email=?, "
				+ "s_account1=?, s_account2=?, s_account3=? where s_id=?";
		jT.update(sql, s_address, s_pNumber, s_email, s_account1, s_account2,
				s_account3, s_id);
	}

	/* 수강신청페이지 : 과목의 총갯수, 선택학과의 총갯수 가져오기 */
	public int s_getListCount(String major) {
		Integer x = 0;
		
		String sql1 = "select count(*) from ssubject";
		String sql2 = "select count(*) from ssubject where d_name=?";
		
		if(major == null || major.equals("major_all")) {
			x = jT.queryForObject(sql1, Integer.class);
		}else {
			x = jT.queryForObject(sql2, Integer.class, major);
		}
		return x;
	}

	/* 수강신청페이지 : 검색과목 몇개인지 들고오기 */
	public int s_searchListCount(String namecode) {
		
		String sql1 = "select count(*) from ssubject where sub_code=?";
		String sql2 = "select count(*) from ssubject where sub_name like '%" + namecode + "%'";
		
		Integer x;
		
		if(namecode.charAt(0) == 'S' || namecode.charAt(0) == 's') {
			x = jT.queryForObject(sql1, Integer.class, namecode);
		}else {
			x = jT.queryForObject(sql2, Integer.class);
		}
		return x;
	}

	/* 수강신청페이지 : 검색 과목DTO DB에서 가져오기 */
	public ArrayList<ssubjectDTO> searchSubjectList(int page, int limit, String namecode) {
//		int TotalOfSubject = s_searchListCount(namecode);
		int start = (page - 1) * limit;
		int index = start;
		
		ArrayList<ssubjectDTO> list = new ArrayList<ssubjectDTO>();
		
		String sql1 = "select * from ssubject where sub_code=? limit "+index+", 5";
		String sql2 = "select * from ssubject where sub_name like '%" + namecode + "%' limit "+index+", 5";
		
		RowMapper<ssubjectDTO> mapper = new RowMapper<ssubjectDTO>() {

			@Override
			public ssubjectDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				ssubjectDTO subject = new ssubjectDTO();
				
				subject.setD_name(rs.getString("d_name"));
				subject.setP_id(rs.getString("p_id"));
				subject.setP_name(rs.getString("p_name"));
				subject.setSub_allday(rs.getInt("sub_allday"));
				subject.setSub_code(rs.getString("sub_code"));
				subject.setSub_day(rs.getString("sub_day"));
				subject.setSub_hakjum(rs.getInt("sub_hakjum"));
				subject.setSub_isu(rs.getString("sub_isu"));
				subject.setSub_name(rs.getString("sub_name"));
				subject.setSub_time(rs.getInt("sub_time"));
				subject.setSub_room(rs.getInt("sub_room"));
				subject.setSub_classtime(rs.getString("sub_classtime"));
				return subject;
			}
		};
		
		if (namecode.charAt(0) == 'S' || namecode.charAt(0) == 's') {
			list = (ArrayList<ssubjectDTO>) jT.query(sql1, mapper, namecode);
		}else {
			list = (ArrayList<ssubjectDTO>) jT.query(sql2, mapper);
		}
		return list;
	}

	/* 수강신청페이지 : 학과불러오기 */
	public ArrayList<departmentDTO> getMajor() {

		String sql = "select * from department";
		RowMapper<departmentDTO> mapper = new RowMapper<departmentDTO>() {

			@Override
			public departmentDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				departmentDTO dto = new departmentDTO();
				dto.setD_code(rs.getString("d_code"));
				dto.setD_name(rs.getString("d_name"));
				return dto;
			}
		};
		
		ArrayList<departmentDTO> majorlist = (ArrayList<departmentDTO>) jT.query(sql, mapper);

		return majorlist;
	}

	/* 수강신청페이지 : 선택학과나 모든 과목DTO DB에서 가져오기 */
	public ArrayList<ssubjectDTO> s_getSubjectList(int page, int limit, String major) {
		//int TotalOfSubject = s_getListCount(major);
		int start = (page - 1) * limit;
		int index = start;
		
		ArrayList<ssubjectDTO> list = new ArrayList<ssubjectDTO>();
		
		String sql1 = "select * from ssubject limit "+index+", 5";
		String sql2 = "select * from ssubject where d_name=? limit "+index+", 5";
		
		RowMapper<ssubjectDTO> mapper = new RowMapper<ssubjectDTO>() {

			@Override
			public ssubjectDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				ssubjectDTO subject = new ssubjectDTO();
				
				subject.setD_name(rs.getString("d_name"));
				subject.setP_id(rs.getString("p_id"));
				subject.setP_name(rs.getString("p_name"));
				subject.setSub_allday(rs.getInt("sub_allday"));
				subject.setSub_code(rs.getString("sub_code"));
				subject.setSub_day(rs.getString("sub_day"));
				subject.setSub_hakjum(rs.getInt("sub_hakjum"));
				subject.setSub_isu(rs.getString("sub_isu"));
				subject.setSub_name(rs.getString("sub_name"));
				subject.setSub_time(rs.getInt("sub_time"));
				subject.setSub_room(rs.getInt("sub_room"));
				subject.setSub_classtime(rs.getString("sub_classtime"));
				subject.setSub_max(rs.getInt("sub_max"));
				
				return subject;
			}
		};
		
		if ((major == null) || (major.equals("major_all"))) {
			list = (ArrayList<ssubjectDTO>) jT.query(sql1, mapper);
		}else {
			list = (ArrayList<ssubjectDTO>) jT.query(sql2, mapper, major);
		}

		return list;
	}

	/* 수강신청페이지 : 해당과목 DB에서 가져오기 */
	public ssubjectDTO getssubjectDTO(String sub_code) {
		
		String sql = "select * from ssubject where sub_code=?";
		RowMapper<ssubjectDTO> mapper = new RowMapper<ssubjectDTO>() {

			@Override
			public ssubjectDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				ssubjectDTO subject = new ssubjectDTO();
				
				subject.setD_name(rs.getString("d_name"));
				subject.setP_id(rs.getString("p_id"));
				subject.setP_name(rs.getString("p_name"));
				subject.setSub_allday(rs.getInt("sub_allday"));
				subject.setSub_code(rs.getString("sub_code"));
				subject.setSub_day(rs.getString("sub_day"));
				subject.setSub_hakjum(rs.getInt("sub_hakjum"));
				subject.setSub_isu(rs.getString("sub_isu"));
				subject.setSub_name(rs.getString("sub_name"));
				subject.setSub_time(rs.getInt("sub_time"));
				subject.setSub_room(rs.getInt("sub_room"));
				subject.setSub_classtime(rs.getString("sub_classtime"));
				subject.setSub_max(rs.getInt("sub_max"));
				
				return subject;
			}
		};
		ssubjectDTO subject = jT.queryForObject(sql, mapper, sub_code);
		return subject;
	}

	/* 수강신청페이지:학생 최대학점 업데이트 / 학점 더하거나 학점 빼기*/
	public void updatehakjum(int hakjum, int s_id) {
		String sql = "update student set s_max=? where s_id=?";
		jT.update(sql, hakjum, s_id);
	}

	/* 수강신청페이지 : 해당과목 수강인원 */
	public int numberOfstudent(String sub_code) {
		String sql = "select count(*) from application where sub_code=?";
		Integer x = jT.queryForObject(sql, Integer.class, sub_code);
		return x;
	}

	/* 수강신청페이지 : 수강신청한 과목 DB에 넣기 */
	public void plusSubject(String sub_code, int s_id, String sub_name) {
		
		String sql = "insert into application(s_id, sub_name, sub_code) values(?,?,?)";
		jT.update(sql, s_id, sub_name, sub_code);
	}

	/* 수강신청페이지 : 내 수강신청 리스트 */
	public ArrayList<ssubjectDTO> mySubject(int s_id) {

		String sql = "select * from ssubject where sub_name in(select sub_name from application where s_id=?) order by sub_time";
		RowMapper<ssubjectDTO> mapper = new RowMapper<ssubjectDTO>() {

			@Override
			public ssubjectDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				ssubjectDTO subject = new ssubjectDTO();
				
				subject.setD_name(rs.getString("d_name"));
				subject.setP_id(rs.getString("p_id"));
				subject.setP_name(rs.getString("p_name"));
				subject.setSub_allday(rs.getInt("sub_allday"));
				subject.setSub_code(rs.getString("sub_code"));
				subject.setSub_day(rs.getString("sub_day"));
				subject.setSub_hakjum(rs.getInt("sub_hakjum"));
				subject.setSub_isu(rs.getString("sub_isu"));
				subject.setSub_name(rs.getString("sub_name"));
				subject.setSub_time(rs.getInt("sub_time"));
				subject.setSub_room(rs.getInt("sub_room"));
				subject.setSub_classtime(rs.getString("sub_classtime"));
				subject.setSub_max(rs.getInt("sub_max"));
				
				return subject;
			}
		};
		
		ArrayList<ssubjectDTO> list = (ArrayList<ssubjectDTO>) jT.query(sql, mapper, s_id);
		return list;
	}

	/* 수강신청페이지 : 내 수강과목 갯수 확인하기 */
	public int countmySubject(int s_id) {
		
		String sql = "select count(*) from application where s_id=? ";
		Integer x = jT.queryForObject(sql, Integer.class, s_id);
		return x;
	}

	/* 수강신청페이지 : 선택한 과목 수강신청에서 삭제*/
	public void deleteSubject(String sub_code, int s_id) {
		String sql = "delete from application where s_id=? and sub_code=?";
		jT.update(sql, s_id, sub_code);
	}
	
	/*수강신청페이지 : 삭제한 과목 성적처리에서 삭제*/
	public void deletelecture(int s_id, String sub_name) {
		String sql = "delete from lecture where s_id=? and sub_name=?";
		jT.update(sql, s_id, sub_name);
	}
	
	
	/* 시간표페이지 : 내 수강신청 리스트를 요일별로 정렬*/
	public ArrayList<ssubjectDTO> lineupWeek(ArrayList<ssubjectDTO> mylist, String day){
		ArrayList<ssubjectDTO> weeklist = new ArrayList<ssubjectDTO>();
		
		for(int i=0; i<mylist.size(); i++) {
			ssubjectDTO dto = mylist.get(i);
			String sub_day = dto.getSub_day();
			if(sub_day.equals(day)) {
				weeklist.add(dto);
			}
		}
		return weeklist;
	}

	/* 성적조회페이지 : 성적조회 목록 가져오기*/
	public ArrayList<lectureDTO> inquirylist(int s_id){
		String sql = "select * from lecture where s_id=?";
		RowMapper<lectureDTO> mapper = new RowMapper<lectureDTO>() {

			@Override
			public lectureDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				lectureDTO score = new lectureDTO();
				score.setS_id(rs.getInt("s_id"));
				score.setSub_name(rs.getString("sub_name"));
				score.setLec_score(rs.getString("lec_score"));
				score.setLec_no_date(rs.getInt("lec_no_date"));
				score.setLec_year(rs.getInt("lec_year"));
				score.setLec_semester(rs.getInt("lec_semester"));

				return score;
			}
			
		};
		
		ArrayList<lectureDTO> scorelist = (ArrayList<lectureDTO>) jT.query(sql, mapper, s_id);

		return scorelist;
	}

	/* 성적조회페이지 : 과목(이수구분, 학점)들고오기 */
	public ssubjectDTO isuhakjumlist(String subject){
		
		String sql = "select * from ssubject where sub_name=?";
		RowMapper<ssubjectDTO> mapper = new RowMapper<ssubjectDTO>() {

			@Override
			public ssubjectDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				ssubjectDTO dto = new ssubjectDTO();
				
				dto.setSub_isu(rs.getString("sub_isu"));
				dto.setSub_hakjum(rs.getInt("sub_hakjum"));
				dto.setSub_name(rs.getString("sub_name"));
				return dto;
			}
		};
		
		ssubjectDTO dto = jT.queryForObject(sql, mapper, subject);
		return dto;
	}

}