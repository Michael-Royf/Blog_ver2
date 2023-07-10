package com.michael.blog.service.impl;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.michael.blog.entity.User;
import com.michael.blog.entity.locationDevice.DeviceMetadata;
import com.michael.blog.repository.DeviceMetadataRepository;
import com.michael.blog.utility.IpLocationUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua_parser.Client;
import ua_parser.Parser;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

@Component

public class DeviceService {

    private static final String UNKNOWN = "UNKNOWN";

@Autowired
    private DeviceMetadataRepository deviceMetadataRepository;
    @Autowired
    private Parser parser;
    @Autowired
    private IpLocationUtils ipLocationUtils;


    public void verifyDevice(User user, HttpServletRequest request) throws IOException, GeoIp2Exception {
        String location = ipLocationUtils.getLocationFromRequest();
        String deviceDetails = getDeviceDetails(request.getHeader("user-agent"));
        DeviceMetadata deviceMetadata = new DeviceMetadata();
        deviceMetadata.setUserId(user.getId());
        deviceMetadata.setLocation(location);
        deviceMetadata.setDeviceDetails(deviceDetails);
        deviceMetadata.setLastLoggedIn(new Date());
        deviceMetadataRepository.save(deviceMetadata);
    }


    private String getDeviceDetails(String userAgent) {
        String deviceDetails = UNKNOWN;

        Client client = parser.parse(userAgent);
        if (Objects.nonNull(client)) {
            deviceDetails = client.userAgent.family + " " + client.userAgent.major + "." + client.userAgent.minor +
                    " - " + client.os.family + " " + client.os.major + "." + client.os.minor;
        }
        return deviceDetails;
    }


}
