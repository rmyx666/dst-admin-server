package com.tugos.dst.admin.entity;


        import com.baomidou.mybatisplus.annotation.TableId;
        import com.baomidou.mybatisplus.annotation.TableName;
        import com.fasterxml.jackson.annotation.JsonFormat;

        import lombok.AllArgsConstructor;
        import lombok.Builder;
        import lombok.Data;
        import lombok.NoArgsConstructor;

        import org.springframework.format.annotation.DateTimeFormat;


        import java.io.Serializable;
        import java.util.Date;


@Data
@TableName("player_log")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerLog implements Serializable {

    @TableId
    private Long id;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    private Date createTime;
    //房间id
    private String roomId;
    //科雷id
    private String userId;
    //玩家昵称
    private String name;
    //玩家角色
    private String prefab;
    //玩家生存天数
    private Integer playerage;


}
