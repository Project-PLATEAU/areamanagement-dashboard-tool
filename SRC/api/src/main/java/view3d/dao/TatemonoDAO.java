package view3d.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Transactional
public class TatemonoDAO {
	
	private String url;
	private String username;
	private String password;
	
	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(TatemonoDAO.class);
	
	@Value("${app.tatemono.view.epsg}")
	private Integer viewEpsg;
	
	@Value("${app.tatemono.data.epsg}")
	private Integer dataEpsg;
	
	@Value("${app.tatemono.default.height}")
	private Double defaultHeight;
	
	@Value("${app.tatemono.table}")
	private String table;
	
	@Value("${app.tatemono.height.column}")
	private String heightColumn;
	
	@Value("${app.tatemono.geom.column}")
	private String geomColumn;

	@Autowired
	public TatemonoDAO(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	/**
	 * 建物テーブルから緯度経度を元に高さ情報を取得する
	 * @param ekwt
	 * @return height
	 * @throws Exception
	 */
	public double getTatemonoHeight(String ekwt) throws Exception {
		double height = defaultHeight;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DriverManager.getConnection(url, username, password);
			String sql = "SELECT " + heightColumn + " as height "+
						"FROM  " + table + " " +
						"WHERE  ST_Intersects(" + geomColumn + ", ST_Transform(ST_GeomFromText('POINT('||'" + ekwt + "'||')', " + viewEpsg + "), " + dataEpsg + "))";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				if(rs.getInt("height") != 0) {
					height = rs.getDouble("height");
					//bufferを加算する
					height = height + 50;
				}
			}
		} catch (Exception e) {
			LOGGER.debug("建物テーブルから高さ情報の取得に失敗 EKWT:"+ekwt);
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
		return height;
	}

}
