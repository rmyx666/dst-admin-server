package com.tugos.dst.admin.service;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.utils.DstServerInfoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ServerInfoService {

    @Autowired
    DataService dataService;

    public DstServerInfoData getServerInfo(Long id) {
        DstServerInfoData dstServerInfoData = dataService.getServerInfoMap().get(id);
        return dstServerInfoData;
    }

    public List<DstServerInfoData> getServerInfoList() {
        List<DstServerInfoData> dstServerInfoData = new ArrayList<>(dataService.getServerInfoMap().values());

        return dstServerInfoData;
    }

    public ResultVO<String> updateServerInfo(DstServerInfoData serverInfo) {
        Map<Long, DstServerInfoData> serverInfoMap = dataService.getServerInfoMap();
        DstServerInfoData dstServerInfoData = serverInfoMap.get(serverInfo.getId());
        dstServerInfoData.setIp(serverInfo.getIp());
        dstServerInfoData.setName(serverInfo.getName());
        dstServerInfoData.setUsername(serverInfo.getUsername());
        dstServerInfoData.setPassword(serverInfo.getPassword());
        serverInfoMap.put(serverInfo.getId(), dstServerInfoData);
        dataService.updateServerInfoMap(serverInfoMap);
        return ResultVO.success();
    }

    public ResultVO<String> saveServerInfo(DstServerInfoData serverInfo) {
        Map<Long, DstServerInfoData> serverInfoMap = dataService.getServerInfoMap();

        if (serverInfoMap.containsKey(serverInfo.getId())) {
            return ResultVO.fail("Id重复");
        }
        if (serverInfoMap.values().stream().anyMatch(x -> x.getIp().equals(serverInfo.getIp()))) {
            return ResultVO.fail("IP重复");
        }

        serverInfoMap.put(serverInfo.getId(), serverInfo);
        dataService.updateServerInfoMap(serverInfoMap);
        return ResultVO.success();
    }

    public void deleteServerInfo(Long id) {
        Map<Long, DstServerInfoData> serverInfoMap = dataService.getServerInfoMap();
        serverInfoMap.remove(id);
        dataService.updateServerInfoMap(serverInfoMap);
    }


}
