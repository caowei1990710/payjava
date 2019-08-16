package com.example.demo.Repository;

import com.example.demo.Model.Deposit;
import com.example.demo.Model.PayDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by snsoft on 27/10/2017.
 */
public interface PayDeviceRepository  extends JpaRepository<PayDevice,Integer>,JpaSpecificationExecutor<PayDevice> {
}
