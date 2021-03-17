package com.leo.main.demo;

import com.leo.commons.lock.LockService;
import com.leo.commons.lock.annotations.DistributedLock;
import com.leo.main.demo.request.DemoRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author: LEO
 * @Date: 2021-03-11 17:03
 * @Description:
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/demo")
public class DemoController {

    private LockService lockService;

    @PostMapping(value = "/lock")
    @DistributedLock(key = "demo", id = "#{#request.id}", expire = 30000)
    public String lock(@RequestBody DemoRequest request) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("error:", e);
        }
        return "ok";
    }


    @PostMapping(value = "/tryLock")
    public String tryLock(@RequestBody DemoRequest request) {
        String lockKey = String.format("%s:%s", "demo", request.getId());
        final String lockValue = request.getId();
        boolean lock = lockService.lock(lockKey, lockValue, 30000);
        if (lock) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("error:", e);
            } finally {
               lockService.unlock(lockKey, lockValue);
            }
            return "ok";
        }
        return "error";
    }

}
