package net.bdavies.app.strip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.bdavies.api.config.IStripConfig;

/**
 * A config pojo for the Strip configs
 *
 * @author ben.davies
 */
@Slf4j
@Data
@AllArgsConstructor
public class StripConfig implements IStripConfig
{
    private String name;
    private int pinNumber;
    private int ledCount;
    private String uid;

    /**
     * The unique identifier for the strip for reactive mode and other services like Home assistant
     *
     * @return a unique id in hexadecimal format
     */
    public int getUid()
    {
        return Integer.parseInt(uid, 2, 4, 16) & 0xFF;
    }
}