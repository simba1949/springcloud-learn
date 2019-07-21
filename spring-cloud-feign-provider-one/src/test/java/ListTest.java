import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SIMBA1949
 * @date 2019/7/12 15:34
 */
public class ListTest {

	@Test
	public void listAddTest(){
		List<String> list = new ArrayList<>();
		for (int i = 0; i < 10; i++){
			list.add("str" + i);
		}

		list.add(1, "君不见黄河之水天上来");


		list.forEach(item -> System.out.println(item));
	}
}
