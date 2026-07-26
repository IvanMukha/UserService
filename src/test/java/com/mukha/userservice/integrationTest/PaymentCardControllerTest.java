package com.mukha.userservice.integrationTest;

import com.mukha.userservice.DTO.PaymentCardDTO;
import com.mukha.userservice.model.PaymentCard;
import com.mukha.userservice.model.User;
import com.mukha.userservice.repository.PaymentCardRepository;
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


class PaymentCardControllerTest extends AbstractIntegrationTest {
    private static final String BASE_URL = "/api/cards";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void cleanUp() {
        paymentCardRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(userEntity("myEmail@gmail.com"));
    }


    private User userEntity(String email) {
        User u = new User();
        u.setEmail(email);
        u.setName("Ivan");
        u.setSurname("Mukha");
        u.setBirthDate(LocalDate.of(2000, Month.APRIL, 20));
        u.setActive(true);
        return u;
    }

    private PaymentCardDTO buildCardDTO(Long userId, String number) {
        PaymentCardDTO dto = new PaymentCardDTO();
        dto.setUserId(userId);
        dto.setNumber(number);
        dto.setHolder("IVAN MUKHA");
        dto.setExpirationDate(LocalDate.now().plusYears(2));
        dto.setActive(true);
        return dto;
    }

    private PaymentCard cardEntity(User user, String number) {
        PaymentCard card = new PaymentCard();
        card.setUser(user);
        card.setNumber(number);
        card.setHolder("IVAN MUKHA");
        card.setExpirationDate(LocalDate.now().plusYears(2));
        card.setActive(true);
        return card;
    }

    @Test
    void createCard_shouldSaveAndReturnCard() throws Exception {
        PaymentCardDTO request = buildCardDTO(user.getId(), "1234567891234567");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("1234567891234567"))
                .andExpect(jsonPath("$.holder").value("IVAN MUKHA"))
                .andExpect(jsonPath("$.id").exists());

        assertThat(paymentCardRepository.existsByNumber("1234567891234567")).isTrue();
        assertThat(paymentCardRepository.findAll()).hasSize(1);
    }

    @Test
    void createCard_shouldReturn409_whenCardNumberAlreadyExists() throws Exception {
        paymentCardRepository.save(cardEntity(user, "1234567891234567"));

        PaymentCardDTO request = buildCardDTO(user.getId(), "1234567891234567");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        assertThat(paymentCardRepository.findAll()).hasSize(1);
    }

    @Test
    void createCard_shouldReturn403_whenCardLimitExceeded() throws Exception {
        for (int i = 0; i < 5; i++) {
            paymentCardRepository.save(cardEntity(user, "123456789123456" + i));
        }

        PaymentCardDTO request = buildCardDTO(user.getId(), "1234567891234568");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        assertThat(paymentCardRepository.findAll()).hasSize(5);
    }

    @Test
    void createCard_shouldReturn400_withFieldErrors_whenValidationFails() throws Exception {
        PaymentCardDTO request = buildCardDTO(null, "");
        request.setExpirationDate(null);
        request.setActive(null);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("One or more field not valid"))
                .andExpect(jsonPath("$.invalid_fields.userId").value("user_id cannot be null"))
                .andExpect(jsonPath("$.invalid_fields.number").value("card number cannot be empty"))
                .andExpect(jsonPath("$.invalid_fields.expirationDate").value("expirationDate cannot be null"))
                .andExpect(jsonPath("$.invalid_fields.active").value("active status cannot be null"));

        assertThat(paymentCardRepository.findAll()).isEmpty();
    }

    @Test
    void createCard_shouldReturn400_whenExpirationDateInPast() throws Exception {
        PaymentCardDTO request = buildCardDTO(user.getId(), "1234567891234567");
        request.setExpirationDate(LocalDate.now().minusDays(1));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("One or more field not valid"))
                .andExpect(jsonPath("$.invalid_fields.expirationDate").value("Card has expired"));
    }

    @Test
    void getById_shouldReturnCard_whenExists() throws Exception {
        PaymentCard saved = paymentCardRepository.save(cardEntity(user, "1234567891234567"));

        mockMvc.perform(get(BASE_URL + "/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.number").value("1234567891234567"));
    }

    @Test
    void getById_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_shouldReturnCardsFilteredByUserId() throws Exception {
        User anotherUser = userRepository.save(userEntity("myEmail1@gmail.com"));

        paymentCardRepository.save(cardEntity(user, "1234567891234567"));
        paymentCardRepository.save(cardEntity(user, "1234567891234568"));
        paymentCardRepository.save(cardEntity(anotherUser, "1234567891234569"));

        mockMvc.perform(get(BASE_URL)
                        .param("userId", String.valueOf(user.getId()))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void getAll_shouldReturnCardsFilteredByHolder() throws Exception {
        PaymentCard card1 = cardEntity(user, "1234567891234567");
        card1.setHolder("IVAN MUKHA");
        paymentCardRepository.save(card1);

        PaymentCard card2 = cardEntity(user, "1234567891234568");
        card2.setHolder("JONH DOE");
        paymentCardRepository.save(card2);

        mockMvc.perform(get(BASE_URL).param("holder", "IVAN MUKHA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void getAll_shouldReturnAllCards_whenNoFilterProvided() throws Exception {
        paymentCardRepository.save(cardEntity(user, "1234567891234567"));
        paymentCardRepository.save(cardEntity(user, "1234567891234568"));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void updateById_shouldUpdateCardInDb() throws Exception {
        PaymentCard saved = paymentCardRepository.save(cardEntity(user, "1234567891234567"));

        PaymentCardDTO update = buildCardDTO(user.getId(), "1234567891234567");
        update.setHolder("UPDATED HOLDER");

        mockMvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder").value("UPDATED HOLDER"));

        PaymentCard fromDb = paymentCardRepository.findById(saved.getId()).orElseThrow();
        assertThat(fromDb.getHolder()).isEqualTo("UPDATED HOLDER");
    }

    @Test
    void updateById_shouldReturn404_whenNotExists() throws Exception {
        PaymentCardDTO update = buildCardDTO(user.getId(), "1234567891234567");

        mockMvc.perform(patch(BASE_URL + "/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateById_shouldReturn400_whenValidationFails() throws Exception {
        PaymentCard saved = paymentCardRepository.save(cardEntity(user, "1234567891234567"));

        PaymentCardDTO update = buildCardDTO(user.getId(), "1234567891234567");
        update.setExpirationDate(LocalDate.now().minusDays(1));

        mockMvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid_fields.expirationDate").value("Card has expired"));

        PaymentCard fromDb = paymentCardRepository.findById(saved.getId()).orElseThrow();
        assertThat(fromDb.getExpirationDate()).isEqualTo(LocalDate.now().plusYears(2));
    }

    @Test
    void changePaymentCardStatus_shouldUpdateStatusInDb() throws Exception {
        PaymentCard saved = paymentCardRepository.save(cardEntity(user, "1234567891234567"));

        mockMvc.perform(patch(BASE_URL + "/{id}/status", saved.getId())
                        .param("isActive", "false"))
                .andExpect(status().isOk());

        PaymentCard fromDb = paymentCardRepository.findById(saved.getId()).orElseThrow();
        assertThat(fromDb.getActive()).isFalse();
    }

    @Test
    void changePaymentCardStatus_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/{id}/status", 9999L)
                        .param("isActive", "true"))
                .andExpect(status().isNotFound());
    }
}
