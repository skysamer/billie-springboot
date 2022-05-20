package com.lab.smartmobility.billie.util;

import com.lab.smartmobility.billie.entity.VehicleReservation;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class DuplicateUtil {
    public static List<VehicleReservation> distinctArray(List<VehicleReservation> target, Object key){
        if(target != null){
            target = target.stream().filter(distinctByKey(o-> o.getVehicle())).collect(Collectors.toList());
        }
        return target;
    }

    //중복 제거를 위한 함수
    public static <T> Predicate<T> distinctByKey(Function<? super T,Object> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
