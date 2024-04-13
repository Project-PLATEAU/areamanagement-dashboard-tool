package view3d.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ChochomokukaiErimane implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** id　*/
	@Id
	@Column(name="id", unique=true, nullable=false,updatable=false)
	private Integer id;

	/** キーコード　*/
	@Column(name="key_code")
	private String keyCode;

	/** 都道府県コード　*/
	@Column(name="pref")
	private String pref;

	/** 自治体コード　*/
	@Column(name="city")
	private String city;

	/** 町丁目コード　*/
	@Column(name="s_area")
	private String sArea;

	/** 都道府県名　*/
	@Column(name="pref_name")
	private String prefName;

	/** 自治体名　*/
	@Column(name="city_name")
	private String cityName;

	/** 町丁目名　*/
	@Column(name="s_name")
	private String sName;

	/** エリアマネジメント団体種別名　*/
	@Column(name="area_management_type")
	private String areaManagementType;
	
	/** 最小座標（経度）　*/
	@Column(name="lon")
	private Double lon;
	
	/**  最小座標（緯度） */
	@Column(name="lat")
	private Double lat;
	
	/** 最小座標（経度）　*/
	@Column(name="min_lon")
	private Double minLon;
	
	/**  最小座標（緯度） */
	@Column(name="min_lat")
	private Double minLat;
	
	/**  最大座標（経度） */
	@Column(name="max_lon")
	private Double maxLon;
	
	/**  最大座標（経度） */
	@Column(name="max_lat")
	private Double maxLat;
	
	
}
