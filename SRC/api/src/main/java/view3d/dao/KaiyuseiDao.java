package view3d.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

@Transactional
public class KaiyuseiDao {

	private String url;
	private String username;
	private String password;

	@Autowired
	public KaiyuseiDao(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	/**
	 * {tableName}テーブルが存在するかどうかチェックする
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public String existTable(String tableName) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String res = null;
		try {
			con = DriverManager.getConnection(url, username, password);
			String sql = "SELECT table_name FROM information_schema.tables WHERE table_name = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, tableName);
			rs = ps.executeQuery();
			if (rs.next()) {
				res = rs.getString(1);
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

	/**
	 * kaiyuusei_{number}テーブルからダッシュボード及び3DViewer表示に必要なデータを取得
	 * @param number
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getMoveNumSum(int number) throws Exception {
		List<Map<String, Object>> res = new ArrayList<>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DriverManager.getConnection(url, username, password);
			String sql = "SELECT \"移動経路\", \"合計\",\"距離\",ST_AsText(ST_Centroid(geom)) FROM kaiyuusei_"+number+"  ORDER BY \"合計\" DESC ;";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			int idx = 1;
			while (rs.next()) {
				idx = 1;
				Map<String, Object> tmp = new HashMap<>();
				tmp.put("移動経路", rs.getString(idx++));
				tmp.put("合計", rs.getDouble(idx++));
				//高さ：距離/2
				tmp.put("高さ", rs.getDouble(idx++));
				String[] point=rs.getString(idx++).split(" ");
				//point："POINT(longitude latitude)"の形
				tmp.put("緯度", Double.parseDouble(point[1].split("\\)")[0]));
				tmp.put("経度", Double.parseDouble(point[0].split("\\(")[1]));
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

	/**
	 * accessspot_{number}テーブルからダッシュボード及び3DViewer表示に必要なデータを取得
	 * @param number
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getFavSpot(int number) throws Exception {
		List<Map<String, Object>> res = new ArrayList<>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DriverManager.getConnection(url, username, password);
			String sql = "SELECT \"スポット名\", \"合計\",latitude,longitude FROM accessspot_"+number+"  ORDER BY \"合計\" DESC ;";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();

			int idx = 1;
			while (rs.next()) {
				idx = 1;
				Map<String, Object> tmp = new HashMap<>();
				tmp.put("スポット名", rs.getString(idx++));
				//高さ：合計+60
				Double sum=rs.getDouble(idx++);
				tmp.put("合計", sum);
				tmp.put("高さ", sum+60);
				tmp.put("緯度", rs.getDouble(idx++));
				tmp.put("経度", rs.getDouble(idx++));
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
