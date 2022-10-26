package com.pintertamas.befake.notificationservice.listener;

import java.util.List;

interface Callback {
    void call(Long id, List<Long> affectedIds);
}
