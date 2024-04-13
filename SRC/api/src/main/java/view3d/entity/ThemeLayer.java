package view3d.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the page_layer database table.
 * 
 */
@Data
@Entity
@Table(name="theme_layer")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ThemeLayer implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ThemeLayerPK id;

	@Column(name="disp_order")
	private Integer dispOrder;

	@Column(name="post_flag", length=1)
	private String postFlag;

}