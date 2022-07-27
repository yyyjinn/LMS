package mvc.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import mvc.modeldto.PersonalDTO;
import mvc.modeldto.lectureDTO;

public class PersonalDAO{
	
private JdbcTemplate jT;
	
	public PersonalDAO(DataSource dataSource) {
		this.jT = new JdbcTemplate(dataSource);
	}
	
	/*info페이지에서 정보 가져오기*/
	public PersonalDTO getinfo(String p_id) {
		
		String sql = "select * from personal where p_id=?";
		RowMapper<PersonalDTO> mapper = new RowMapper<PersonalDTO>() {

			@Override
			public PersonalDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				PersonalDTO dto = new PersonalDTO();
				dto.setP_id(rs.getString("p_id"));
				dto.setP_name(rs.getString("p_name"));
				dto.setP_college(rs.getString("p_college"));
				dto.setP_department(rs.getString("p_department"));
				dto.setP_major(rs.getString("p_major"));
				dto.setP_office(rs.getString("p_office"));
				dto.setP_oNumber(rs.getString("p_oNumber"));
				dto.setP_email(rs.getString("p_email"));
				return dto;
			}
		};
		PersonalDTO dto = jT.queryForObject(sql, mapper, p_id);
		return dto;
	}
	
	/*info페이지에서 수정 : 정보 업데이트*/
	public void update(String p_adress, String p_phone, String p_email, String p_id){
		String sql = "update personal set p_office=?, p_oNumber=?, p_email=? where p_id=?";
		jT.update(sql, p_adress, p_phone, p_email, p_id);
	}

	/*과목 수업일수 들고오기*/
	public String getstudyday(String sub_name) {
		String sql = "select * from ssubject where sub_name=?";
		RowMapper<String> mapper = new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return String.valueOf(rs.getInt("sub_allday"));
			}
		};
		
		String suballday = jT.queryForObject(sql, mapper, sub_name);
		
		return suballday;
	}

	/*강의테이블에서 과목명+수강학생 결석일, 학점 들고오기*/
	public lectureDTO getlec(String sub_name, String s_id) {
		
		String sql = "select * from lecture where sub_name=? and s_id=?";
		RowMapper<lectureDTO> mapper = new RowMapper<lectureDTO>() {

			@Override
			public lectureDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				lectureDTO dto = new lectureDTO();
				dto.setLec_no_date(rs.getInt("lec_no_date"));
				dto.setLec_score(rs.getString("lec_score"));
				return dto;
			}
		};
		List<lectureDTO> result = jT.query(sql, mapper, sub_name, s_id);
		
		return result.isEmpty()? new lectureDTO() : result.get(0);
	}

	/*강의테이블에 학점, 결석일, 년도, 학기 넣기*/
	public void putScore(String sub_name, String s_id, String lec_point, int absence) {
		
		int sid = Integer.valueOf(s_id);
		
		Calendar ld = Calendar.getInstance();
		int year = ld.get(Calendar.YEAR);
		int month = ld.get(Calendar.MONTH)+1;
		int semester = 0;
		
		if(month>=1&&month<=6) {
			semester = 1;
		}
		else if(month>=7&&month<=12) {
			semester = 2;
		}
		
		String sql = "insert into lecture (s_id, sub_name, lec_score, lec_no_date, lec_year, lec_semester) values(?,?,?,?,?,?)";
		jT.update(sql, sid, sub_name, lec_point, absence, year, semester);
	}
	
	/*강의테이블에 업데이트*/
	public void updateScore(String sub_name, String s_id, String lec_point, int absence) {
		int sid = Integer.valueOf(s_id);
		
		String sql = "update lecture set lec_score=?, lec_no_date=? where s_id = ? and sub_name=?";
		jT.update(sql, lec_point, absence, sid, sub_name);		
	}
	
}