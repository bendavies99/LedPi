package net.bdavies.network;

import java.net.InetAddress;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.config.IApplicationConfig;

/**
 * A Client POJO for a client connection to send data to for the debug mode
 *
 * @see IApplicationConfig#isDebugMode()
 * @author ben.davies
 */
@Slf4j
@Data
public class RemoteClient
{
    private final InetAddress address;
    private final int port;
}
