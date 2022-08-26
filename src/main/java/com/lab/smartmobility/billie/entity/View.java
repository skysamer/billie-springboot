package com.lab.smartmobility.billie.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@RedisHash("view")
@Builder
public class View implements Serializable {
    @Id
    private String id;
    private long views;
    private LocalDateTime refreshTime;

    public void refresh(long views, LocalDateTime refreshTime){
        if(refreshTime.isAfter(this.refreshTime)){
            this.views = views;
            this.refreshTime = refreshTime;
        }
    }

}
