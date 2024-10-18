package com.tugos.dst.admin.service;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.dao.DstServerInfoDataMapper;
import com.tugos.dst.admin.entity.DstServerInfoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ServerInfoService {

    @Autowired
    DataService dataService;

    @Autowired
    DstServerInfoDataMapper dstServerInfoDataMapper;


    public DstServerInfoData getServerInfo(Long id) {
        return dstServerInfoDataMapper.selectById(id);
    }

    public List<DstServerInfoData> getServerInfoList() {

        return new ArrayList<>(dstServerInfoDataMapper.selectList(null));
    }

    public ResultVO<String> updateServerInfo(DstServerInfoData serverInfo) {
        DstServerInfoData dstServerInfoData = dstServerInfoDataMapper.selectById(serverInfo.getId());

        dstServerInfoData.setIp(serverInfo.getIp());
        dstServerInfoData.setName(serverInfo.getName());
        dstServerInfoData.setUsername(serverInfo.getUsername());
        dstServerInfoData.setPassword(serverInfo.getPassword());
        dstServerInfoDataMapper.updateById(dstServerInfoData);
        return ResultVO.success();
    }

    public ResultVO<String> saveServerInfo(DstServerInfoData serverInfo) {
        List<DstServerInfoData> dstServerInfoData = dstServerInfoDataMapper.selectList(null);

        if (dstServerInfoData.stream().anyMatch(x -> x.getId().equals(serverInfo.getId()))) {
            return ResultVO.fail("Id重复");
        }
        if (dstServerInfoData.stream().anyMatch(x -> x.getIp().equals(serverInfo.getIp()))) {
            return ResultVO.fail("IP重复");
        }

        dstServerInfoDataMapper.insert(serverInfo);
        return ResultVO.success();
    }

    public void deleteServerInfo(Long id) {
        dstServerInfoDataMapper.deleteById(id);
    }


}
