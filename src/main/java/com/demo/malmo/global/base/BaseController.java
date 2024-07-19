package com.demo.malmo.global.base;

import com.demo.malmo.global.exception.BaseException;
import java.util.Optional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class BaseController {

    public String getUserId() {
        return Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .map(ServletRequestAttributes::getRequest)
            .map(request -> request.getHeader("user-id"))
            .orElseThrow(() -> new BaseException("user-id not found"));
    }
}
