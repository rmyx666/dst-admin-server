package com.tugos.dst.admin.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tugos.dst.admin.entity.PlayerLog;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface PlayerLogMapper extends BaseMapper<PlayerLog>{


}