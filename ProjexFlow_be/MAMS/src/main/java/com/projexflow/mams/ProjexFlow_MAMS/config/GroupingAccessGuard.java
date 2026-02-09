package com.projexflow.mams.ProjexFlow_MAMS.config;

import com.projexflow.mams.ProjexFlow_MAMS.exception.GroupingNotCompletedException;
import com.projexflow.mams.ProjexFlow_MAMS.service.GmsClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.beans.factory.ObjectProvider;


@Component
@RequiredArgsConstructor
public class GroupingAccessGuard {

    private static final String ATTR_GROUPING_STATUS_PREFIX = "__GMS_GROUPING_STATUS__";

    private final ObjectProvider<GmsClient> gmsClientProvider;

    private GmsClient gms() {
        return gmsClientProvider.getObject();
    }


    public void assertAccessible(Long batchId) {
        if (batchId == null) return;

        HttpServletRequest req = currentRequest();
        String cacheKey = ATTR_GROUPING_STATUS_PREFIX + batchId;
        if (req != null) {
            Object cached = req.getAttribute(cacheKey);
            if (cached instanceof String s) {
                if ("OPEN".equalsIgnoreCase(s)) throw new GroupingNotCompletedException();
                return;
            }
        }

        String status = gms().groupingStatus(batchId);
        if (req != null) req.setAttribute(cacheKey, status);

        if ("OPEN".equalsIgnoreCase(status)) {
            throw new GroupingNotCompletedException();
        }
    }

    private HttpServletRequest currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes sra)) return null;
        return sra.getRequest();
    }
}
