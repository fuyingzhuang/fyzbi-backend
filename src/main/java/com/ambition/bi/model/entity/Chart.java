package com.ambition.bi.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author Ambition
 * @since 2023-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Chart对象", description="")
public class Chart implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("`goal`")
    private String goal;

    @TableField("`name`")
    private String name;

    @TableField("chartData")
    private String chartData;

    @TableField("chartType")
    private String chartType;

    @TableField("genResult")
    private String genResult;

    @TableField("userId")
    private Long userId;

    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("isDelete")
    @TableLogic
    private Integer isDelete;


}
