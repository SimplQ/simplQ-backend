package me.simplq.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface DeviceRepository extends JpaRepository<Device, String> {
    Stream<Device> findByOwnerId(String ownerId);
}
