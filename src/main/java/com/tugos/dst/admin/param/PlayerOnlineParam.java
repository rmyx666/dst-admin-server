package com.tugos.dst.admin.param;

import lombok.Data;

import java.util.Date;

@Data
public class PlayerOnlineParam {
    String roomId;
    Date startTime;
    Date endTime;
    String type;
}
