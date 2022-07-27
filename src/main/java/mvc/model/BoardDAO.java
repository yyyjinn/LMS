package mvc.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import mvc.modeldto.BoardDTO;

public class BoardDAO {
	
	private JdbcTemplate jT;
	
	public BoardDAO(DataSource dataSource) {
		this.jT = new JdbcTemplate(dataSource);
	}
		
	/*총 게시글이 몇개인지 가져오는 메서드*/
	public int getListCount(String subjects) {

		String sql;
		Integer x;
		
		if((subjects == null)||(subjects.equals("sub_all"))) {
			sql = "select count(*) from post";
			x = jT.queryForObject(sql, Integer.class);
		}else {
			sql = "select count(*) from post where sub_name=?";
			x = jT.queryForObject(sql, Integer.class, subjects);
		}
		return x;
	}
	
	//////////-----------확인해보기, 파라메터 subject 지워도 되는지 확인
	/*db에 저장된 게시글의 모든 정보를 가져오는 메서드, 리턴 : 어레이리스트*/
	public ArrayList<BoardDTO> getBoardList(int page, int limit, String subjects){
		//int TotalOfPost = getListCount(subjects);
		int start = (page -1) * limit;
		int index = start;
		
		String sql;
		ArrayList<BoardDTO> list = null;
		
		RowMapper<BoardDTO> mapper = new RowMapper<BoardDTO>() {
			
			@Override
			public BoardDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				BoardDTO board = new BoardDTO();
				
				board.setPo_num(rs.getInt("po_num"));
				board.setPo_subject(rs.getString("po_subject"));
				board.setPo_date(rs.getString("po_date"));
				board.setSub_name(rs.getString("sub_name"));
				board.setP_oNumber(rs.getString("p_oNumber"));
				board.setN_contents(rs.getString("n_contents"));
				board.setP_name(rs.getString("p_name"));
				board.setP_id(rs.getString("p_id"));
				
				return board;
			}
		};
		if((subjects == null)||(subjects.equals("sub_all"))) {
			sql = "select * from post ORDER BY po_num DESC limit "+index+", 10";
			list = (ArrayList<BoardDTO>) jT.query(sql, mapper);
		}else {
			sql = "select * from post where sub_name=? ORDER BY po_num DESC limit "+index+", 10";
			list = (ArrayList<BoardDTO>) jT.query(sql, mapper, subjects);
		}
		return list;
	}

	/*db에서 내 과목 가져오는 메서드*/
	public ArrayList<String> getmySubject(String id){
		String sql = "select * from ssubject where p_id=?";
		
		RowMapper<String> mapper = new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				String sub_name = rs.getString("sub_name");
				return sub_name;
			}
		};
		
		ArrayList<String> sub_list = (ArrayList<String>) jT.query(sql, mapper, id);
		
		return sub_list;
	}
	
	/*작성글 DB에 등록하기*/
	public void insertBoard(BoardDTO board)  {
		String sql = "insert into post(po_subject, po_date, sub_name, p_oNumber, n_contents, p_name, p_id, po_filename, po_realname) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		//int result로 결과값 받아서 db에 등록이 되었는지 확인할 수 있음
		jT.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pstmt = con.prepareStatement(sql);
				
				pstmt.setString(1, board.getPo_subject());
				pstmt.setString(2, board.getPo_date());
				pstmt.setString(3, board.getSub_name());
				pstmt.setString(4, board.getP_oNumber());
				pstmt.setString(5, board.getN_contents());
				pstmt.setString(6, board.getP_name());
				pstmt.setString(7, board.getP_id());
				pstmt.setString(8, board.getPo_filename());
				pstmt.setString(9, board.getPo_realname());

				return pstmt;
			}
		});
	} 

	/*선택된 글 DB에서 상세 내용 가져오기*/
	public BoardDTO getBoardByNum(int num) {
		
		String sql = "select * from post where po_num = ?";
		RowMapper<BoardDTO> mapper = new RowMapper<BoardDTO>() {

			@Override
			public BoardDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				BoardDTO board = new BoardDTO();
			
				board.setPo_num(rs.getInt("po_num"));
				board.setPo_subject(rs.getString("po_subject"));
				board.setPo_date(rs.getString("po_date"));
				board.setSub_name(rs.getString("sub_name"));
				board.setP_oNumber(rs.getString("p_oNumber"));
				board.setN_contents(rs.getString("n_contents"));
				board.setP_name(rs.getString("p_name"));
				board.setP_id(rs.getString("p_id"));
				board.setPo_filename(rs.getString("po_filename"));
				board.setPo_realname(rs.getString("po_realname"));
				
				return board;
			}
		};
		
		BoardDTO board = jT.queryForObject(sql, mapper, num);
		return board;
	}
	
	/*선택된 글 삭제*/
	public void deleteBoard(int num) {
		String sql = "delete from post where po_num=?";
		jT.update(sql, num);
		//System.out.println(result+"개 행 삭제성공");
	}
	
	/*선택된 글 내용 업데이트*/
	public void updateBoard(BoardDTO board) {
		
		String sql = "update post set sub_name=?, po_subject=?, po_filename=?, n_contents=?, po_date=?, po_realname=? where po_num=?";
		jT.update(sql, board.getSub_name(), board.getPo_subject(), board.getPo_filename(), 
				board.getN_contents(), board.getPo_date(), board.getPo_realname());
	} 
}
