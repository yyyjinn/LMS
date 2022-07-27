package mvc.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import mvc.modeldto.notice_boardDTO;

public class notice_boardDAO{
	
	private JdbcTemplate jT;
	
	public notice_boardDAO(DataSource dataSource) {
		this.jT = new JdbcTemplate(dataSource);
	};

	/*공지사항 게시글 갯수*/
	public int getListCount(String items, String text){
		Integer x;
		
		String sql1 = "select count(*) from notice";
		String sql2 = "select count(*) from notice where " + items + " like '%" + text + "%'";
		
		if(items==null && text==null) {
			x = jT.queryForObject(sql1, Integer.class);
		}else {
			x = jT.queryForObject(sql2, Integer.class);
		}
		
		return x;
	}
	
	/*공지사항 게시글 갯수, 오버로딩*/
	public int getListCount(){
		String sql = "select count(*) from notice";
		Integer x = jT.queryForObject(sql, Integer.class);	
		return x;
	}
	
	/*공지사항 목록 가져오기*/
	public ArrayList<notice_boardDTO> getBoardList(int pageNum, int limit, String items, String text){

		//int total_record = getListCount(items, text);
		int start = (pageNum - 1) * limit;
		int index = start;
		
		String sql;
		
		ArrayList<notice_boardDTO> list = null;
		
		RowMapper<notice_boardDTO> mapper = new RowMapper<notice_boardDTO>() {
			@Override
			public notice_boardDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				notice_boardDTO nbDTO = new notice_boardDTO();
				
				nbDTO.setN_num(rs.getInt("n_num"));
				nbDTO.setN_subject(rs.getString("n_subject"));
				nbDTO.setN_date(rs.getString("n_date"));
				nbDTO.setP_department(rs.getString("p_department"));
				nbDTO.setP_oNumber(rs.getString("p_oNumber"));
				nbDTO.setN_contents(rs.getString("n_contents"));
				nbDTO.setP_name(rs.getString("p_name"));
				nbDTO.setP_id(rs.getString("p_id"));
				
				return nbDTO;
			}
		};
		
		if(items==null && text==null) {
			sql = "select * from notice order by n_num desc limit "+index+", 10";
			list = (ArrayList<notice_boardDTO>) jT.query(sql, mapper);
		}else {
			sql = "select * from notice where " + items + " like '%" + text + "%' order by n_num desc limit "+index+", 10";
			list = (ArrayList<notice_boardDTO>) jT.query(sql, mapper);
		}
		return list;
	}
	
	/*공지사항 목록 가져오기, 오버로딩*/
	public ArrayList<notice_boardDTO> getBoardList(int pageNum, int limit){
		//int total_record = getListCount();
		int start = (pageNum - 1) * limit;
		int index = start;
		
		String sql = "select * from notice order by n_num desc limit "+index+", 4";
		
		RowMapper<notice_boardDTO> mapper = new RowMapper<notice_boardDTO>() {
			@Override
			public notice_boardDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				notice_boardDTO nbDTO = new notice_boardDTO();
				
				nbDTO.setN_num(rs.getInt("n_num"));
				nbDTO.setN_subject(rs.getString("n_subject"));
				nbDTO.setN_date(rs.getString("n_date"));
				nbDTO.setP_department(rs.getString("p_department"));
				nbDTO.setP_oNumber(rs.getString("p_oNumber"));
				nbDTO.setN_contents(rs.getString("n_contents"));
				nbDTO.setP_name(rs.getString("p_name"));
				nbDTO.setP_id(rs.getString("p_id"));
				
				return nbDTO;
			}
		};
		
		ArrayList<notice_boardDTO> nblist = (ArrayList<notice_boardDTO>) jT.query(sql, mapper);

		return nblist;
	}

	/*새 글 작성 시 데이터 집어넣기*/
	public void insertBoard(notice_boardDTO nbDTO){
		String sql = "insert into notice(n_subject, n_date, p_department, p_oNumber, n_filename, n_realname, n_contents, p_name, p_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		jT.update(sql, nbDTO.getN_subject(), nbDTO.getN_date(), nbDTO.getP_department(),
				nbDTO.getP_oNumber(), nbDTO.getN_filename(), nbDTO.getN_realname(),
				nbDTO.getN_contents(), nbDTO.getP_name(), nbDTO.getP_id());
	}
	
	/*제목 클릭 시 해당글 가져오기*/
	public notice_boardDTO getClick_title(int n_num){
		
		String sql = "select * from notice where n_num = ? ";
		RowMapper<notice_boardDTO> mapper = new RowMapper<notice_boardDTO>() {

			@Override
			public notice_boardDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				notice_boardDTO nbDTO = new notice_boardDTO();
				nbDTO.setN_subject(rs.getString("n_subject"));
				nbDTO.setN_date(rs.getString("n_date"));
				nbDTO.setP_department(rs.getString("p_department"));
				nbDTO.setP_oNumber(rs.getString("p_oNumber"));
				nbDTO.setN_filename(rs.getString("n_filename"));
				nbDTO.setN_realname(rs.getString("n_realname"));
				nbDTO.setN_contents(rs.getString("n_contents"));
				nbDTO.setP_id(rs.getString("p_id"));
				return nbDTO;
			}
		};
		
		notice_boardDTO nbDTO = jT.queryForObject(sql, mapper, n_num);
		return nbDTO;
	}
	
	/*글 수정페이지 : 글정보 가져오기*/
	//위 함수랑 합칠 수 있을 것 같은데,, 합쳐보기
	public notice_boardDTO editform(int n_num){
		
		String sql = "select * from notice where n_num = ? ";
		
		RowMapper<notice_boardDTO> mapper = new RowMapper<notice_boardDTO>() {

			@Override
			public notice_boardDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				notice_boardDTO nbDTO = new notice_boardDTO();
				nbDTO = new notice_boardDTO();
				nbDTO.setN_subject(rs.getString("n_subject"));
				nbDTO.setP_department(rs.getString("p_department"));
				String contents = rs.getString("n_contents");
				contents = contents.replace("<br>", "\r\n");
				nbDTO.setN_contents(contents);
				return nbDTO;
			}	
		};
		
		notice_boardDTO nbDTO = jT.queryForObject(sql, mapper, n_num);
		return nbDTO;	
	}
	
	/*수정된 글 업데이트*/
	public void update_editnotice(int n_num, String title, String contents, String write_day){
		
		String sql = "update notice set n_subject=?, n_date=?, n_contents=? where n_num=?";
		jT.update(sql, title, write_day, contents, n_num);
	}
	
	/*선택된 글 삭제*/
	public void deleteDAO(int n_num){
		
		String sql = "delete from notice where n_num=?";
		jT.update(sql, n_num);
	}

}