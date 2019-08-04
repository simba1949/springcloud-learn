package top.simba1949.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author SIMBA1949
 * @date 2019/8/3 11:11
 */
@Data
public class User implements Serializable {
	private static final long serialVersionUID = 2474529628017078524L;
	private Long id;
	private String username;
	private Date birthday;
}
