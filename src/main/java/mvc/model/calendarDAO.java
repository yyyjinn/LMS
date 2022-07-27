package mvc.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import mvc.modeldto.calendarDTO;

public class calendarDAO {
	
	private JdbcTemplate jT;
	
	public calendarDAO(DataSource dataSource) {
		this.jT = new JdbcTemplate(dataSource);
	}
	
	/*총 일정이 몇개인지 가져오는 메서드*/
	public int getListCount() {
		
		String sql="select count(*) from calendar";
		Integer x = jT.queryForObject(sql, Integer.class);
		
		return x;
	}
	
	/*캘린더의 상세정보를 가져오는 메서드*/
	public ArrayList<calendarDTO> getCallist(int page, int limit){
		
		//int totalofcalendar = getListCount();
		int start = (page -1)*limit;
		int index = start;
		
		String sql = "select * from calendar limit "+index+", 4";
		
		RowMapper<calendarDTO> mapper = new RowMapper<calendarDTO>() {

			@Override
			public calendarDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				calendarDTO calendar = new calendarDTO();
				
				calendar.setCal_num(rs.getInt("cal_num"));
				calendar.setDate1(rs.getString("cal_date1"));
				calendar.setDate2(rs.getString("cal_date2"));
				calendar.setCal_contents(rs.getString("cal_contents"));
				
				return calendar;
			}
		};
			
		ArrayList<calendarDTO> list = (ArrayList<calendarDTO>) jT.query(sql, mapper);
	
		return list;

	}
	
}