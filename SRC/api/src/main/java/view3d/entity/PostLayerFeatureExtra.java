package view3d.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the graph_table database table.
 * 
 */
@Data
@Entity
@Table(name="post_layer_feature")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostLayerFeatureExtra implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@GeneratedValue(
	        strategy = GenerationType.SEQUENCE,
	        generator = "post_layer_feature_feature_id_seq")
    @SequenceGenerator(
        name = "post_layer_feature_feature_id_seq",
        sequenceName = "post_layer_feature_feature_id_seq",
        initialValue = 1,
        allocationSize = 1)
	@Id
	@Column(name="feature_id", unique=true, nullable=false,updatable=false)
	private Integer featureId;
	
	@Column(name="layer_id")
	private Integer layerId;
	
	@Column(name="publish_flag")
	private String publishFlag;
	
	private String longitude;
	
	private String latitude;
	
	@Column(name="post_user_id")
	private Integer postUserId;
	
	@Column(name="post_datetime")
	private LocalDateTime postDatetime;
	
	@Column(name="parent_feature_id")
	private Integer parentFeatureId;
	
	@Column(name="item_1")
	private String item1;
	
	@Column(name="item_2")
	private String item2;
	
	@Column(name="item_3")
	private String item3;
	
	@Column(name="item_4")
	private String item4;
	
	@Column(name="item_5")
	private String item5;
	
	@Column(name="item_6")
	private String item6;
	
	@Column(name="item_7")
	private String item7;
	
	@Column(name="item_8")
	private String item8;
	
	@Column(name="item_9")
	private String item9;
	
	@Column(name="item_10")
	private String item10;

}
