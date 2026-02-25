package org.skypro.teamwork.controller;

import org.skypro.teamwork.dto.ServiceInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class InfoController {

    private final BuildProperties buildProperties;

    public InfoController(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @GetMapping("/info")
    public ResponseEntity<ServiceInfoDto> getServiceInfo() {
        ServiceInfoDto info = new ServiceInfoDto(
                buildProperties.getName(),
                buildProperties.getVersion()
        );

        return ResponseEntity.ok(info);
    }
}