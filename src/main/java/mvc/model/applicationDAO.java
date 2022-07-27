package mvc.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import mvc.modeldto.StudentDTO;

public class applicationDAO {
	
	private JdbcTemplate jT;
	
	public applicationDAO(DataSource dataSource) {
		this.jT = new JdbcTemplate(dataSource);
	}
	
	/* 과목명으로 수강신청한 인원 구하기*/
	public int getCount(String sub_name){
		Integer x = jT.queryForObject("select count(*) from application where sub_name=?", Integer.class, sub_name);
		return x;
	}
	
	public ArrayList<StudentDTO> getStudentList(String listfirst){
		
		String sql = "select * from student where s_id in (select s_id from application where sub_name=?)";
		
		RowMapper<StudentDTO> mapper = new RowMapper<StudentDTO>() {

			@Override
			public StudentDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				StudentDTO dto = new StudentDTO();
				dto.setS_id(rs.getInt("s_id"));
				dto.setS_name(rs.getString("s_name"));
				
				return dto;
			}
		
		};
		ArrayList<StudentDTO> results = (ArrayList<StudentDTO>) jT.query(sql, mapper, listfirst);
		return results;
	}
	
	public ArrayList<StudentDTO> get_Stu_list(String subject_name){
		
		String sql1 = "select * from application where sub_name=?";
		RowMapper<Integer> mapper1 = new RowMapper<Integer>() {

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				int s_id = rs.getInt("s_id");
				return s_id;
			}
			
		};
		
		ArrayList<Integer> stuId = (ArrayList<Integer>) jT.query(sql1, mapper1, subject_name);
		
		ArrayList<StudentDTO> stuArr = new ArrayList<StudentDTO>();
		
		String sql2 = "select * from student where s_id=?";
		RowMapper<StudentDTO> mapper2 = new RowMapper<StudentDTO>() {

			@Override
			public StudentDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				StudentDTO stuDTO = new StudentDTO();
				stuDTO.setS_id(rs.getInt("s_id"));
				stuDTO.setS_name(rs.getString("s_name"));
				stuDTO.setD_name(rs.getString("d_name"));
				stuDTO.setS_grade(rs.getInt("s_grade"));
				return stuDTO;
			}
		};
		
		for(int i=0; i<stuId.size(); i++) {
			int id = stuId.get(i);
			StudentDTO dto = jT.queryForObject(sql2, mapper2, id);
			stuArr.add(dto);
		}
		return stuArr;
	}
}
