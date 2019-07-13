package top.simba1949.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/7/12 14:25
 */
@Data
public class UserDTO implements Serializable {
	private static final long serialVersionUID = 5572396433234547850L;
	private Integer id;
	private String username;
	private String password;
	private Date birthday;
	private Boolean adultFlag;
	private List<String> stringList;
}
