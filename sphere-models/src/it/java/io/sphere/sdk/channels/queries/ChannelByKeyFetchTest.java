package io.sphere.sdk.channels.queries;

import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.channels.ChannelFixtures;
import io.sphere.sdk.test.IntegrationTest;
import org.junit.Test;

import java.util.Optional;

import static io.sphere.sdk.channels.ChannelRoles.INVENTORY_SUPPLY;
import static org.assertj.core.api.Assertions.*;

public class ChannelByKeyFetchTest extends IntegrationTest {
    @Test
    public void execution() throws Exception {
        ChannelFixtures.withPersistentChannel(client(), INVENTORY_SUPPLY, channel -> {
            final Optional<Channel> channelOptional = execute(ChannelByKeyFetch.of(channel.getKey()));
            assertThat(channelOptional.map(Channel::getId)).contains(channel.getId());
        });
    }
}