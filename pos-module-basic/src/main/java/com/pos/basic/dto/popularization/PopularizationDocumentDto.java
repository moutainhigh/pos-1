/*
 * Copyright (c) 2016 ywmj.com. All Rights Reserved.
 */
package com.pos.basic.dto.popularization;

import com.pos.common.util.validation.FieldChecker;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 推广文案信息DTO
 *
 * @author wangbing
 * @version 1.0, 2017/11/30
 */
public class PopularizationDocumentDto implements Serializable {

    private static final long serialVersionUID = -5090694475617474318L;
    @ApiModelProperty("自增主键id（更新时必传此参数）")
    private Long id;

    @ApiModelProperty("推广文本")
    private String document;

    @ApiModelProperty("推广图片列表")
    private List<String> images;

    @ApiModelProperty("是否可用")
    private Boolean available;

    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 新增或更新时必填字段检查
     */
    public void check() {
        FieldChecker.checkMaxLength(document, 700, "document");
        if (!CollectionUtils.isEmpty(images)) {
            FieldChecker.checkMaxSize(images, 9, "图片", "张数");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
