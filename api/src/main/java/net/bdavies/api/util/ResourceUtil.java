package net.bdavies.api.util;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.sun.management.OperatingSystemMXBean;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * The utility class for getting the system resources and converting the to json
 *
 * @author ben.davies
 */
@Slf4j
@UtilityClass
public class ResourceUtil
{
    private static OperatingSystemMXBean bean;

    /**
     * Get the system resources in a json format:-
     *  {"memory": { free: str, used: str }, "cpu": { process: str, system: str }}
     *
     * @return the resources json string
     */
    public static String getSystemResourcesAsString() {
        if (bean == null) {
            bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        }
        Map<String, Object> memory = new HashMap<>();
        Map<String, Object> cpu = new HashMap<>();
        val usedMem = humanReadableByteCountBin(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        val freeMem = humanReadableByteCountBin(Runtime.getRuntime().freeMemory());
        val procCPU = BigDecimal.valueOf(bean.getProcessCpuLoad()).setScale(2, RoundingMode.CEILING)
            .multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.CEILING);
        val sysCpu = BigDecimal.valueOf(bean.getSystemCpuLoad()).setScale(2, RoundingMode.CEILING)
            .multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.CEILING);
        memory.put("free", freeMem);
        memory.put("used", usedMem);
        cpu.put("process", String.valueOf(procCPU));
        cpu.put("system", String.valueOf(sysCpu));
        return new GsonBuilder().create().toJson(Map.of("memory", memory, "cpu", cpu));
    }

    /**
     * Convert a number of bytes to a shorthand string e.g. 999999999 -> 99MB
     *
     * @param bytes the bytes number
     * @return the new human readable string
     */
    private static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }
}
