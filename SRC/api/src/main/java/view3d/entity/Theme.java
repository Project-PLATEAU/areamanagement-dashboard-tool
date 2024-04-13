package view3d.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the page database table.
 * 
 */
@Data
@Entity
@Table(name="theme")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Theme implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="theme_id", unique=true, nullable=false,updatable=false)
	private Integer themeId;

	@Column(name="disp_order")
	private Integer dispOrder;

	@Column(name="theme_name", length=100)
	private String themeName;
	
	@Column(name="theme_group_name", length=100)
	private String themeGroupName;

	@Column(name="post_flag", length=1)
	private String postFlag;

	@Column(name="publish_flag", length=1)
	private String publishFlag;
	
	@Column(name="information_text")
	private String informationText;
	
	@Column(name="switch_flag", length=1)
	private String switchFlag;
	
	@Column(name="switch_query")
	private String switchQuery;
	
	@Column(name="switch_item_name_column_name")
	private String switchItemNameColumnName;
	
	@Column(name="switch_item_value_column_name")
	private String switchItemValueColumnName;
	
	@Column(name="switch_placeholder_name")
	private String switchPlaceholderName;
	
	@Column(name="switch_placeholder_default_value")
	private String switchPlaceholderDefaultValue;

}