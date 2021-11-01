package me.simplq.dao;

import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, String> {
  Stream<Device> findByOwnerId(String ownerId);
}
