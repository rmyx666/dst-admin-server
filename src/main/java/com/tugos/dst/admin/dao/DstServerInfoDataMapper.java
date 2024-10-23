package com.tugos.dst.admin.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tugos.dst.admin.entity.ServerInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface DstServerInfoDataMapper extends BaseMapper<ServerInfo>{


}