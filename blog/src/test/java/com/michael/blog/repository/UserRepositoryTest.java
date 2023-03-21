package com.michael.blog.repository;

import com.michael.blog.entity.User;
import com.michael.blog.entity.enumeration.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {


    @Autowired
    private UserRepository userRepository;

    private User user;
    private String username = "MichaelRoyf";
    private String email = "michael@gmail.com";

    @BeforeEach
    public void setup() {
        user = User.builder()
                .firstName("Michael")
                .lastName("Royf")
                .username(username)
                .userId("userID")
                .email(email)
              // .isActive(false)
               // .isNotLocked(true)
                .password("password")
                .role(UserRole.ROlE_USER)
                .build();
    }


    @Test
    void itShouldFindByEmail() {
        //Given
        user = userRepository.save(user);
        //When
        User userDb = userRepository.findByEmail(email).get();
        //Then
        assertThat(userDb).isNotNull();
        assertThat(userDb.getUsername()).isEqualTo(username);
    }

    @Test
    void itShouldFindByUsername() {
        //Given
        user = userRepository.save(user);
        //When
        User userDB = userRepository.findByUsername(username).get();
        //Then
        assertThat(userDB).isNotNull();
        assertThat(userDB.getEmail()).isEqualTo(email);
    }

    @Test
    void itShouldFindByUsernameOrEmail() {
        //Given

        //When
        //Then
    }

    @Test
    void itShouldExistsByUsername() {
        //Given
        user = userRepository.save(user);
        //When
        Boolean isExists = userRepository.existsByUsername(username);
        //Then
        assertThat(isExists).isTrue();
    }

    @Test
    void itShouldExistsByEmail() {
        //Given
        user = userRepository.save(user);
        //When
        Boolean isExists = userRepository.existsByEmail(email);
        //Then
        assertThat(isExists).isTrue();
    }

    @Test
    void itShouldEnableUser() {
        //Given
     user.setIsActive(false);
        userRepository.save(user);
        //When
      int i=  userRepository.enableUser(email);
         User userDB= userRepository.findByEmail(email).get();
        //Then
       assertThat(i).isPositive();
   //     assertThat(userDB.getIsActive()).isTrue();

    }

    @Test
    void itShouldDisabledUser() {
        //Given
        user.setIsActive(true);
        user = userRepository.save(user);
        //When
        int i = userRepository.disabledUser(email);
        User userDB = userRepository.findByEmail(email).get();
        //Then
        assertThat(i).isPositive();
      //  assertThat(userDB.getIsActive()).isFalse();
    }
}