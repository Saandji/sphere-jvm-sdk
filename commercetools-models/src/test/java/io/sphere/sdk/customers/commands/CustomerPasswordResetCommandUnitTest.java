package io.sphere.sdk.customers.commands;

import io.sphere.sdk.models.Versioned;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class CustomerPasswordResetCommandUnitTest {
    @Test
    public void toStringDoesNotContainPassword() {
        final CustomerPasswordResetCommand cmd = CustomerPasswordResetCommand.of(Versioned.of("id", 3L), "tokenvalue", "secret");
        assertThat(cmd.toString()).doesNotContain("secret");
    }
}