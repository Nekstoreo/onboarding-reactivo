package co.com.bancolombia.onboarding;

import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;

public class MyBlockHoundIntegration implements BlockHoundIntegration {
    @Override
    public void applyTo(BlockHound.Builder builder) {
        builder.allowBlockingCallsInside("java.io.FileInputStream", "readBytes");
        builder.allowBlockingCallsInside("java.io.FileInputStream", "read");
        builder.allowBlockingCallsInside("java.net.URL", "openConnection");
        builder.allowBlockingCallsInside("java.util.Properties", "load");
        builder.allowBlockingCallsInside("java.util.Properties", "load0");
    }
}
