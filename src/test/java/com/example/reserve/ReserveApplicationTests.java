package com.example.reserve;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ReserveApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	@Before
	public void deleteAllBeforeTests() throws Exception {
		reservationRepository.deleteAll();
	}
	
	@Test
	public void shouldReturnRepositoryIndex() throws Exception {

		mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(
				jsonPath("$._links.reserve").exists());
	}
	
	@Test
	public void shouldCreateEntity() throws Exception {

		mockMvc.perform(post("/reserve").content(
				"{\"guestName\":\"Frodo\",\"dateFrom\":\"2018-03-09\",\"dateTo\":\"2018-03-10\",\"price\":\"110\",\"state\":\"F\"}"))
				.andExpect(status().isCreated()).andExpect(
								header().string("Location", containsString("reserve/")));
	}
	
	@Test
	public void shouldRetrieveEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/reserve").content(
				"{\"guestName\":\"Frodo\",\"dateFrom\":\"2018-03-09\",\"dateTo\":\"2018-03-10\",\"price\":\"110\",\"state\":\"F\"}"))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(get(location)).andExpect(status().isOk())
		.andExpect(jsonPath("$.guestName").value("Frodo"))
		.andExpect(jsonPath("$.dateFrom").value("2018-03-09"))
		.andExpect(jsonPath("$.dateTo").value("2018-03-10"))
		.andExpect(jsonPath("$.price").value("110"))
		.andExpect(jsonPath("$.state").value("F"));
	}
	
	@Test
	public void shouldQueryEntity() throws Exception {

		mockMvc.perform(post("/reserve").content(
				"{\"guestName\":\"Frodo\",\"dateFrom\":\"2018-03-09\",\"dateTo\":\"2018-03-10\",\"price\":\"110\",\"state\":\"F\"}"))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/reserve/search/findByGuestName?name={name}", "Frodo"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.reserve[0].guestName").value("Frodo"));
	}
	
	@Test
	public void shouldUpdateEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/reserve").content(
				"{\"guestName\":\"Frodo\",\"dateFrom\":\"2018-03-09\",\"dateTo\":\"2018-03-10\",\"price\":\"110\",\"state\":\"F\"}"))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(put(location).content(
				"{\"guestName\":\"Bruce\",\"dateFrom\":\"2018-03-09\",\"dateTo\":\"2018-03-10\",\"price\":\"130\",\"state\":\"F\"}")).andExpect(
						status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk())
				.andExpect(jsonPath("$.guestName").value("Bruce"))
				.andExpect(jsonPath("$.price").value("130"));
	}
	
	@Test
	public void shouldPartiallyUpdateEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/reserve").content(
				"{\"guestName\":\"Frodo\",\"dateFrom\":\"2018-03-09\",\"dateTo\":\"2018-03-10\",\"price\":\"110\",\"state\":\"F\"}"))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");

		mockMvc.perform(patch(location).content("{\"state\":\"I\"}")).andExpect(
						status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(
				jsonPath("$.guestName").value("Frodo")).andExpect(
						jsonPath("$.state").value("I"));
	}

	@Test
	public void shouldDeleteEntity() throws Exception {

		MvcResult mvcResult = mockMvc.perform(post("/reserve").content(
				"{\"guestName\":\"Frodo\",\"dateFrom\":\"2018-03-09\",\"dateTo\":\"2018-03-10\",\"price\":\"110\",\"state\":\"F\"}"))
				.andExpect(status().isCreated()).andReturn();

		String location = mvcResult.getResponse().getHeader("Location");
		mockMvc.perform(delete(location)).andExpect(status().isNoContent());

		mockMvc.perform(get(location)).andExpect(status().isNotFound());
	}
	
	@Test
	public void shouldRateLimitUpdateState() throws Exception {
		// To be implemented
	}

}
