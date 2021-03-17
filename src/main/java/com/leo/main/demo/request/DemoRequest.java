package com.leo.main.demo.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: LEO
 * @Date: 2021-03-16 21:15
 * @Description:
 */
@Data
public class DemoRequest implements Serializable {
    private static final long serialVersionUID = 8731899176605412280L;

    private String id;

}
