package com.tugos.dst.admin.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tugos.dst.admin.entity.PlayerLog;
import com.tugos.dst.admin.entity.SystemSetting;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface SystemSettingMapper extends BaseMapper<SystemSetting>{


}