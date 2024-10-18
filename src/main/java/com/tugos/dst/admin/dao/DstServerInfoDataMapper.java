package com.tugos.dst.admin.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tugos.dst.admin.entity.DstServerInfoData;
import com.tugos.dst.admin.entity.PlayerLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.io.Serializable;
@Component
@Mapper
public interface DstServerInfoDataMapper extends BaseMapper<DstServerInfoData>{


}