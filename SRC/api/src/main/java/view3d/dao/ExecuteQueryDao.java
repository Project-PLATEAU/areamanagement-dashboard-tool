package view3d.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

@Transactional
public class ExecuteQueryDao {
	private String url;
	private String username;
	private String password;
	
	public ExecuteQueryDao(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * queryを実行しデータを取得する
	 * @param sql　
	 * @return　List<Map<String, Object>>
	 * @throws Exception
	 */
	public List<Map<String, Object>> executeQuery(String sql) throws Exception {
		List<Map<String, Object>> res = new ArrayList<>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;

		try {
			con = DriverManager.getConnection(url, username, password);
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			rsmd= rs.getMetaData();
			while (rs.next()) {
				Map<String, Object> tmp = new HashMap<>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				   tmp.put(rsmd.getColumnName(i), rs.getObject(i));
				}
				res.add(tmp);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
			if (con != null && !con.isClosed()) {
				con.close();
			}
		}

		return res;
	}
}
