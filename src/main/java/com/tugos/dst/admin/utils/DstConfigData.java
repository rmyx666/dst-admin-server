package com.tugos.dst.admin.utils;

import com.google.common.collect.Maps;
import com.tugos.dst.admin.entity.User;
import lombok.Data;

import java.util.Map;

/**
 * @author qinming
 * @date 2020-10-25 23:13:42
 * <p> 本地数据库 </p>
 */
public class DstConfigData {

    /**
     * 智能更新标志
     */
    public static final Boolean smartUpdate = true;

    public static final User USER_INFO = new User();

    public static Map<String,DstConfigRoomData> ROOM_INFO_MAP;


}
