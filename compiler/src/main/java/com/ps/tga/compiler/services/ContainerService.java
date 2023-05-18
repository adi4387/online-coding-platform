package com.ps.tga.compiler.services;

import com.ps.tga.compiler.models.ProcessOutput;
import com.ps.tga.compiler.utils.CmdUtils;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Slf4j
@Service("docker")
@RequiredArgsConstructor
public class ContainerService {

    public static final String DOCKER = "docker";
    private final MeterRegistry meterRegistry;
    private Timer runTimer;
    private final CompilerProperties compilerProperties;

    @PostConstruct
    public void init() {
        runTimer = meterRegistry.timer("CONTAINER_RUN_TIMER", "container", DOCKER);
    }

    public ProcessOutput runContainer(
            String imageName,
            String containerName,
            long timeout,
            Map<String, String> envVariables) {

        return runTimer.record(() -> {
            String[] dockerCommand = buildDockerCommand(imageName, containerName, compilerProperties.volume()
                    + ":" + "/app", envVariables);
            return CmdUtils.executeProcess(dockerCommand, timeout);
        });
    }

    private String[] buildDockerCommand(String imageName,
                                        String containerName,
                                        String volumeMounting,
                                        Map<String, String> envVariables) {
        List<String> dockerCommandList = new ArrayList<>(Arrays.asList(DOCKER, "run", "--rm", "--name", containerName, "-v", volumeMounting));
        for (Entry<String, String> entry : envVariables.entrySet()) {
            dockerCommandList.add("-e");
            dockerCommandList.add(entry.getKey() + "=" + entry.getValue());
        }

        dockerCommandList.add(imageName);
        return dockerCommandList.toArray(new String[0]);
    }
}
