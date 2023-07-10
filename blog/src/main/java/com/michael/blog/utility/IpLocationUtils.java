package com.michael.blog.utility;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Slf4j
@Component
public class IpLocationUtils {
    @Autowired
    private HttpServletRequest request;


    public String getLocationFromRequest() throws IOException, GeoIp2Exception {
        //  String IP = "128.101.101.101"; //usa
        //  String IP = "41.238.0.198"; //egipet
        String IP = "147.235.209.182"; //my
        //   String IP = getClientIP();
        String dbLocation = "F:\\geo\\GeoLite2-City_20230425.tar\\GeoLite2-City_20230425\\GeoLite2-City_20230425\\GeoLite2-City.mmdb";
        File database = new File(dbLocation);
        DatabaseReader dbr = new DatabaseReader.Builder(database).build();
        InetAddress ipA = InetAddress.getByName(IP);
        CityResponse response = dbr.city(ipA);
        String country = response.getCountry().getName();
        String city = response.getCity().getName();
        String postal = response.getPostal().getCode();
        String state = response.getLeastSpecificSubdivision().getName();
        log.info("User Country " + country);
        log.info("User City " + city);
        log.info("IP is {}", IP);
        String location= country+ ", " + city;
        return location;

    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
