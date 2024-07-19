package com.demo.malmo.global.base;

import java.util.Optional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class BaseController {

    public String getUserId() {
        return Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .map(ServletRequestAttributes::getRequest)
            .map(request -> request.getHeader("user-id"))
            .orElse("testt");
//            .orElseThrow(() -> new PoliException("user-id not found"));
    }
}
