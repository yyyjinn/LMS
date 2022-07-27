package mvc.model;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDAO{
	private JdbcTemplate jT;
	
	public MemberDAO(DataSource dataSource) {
		this.jT = new JdbcTemplate(dataSource);
	}

	/*회원가입 : 주민등록번호로 학번/직번 조회*/
	public String[] get_NameId(String jumin1, String jumin2){
		String[] nameId = {null, null, null, null}; //이름, 아이디, 비밀번호, 임의변수
		List<String[]> list = null;
		
		String sql1 = "select s_name, s_id, s_passwd from student where s_regNumber1=? and s_regNumber2=?";
		String sql2 = "select p_name, p_id, p_passwd from personal where p_regNumber1=? and p_regNumber2=?";
		
		RowMapper<String[]> mapper1 = new RowMapper<String[]>() {

			@Override
			public String[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				String[] result = new String[4];
				result[0] = rs.getString("s_name");
				result[1] = rs.getString("s_id");
				if(rs.getString("s_passwd")==null) {
				//비밀번호가 없으면
					result[2] = "0";
					result[3] = "0";
				}
				else {
					//비밀번호가 있으면
					result[2] = "1";
					result[3] = "1";
				}
				return result;
			}
		};
		
		RowMapper<String[]> mapper2 = new RowMapper<String[]>() {

			@Override
			public String[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				String[] result = new String[4];
				result[0] = rs.getString("p_name");
				result[1] = rs.getString("p_id");
				if(rs.getString("p_passwd")==null) {
				//비밀번호가 없으면
					result[2] = "0";
					result[3] = "0";
				}
				else {
					//비밀번호가 있으면
					result[2] = "1";
					result[3] = "1";
				}
				return result;
			}
		};
		
		list = (List<String[]>) jT.query(sql1, mapper1, jumin1, jumin2);
		if(list.size()==0) {
			list = (List<String[]>) jT.query(sql2, mapper2, jumin1, jumin2);
		}
		if(list.size()!=0) {
			nameId = list.get(0);
		}
		return nameId;
	}
	
	/*회원가입 : 비밀번호 초기 세팅*/
	public void update_pw(String id, String pw){
		String sql1 = "update personal set p_passwd=? where p_id=?";
		String sql2 = "update student set s_passwd=? where s_id=?";
		
		if(id.charAt(0)=='p') {
			jT.update(sql1, pw, id);
		}
		else {
			jT.update(sql2, pw, id);
		}
	}
	
	/*로그인*/
	public String[] login(String id, String pw){
		String[] IdPwNum = {null, null, null};
		List<String[]> list = null;
		
		String sql1 = "select p_name from personal where p_id=? and p_passwd=?";
		String sql2 = "select s_name from student where s_id=? and s_passwd=?";
		
		RowMapper<String[]> mapper1 = new RowMapper<String[]>(){

			@Override
			public String[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				String[] result = new String[3];
				result[0] = id;
				result[1] = rs.getString("p_name");
				result[2] = "1";
				return result;
			}
			
		};
		
		RowMapper<String[]> mapper2 = new RowMapper<String[]>(){

			@Override
			public String[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				String[] result = new String[3];
				result[0] = id;
				result[1] = rs.getString("s_name");
				result[2] = "2";
				return result;
			}
		};
		
		if(id.charAt(0)=='p') {
			list = jT.query(sql1, mapper1, id, pw);
		}else {
			list = jT.query(sql2, mapper2, id, pw);
		}
		
		if(list.size()!=0) {
			IdPwNum = list.get(0);
		}
		
		if(IdPwNum[0]==null && IdPwNum[1]==null && IdPwNum[2]==null) {
			IdPwNum[2] = "3";
		}
		return IdPwNum;
	}
	
	
	/*회원정보 : 비밀번호 일치여부*/
	public int checkpw(String id, String pw) {
		Integer x;
		String sql1 = "select count(*) from personal where p_id=? and p_passwd=?";
		String sql2 = "select count(*) from student where s_id=? and s_passwd=?";
		
		if(id.charAt(0)=='p') {
			x = jT.queryForObject(sql1, Integer.class, id, pw);
		}else {
			x = jT.queryForObject(sql2, Integer.class, id, pw);
		}
		return x;
	}
	
	/*회원정보 : 비밀번호 변경*/
	public void changepasswd(String id, String pw_new) {
		
		String sql1 = "update personal set p_passwd=? where p_id=?";
		String sql2 = "update student set s_passwd=? where s_id=?";
		
		if(id.charAt(0)=='p') {
			jT.update(sql1, pw_new, id);
		}else {
			jT.update(sql2, pw_new, id);
		}	
	}
}


