package org.example.mobile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mobile.repository.MobileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MobileControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private MobileRepository mobileRepository;

    @InjectMocks
    @Autowired
    private MobileController mobileController;

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON string

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mobileController).build();
    }

    @Test
    public void shouldReturnAllMobiles() throws Exception {
        when(mobileRepository.findAll()).thenReturn(List.of(new Mobile(1L,"iPhone", "12", 999.99, "USD", 10, null)));

        mockMvc.perform(get("/mobiles")
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].brand", is("iPhone")));
    }

    @Test
    public void shouldReturnMobileById() throws Exception {
        Long id = 1L;
        Mobile mobile = new Mobile(1L,"Samsung", "S20", 800.00, "USD", 5, null);
        when(mobileRepository.findById(id)).thenReturn(Optional.of(mobile));

        mockMvc.perform(get("/mobiles/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.brand", is("Samsung")));
    }

    @Test
    public void shouldCreateMobile() throws Exception {
        // Create a mock mobile object as a JSON string
        String mobileJson = "{ \"brand\": \"Samsung\", \"model\": \"Galaxy S21\", \"price\": 799.99, \"currency\": \"USD\", \"stock\": 100 }";

        // Create a mock image file (can be any file, just for testing purposes)
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes()
        );

        // Perform the request
        mockMvc.perform(multipart("/mobiles")
                                .file(file)  // Attach the image file
                                .param("mobile", mobileJson)  // Attach the JSON string for the mobile
                                .contentType(MediaType.MULTIPART_FORM_DATA))  // Ensure the content type is multipart
               .andExpect(status().isCreated());  // Expect a 201 Created status
    }
    @Test
    public void shouldUpdateMobile() throws Exception {
        // Prepare data for updating
        String mobileJson = "{ \"brand\": \"Samsung\", \"model\": \"Galaxy S22\", \"price\": 849.99, \"currency\": \"USD\", \"stock\": 80 }";
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "updated-image.jpg",
                "image/jpeg",
                "updated image content".getBytes()
        );

        // Mocking a Mobile object with ID 1
        Mobile existingMobile = new Mobile();
        existingMobile.setId(1L);
        existingMobile.setBrand("Samsung");
        existingMobile.setModel("Galaxy S21");
        existingMobile.setPrice(799.99);
        existingMobile.setCurrency("USD");
        existingMobile.setStock(100);

        when(mobileRepository.findById(1L)).thenReturn(java.util.Optional.of(existingMobile));
        when(mobileRepository.save(any(Mobile.class))).thenReturn(existingMobile);

        // Perform the update test (use PUT instead of POST)
        mockMvc.perform(multipart("/mobiles/1")
                                .file(file)  // Attach the updated image file
                                .param("mobile", mobileJson)  // Attach the JSON string for the mobile
                                .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isOk())  // Expect a 200 OK status
               .andExpect(jsonPath("$.model").value("Galaxy S22"))  // Verify the model is updated
               .andExpect(jsonPath("$.price").value(849.99));  // Verify the price is updated
    }

    @Test
    public void shouldDeleteMobile() throws Exception {
        // Assuming the mobile with ID 1 exists in the DB
        mockMvc.perform(delete("/mobiles/1"))
               .andExpect(status().isNoContent());  // Expect a 204 No Content status
    }
    @Test
    public void shouldDeleteAllMobiles() throws Exception {
        // Perform the delete all request
        mockMvc.perform(delete("/mobiles/deleteAll"))
               .andExpect(status().isNoContent());  // Expect a 204 No Content status
    }

    @Test
    public void shouldUploadImage() throws Exception {
        // Create a mock image file
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "upload-test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes()
        );

        mockMvc.perform(multipart("/mobiles/uploadImage")
                                .file(file)  // Attach the image file
                                .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isOk())  // Expect a 200 OK status
               .andExpect(content().string("Image uploaded successfully"));  // Verify the response message
    }

}
