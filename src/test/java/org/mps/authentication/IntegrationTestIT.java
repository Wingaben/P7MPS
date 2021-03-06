package org.mps.authentication;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class IntegrationTestIT {
    private CredentialValidator cv = null;
    private Date birthdate = null;
    private PasswordString passwordString = null;
    private CredentialStore credentialStore = null;
    private UserRegistration userRegistration = null;

    @Test
    void validatorValidateReturnsBirthdayInvalidIfBirthdayIsInvalid(){
        birthdate = new Date(0, 1, 2020);
        passwordString = new PasswordString("hello.123;");
        credentialStore = new CredentialStoreSet();
        cv = new CredentialValidator(birthdate, passwordString, credentialStore);
        assertThat(cv.validate()).isEqualTo(CredentialValidator.ValidationStatus.BIRTHDAY_INVALID);
    }

    @Test
    void validatorValidateReturnsPasswordInvalidIfPasswordIsInvalid(){
        birthdate = new Date(29, 2, 1980);
        passwordString = new PasswordString("hello");
        cv = new CredentialValidator(birthdate, passwordString, credentialStore);
        assertThat(cv.validate()).isEqualTo(CredentialValidator.ValidationStatus.PASSWORD_INVALID);
    }

    @Test
    void validatorValidateReturnsCredentialExistingIfCredentialContainsPairOfDateAndPassword(){
        birthdate = new Date(29, 2, 1980);
        passwordString = new PasswordString("hello.123;");
        credentialStore = new CredentialStoreSet();
        credentialStore.register(birthdate,passwordString);
        cv = new CredentialValidator(birthdate, passwordString, credentialStore);
        assertThat(cv.validate()).isEqualTo(CredentialValidator.ValidationStatus.EXISTING_CREDENTIAL);
    }

    @Test
    void validatorValidateReturnsValidationOKIfAllParametersAreOK(){
        birthdate = new Date(21, 2, 1983);
        passwordString = new PasswordString("hello.124;");
        credentialStore = new CredentialStoreSet();
        cv = new CredentialValidator(birthdate, passwordString, credentialStore);
        assertThat(cv.validate()).isEqualTo(CredentialValidator.ValidationStatus.VALIDATION_OK);
    }

    @Test
    void userRegistrationOK(){
        int size = registrationOfASpecificUserAndReturningInitialSize();
        assertThat(credentialStore.credentialExists(birthdate, passwordString)).isTrue();
        assertThat(credentialStore.size()).isEqualTo(size+1);
    }


    @Test
    void userRegistrationWithExisitingCredentialsDoesntRegister(){
        registrationOfASpecificUserAndReturningInitialSize();
        int size = credentialStore.size();
        userRegistration.register(birthdate, passwordString, credentialStore);
        assertThat(credentialStore.size()).isEqualTo(size);
    }
    @Test
    void credentialRegistrationWithExisitingValuesThrowsCredentialsException(){
        birthdate = new Date(10, 7, 1997);
        passwordString = new PasswordString("hello.992;");
        credentialStore = new CredentialStoreSet();
        credentialStore.register(birthdate, passwordString);
        assertThatExceptionOfType(CredentialExistsException.class).isThrownBy(()
                -> credentialStore.register(birthdate, passwordString));
    }

    // useful functions
    private int registrationOfASpecificUserAndReturningInitialSize() {
        birthdate = new Date(10, 7, 1997);
        passwordString = new PasswordString("hello.992;");
        credentialStore = new CredentialStoreSet();
        userRegistration = new UserRegistration();
        int size = credentialStore.size();
        userRegistration.register(birthdate, passwordString, credentialStore);
        return size;
    }
}
