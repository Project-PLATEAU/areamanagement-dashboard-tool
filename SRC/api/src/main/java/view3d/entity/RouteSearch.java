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

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RouteSearch  implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 結果ID */
	@Id
	@Column(name="result_id")
	private int resultId;
	
	/** 優先度 */
	@Column(name="priority")
	private int priority;
	
	/** 総距離 */
	@Column(name="distance")
	private Double distance;
	
	/** 経路探索結果GeoJson */
	@Column(name="geojson")
	private String geojson;
	
	/** 経路探索結果の最小経度 */
	@Column(name="minlon")
	private String minlon;
	
	/** 経路探索結果の最大経度 */
	@Column(name="maxlon")
	private String maxlon;
	
	/** 経路探索結果の最小緯度 */
	@Column(name="minlat")
	private String minlat;
	
	/** 経路探索結果の最大緯度 */
	@Column(name="maxlat")
	private String maxlat;
}
