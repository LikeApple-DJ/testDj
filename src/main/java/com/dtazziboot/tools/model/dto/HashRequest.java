package com.dtazziboot.tools.model.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * 哈希算法请求参数。
 */
public class HashRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 待哈希文本，必填。 */
    @NotBlank(message = "text cannot be blank")
    private String text;

    /** 算法，取值 MD5 / SHA256。 */
    @NotBlank(message = "algorithm cannot be blank")
    private String algorithm;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}