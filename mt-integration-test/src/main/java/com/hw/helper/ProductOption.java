package com.hw.helper;

import lombok.Data;

import java.util.List;

@Data
public class ProductOption {
    public String title;
    public List<OptionItem> options;
}
