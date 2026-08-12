package com.mukha.userservice.integrationTest;

import com.mukha.userservice.dto.UserDTO;
import com.mukha.userservice.model.User;
import com.mukha.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/v1/api/users";
    private static final String EMAIL_1 = "myEmail1@gmail.com";
    private static final String EMAIL_2 = "myEmail2@gmail.com";
    private static final String EMAIL_3 = "myEmail3@gmail.com";
    private static final String EMAIL_4 = "myEmail4@gmail.com";
    private static final String NAME_1 = "Ivan";
    private static final String NAME_2 = "Jonh";
    private static final String NAME_3 = "Alan";
    private static final String NAME_4 = "Ivan";
    private static final String SURNAME_1 = "Mukha";
    private static final String SURNAME_2 = "Doe";
    private static final String SURNAME_3 = "Smith";
    private static final String SURNAME_4 = "Potapov";

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
        dto.setKeycloakUUID(UUID.randomUUID());
        dto.setName(NAME_1);
        dto.setSurname(SURNAME_1);
        dto.setBirthDate(LocalDate.of(2000, Month.APRIL, 20));
        dto.setEmail(email);
        dto.setActive(true);
        return dto;
    }

    private User userEntity(String email, String name, String surname) {
        User u = new User();
        u.setKeycloakUUID(UUID.randomUUID());
        u.setEmail(email);
        u.setName(name);
        u.setSurname(surname);
        u.setBirthDate(LocalDate.of(1990, Month.MAY, 20));
        u.setActive(true);
        return u;
    }

    @Test
    @WithMockUser(authorities = "admin")
    void createUser_shouldSaveAndReturnUser() throws Exception {
        UserDTO request = buildUserDTO(EMAIL_1);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(EMAIL_1))
                .andExpect(jsonPath("$.name").value(NAME_1))
                .andExpect(jsonPath("$.id").exists());

        assertThat(userRepository.existsByEmail(EMAIL_1)).isTrue();
        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(authorities = "admin")
    void createUser_shouldReturn409_whenEmailAlreadyExists() throws Exception {
        userRepository.save(userEntity(EMAIL_1, NAME_1, SURNAME_1));

        UserDTO request = buildUserDTO(EMAIL_1);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(authorities = "admin")
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
    @WithMockUser(authorities = "admin")
    void createUser_shouldReturn400_whenBirthDateInFuture() throws Exception {
        UserDTO request = buildUserDTO(EMAIL_1);
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
    @WithMockUser(authorities = "admin")
    void getById_shouldReturnUser_whenExists() throws Exception {
        User saved = userRepository.save(userEntity(EMAIL_1, NAME_1, SURNAME_1));

        mockMvc.perform(get(BASE_URL + "/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.email").value(EMAIL_1))
                .andExpect(jsonPath("$.name").value((NAME_1)))
                .andExpect(jsonPath("$.surname").value(SURNAME_1));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getById_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getByEmail_shouldReturnUser_whenExists() throws Exception {
        User saved = userRepository.save(userEntity(EMAIL_1, NAME_1, SURNAME_1));

        mockMvc.perform(get(BASE_URL + "/email")
                        .param("email", EMAIL_1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.email").value(EMAIL_1))
                .andExpect(jsonPath("$.name").value(NAME_1))
                .andExpect(jsonPath("$.surname").value(SURNAME_1));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getByEmail_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(get(BASE_URL + "/email")
                        .param("email", "notExistEmail@gmail.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getAllByUserId_shouldReturnAllUsers_whenUserExists() throws Exception {
        User user1 = userRepository.save(userEntity(EMAIL_1, NAME_1, SURNAME_1));
        User user2 = userRepository.save(userEntity(EMAIL_2, NAME_2, SURNAME_2));
        User user3 = userRepository.save(userEntity(EMAIL_3, NAME_3, SURNAME_3));
        userRepository.save(userEntity(EMAIL_4, NAME_4, SURNAME_4));

        String idsParam = user1.getId() + "," + user2.getId() + "," + user3.getId();

        mockMvc.perform(get(BASE_URL + "/batch")
                        .param("ids", idsParam))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value(NAME_1))
                .andExpect(jsonPath("$[1].name").value(NAME_2))
                .andExpect(jsonPath("$[2].name").value(NAME_3));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getAllByUserId_shouldReturnEmptyList_whenUserNotExists() throws Exception {
        userRepository.save(userEntity(EMAIL_1, NAME_1, SURNAME_1));
        userRepository.save(userEntity(EMAIL_2, NAME_2, SURNAME_2));
        userRepository.save(userEntity(EMAIL_3, NAME_3, SURNAME_3));
        userRepository.save(userEntity(EMAIL_4, NAME_4, SURNAME_4));

        mockMvc.perform(get(BASE_URL + "/batch")
                        .param("ids", "997,998,999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getAll_shouldFilterByName() throws Exception {
        userRepository.save(userEntity(EMAIL_1, NAME_1, SURNAME_1));
        userRepository.save(userEntity(EMAIL_2, NAME_2, SURNAME_2));
        userRepository.save(userEntity(EMAIL_3, NAME_3, SURNAME_3));
        userRepository.save(userEntity(EMAIL_4, NAME_4, SURNAME_4));

        mockMvc.perform(get(BASE_URL)
                        .param("name", NAME_1)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));


    }

    @Test
    @WithMockUser(authorities = "admin")
    void getAll_shouldFilterBySurnameAndPaged() throws Exception {
        userRepository.save(userEntity(EMAIL_1, NAME_1, SURNAME_1));
        userRepository.save(userEntity(EMAIL_2, NAME_2, SURNAME_1));
        userRepository.save(userEntity(EMAIL_3, NAME_3, SURNAME_3));
        userRepository.save(userEntity(EMAIL_4, NAME_4, SURNAME_4));

        mockMvc.perform(get(BASE_URL)
                        .param("surname", SURNAME_1)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void getAll_shouldReturnAllUsers_whenNoFilterProvidedAndPaged() throws Exception {
        userRepository.save(userEntity(EMAIL_1, NAME_1, SURNAME_1));
        userRepository.save(userEntity(EMAIL_2, NAME_2, SURNAME_2));
        userRepository.save(userEntity(EMAIL_3, NAME_3, SURNAME_3));
        userRepository.save(userEntity(EMAIL_4, NAME_4, SURNAME_4));

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)))
                .andExpect(jsonPath("$.totalElements").value(4));
    }

    @Test
    @WithMockUser(authorities = "admin")
    void updateById_shouldUpdateUserInDb() throws Exception {
        User saved = userRepository.save(userEntity(EMAIL_1, NAME_1, SURNAME_1));

        UserDTO update = buildUserDTO(EMAIL_2);
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
    @WithMockUser(authorities = "admin")
    void updateById_shouldReturn404_whenNotExists() throws Exception {
        UserDTO update = buildUserDTO(EMAIL_1);

        mockMvc.perform(patch(BASE_URL + "/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "admin")
    void changeUserStatus_shouldUpdateStatusInDb() throws Exception {
        User saved = userRepository.save(userEntity(EMAIL_1, NAME_1, SURNAME_1));

        mockMvc.perform(patch(BASE_URL + "/{id}/status", saved.getId())
                        .param("isActive", "false"))
                .andExpect(status().isNoContent());

        User fromDb = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(fromDb.getActive()).isFalse();
    }

    @Test
    @WithMockUser(authorities = "admin")
    void changeUserStatus_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/{id}/status", 9999L)
                        .param("isActive", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.instance").value(BASE_URL));
    }

    @Test
    @WithMockUser(authorities = "user")
    void getAll_shouldReturn403_whenUserIsNotAdmin() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Forbidden"));
    }
}
