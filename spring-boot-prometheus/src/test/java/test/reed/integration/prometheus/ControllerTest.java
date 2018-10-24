package test.reed.integration.prometheus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import com.alibaba.fastjson.JSON;
import com.reed.integration.prometheus.PrometheusApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PrometheusApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest {

	private MockMvc mockMvc; // 模拟MVC对象，通过MockMvcBuilders.webAppContextSetup(this.wac).build()初始化。

	@Autowired
	private WebApplicationContext wac; // 注入WebApplicationContext

	@Before
	public void setup() {
		// MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testEdit() throws Exception {
		String t = "test";
		MvcResult result = mockMvc
				.perform(post("/apps/edit").contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(t)))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andReturn();// 返回执行请求的结果
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void testView() throws Exception {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("id", "1");
		MvcResult r = mockMvc.perform(post("/apps/view").params(map)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();// 返回执行请求的结果
		System.out.println(r.getResponse().getContentAsString());
	}

	@Test	
	public void testPage() throws Exception {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("page", "1");
		map.add("size", "2");
		map.add("appName", "test");
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/apps/list").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.params(map))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andReturn();// 返回执行请求的结果
		System.out.println(result.getResponse().getContentAsString());

	}
}
