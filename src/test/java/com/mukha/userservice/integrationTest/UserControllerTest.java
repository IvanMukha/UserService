package com.mukha.userservice.integrationTest;

import com.mukha.userservice.dto.UserDTO;
import com.mukha.userservice.model.User;
import com.mukha.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    private UserDTO buildUserDTO(String email) {
        UserDTO dto = new UserDTO();
        dto.setName("Ivan");
        dto.setSurname("Mukha");
        dto.setBirthDate(LocalDate.of(2000, Month.APRIL, 20));
        dto.setEmail(email);
        dto.setActive(true);
        return dto;
    }

    private User userEntity(String email, String name, String surname) {
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setSurname(surname);
        u.setBirthDate(LocalDate.of(1990, Month.MAY, 20));
        u.setActive(true);
        return u;
    }

    @Test
    void createUser_shouldSaveAndReturnUser() throws Exception {
        UserDTO request = buildUserDTO("myEmail@gmail.com");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("myEmail@gmail.com"))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.id").exists());

        assertThat(userRepository.existsByEmail("myEmail@gmail.com")).isTrue();
        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    void createUser_shouldReturn409_whenEmailAlreadyExists() throws Exception {
        userRepository.save(userEntity("duplicate@gmail.com", "Ivan", "Mukha"));

        UserDTO request = buildUserDTO("duplicate@gmail.com");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    void createUser_shouldReturn400_withFieldErrors_whenValidationFails() throws Exception {
        UserDTO request = buildUserDTO("notEmail");
        request.setName("");
        request.setBirthDate(null);
        request.setActive(null);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("One or more field not valid"))
                .andExpect(jsonPath("$.invalid_fields.name").value("name cannot be empty"))
                .andExpect(jsonPath("$.invalid_fields.email").value("email must be valid"))
                .andExpect(jsonPath("$.invalid_fields.birthDate").value("birth date cannot be null"))
                .andExpect(jsonPath("$.invalid_fields.active").value("active status cannot be null"));

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    void createUser_shouldReturn400_whenBirthDateInFuture() throws Exception {
        UserDTO request = buildUserDTO("myEmail@gmail.com");
        request.setBirthDate(LocalDate.now().plusDays(1));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("One or more field not valid"))
                .andExpect(jsonPath("$.invalid_fields.birthDate").value("birth day must be in the past"));
    }

    @Test
    void getById_shouldReturnUser_whenExists() throws Exception {
        User saved = userRepository.save(userEntity("myEmail@gmail.com", "Ivan", "Mukha"));

        mockMvc.perform(get(BASE_URL + "/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.email").value("myEmail@gmail.com"))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.surname").value("Mukha"));
    }

    @Test
    void getById_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_shouldFilterByName() throws Exception {
        userRepository.save(userEntity("myEmail1@gmail.com", "Ivan", "Mukha"));
        userRepository.save(userEntity("myEmail2@gmail.com", "Ivan", "Smith"));
        userRepository.save(userEntity("myEmail3@gmail.com", "John", "Mukha"));
        userRepository.save(userEntity("myEmail4@gmail.com", "John", "Doe"));


        mockMvc.perform(get(BASE_URL)
                        .param("name", "Ivan")
                        .param("page","0")
                        .param("size","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));


    }

    @Test
    void getAll_shouldFilterBySurnameAndPaged() throws Exception {
        userRepository.save(userEntity("myEmail1@gmail.com", "Ivan", "Mukha"));
        userRepository.save(userEntity("myEmail2@gmail.com", "Ivan", "Smith"));
        userRepository.save(userEntity("myEmail3@gmail.com", "John", "Mukha"));
        userRepository.save(userEntity("myEmail4@gmail.com", "John", "Doe"));


        mockMvc.perform(get(BASE_URL)
                        .param("surname", "Mukha")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getAll_shouldReturnAllUsers_whenNoFilterProvidedAndPaged() throws Exception {
        userRepository.save(userEntity("myEmail1@gmail.com", "Ivan", "Mukha"));
        userRepository.save(userEntity("myEmail2@gmail.com", "John", "Doe"));
        userRepository.save(userEntity("myEmail3@gmail.com", "Alan", "Smith"));
        userRepository.save(userEntity("myEmail4@gmail.com", "Ivan", "NeMukha"));

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)))
                .andExpect(jsonPath("$.totalElements").value(4));
    }

    @Test
    void updateById_shouldUpdateUserInDb() throws Exception {
        User saved = userRepository.save(userEntity("myEmail@1gmail.com", "Ivan", "Mukha"));

        UserDTO update = buildUserDTO("myEmail1@gmail.com");
        update.setName("UpdatedName");

        mockMvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"));

        User fromDb = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(fromDb.getName()).isEqualTo("UpdatedName");
    }

    @Test
    void updateById_shouldReturn404_whenNotExists() throws Exception {
        UserDTO update = buildUserDTO("myEmail5@gmail.com");

        mockMvc.perform(patch(BASE_URL + "/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeUserStatus_shouldUpdateStatusInDb() throws Exception {
        User saved = userRepository.save(userEntity("myEmail@gmail.com", "Ivan", "Mukha"));

        mockMvc.perform(patch(BASE_URL + "/{id}/status", saved.getId())
                        .param("isActive", "false"))
                .andExpect(status().isNoContent());

        User fromDb = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(fromDb.getActive()).isFalse();
    }

    @Test
    void changeUserStatus_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/{id}/status", 9999L)
                        .param("isActive", "true"))
                .andExpect(status().isNotFound());
    }
}
