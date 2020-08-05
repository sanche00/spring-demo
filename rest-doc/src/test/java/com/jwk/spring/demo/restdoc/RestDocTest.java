package com.jwk.spring.demo.restdoc;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwk.spring.demo.controller.RestDocDemoController;
import com.jwk.spring.demo.dto.RestDocDemoDTO.GetRes;
import com.jwk.spring.demo.dto.RestDocDemoDTO.PostReq;
import com.jwk.spring.demo.dto.RestDocDemoDTO.PostRes;
import com.jwk.spring.demo.service.RestDocService;
import com.jwk.spring.demo.service.RestDocServiceImpl;

@ExtendWith(value = { RestDocumentationExtension.class, SpringExtension.class })
@WebMvcTest(value = { RestDocDemoController.class ,RestDocServiceImpl.class}) // (2)
@AutoConfigureRestDocs // (3)
public class RestDocTest {
	
	protected MockMvc mockMvc;
	private RestDocumentationResultHandler document;

	@Autowired
	ObjectMapper objectMapper;
	@MockBean
	private RestDocService restDocService;
	@BeforeEach
	public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
		this.document = MockMvcRestDocumentation.document("{method-name}", Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
				Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation)).alwaysDo(document).build();
	}
	
	
	@Test
	public void retGetTest() throws Exception {
		Mockito.when(restDocService.get(ArgumentMatchers.any())).thenReturn(GetRes.builder().id("id").idx(1).name("name").build());
		mockMvc.perform(RestDocumentationRequestBuilders.get("/restDoc/get/{id}", 1L).accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isOk()).andDo(document.document(
						RequestDocumentation.pathParameters(RequestDocumentation.parameterWithName("id").description("Member's id")),
						PayloadDocumentation.responseFields(PayloadDocumentation.fieldWithPath("id").description("아이디입니다."),
								PayloadDocumentation.fieldWithPath("name").description("이름입니다."),
								PayloadDocumentation.fieldWithPath("idx").description("index 입니다."))))
		;
	}
	
	@Test
	public void retPostTest() throws Exception {
		
		String jsonStr = objectMapper.writeValueAsString(PostReq.builder().id("id").idx(1).name("name").build()); 
		Mockito.when(restDocService.post(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(PostRes.builder().id("id").idx(1).name("name").build());
		mockMvc.perform(RestDocumentationRequestBuilders
				.post("/restDoc/post/{id}", 1L)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonStr))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(document.document(
						RequestDocumentation.pathParameters(RequestDocumentation.parameterWithName("id").description("Member's id")),
						PayloadDocumentation.requestFields(PayloadDocumentation.fieldWithPath("id").description("아이디입니다."),
								PayloadDocumentation.fieldWithPath("name").description("이름입니다."),
								PayloadDocumentation.fieldWithPath("idx").description("index 입니다.")),
						PayloadDocumentation.responseFields(PayloadDocumentation.fieldWithPath("id").description("아이디입니다."),
								PayloadDocumentation.fieldWithPath("name").description("이름입니다."),
								PayloadDocumentation.fieldWithPath("idx").description("index 입니다."))))
		;
	}
}
