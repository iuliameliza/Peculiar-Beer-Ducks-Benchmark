package com.proj.co.specs;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

public class SystemInformation {
    private final String compModel;
    private final String osType;
    private final String cpuType;
    private final String ramTotalSize;

    public SystemInformation() {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        CentralProcessor processor = hardware.getProcessor();
        CentralProcessor.ProcessorIdentifier processorIdentifier = processor.getProcessorIdentifier();
        long totalMemory = hardware.getMemory().getTotal();

        compModel = hardware.getComputerSystem().getModel();
        osType = operatingSystem.getFamily() + " (" + operatingSystem.getManufacturer() + ")";
        cpuType = processorIdentifier.getName();
        ramTotalSize = FormatUtil.formatBytes(totalMemory);
    }

    public String getCompModel() {
        return compModel;
    }

    public String getOsType() {
        return osType;
    }

    public String getCpuType() {
        return cpuType;
    }

    public String getRamTotalSize() {
        return ramTotalSize;
    }
}
