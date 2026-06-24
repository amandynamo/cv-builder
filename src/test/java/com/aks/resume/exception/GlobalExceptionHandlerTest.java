package com.aks.resume.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void resumeNotFound_returns404() throws Exception {
        mockMvc.perform(get("/test/resume-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void userNotFound_returns404() throws Exception {
        mockMvc.perform(get("/test/user-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void unauthorizedAccess_returns403() throws Exception {
        mockMvc.perform(get("/test/unauthorized"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    void aiServiceException_returns502() throws Exception {
        mockMvc.perform(get("/test/ai-error"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.error").value("Bad Gateway"));
    }

    @Test
    void genericException_returns500() throws Exception {
        mockMvc.perform(get("/test/generic-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }

    @RestController
    static class TestController {

        @GetMapping("/test/resume-not-found")
        void resumeNotFound() {
            throw new ResumeNotFoundException("Resume with id 1 not found");
        }

        @GetMapping("/test/user-not-found")
        void userNotFound() {
            throw new UserNotFoundException("User not found");
        }

        @GetMapping("/test/unauthorized")
        void unauthorized() {
            throw new UnauthorizedAccessException("Access denied");
        }

        @GetMapping("/test/ai-error")
        void aiError() {
            throw new AIServiceException("OpenAI service unavailable");
        }

        @GetMapping("/test/generic-error")
        void genericError() {
            throw new RuntimeException("Unexpected failure");
        }
    }
}
