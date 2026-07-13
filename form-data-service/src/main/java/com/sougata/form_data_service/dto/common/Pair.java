package com.sougata.form_data_service.dto.common;

public record Pair<K, V>(
        K key, V Value
) {
}
