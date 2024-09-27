package com.tugos.dst.admin.vo;


import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Title 游戏备份文件列表上传
 * @Description
 * @author wgr
 * @param null
 * @return
 * @date 2024/9/27 11:37
 */
@Data
public class FileNamesVO {

    /**
     * 文件名称
     */
    private List<String> fileNames;


}
