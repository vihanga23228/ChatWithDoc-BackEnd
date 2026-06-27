package com.local.chatwithdocbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.local.chatwithdocbackend.dto.CreateUserRequest;
import com.local.chatwithdocbackend.dto.LoginRequest;
import com.local.chatwithdocbackend.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldRegisterLoginAndAccessProtectedEndpointsBasedOnRoles() throws Exception {
        // 1. Register a Patient
        CreateUserRequest patientRegister = new CreateUserRequest("Patient John", "patient@doc.com", "patient123", Role.PATIENT);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRegister)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Patient John"))
                .andExpect(jsonPath("$.email").value("patient@doc.com"))
                .andExpect(jsonPath("$.role").value("PATIENT"));

        // 2. Register a Doctor
        CreateUserRequest doctorRegister = new CreateUserRequest("Doctor Smith", "doctor@doc.com", "doctor123", Role.DOCTOR);
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorRegister)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Doctor Smith"))
                .andExpect(jsonPath("$.email").value("doctor@doc.com"))
                .andExpect(jsonPath("$.role").value("DOCTOR"));

        // 3. Login Patient
        LoginRequest patientLogin = new LoginRequest("patient@doc.com", "patient123");
        MvcResult patientLoginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("PATIENT"))
                .andReturn();

        String patientToken = objectMapper.readTree(patientLoginResult.getResponse().getContentAsString())
                .get("token").asText();

        // 4. Login Doctor
        LoginRequest doctorLogin = new LoginRequest("doctor@doc.com", "doctor123");
        MvcResult doctorLoginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("DOCTOR"))
                .andReturn();

        String doctorToken = objectMapper.readTree(doctorLoginResult.getResponse().getContentAsString())
                .get("token").asText();

        // 5. Test Access: Patient Token on Patient Endpoint -> Should succeed
        mockMvc.perform(get("/api/demo/patient")
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isOk());

        // 6. Test Access: Patient Token on Doctor Endpoint -> Should be forbidden (403)
        mockMvc.perform(get("/api/demo/doctor")
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isForbidden());

        // 7. Test Access: Doctor Token on Doctor Endpoint -> Should succeed
        mockMvc.perform(get("/api/demo/doctor")
                        .header("Authorization", "Bearer " + doctorToken))
                .andExpect(status().isOk());

        // 8. Test Access: Doctor Token on Patient Endpoint -> Should be forbidden (403)
        mockMvc.perform(get("/api/demo/patient")
                        .header("Authorization", "Bearer " + doctorToken))
                .andExpect(status().isForbidden());

        // 9. Test Access: Unauthorized -> Should fail (403 / 401 depending on filter entrypoint; default is 403 Forbidden for missing credentials in method security or access rules)
        mockMvc.perform(get("/api/demo/any"))
                .andExpect(status().isForbidden());

        // 10. Test Access: Any authenticated user -> Should succeed for both
        mockMvc.perform(get("/api/demo/any")
                        .header("Authorization", "Bearer " + patientToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/demo/any")
                        .header("Authorization", "Bearer " + doctorToken))
                .andExpect(status().isOk());
    }
}
