package cn.edu.bupt.backendfinal;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.edu.bupt.backendfinal.entity.User;
import cn.edu.bupt.backendfinal.mapper.UserMapper;

@SpringBootTest
class BackendFinalApplicationTests {
	@Autowired
	private UserMapper userMapper;

	@Test
	void testSelect() {
		System.out.println(("----- selectAll method test ------"));
		List<User> userList = userMapper.selectList(null);
		Assertions.assertEquals(1, userList.size());
		userList.forEach(System.out::println);
	}

}
